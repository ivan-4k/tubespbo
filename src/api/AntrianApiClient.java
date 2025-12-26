package api;

import java.util.*;
import java.net.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class AntrianApiClient {
    private static final String BASE_URL = "http://localhost/app-klinik/public";
    private Gson gson;
    
    public AntrianApiClient() {
        this.gson = new Gson();
    }
    
    private String makeRequest(String endpoint, String method, String requestBody) throws Exception {
        // PERBAIKAN: Gunakan URI terlebih dahulu
        URI uri = new URI(BASE_URL + endpoint);
        URL url = uri.toURL();
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(5000);
        
        if (requestBody != null && !requestBody.isEmpty() && 
            (method.equals("POST") || method.equals("PUT"))) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }
        
        int responseCode = conn.getResponseCode();
        
        InputStream inputStream;
        if (responseCode >= 200 && responseCode < 300) {
            inputStream = conn.getInputStream();
        } else {
            inputStream = conn.getErrorStream();
        }
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            
            if (responseCode >= 200 && responseCode < 300) {
                return response.toString();
            } else {
                throw new Exception("HTTP " + responseCode + ": " + response.toString());
            }
        }
    }
    
    // PERBAIKAN: Tambahkan @SuppressWarnings untuk unchecked cast
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDaftarDokter() throws Exception {
        String response = makeRequest("/dokter", "GET", null);
        
        Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> parsed = gson.fromJson(response, responseType);
        
        if (parsed.get("success") != null && 
            Boolean.parseBoolean(parsed.get("success").toString())) {
            
            Map<String, Object> data = (Map<String, Object>) parsed.get("data");
            
            if (data != null && data.get("dokter") != null) {
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                return gson.fromJson(gson.toJson(data.get("dokter")), listType);
            }
        }
        
        throw new Exception(parsed.get("message") != null ? 
                          parsed.get("message").toString() : "Failed to get doctors");
    }
    
    // PERBAIKAN: Tambahkan @SuppressWarnings untuk unchecked cast
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAntrianAktif() throws Exception {
        String response = makeRequest("/antrian/aktif", "GET", null);
        
        Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> parsed = gson.fromJson(response, responseType);
        
        if (parsed.get("success") != null && 
            Boolean.parseBoolean(parsed.get("success").toString())) {
            
            Map<String, Object> data = (Map<String, Object>) parsed.get("data");
            
            if (data != null && data.get("antrian") != null) {
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                return gson.fromJson(gson.toJson(data.get("antrian")), listType);
            }
        }
        
        return new ArrayList<>(); // Return empty list if no data
    }
    
    // PERBAIKAN: Tambahkan @SuppressWarnings untuk unchecked cast
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAntrianSelesai() throws Exception {
        String response = makeRequest("/antrian/selesai", "GET", null);
        
        Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> parsed = gson.fromJson(response, responseType);
        
        if (parsed.get("success") != null && 
            Boolean.parseBoolean(parsed.get("success").toString())) {
            
            Map<String, Object> data = (Map<String, Object>) parsed.get("data");
            
            if (data != null && data.get("antrian") != null) {
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                return gson.fromJson(gson.toJson(data.get("antrian")), listType);
            }
        }
        
        return new ArrayList<>(); // Return empty list if no data
    }
    
    public Map<String, Object> ambilAntrian(String nama, int umur, String alamat, 
                                           String keluhan, int idDokter) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("nama", nama);
        requestBody.put("umur", umur);
        requestBody.put("alamat", alamat);
        requestBody.put("keluhan", keluhan);
        requestBody.put("dokter_id", idDokter);
        
        String jsonBody = gson.toJson(requestBody);
        String response = makeRequest("/antrian", "POST", jsonBody);
        
        Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(response, responseType);
    }
    
    public boolean updateStatusAntrian(int nomorAntrian, String status) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", status);
        
        String jsonBody = gson.toJson(requestBody);
        String endpoint = "/antrian/" + nomorAntrian + "/status";
        
        String response = makeRequest(endpoint, "PUT", jsonBody);
        
        Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> parsed = gson.fromJson(response, responseType);
        
        return parsed.get("success") != null && 
               Boolean.parseBoolean(parsed.get("success").toString());
    }
    
    public boolean isServerAvailable() {
        try {
            String response = makeRequest("/health", "GET", null);
            
            Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> parsed = gson.fromJson(response, responseType);
            
            return parsed.get("success") != null && 
                   Boolean.parseBoolean(parsed.get("success").toString());
        } catch (Exception e) {
            return false;
        }
    }
}