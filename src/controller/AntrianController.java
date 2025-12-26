package controller;

import java.util.*;
import api.AntrianApiClient;

public class AntrianController {
    private List<Map<String, Object>> antrianAktif = new ArrayList<>();
    private List<Map<String, Object>> antrianSelesai = new ArrayList<>();
    private List<Map<String, Object>> dokterList = new ArrayList<>();
    
    // API Client untuk komunikasi dengan server
    private AntrianApiClient apiClient;
    
    public AntrianController() {
        // Inisialisasi API Client
        this.apiClient = new AntrianApiClient();
        // Load data awal dari server via API
        loadInitialData();
    }
    
    private void loadInitialData() {
        try {
            // Panggil API untuk ambil data dokter dari server
            dokterList = apiClient.getDaftarDokter();
            
            // Panggil API untuk ambil antrian aktif dari server
            antrianAktif = apiClient.getAntrianAktif();
            
            // Panggil API untuk ambil antrian selesai dari server
            antrianSelesai = apiClient.getAntrianSelesai();
            
            System.out.println("Data berhasil di-load dari server");
            
        } catch (Exception e) {
            System.out.println("Gagal load data dari server, menggunakan data dummy: " + e.getMessage());
            loadDokterDummy(); // Fallback ke dummy data
        }
    }
    
    private void loadDokterDummy() {
        // Data dummy hanya untuk testing saat server offline
        Map<String, Object> dokter1 = new HashMap<>();
        dokter1.put("id", 1);
        dokter1.put("nama", "Dr. Ahmad");
        dokter1.put("spesialis", "Umum");
        dokter1.put("ruangan", "101");
        
        Map<String, Object> dokter2 = new HashMap<>();
        dokter2.put("id", 2);
        dokter2.put("nama", "Dr. Siti");
        dokter2.put("spesialis", "Anak");
        dokter2.put("ruangan", "102");
        
        dokterList.add(dokter1);
        dokterList.add(dokter2);
    }
    
    // ================= DOKTER =================
    public List<Map<String, Object>> getDaftarDokter() {
        try {
            // Coba ambil dari server terlebih dahulu
            return apiClient.getDaftarDokter();
        } catch (Exception e) {
            // Fallback ke data lokal jika server offline
            System.out.println("Gagal ambil dokter dari server: " + e.getMessage());
            return new ArrayList<>(dokterList);
        }
    }
    
    public Map<String, String> getDetailDokter(int idDokter) {
        Map<String, String> detail = new HashMap<>();
        
        for (Map<String, Object> dokter : dokterList) {
            if (dokter.get("id").equals(idDokter)) {
                detail.put("spesialis", (String) dokter.get("spesialis"));
                detail.put("ruangan", (String) dokter.get("ruangan"));
                break;
            }
        }
        
        return detail;
    }
    
    // ================= ANTRIAN =================
    public Map<String, Object> ambilAntrian(String nama, int umur, String alamat, 
                                           String keluhan, int idDokter) {
        // Validasi client-side terlebih dahulu
        if (nama == null || nama.trim().isEmpty()) {
            return createErrorResponse("Nama pasien tidak boleh kosong");
        }
        
        if (umur <= 0 || umur > 120) {
            return createErrorResponse("Umur tidak valid (1-120 tahun)");
        }
        
        try {
            // Kirim ke server via API
            Map<String, Object> response = apiClient.ambilAntrian(nama, umur, alamat, keluhan, idDokter);
            
            // Jika berhasil, update data lokal
            if ((Boolean) response.get("success")) {
                // Tambahkan ke antrian aktif lokal
                Map<String, Object> antrianBaru = new HashMap<>();
                antrianBaru.put("nomor", response.get("nomorAntrian"));
                antrianBaru.put("nama", nama);
                antrianBaru.put("umur", umur);
                antrianBaru.put("alamat", alamat);
                antrianBaru.put("keluhan", keluhan);
                antrianBaru.put("dokter", getNamaDokter(idDokter));
                antrianBaru.put("spesialis", getSpesialisDokter(idDokter));
                antrianBaru.put("ruangan", getRuanganDokter(idDokter));
                antrianBaru.put("status", "MENUNGGU");
                
                antrianAktif.add(antrianBaru);
            }
            
            return response;
            
        } catch (Exception e) {
            System.out.println("Error API: " + e.getMessage());
            
            // Fallback: simpan di lokal jika server offline
            return ambilAntrianLokal(nama, umur, alamat, keluhan, idDokter);
        }
    }
    
