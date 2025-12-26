package api;

import java.util.*;
import java.net.*;

public class AntrianApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    
    public List<Map<String, Object>> getDaftarDokter() throws Exception {
        // Simulasi API call ke server
        // TODO: Implementasi REST API call sebenarnya
        throw new Exception("Server belum tersedia");
    }
    
    public List<Map<String, Object>> getAntrianAktif() throws Exception {
        // TODO: Implementasi REST API call sebenarnya
        throw new Exception("Server belum tersedia");
    }
    
    public List<Map<String, Object>> getAntrianSelesai() throws Exception {
        // TODO: Implementasi REST API call sebenarnya
        throw new Exception("Server belum tersedia");
    }
    
    public Map<String, Object> ambilAntrian(String nama, int umur, String alamat, 
                                           String keluhan, int idDokter) throws Exception {
        // TODO: Implementasi REST API call sebenarnya
        throw new Exception("Server belum tersedia");
    }
    
    public boolean updateStatusAntrian(int nomorAntrian, String status) throws Exception {
        // TODO: Implementasi REST API call sebenarnya
        throw new Exception("Server belum tersedia");
    }
    
    public boolean isServerAvailable() {
        // Cek koneksi ke server
        try {
            URL url = new URI(BASE_URL + "/health").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            int responseCode = conn.getResponseCode();
            return (responseCode == 200);
        } catch (Exception e) {
            return false;
        }
    }
}