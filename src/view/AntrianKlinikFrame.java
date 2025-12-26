package view;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.AntrianController;

public class AntrianKlinikFrame extends JFrame {

    // ================= COMPONENTS =================
    private JTextField txtNama, txtUmur, txtAlamat, txtKeluhan;
    private JTextField txtSpesialis, txtRuangan;
    private JComboBox<String> cbDokter;
    private JLabel lblWaktu;
    private JTable tblAktif, tblSelesai;
    private DefaultTableModel modelAktif, modelSelesai;
    private JTabbedPane tabbedPane;
    
    // ================= CONTROLLER =================
    private AntrianController controller;

    // ================= CONSTRUCTOR =================
    public AntrianKlinikFrame(AntrianController controller) {
        this.controller = controller;
        
        initializeFrame();
        setupUI();
        initializeData();
    }

    // ================= INITIALIZATION =================
    private void initializeFrame() {
        setTitle("Sistem Antrian Klinik");
        setSize(1350, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
    }

    private void setupUI() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Tab 1: Form & Table
        JPanel formTablePanel = createFormTablePanel();
        tabbedPane.addTab("📝 Form Antrian", formTablePanel);
        
        // Tab 3: Statistics Dashboard
        JPanel statsPanel = createStatisticsPanel();
        tabbedPane.addTab("📊 Dashboard", statsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initializeData() {
        loadDokter();
        startClock();
        loadAntrianAktif();
        loadAntrianSelesai();
    }

    // ================= PANEL CREATION =================
    private JPanel createFormTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(createHeaderPanel(), BorderLayout.NORTH);
        panel.add(createFormPanel(), BorderLayout.WEST);
        panel.add(createTablePanel(), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JLabel title = new JLabel("SISTEM ANTRIAN KLINIK", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));

        lblWaktu = new JLabel("", JLabel.RIGHT);
        lblWaktu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWaktu.setForeground(new Color(102, 102, 102));

        header.add(title, BorderLayout.CENTER);
        header.add(lblWaktu, BorderLayout.EAST);
        
        return header;
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Form Pasien"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        form.setPreferredSize(new Dimension(380, 0));
        form.setBackground(new Color(248, 249, 250));

        // Initialize text fields
        txtNama = createTextField();
        txtUmur = createTextField();
        txtAlamat = createTextField();
        txtKeluhan = createTextField();
        txtSpesialis = createTextField();
        txtRuangan = createTextField();

        txtSpesialis.setEditable(false);
        txtRuangan.setEditable(false);
        txtSpesialis.setBackground(new Color(240, 240, 240));
        txtRuangan.setBackground(new Color(240, 240, 240));

        cbDokter = new JComboBox<>();
        cbDokter.addActionListener(e -> tampilkanDetailDokter());

        // Setup constraints
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // Nama Pasien
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(createFormLabel("Nama Pasien"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtNama, gbc);
        
        row++;
        
        // Umur
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(createFormLabel("Umur"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtUmur, gbc);
        
        row++;
        
        // Alamat Pasien
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(createFormLabel("Alamat Pasien"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtAlamat, gbc);
        
        row++;
        
        // Jenis Keluhan
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(createFormLabel("Jenis Keluhan"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtKeluhan, gbc);
        
        row++;
        
        // Nama Dokter
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(createFormLabel("Nama Dokter"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(cbDokter, gbc);
        
        row++;
        
        // Spesialis
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(createFormLabel("Spesialis"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtSpesialis, gbc);
        
        row++;
        
        // Ruangan
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        form.add(createFormLabel("Ruangan"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        form.add(txtRuangan, gbc);
        
        row++;
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(new Color(248, 249, 250));
        
        JButton btnAmbil = createButton("Ambil Antrian", new Color(40, 167, 69));
        btnAmbil.addActionListener(e -> ambilAntrian());
        
        JButton btnClear = createButton("Clear Form", new Color(108, 117, 125));
        btnClear.addActionListener(e -> clearForm());
        
        buttonPanel.add(btnAmbil);
        buttonPanel.add(btnClear);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        form.add(buttonPanel, gbc);
        
        return form;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Active Queue Table
        JPanel activePanel = createTableSection("Antrian Aktif", createActiveTable());
        
        // Completed Queue Table
        JPanel completedPanel = createTableSection("Antrian Selesai", createCompletedTable());
        
        panel.add(activePanel, BorderLayout.CENTER);
        panel.add(completedPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTableSection(String title, JScrollPane tableScroll) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        
        // Control buttons for active queue only
        if (title.equals("Antrian Aktif")) {
            JPanel controlPanel = createControlPanel();
            panel.add(controlPanel, BorderLayout.NORTH);
        }
        
        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createActiveTable() {
        modelAktif = new DefaultTableModel(
            new String[]{"No", "Nama", "Umur", "Alamat", "Keluhan", "Dokter", "Spesialis", "Ruangan", "Status"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAktif = new JTable(modelAktif);
        customizeTable(tblAktif);
        setWarnaStatus(tblAktif, 8);

        JScrollPane scrollPane = new JScrollPane(tblAktif);
        scrollPane.setPreferredSize(new Dimension(900, 250));
        return scrollPane;
    }

    private JScrollPane createCompletedTable() {
        modelSelesai = new DefaultTableModel(
            new String[]{"No", "Nama", "Umur", "Alamat", "Keluhan", "Dokter", "Selesai"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSelesai = new JTable(modelSelesai);
        customizeTable(tblSelesai);

        JScrollPane scrollPane = new JScrollPane(tblSelesai);
        scrollPane.setPreferredSize(new Dimension(900, 200));
        return scrollPane;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(248, 249, 250));
        
        JButton btnLayani = createButton("Layani", new Color(0, 123, 255));
        JButton btnSelesai = createButton("Selesai", new Color(40, 167, 69));
        JButton btnRefresh = createButton("Refresh", new Color(108, 117, 125));
        
        btnLayani.addActionListener(e -> ubahStatus("DILAYANI"));
        btnSelesai.addActionListener(e -> ubahStatus("SELESAI"));
        btnRefresh.addActionListener(e -> refreshTables());
        
        panel.add(btnLayani);
        panel.add(btnSelesai);
        panel.add(btnRefresh);
        
        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Title
        JLabel title = new JLabel("📊 Dashboard Statistik", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 58, 64));
        panel.add(title, BorderLayout.NORTH);
        
        // Statistics cards
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        statsGrid.setBackground(Color.WHITE);
        
        // Create statistic cards
        statsGrid.add(createStatCard("Total Antrian", "0", new Color(0, 123, 255)));
        statsGrid.add(createStatCard("Menunggu", "0", new Color(255, 193, 7)));
        statsGrid.add(createStatCard("Dilayani", "0", new Color(40, 167, 69)));
        statsGrid.add(createStatCard("Selesai", "0", new Color(108, 117, 125)));
        
        panel.add(statsGrid, BorderLayout.CENTER);
        
        // Refresh button
        JButton btnRefreshStats = createButton("Refresh Statistik", new Color(0, 123, 255));
        btnRefreshStats.addActionListener(e -> updateStatistics());
        panel.add(btnRefreshStats, BorderLayout.SOUTH);
        
        return panel;
    }

    // ================= UI COMPONENT CREATORS =================
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return field;
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Title
        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(73, 80, 87));
        
        // Value
        JLabel lblValue = new JLabel(value, JLabel.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);
        
        // Icon
        JLabel icon = new JLabel(getIconForTitle(title), JLabel.CENTER);
        
        card.add(icon, BorderLayout.NORTH);
        card.add(lblTitle, BorderLayout.CENTER);
        card.add(lblValue, BorderLayout.SOUTH);
        
        return card;
    }

    private String getIconForTitle(String title) {
        switch (title) {
            case "Total Antrian": return "📋";
            case "Menunggu": return "⏳";
            case "Dilayani": return "👨‍⚕️";
            case "Selesai": return "✅";
            default: return "📊";
        }
    }

    private void customizeTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 58, 64));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(222, 226, 230));
    }

    // ================= TABLE RENDERER =================
    private void setWarnaStatus(JTable table, int kolomStatus) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = table.getValueAt(row, kolomStatus).toString();
                    switch (status.toUpperCase()) {
                        case "MENUNGGU":
                            c.setBackground(new Color(255, 243, 205));
                            break;
                        case "DILAYANI":
                            c.setBackground(new Color(212, 237, 218));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                }

                return c;
            }
        });
    }

    // ================= DATA LOADING =================
    private void loadDokter() {
        cbDokter.removeAllItems();
        cbDokter.addItem("-- Pilih Dokter --");
        
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Controller tidak tersedia", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Map<String, Object>> dokterList = controller.getDaftarDokter();
        
        if (dokterList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data dokter", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Map<String, Object> dokter : dokterList) {
                String item = String.format("%s | %s | %s | %s",
                    dokter.get("id"),
                    dokter.get("nama"),
                    dokter.get("spesialis"),
                    dokter.get("ruangan")
                );
                cbDokter.addItem(item);
            }
        }
    }

    private void tampilkanDetailDokter() {
        if (cbDokter.getSelectedItem() == null || 
            cbDokter.getSelectedItem().toString().equals("-- Pilih Dokter --")) {
            txtSpesialis.setText("");
            txtRuangan.setText("");
            return;
        }
        
        String selectedItem = cbDokter.getSelectedItem().toString();
        String[] parts = selectedItem.split("\\|");
        
        if (parts.length >= 4) {
            try {
                int idDokter = Integer.parseInt(parts[0].trim());
                Map<String, String> detail = controller.getDetailDokter(idDokter);
                
                txtSpesialis.setText(detail.getOrDefault("spesialis", ""));
                txtRuangan.setText(detail.getOrDefault("ruangan", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAntrianAktif() {
        if (modelAktif == null) return;
        
        modelAktif.setRowCount(0);
        List<Map<String, Object>> antrianList = controller.getAntrianAktif();
        
        for (Map<String, Object> antrian : antrianList) {
            modelAktif.addRow(new Object[]{
                antrian.get("nomor"),
                antrian.get("nama"),
                antrian.get("umur"),
                antrian.get("alamat"),
                antrian.get("keluhan"),
                antrian.get("dokter"),
                antrian.get("spesialis"),
                antrian.get("ruangan"),
                antrian.get("status")
            });
        }
    }

    private void loadAntrianSelesai() {
        if (modelSelesai == null) return;
        
        modelSelesai.setRowCount(0);
        List<Map<String, Object>> antrianList = controller.getAntrianSelesai();
        
        for (Map<String, Object> antrian : antrianList) {
            modelSelesai.addRow(new Object[]{
                antrian.get("nomor"),
                antrian.get("nama"),
                antrian.get("umur"),
                antrian.get("alamat"),
                antrian.get("keluhan"),
                antrian.get("dokter"),
                "Belum ada waktu"
            });
        }
    }

    // ================= BUSINESS LOGIC =================
    private void ambilAntrian() {
        // Validation
        if (txtNama.getText().trim().isEmpty()) {
            showError("Nama pasien tidak boleh kosong");
            txtNama.requestFocus();
            return;
        }
        
        if (txtUmur.getText().trim().isEmpty()) {
            showError("Umur tidak boleh kosong");
            txtUmur.requestFocus();
            return;
        }
        
        if (txtAlamat.getText().trim().isEmpty()) {
            showError("Alamat tidak boleh kosong");
            txtAlamat.requestFocus();
            return;
        }
        
        if (txtKeluhan.getText().trim().isEmpty()) {
            showError("Keluhan tidak boleh kosong");
            txtKeluhan.requestFocus();
            return;
        }
        
        if (cbDokter.getSelectedItem() == null || 
            cbDokter.getSelectedItem().toString().equals("-- Pilih Dokter --")) {
            showError("Pilih dokter terlebih dahulu");
            cbDokter.requestFocus();
            return;
        }

        try {
            String nama = txtNama.getText().trim();
            int umur = Integer.parseInt(txtUmur.getText().trim());
            String alamat = txtAlamat.getText().trim();
            String keluhan = txtKeluhan.getText().trim();
            
            String selectedDokter = cbDokter.getSelectedItem().toString();
            int idDokter = Integer.parseInt(selectedDokter.split("\\|")[0].trim());

            Map<String, Object> result = controller.ambilAntrian(nama, umur, alamat, keluhan, idDokter);
            
            if ((Boolean) result.get("success")) {
                showSuccess("Antrian berhasil!\nNomor Antrian: " + result.get("nomorAntrian"));
                clearForm();
                refreshTables();
            } else {
                showError("Gagal: " + result.get("message"));
            }
            
        } catch (NumberFormatException e) {
            showError("Umur harus berupa angka!");
            txtUmur.requestFocus();
        } catch (Exception e) {
            showError("Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ubahStatus(String status) {
        int row = tblAktif.getSelectedRow();
        if (row == -1) {
            showError("Pilih antrian terlebih dahulu!");
            return;
        }

        int nomorAntrian = (int) modelAktif.getValueAt(row, 0);
        
        boolean success = controller.updateStatusAntrian(nomorAntrian, status);
        if (success) {
            String message = status.equals("DILAYANI") ? 
                "Antrian " + nomorAntrian + " sedang dilayani" :
                "Antrian " + nomorAntrian + " telah selesai";
            showSuccess(message);
            refreshTables();
        } else {
            showError("Gagal mengupdate status antrian");
        }
    }

    private void clearForm() {
        txtNama.setText("");
        txtUmur.setText("");
        txtAlamat.setText("");
        txtKeluhan.setText("");
        txtSpesialis.setText("");
        txtRuangan.setText("");
        cbDokter.setSelectedIndex(0);
        txtNama.requestFocus();
    }

    private void refreshTables() {
        loadAntrianAktif();
        loadAntrianSelesai();
    }

    private void updateStatistics() {
        showInfo("Statistik akan diperbarui di versi berikutnya");
    }

    // ================= UTILITY METHODS =================
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= CLOCK =================
    private void startClock() {
        new Timer(1000, e ->
            lblWaktu.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()))
        ).start();
    }

    // ================= CLEANUP =================
    @Override
    public void dispose() {
        super.dispose();
    }
}