    private Map<String, Object> ambilAntrianLokal(String nama, int umur, String alamat, 
                                                 String keluhan, int idDokter) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int nomorAntrian = antrianAktif.size() + 1;
            
            Map<String, Object> antrianBaru = new HashMap<>();
            antrianBaru.put("nomor", nomorAntrian);
            antrianBaru.put("nama", nama);
            antrianBaru.put("umur", umur);
            antrianBaru.put("alamat", alamat);
            antrianBaru.put("keluhan", keluhan);
            antrianBaru.put("dokter", getNamaDokter(idDokter));
            antrianBaru.put("spesialis", getSpesialisDokter(idDokter));
            antrianBaru.put("ruangan", getRuanganDokter(idDokter));
            antrianBaru.put("status", "MENUNGGU");
            
            antrianAktif.add(antrianBaru);
            
            response.put("success", true);
            response.put("message", "Antrian berhasil diambil (Mode Offline)");
            response.put("nomorAntrian", nomorAntrian);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }
        
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
    
    private String getNamaDokter(int idDokter) {
        for (Map<String, Object> dokter : dokterList) {
            if (dokter.get("id").equals(idDokter)) {
                return (String) dokter.get("nama");
            }
        }
        return "Tidak Diketahui";
    }
    
    private String getSpesialisDokter(int idDokter) {
        for (Map<String, Object> dokter : dokterList) {
            if (dokter.get("id").equals(idDokter)) {
                return (String) dokter.get("spesialis");
            }
        }
        return "-";
    }
    
    private String getRuanganDokter(int idDokter) {
        for (Map<String, Object> dokter : dokterList) {
            if (dokter.get("id").equals(idDokter)) {
                return (String) dokter.get("ruangan");
            }
        }
        return "-";
    }
    
    public List<Map<String, Object>> getAntrianAktif() {
        try {
            // Coba ambil dari server
            return apiClient.getAntrianAktif();
        } catch (Exception e) {
            // Fallback ke data lokal
            System.out.println("Gagal ambil antrian aktif dari server: " + e.getMessage());
            return new ArrayList<>(antrianAktif);
        }
    }
    
    public List<Map<String, Object>> getAntrianSelesai() {
        try {
            // Coba ambil dari server
            return apiClient.getAntrianSelesai();
        } catch (Exception e) {
            // Fallback ke data lokal
            System.out.println("Gagal ambil antrian selesai dari server: " + e.getMessage());
            return new ArrayList<>(antrianSelesai);
        }
    }
    
    public boolean updateStatusAntrian(int nomorAntrian, String status) {
        try {
            // Kirim update ke server
            boolean success = apiClient.updateStatusAntrian(nomorAntrian, status);
            
            if (success) {
                // Update data lokal juga
                updateStatusAntrianLokal(nomorAntrian, status);
            }
            
            return success;
            
        } catch (Exception e) {
            System.out.println("Error API update status: " + e.getMessage());
            
            // Fallback: update lokal saja
            return updateStatusAntrianLokal(nomorAntrian, status);
        }
    }
    
    private boolean updateStatusAntrianLokal(int nomorAntrian, String status) {
        for (Map<String, Object> antrian : antrianAktif) {
            if (antrian.get("nomor").equals(nomorAntrian)) {
                antrian.put("status", status);
                
                if (status.equals("SELESAI")) {
                    antrianSelesai.add(new HashMap<>(antrian));
                    antrianAktif.remove(antrian);
                }
                
                return true;
            }
        }
        return false;
    }
    
    // ================= UTILITY =================
    public boolean isConnected() {
        try {
            return apiClient.isServerAvailable();
        } catch (Exception e) {
            return false;
        }
    }
}