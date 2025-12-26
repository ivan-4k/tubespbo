package controller;

import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;
import config.DatabaseConnection;

public class AntrianController {
    
    private Connection connection;
    
    public AntrianController() {
        try {
            this.connection = DatabaseConnection.getConnection();
            System.out.println("Koneksi database berhasil");
        } catch (Exception e) { // Tangkap Exception bukan SQLException
            System.err.println("Gagal koneksi database: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Gagal terhubung ke database: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ================= DOKTER =================
    public List<Map<String, Object>> getDaftarDokter() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        if (!isConnected()) {
            System.err.println("Koneksi database tidak tersedia");
            return result;
        }
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM dokter ORDER BY nama_dokter")) {
            
            while (rs.next()) {
                Map<String, Object> dokter = new HashMap<>();
                dokter.put("id", rs.getInt("id_dokter"));
                dokter.put("nama", rs.getString("nama_dokter"));
                dokter.put("spesialis", rs.getString("spesialis"));
                dokter.put("ruangan", rs.getString("ruangan"));
                result.add(dokter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public Map<String, String> getDetailDokter(int idDokter) {
        Map<String, String> detail = new HashMap<>();
        
        if (!isConnected()) {
            return detail;
        }
        
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT spesialis, ruangan FROM dokter WHERE id_dokter = ?")) {
            ps.setInt(1, idDokter);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                detail.put("spesialis", rs.getString("spesialis"));
                detail.put("ruangan", rs.getString("ruangan"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return detail;
    }
    
    // ================= PASIEN =================
    public int tambahPasien(String nama, int umur, String alamat, String keluhan) {
        if (!isConnected()) {
            return -1;
        }
        
        int generatedId = -1;
        
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO pasien(nama_pasien, umur, alamat, jenis_keluhan) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nama);
            ps.setInt(2, umur);
            ps.setString(3, alamat);
            ps.setString(4, keluhan);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error menambah pasien: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return generatedId;
    }
    
    // ================= ANTRIAN =================
    public int getNomorAntrianBerikutnya() {
        if (!isConnected()) {
            return 1;
        }
        
        int nomorBerikutnya = 1;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT IFNULL(MAX(nomor_antrian), 0) + 1 FROM antrian")) {
            
            if (rs.next()) {
                nomorBerikutnya = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return nomorBerikutnya;
    }
    
    public Map<String, Object> ambilAntrian(String nama, int umur, String alamat, String keluhan, int idDokter) {
        Map<String, Object> response = new HashMap<>();
        
        if (!isConnected()) {
            response.put("success", false);
            response.put("message", "Database tidak terhubung");
            return response;
        }
        
        try {
            // Validasi input
            if (nama == null || nama.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Nama pasien tidak boleh kosong");
                return response;
            }
            
            if (umur <= 0 || umur > 120) {
                response.put("success", false);
                response.put("message", "Umur tidak valid (1-120 tahun)");
                return response;
            }
            
            if (alamat == null || alamat.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Alamat tidak boleh kosong");
                return response;
            }
            
            if (keluhan == null || keluhan.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Keluhan tidak boleh kosong");
                return response;
            }
            
            // Cek dokter ada
            if (!isDokterExists(idDokter)) {
                response.put("success", false);
                response.put("message", "Dokter tidak ditemukan");
                return response;
            }
            
            // Tambah pasien
            int idPasien = tambahPasien(nama, umur, alamat, keluhan);
            if (idPasien == -1) {
                response.put("success", false);
                response.put("message", "Gagal menambahkan pasien");
                return response;
            }
            
            // Ambil nomor antrian
            int nomorAntrian = getNomorAntrianBerikutnya();
            
            // Tambah antrian
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO antrian(nomor_antrian, id_pasien, id_dokter, status, waktu_ambil) " +
                    "VALUES (?, ?, ?, 'MENUNGGU', NOW())")) {
                
                ps.setInt(1, nomorAntrian);
                ps.setInt(2, idPasien);
                ps.setInt(3, idDokter);
                ps.executeUpdate();
                
                response.put("success", true);
                response.put("message", "Antrian berhasil diambil");
                response.put("nomorAntrian", nomorAntrian);
                response.put("idPasien", idPasien);
                
            } catch (SQLException e) {
                response.put("success", false);
                response.put("message", "Gagal mengambil antrian: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    private boolean isDokterExists(int idDokter) {
        if (!isConnected()) return false;
        
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT(*) FROM dokter WHERE id_dokter = ?")) {
            ps.setInt(1, idDokter);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Map<String, Object>> getAntrianAktif() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        if (!isConnected()) {
            return result;
        }
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT a.nomor_antrian, p.nama_pasien, p.umur, p.alamat, p.jenis_keluhan, " +
                     "d.nama_dokter, d.spesialis, d.ruangan, a.status " +
                     "FROM antrian a " +
                     "JOIN pasien p ON a.id_pasien = p.id_pasien " +
                     "JOIN dokter d ON a.id_dokter = d.id_dokter " +
                     "WHERE a.status IN ('MENUNGGU', 'DILAYANI') " +
                     "ORDER BY CASE a.status WHEN 'MENUNGGU' THEN 1 ELSE 2 END, a.nomor_antrian")) {
            
            while (rs.next()) {
                Map<String, Object> antrian = new HashMap<>();
                antrian.put("nomor", rs.getInt("nomor_antrian"));
                antrian.put("nama", rs.getString("nama_pasien"));
                antrian.put("umur", rs.getInt("umur"));
                antrian.put("alamat", rs.getString("alamat"));
                antrian.put("keluhan", rs.getString("jenis_keluhan"));
                antrian.put("dokter", rs.getString("nama_dokter"));
                antrian.put("spesialis", rs.getString("spesialis"));
                antrian.put("ruangan", rs.getString("ruangan"));
                antrian.put("status", rs.getString("status"));
                result.add(antrian);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public List<Map<String, Object>> getAntrianSelesai() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        if (!isConnected()) {
            return result;
        }
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT a.nomor_antrian, p.nama_pasien, p.umur, p.alamat, p.jenis_keluhan, " +
                     "d.nama_dokter, a.waktu_selesai " +
                     "FROM antrian a " +
                     "JOIN pasien p ON a.id_pasien = p.id_pasien " +
                     "JOIN dokter d ON a.id_dokter = d.id_dokter " +
                     "WHERE a.status = 'SELESAI' " +
                     "ORDER BY a.waktu_selesai DESC")) {
            
            while (rs.next()) {
                Map<String, Object> antrian = new HashMap<>();
                antrian.put("nomor", rs.getInt("nomor_antrian"));
                antrian.put("nama", rs.getString("nama_pasien"));
                antrian.put("umur", rs.getInt("umur"));
                antrian.put("alamat", rs.getString("alamat"));
                antrian.put("keluhan", rs.getString("jenis_keluhan"));
                antrian.put("dokter", rs.getString("nama_dokter"));
                antrian.put("waktu_selesai", rs.getTimestamp("waktu_selesai"));
                result.add(antrian);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public boolean updateStatusAntrian(int nomorAntrian, String status) {
        if (!isConnected()) {
            return false;
        }
        
        try {
            String waktuKolom = "";
            if ("DILAYANI".equals(status)) {
                waktuKolom = ", waktu_mulai = NOW()";
            } else if ("SELESAI".equals(status)) {
                waktuKolom = ", waktu_selesai = NOW()";
            }
            
            String sql = "UPDATE antrian SET status = ?" + waktuKolom + " WHERE nomor_antrian = ?";
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, nomorAntrian);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error update status: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ================= UTILITY =================
    public int getJumlahAntrianAktif() {
        if (!isConnected()) {
            return 0;
        }
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM antrian WHERE status IN ('MENUNGGU', 'DILAYANI')")) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database ditutup");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}