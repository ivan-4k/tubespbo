package view;

import api.WebSocketAntrianClient;
import com.google.gson.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WebSocketAntrianPanel extends JPanel {
    
    private JLabel lblStatus;
    private JLabel lblAntrianSekarang;
    private JLabel lblPasienSekarang;
    private JLabel lblDokterSekarang;
    private JTextArea txtLog;
    private DefaultListModel<String> listModelAntrian;
    private JList<String> listAntrian;
    
    private WebSocketAntrianClient webSocketClient;
    private List<JsonObject> antrianList = new ArrayList<>();
    
    public WebSocketAntrianPanel() {
        initComponents();
        connectWebSocket();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("🩺 DISPLAY ANTRIAN REAL-TIME", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        lblStatus = new JLabel("⏳ MENGHUBUNGKAN...", JLabel.RIGHT);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblStatus, BorderLayout.EAST);
        
        // Antrian Sekarang Panel
        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.setBorder(BorderFactory.createTitledBorder("ANTRIAN SEDANG DILAYANI"));
        currentPanel.setBackground(new Color(240, 248, 255));
        
        JPanel antrianInfoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        antrianInfoPanel.setBackground(new Color(240, 248, 255));
        
        lblAntrianSekarang = new JLabel("No. -", JLabel.CENTER);
        lblAntrianSekarang.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblAntrianSekarang.setForeground(new Color(220, 20, 60));
        
        lblPasienSekarang = new JLabel("Nama: -", JLabel.CENTER);
        lblPasienSekarang.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        lblDokterSekarang = new JLabel("Dokter: -", JLabel.CENTER);
        lblDokterSekarang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        antrianInfoPanel.add(lblAntrianSekarang);
        antrianInfoPanel.add(lblPasienSekarang);
        antrianInfoPanel.add(lblDokterSekarang);
        
        currentPanel.add(antrianInfoPanel, BorderLayout.CENTER);
        
        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnRefresh = new JButton("🔄 Refresh");
        JButton btnPanggil = new JButton("📢 Panggil Berikutnya");
        
        btnRefresh.addActionListener(e -> refreshData());
        btnPanggil.addActionListener(e -> panggilAntrianBerikutnya());
        
        controlPanel.add(btnRefresh);
        controlPanel.add(btnPanggil);
        
        currentPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Antrian List Panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("DAFTAR ANTRIAN MENUNGGU"));
        
        listModelAntrian = new DefaultListModel<>();
        listAntrian = new JList<>(listModelAntrian);
        listAntrian.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(listAntrian);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Log Panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("LOG REAL-TIME"));
        
        txtLog = new JTextArea(8, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(txtLog);
        
        logPanel.add(logScroll, BorderLayout.CENTER);
        
        // Layout utama
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.add(currentPanel);
        topPanel.add(listPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
        
        // Timer untuk update time
        new Timer(1000, e -> updateTime()).start();
    }
    
    private void connectWebSocket() {
        String wsUrl = "ws://localhost:3000/ws/antrian";
        
        try {
            webSocketClient = new WebSocketAntrianClient(wsUrl, 
                this::handleWebSocketMessage,
                this::updateConnectionStatus
            );
            
            // Connect to WebSocket
            webSocketClient.connect();
            
        } catch (Exception e) {
            appendLog("❌ Error connecting WebSocket: " + e.getMessage());
        }
    }
    
    private void updateConnectionStatus(boolean isConnected) {
        SwingUtilities.invokeLater(() -> {
            if (isConnected) {
                lblStatus.setText("✅ TERHUBUNG");
                lblStatus.setForeground(Color.GREEN);
            } else {
                lblStatus.setText("❌ TERPUTUS");
                lblStatus.setForeground(Color.RED);
            }
        });
    }
    
    private void handleWebSocketMessage(String message) {
        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            JsonObject json = jsonElement.getAsJsonObject();
            String type = json.get("type").getAsString();
            JsonObject data = json.get("data").getAsJsonObject();
            
            SwingUtilities.invokeLater(() -> {
                appendLog("📨 [" + type + "]: " + data.toString());
                
                switch (type) {
                    case "ANTRIAN_BARU":
                        handleAntrianBaru(data);
                        break;
                    case "STATUS_UPDATED":
                        handleStatusUpdate(data);
                        break;
                    case "PANGGIL_ANTRIAN":
                        handlePanggilanAntrian(data);
                        break;
                    case "DASHBOARD_UPDATE":
                        updateDashboard(data);
                        break;
                }
            });
            
        } catch (Exception e) {
            appendLog("❌ Error parsing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleAntrianBaru(JsonObject antrian) {
        antrianList.add(antrian);
        updateAntrianList();
        playNotificationSound();
        showNotification("Antrian Baru", 
            "No. " + antrian.get("nomor").getAsInt() + " - " + 
            antrian.get("namaPasien").getAsString());
    }
    
    private void handleStatusUpdate(JsonObject antrian) {
        int nomor = antrian.get("nomor").getAsInt();
        String status = antrian.get("status").getAsString();
        
        // Update in list
        for (int i = 0; i < antrianList.size(); i++) {
            JsonObject item = antrianList.get(i);
            if (item.get("nomor").getAsInt() == nomor) {
                antrianList.set(i, antrian);
                break;
            }
        }
        
        if (status.equals("DILAYANI")) {
            updateCurrentAntrian(antrian);
            playCallSound();
        }
        
        updateAntrianList();
    }
    
    private void handlePanggilanAntrian(JsonObject antrian) {
        updateCurrentAntrian(antrian);
        playCallSound();
        showNotification("Panggilan Antrian", 
            "No. " + antrian.get("nomor").getAsInt() + " silakan ke ruang " + 
            antrian.get("ruangan").getAsString());
    }
    
    private void updateCurrentAntrian(JsonObject antrian) {
        lblAntrianSekarang.setText("No. " + antrian.get("nomor").getAsInt());
        lblPasienSekarang.setText("Nama: " + antrian.get("namaPasien").getAsString());
        lblDokterSekarang.setText("Dokter: " + antrian.get("namaDokter").getAsString() + 
                                 " - " + antrian.get("ruangan").getAsString());
        
        // Highlight effect
        lblAntrianSekarang.setForeground(Color.RED);
        new Timer(1000, e -> {
            lblAntrianSekarang.setForeground(Color.BLACK);
            ((Timer)e.getSource()).stop();
        }).start();
    }
    
    private void updateAntrianList() {
        listModelAntrian.clear();
        
        antrianList.stream()
            .filter(a -> a.get("status").getAsString().equals("MENUNGGU"))
            .sorted((a1, a2) -> Integer.compare(
                a1.get("nomor").getAsInt(), 
                a2.get("nomor").getAsInt()))
            .forEach(antrian -> {
                String namaPasien = antrian.get("namaPasien").getAsString();
                String namaDokter = antrian.get("namaDokter").getAsString();
                String keluhan = antrian.has("keluhan") ? 
                    antrian.get("keluhan").getAsString() : "";
                
                String item = String.format("No. %03d | %-20s | %-15s | %s",
                    antrian.get("nomor").getAsInt(),
                    namaPasien.length() > 20 ? 
                        namaPasien.substring(0, 17) + "..." : 
                        namaPasien,
                    namaDokter,
                    keluhan.length() > 15 ?
                        keluhan.substring(0, 12) + "..." :
                        keluhan
                );
                listModelAntrian.addElement(item);
            });
    }
    
    private void updateDashboard(JsonObject dashboard) {
        // Update statistics if needed
        int totalMenunggu = dashboard.has("totalMenunggu") ? 
            dashboard.get("totalMenunggu").getAsInt() : 0;
        int totalDilayani = dashboard.has("totalDilayani") ? 
            dashboard.get("totalDilayani").getAsInt() : 0;
            
        appendLog("📊 Dashboard updated: " + 
                 totalMenunggu + " menunggu, " +
                 totalDilayani + " dilayani");
    }
    
    private void refreshData() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            try {
                JsonObject data = new JsonObject();
                data.addProperty("action", "GET_DASHBOARD");
                
                JsonObject message = new JsonObject();
                message.addProperty("type", "REQUEST_DATA");
                message.add("data", data);
                
                webSocketClient.send(message.toString());
                appendLog("🔄 Refresh request sent");
            } catch (Exception e) {
                appendLog("❌ Error sending refresh: " + e.getMessage());
            }
        } else {
            appendLog("⚠️ WebSocket not connected");
        }
    }
    
    private void panggilAntrianBerikutnya() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            try {
                JsonObject data = new JsonObject();
                data.addProperty("action", "PANGGIL_BERIKUTNYA");
                
                JsonObject message = new JsonObject();
                message.addProperty("type", "PANGGIL_ANTRIAN");
                message.add("data", data);
                
                webSocketClient.send(message.toString());
                appendLog("📢 Panggil antrian request sent");
            } catch (Exception e) {
                appendLog("❌ Error sending panggil: " + e.getMessage());
            }
        } else {
            appendLog("⚠️ WebSocket not connected");
        }
    }
    
    private void appendLog(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        SwingUtilities.invokeLater(() -> {
            txtLog.append("[" + timestamp + "] " + message + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }
    
    private void updateTime() {
        // Optional: Update time display
    }
    
    private void playNotificationSound() {
        Toolkit.getDefaultToolkit().beep();
    }
    
    private void playCallSound() {
        for (int i = 0; i < 3; i++) {
            Toolkit.getDefaultToolkit().beep();
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        }
    }
    
    private void showNotification(String title, String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE)
        );
    }
    
    @Override
    public void removeNotify() {
        if (webSocketClient != null) {
            try {
                webSocketClient.close();
            } catch (Exception e) {
                appendLog("⚠️ Error closing WebSocket: " + e.getMessage());
            }
        }
        super.removeNotify();
    }
}