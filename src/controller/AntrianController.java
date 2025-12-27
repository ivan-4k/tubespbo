package controller;

import java.util.*;
import api.AntrianApiClient;

public class AntrianController {
    private List<Map<String, Object>> antrianAktif = new ArrayList<>();
    private List<Map<String, Object>> antrianSelesai = new ArrayList<>();
    private List<Map<String, Object>> dokterList = new ArrayList<>();
    
    private AntrianApiClient apiClient;
    private boolean isServerOnline = false;
    
    public AntrianController() {
        this.apiClient = new AntrianApiClient();
        
        // Cek koneksi server
        this.isServerOnline = checkServerConnection();
        System.out.println("Server status: " + (isServerOnline ? "ONLINE" : "OFFLINE"));
        
        // Load data awal
        loadInitialData();
    }
    
    private boolean checkServerConnection() {
        try {
            return apiClient.isServerAvailable();
        } catch (Exception e) {
            System.out.println("⚠️ Server tidak tersedia: " + e.getMessage());
            return false;
        }
    }
    
    private void loadInitialData() {
        if (isServerOnline) {
            try {
                // Load dokter dari server
                dokterList = apiClient.getDaftarDokter();
                System.out.println("✅ Data dokter berhasil di-load dari server: " + dokterList.size() + " dokter");
                
                // Load antrian aktif dari server
                antrianAktif = apiClient.getAntrianAktif();
                System.out.println("✅ Antrian aktif berhasil di-load dari server: " + antrianAktif.size() + " antrian");
                
                // Load antrian selesai dari server
                antrianSelesai = apiClient.getAntrianSelesai();
                System.out.println("✅ Antrian selesai berhasil di-load dari server: " + antrianSelesai.size() + " antrian");
                
            } catch (Exception e) {
                System.out.println("❌ Gagal load data dari server: " + e.getMessage());
                isServerOnline = false;
                loadDokterDummy();
            }
        } else {
            System.out.println("⚠️ Mode OFFLINE: Menggunakan data dummy");
            loadDokterDummy();
        }
    }
    
    private void loadDokterDummy() {
        // Data dummy untuk mode offline
        dokterList.clear();
        
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
        
        Map<String, Object> dokter3 = new HashMap<>();
        dokter3.put("id", 3);
        dokter3.put("nama", "Dr. Budi");
        dokter3.put("spesialis", "Bedah");
        dokter3.put("ruangan", "103");
        
        dokterList.add(dokter1);
        dokterList.add(dokter2);
        dokterList.add(dokter3);
        
        System.out.println("✅ Data dummy dokter dimuat: " + dokterList.size() + " dokter");
    }
    
