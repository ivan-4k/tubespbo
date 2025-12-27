package api;

import java.util.*;
import java.net.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class AntrianApiClient {
    private static final String BASE_URL = "http://localhost/application-tier/public";
    private Gson gson;
    
    public AntrianApiClient() {
        this.gson = new Gson();
    }
    
    private String makeRequest(String endpoint, String method, String requestBody) throws Exception {
        try {
            String fullUrl = BASE_URL + endpoint;
            System.out.println("🌐 Connecting to: " + fullUrl);
            
            URI uri = new URI(fullUrl);
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
            System.out.println("   Response Code: " + responseCode);
            
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
                
                // DEBUG: Print raw response
                String rawResponse = response.toString();
                System.out.println("   Raw Response (first 500 chars):");
                System.out.println("   " + (rawResponse.length() > 500 ? rawResponse.substring(0, 500) + "..." : rawResponse));
                
                if (responseCode >= 200 && responseCode < 300) {
                    return rawResponse;
                } else {
                    throw new Exception("HTTP " + responseCode + ": " + rawResponse);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Connection error: " + e.getMessage());
            throw e;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDaftarDokter() throws Exception {
        try {
            String response = makeRequest("/dokter", "GET", null);
            
            // Cek jika response adalah HTML (bukan JSON)
            if (response.trim().startsWith("<")) {
                throw new Exception("Server returned HTML instead of JSON. Check API endpoint.");
            }
            
            System.out.println("📦 Parsing dokter response...");
            
            Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> parsed = gson.fromJson(response, responseType);
            
            System.out.println("✅ Parsed successfully. Keys: " + parsed.keySet());
            
            if (parsed.get("success") != null && 
                Boolean.parseBoolean(parsed.get("success").toString())) {
                
                Object dataObj = parsed.get("data");
                if (dataObj instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    
                    Object dokterObj = data.get("dokter");
                    if (dokterObj != null) {
                        Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                        List<Map<String, Object>> dokterList = gson.fromJson(gson.toJson(dokterObj), listType);
                        System.out.println("✅ Found " + dokterList.size() + " doctors");
                        return dokterList;
                    }
                }
            }
            
            String errorMsg = parsed.get("message") != null ? 
                             parsed.get("message").toString() : "Failed to get doctors";
            System.out.println("❌ Error: " + errorMsg);
            throw new Exception(errorMsg);
            
        } catch (Exception e) {
            System.out.println("❌ Error in getDaftarDokter: " + e.getMessage());
            throw e;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAntrianAktif() throws Exception {
        try {
            String response = makeRequest("/antrian/aktif", "GET", null);
            
            Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> parsed = gson.fromJson(response, responseType);
            
            if (parsed.get("success") != null && 
                Boolean.parseBoolean(parsed.get("success").toString())) {
                
                Object dataObj = parsed.get("data");
                if (dataObj instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    
                    Object antrianObj = data.get("antrian");
                    if (antrianObj != null) {
                        Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                        return gson.fromJson(gson.toJson(antrianObj), listType);
                    }
                }
            }
            
            return new ArrayList<>(); // Return empty list if no data
            
        } catch (Exception e) {
            System.out.println("❌ Error in getAntrianAktif: " + e.getMessage());
            return new ArrayList<>(); // Return empty list on error
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAntrianSelesai() throws Exception {
        try {
            String response = makeRequest("/antrian/selesai", "GET", null);
            
            Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> parsed = gson.fromJson(response, responseType);
            
            if (parsed.get("success") != null && 
                Boolean.parseBoolean(parsed.get("success").toString())) {
                
                Object dataObj = parsed.get("data");
                if (dataObj instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) dataObj;
                    
                    Object antrianObj = data.get("antrian");
                    if (antrianObj != null) {
                        Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                        return gson.fromJson(gson.toJson(antrianObj), listType);
                    }
                }
            }
            
            return new ArrayList<>(); // Return empty list if no data
            
        } catch (Exception e) {
            System.out.println("❌ Error in getAntrianSelesai: " + e.getMessage());
            return new ArrayList<>(); // Return empty list on error
        }
    }
    
    public Map<String, Object> ambilAntrian(String nama, int umur, String alamat, 
                                           String keluhan, int idDokter) throws Exception {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nama", nama);
            requestBody.put("umur", umur);
            requestBody.put("alamat", alamat);
            requestBody.put("keluhan", keluhan);
            requestBody.put("dokter_id", idDokter);
            
            String jsonBody = gson.toJson(requestBody);
            System.out.println("📤 Sending request: " + jsonBody);
            
            String response = makeRequest("/antrian", "POST", jsonBody);
            
            Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
            return gson.fromJson(response, responseType);
            
        } catch (Exception e) {
            System.out.println("❌ Error in ambilAntrian: " + e.getMessage());
            throw e;
        }
    }
    
    public boolean updateStatusAntrian(int nomorAntrian, String status) throws Exception {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("status", status);
            
            String jsonBody = gson.toJson(requestBody);
            String endpoint = "/antrian/" + nomorAntrian + "/status";
            
            System.out.println("📤 Updating status: " + endpoint);
            
            String response = makeRequest(endpoint, "PUT", jsonBody);
            
            Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> parsed = gson.fromJson(response, responseType);
            
            return parsed.get("success") != null && 
                   Boolean.parseBoolean(parsed.get("success").toString());
            
        } catch (Exception e) {
            System.out.println("❌ Error in updateStatusAntrian: " + e.getMessage());
            return false;
        }
    }
    
    public boolean isServerAvailable() {
        try {
            System.out.println("🔍 Checking server availability...");
            String response = makeRequest("/health", "GET", null);
            
            // Cek jika response HTML
            if (response.trim().startsWith("<")) {
                System.out.println("❌ Server returned HTML, not JSON");
                return false;
            }
            
            Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> parsed = gson.fromJson(response, responseType);
            
            boolean available = parsed.get("success") != null && 
                               Boolean.parseBoolean(parsed.get("success").toString());
            
            System.out.println("   Server status: " + (available ? "✅ ONLINE" : "❌ OFFLINE"));
            return available;
            
        } catch (Exception e) {
            System.out.println("   Server check failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }
    }
}