    // ================= DOKTER =================
    public List<Map<String, Object>> getDaftarDokter() {
        if (isServerOnline) {
            try {
                List<Map<String, Object>> serverDokter = apiClient.getDaftarDokter();
                if (serverDokter != null && !serverDokter.isEmpty()) {
                    dokterList = serverDokter; // Update local cache
                    return new ArrayList<>(serverDokter);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Gagal ambil dokter dari server: " + e.getMessage());
                isServerOnline = false;
            }
        }
        
        // Fallback to local data
        return new ArrayList<>(dokterList);
    }
    
    public Map<String, String> getDetailDokter(int idDokter) {
        Map<String, String> detail = new HashMap<>();
        
        for (Map<String, Object> dokter : dokterList) {
            // Handle both Integer and String ID
            Object id = dokter.get("id");
            boolean isMatch = false;
            
            if (id instanceof Integer) {
                isMatch = ((Integer) id) == idDokter;
            } else if (id instanceof String) {
                try {
                    isMatch = Integer.parseInt((String) id) == idDokter;
                } catch (NumberFormatException e) {
                    // Skip if ID is not numeric
                }
            }
            
            if (isMatch) {
                detail.put("spesialis", dokter.get("spesialis") != null ? dokter.get("spesialis").toString() : "");
                detail.put("ruangan", dokter.get("ruangan") != null ? dokter.get("ruangan").toString() : "");
                break;
            }
        }
        
        return detail;
    }
    
    // ================= ANTRIAN =================
    public Map<String, Object> ambilAntrian(String nama, int umur, String alamat, 
                                       String keluhan, int idDokter) {
    // Client-side validation
    if (nama == null || nama.trim().isEmpty()) {
        return createErrorResponse("Nama pasien tidak boleh kosong");
    }
    
    if (umur <= 0 || umur > 120) {
        return createErrorResponse("Umur tidak valid (1-120 tahun)");
    }
    
    if (alamat == null || alamat.trim().isEmpty()) {
        return createErrorResponse("Alamat tidak boleh kosong");
    }
    
    if (keluhan == null || keluhan.trim().isEmpty()) {
        return createErrorResponse("Keluhan tidak boleh kosong");
    }
    
    // Get doctor info for local storage
    Map<String, String> dokterInfo = getDoctorInfoForId(idDokter);
    
    if (isServerOnline) {
        try {
            Map<String, Object> response = apiClient.ambilAntrian(nama, umur, alamat, keluhan, idDokter);
            
            // Parse response - langsung gunakan response sebagai Map
            boolean success = response.get("success") != null && 
                            Boolean.parseBoolean(response.get("success").toString());
            
            if (success) {
                // Add to local cache
                Map<String, Object> antrianBaru = createAntrianData(
                    response.get("nomorAntrian") != null ? response.get("nomorAntrian") : 
                    response.get("nomor"),
                    nama, umur, alamat, keluhan, dokterInfo
                );
                
                antrianAktif.add(antrianBaru);
                
                return Map.of(
                    "success", true,
                    "message", response.get("message") != null ? 
                              response.get("message").toString() : "Antrian berhasil diambil",
                    "nomorAntrian", antrianBaru.get("nomor")
                );
            } else {
                // Server returned error
                return Map.of(
                    "success", false,
                    "message", response.get("message") != null ? 
                              response.get("message").toString() : "Gagal mengambil antrian"
                );
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Error API: " + e.getMessage());
            isServerOnline = false;
            
            // Fallback to local mode
            return ambilAntrianLokal(nama, umur, alamat, keluhan, dokterInfo);
        }
    } else {
        // Offline mode
        return ambilAntrianLokal(nama, umur, alamat, keluhan, dokterInfo);
    }
}
    
    private Map<String, String> getDoctorInfoForId(int idDokter) {
        Map<String, String> info = new HashMap<>();
        
        for (Map<String, Object> dokter : dokterList) {
            Object id = dokter.get("id");
            boolean isMatch = false;
            
            if (id instanceof Integer) {
                isMatch = ((Integer) id) == idDokter;
            } else if (id instanceof String) {
                try {
                    isMatch = Integer.parseInt((String) id) == idDokter;
                } catch (NumberFormatException e) {
                    // Skip
                }
            }
            
            if (isMatch) {
                info.put("nama", dokter.get("nama") != null ? dokter.get("nama").toString() : "Tidak Diketahui");
                info.put("spesialis", dokter.get("spesialis") != null ? dokter.get("spesialis").toString() : "-");
                info.put("ruangan", dokter.get("ruangan") != null ? dokter.get("ruangan").toString() : "-");
                break;
            }
        }
        
        if (info.isEmpty()) {
            info.put("nama", "Tidak Diketahui");
            info.put("spesialis", "-");
            info.put("ruangan", "-");
        }
        
        return info;
    }
    
    private Map<String, Object> createAntrianData(Object nomor, String nama, int umur, String alamat, 
                                                 String keluhan, Map<String, String> dokterInfo) {
        Map<String, Object> antrian = new HashMap<>();
        
        // Convert nomor to appropriate type
        if (nomor instanceof Integer) {
            antrian.put("nomor", nomor);
        } else if (nomor instanceof String) {
            try {
                antrian.put("nomor", Integer.parseInt((String) nomor));
            } catch (NumberFormatException e) {
                antrian.put("nomor", nomor); // Keep as string if not numeric
            }
        } else {
            antrian.put("nomor", nomor != null ? nomor : antrianAktif.size() + 1);
        }
        
        antrian.put("nama", nama);
        antrian.put("umur", umur);
        antrian.put("alamat", alamat);
        antrian.put("keluhan", keluhan);
        antrian.put("dokter", dokterInfo.get("nama"));
        antrian.put("spesialis", dokterInfo.get("spesialis"));
        antrian.put("ruangan", dokterInfo.get("ruangan"));
        antrian.put("status", "MENUNGGU");
        
        return antrian;
    }
    
    private Map<String, Object> ambilAntrianLokal(String nama, int umur, String alamat, 
                                                 String keluhan, Map<String, String> dokterInfo) {
        int nomorAntrian = antrianAktif.size() + antrianSelesai.size() + 1;
        
        Map<String, Object> antrianBaru = createAntrianData(
            nomorAntrian, nama, umur, alamat, keluhan, dokterInfo
        );
        
        antrianAktif.add(antrianBaru);
        
        System.out.println("📝 Antrian disimpan secara lokal (Mode Offline)");
        
        return Map.of(
            "success", true,
            "message", "Antrian berhasil diambil (Mode Offline)",
            "nomorAntrian", antrianBaru.get("nomor")
        );
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        return Map.of(
            "success", false,
            "message", message
        );
    }
    
    public List<Map<String, Object>> getAntrianAktif() {
        if (isServerOnline) {
            try {
                List<Map<String, Object>> serverAntrian = apiClient.getAntrianAktif();
                if (serverAntrian != null && !serverAntrian.isEmpty()) {
                    // Update local cache with server data
                    antrianAktif.clear();
                    antrianAktif.addAll(serverAntrian);
                    return new ArrayList<>(serverAntrian);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Gagal ambil antrian aktif dari server: " + e.getMessage());
                isServerOnline = false;
            }
        }
        
        return new ArrayList<>(antrianAktif);
    }
    
    public List<Map<String, Object>> getAntrianSelesai() {
        if (isServerOnline) {
            try {
                List<Map<String, Object>> serverAntrian = apiClient.getAntrianSelesai();
                if (serverAntrian != null && !serverAntrian.isEmpty()) {
                    // Update local cache with server data
                    antrianSelesai.clear();
                    antrianSelesai.addAll(serverAntrian);
                    return new ArrayList<>(serverAntrian);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Gagal ambil antrian selesai dari server: " + e.getMessage());
                isServerOnline = false;
            }
        }
        
        return new ArrayList<>(antrianSelesai);
    }
    
    public boolean updateStatusAntrian(int nomorAntrian, String status) {
        // Validate status
        List<String> validStatus = Arrays.asList("MENUNGGU", "DILAYANI", "SELESAI", "BATAL");
        if (!validStatus.contains(status.toUpperCase())) {
            System.out.println("❌ Status tidak valid: " + status);
            return false;
        }
        
        status = status.toUpperCase();
        
        if (isServerOnline) {
            try {
                boolean serverSuccess = apiClient.updateStatusAntrian(nomorAntrian, status);
                
                if (serverSuccess) {
                    // Also update local cache
                    updateStatusAntrianLokal(nomorAntrian, status);
                    return true;
                } else {
                    return false;
                }
                
            } catch (Exception e) {
                System.out.println("⚠️ Gagal update status ke server: " + e.getMessage());
                isServerOnline = false;
                
                // Fallback to local update
                return updateStatusAntrianLokal(nomorAntrian, status);
            }
        } else {
            // Offline mode
            return updateStatusAntrianLokal(nomorAntrian, status);
        }
    }
    
    private boolean updateStatusAntrianLokal(int nomorAntrian, String status) {
        // Search in active queue
        for (Map<String, Object> antrian : antrianAktif) {
            Object nomor = antrian.get("nomor");
            boolean isMatch = false;
            
            if (nomor instanceof Integer) {
                isMatch = ((Integer) nomor) == nomorAntrian;
            } else if (nomor instanceof String) {
                try {
                    isMatch = Integer.parseInt((String) nomor) == nomorAntrian;
                } catch (NumberFormatException e) {
                    // Try direct string comparison
                    isMatch = nomor.toString().equals(String.valueOf(nomorAntrian));
                }
            }
            
            if (isMatch) {
                antrian.put("status", status);
                
                // If completed, move to completed queue
                if (status.equals("SELESAI") || status.equals("BATAL")) {
                    Map<String, Object> completedAntrian = new HashMap<>(antrian);
                    
                    // Add completion timestamp
                    completedAntrian.put("waktu_selesai", new Date().toString());
                    
                    antrianSelesai.add(completedAntrian);
                    antrianAktif.remove(antrian);
                }
                
                System.out.println("✅ Status antrian " + nomorAntrian + " diupdate ke: " + status);
                return true;
            }
        }
        
        System.out.println("❌ Antrian tidak ditemukan: " + nomorAntrian);
        return false;
    }
    
    // ================= UTILITY =================
    public boolean isConnected() {
        return isServerOnline;
    }
    
    public void reconnect() {
        isServerOnline = checkServerConnection();
        if (isServerOnline) {
            System.out.println("✅ Terhubung kembali ke server");
            loadInitialData();
        } else {
            System.out.println("❌ Gagal terhubung ke server");
        }
    }
    
    public String getConnectionStatus() {
        return isServerOnline ? "ONLINE" : "OFFLINE";
    }
    
    // Untuk debugging
    public void printDebugInfo() {
        System.out.println("\n=== DEBUG INFO ===");
        System.out.println("Server Status: " + getConnectionStatus());
        System.out.println("Jumlah Dokter: " + dokterList.size());
        System.out.println("Antrian Aktif: " + antrianAktif.size());
        System.out.println("Antrian Selesai: " + antrianSelesai.size());
        
        System.out.println("\nDaftar Dokter:");
        for (Map<String, Object> dokter : dokterList) {
            System.out.println("  - " + dokter.get("nama") + " (ID: " + dokter.get("id") + ")");
        }
        
        System.out.println("\nAntrian Aktif:");
        for (Map<String, Object> antrian : antrianAktif) {
            System.out.println("  - #" + antrian.get("nomor") + ": " + 
                             antrian.get("nama") + " (" + antrian.get("status") + ")");
        }
        System.out.println("==================\n");
    }
}