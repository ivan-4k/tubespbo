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
    
    // ================= CONTROLLER =================
    private AntrianController controller;

    // ================= CONSTRUCTOR =================
    public AntrianKlinikFrame(AntrianController controller) {
        this.controller = controller;
        
        setTitle("Sistem Antrian Klinik");
        setSize(1350, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(initHeader(), BorderLayout.NORTH);
        add(initForm(), BorderLayout.WEST);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(initTables(), BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        loadDokter();
        startClock();
        loadAntrianAktif();
        loadAntrianSelesai();
    }

    // ================= HEADER =================
    private JPanel initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("SISTEM ANTRIAN KLINIK", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblWaktu = new JLabel("", JLabel.RIGHT);
        lblWaktu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        header.add(title, BorderLayout.CENTER);
        header.add(lblWaktu, BorderLayout.EAST);
        return header;
    }

    // ================= FORM =================
    private JPanel initForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder("Form Pasien"));
        form.setPreferredSize(new Dimension(360, 0));

        txtNama = new JTextField();
        txtUmur = new JTextField();
        txtAlamat = new JTextField();
        txtKeluhan = new JTextField();
        txtSpesialis = new JTextField();
        txtRuangan = new JTextField();

        txtSpesialis.setEditable(false);
        txtRuangan.setEditable(false);

        cbDokter = new JComboBox<>();
        cbDokter.addActionListener(e -> tampilkanDetailDokter());

        Dimension size = new Dimension(Integer.MAX_VALUE, 28);
        for (JComponent c : new JComponent[]{
                txtNama, txtUmur, txtAlamat, txtKeluhan,
                txtSpesialis, txtRuangan, cbDokter
        }) {
            c.setMaximumSize(size);
        }

        JButton btnAmbil = new JButton("Ambil Antrian");
        btnAmbil.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAmbil.addActionListener(e -> ambilAntrian());

        form.add(new JLabel("Nama Pasien"));
        form.add(txtNama);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Umur"));
        form.add(txtUmur);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Alamat Pasien"));
        form.add(txtAlamat);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Jenis Keluhan"));
        form.add(txtKeluhan);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Nama Dokter"));
        form.add(cbDokter);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Spesialis"));
        form.add(txtSpesialis);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Ruangan"));
        form.add(txtRuangan);
        form.add(Box.createVerticalStrut(15));

        form.add(btnAmbil);
        return form;
    }

    // ================= TABLE PANEL =================
    private JPanel initTables() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        // ===== ANTRIAN AKTIF =====
        modelAktif = new DefaultTableModel(new String[]{
                "No", "Nama", "Umur", "Alamat", "Keluhan",
                "Dokter", "Spesialis", "Ruangan", "Status"
        }, 0);

        tblAktif = new JTable(modelAktif);
        tblAktif.setRowHeight(26);
        setWarnaStatus(tblAktif, 8);

        JScrollPane spAktif = new JScrollPane(tblAktif);
        spAktif.setPreferredSize(new Dimension(1000, 250));
        spAktif.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        spAktif.setBorder(BorderFactory.createTitledBorder("Antrian Aktif"));

        JButton btnLayani = new JButton("Layani");
        JButton btnSelesai = new JButton("Selesai");
        btnLayani.addActionListener(e -> ubahStatus("DILAYANI"));
        btnSelesai.addActionListener(e -> ubahStatus("SELESAI"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.add(btnLayani);
        btnPanel.add(btnSelesai);

        panel.add(spAktif);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnPanel);
        panel.add(Box.createVerticalStrut(25));

        // ===== ANTRIAN SELESAI =====
        modelSelesai = new DefaultTableModel(new String[]{
                "No", "Nama", "Umur", "Alamat", "Keluhan", "Dokter", "Selesai"
        }, 0);

        tblSelesai = new JTable(modelSelesai);
        tblSelesai.setRowHeight(26);

        JScrollPane spSelesai = new JScrollPane(tblSelesai);
        spSelesai.setPreferredSize(new Dimension(1000, 230));
        spSelesai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        spSelesai.setBorder(BorderFactory.createTitledBorder("Antrian Selesai"));

        panel.add(spSelesai);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ================= WARNA STATUS =================
    private void setWarnaStatus(JTable table, int kolomStatus) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String status = table.getValueAt(row, kolomStatus).toString();
                if (status.equalsIgnoreCase("MENUNGGU"))
                    c.setBackground(new Color(255, 255, 200));
                else if (status.equalsIgnoreCase("DILAYANI"))
                    c.setBackground(new Color(200, 255, 200));
                else
                    c.setBackground(Color.WHITE);

                if (isSelected)
                    c.setBackground(new Color(184, 207, 229));

                return c;
            }
        });
    }

    // ================= DATA LOADING =================
    private void loadDokter() {
        cbDokter.removeAllItems();
        List<Map<String, Object>> dokterList = controller.getDaftarDokter();
        
        for (Map<String, Object> dokter : dokterList) {
            String item = dokter.get("id") + " | " +
                         dokter.get("nama") + " | " +
                         dokter.get("spesialis") + " | " +
                         dokter.get("ruangan");
            cbDokter.addItem(item);
        }
    }

    private void tampilkanDetailDokter() {
        if (cbDokter.getSelectedItem() == null) return;
        
        String selectedItem = cbDokter.getSelectedItem().toString();
        String[] parts = selectedItem.split("\\|");
        
        if (parts.length >= 1) {
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
                antrian.get("waktu_selesai")
            });
        }
    }

    // ================= LOGIC =================
    private void ambilAntrian() {
        // Validasi form
        if (txtNama.getText().isEmpty() || txtUmur.getText().isEmpty() ||
            txtAlamat.getText().isEmpty() || txtKeluhan.getText().isEmpty() ||
            cbDokter.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data!");
            return;
        }

        try {
            String nama = txtNama.getText();
            int umur = Integer.parseInt(txtUmur.getText());
            String alamat = txtAlamat.getText();
            String keluhan = txtKeluhan.getText();
            
            String selectedDokter = cbDokter.getSelectedItem().toString();
            int idDokter = Integer.parseInt(selectedDokter.split("\\|")[0].trim());

            // Gunakan controller untuk ambil antrian
            Map<String, Object> result = controller.ambilAntrian(nama, umur, alamat, keluhan, idDokter);
            
            if ((Boolean) result.get("success")) {
                JOptionPane.showMessageDialog(this,
                    "Antrian berhasil! Nomor: " + result.get("nomorAntrian"));
                
                // Reset form
                txtNama.setText("");
                txtUmur.setText("");
                txtAlamat.setText("");
                txtKeluhan.setText("");
                txtSpesialis.setText("");
                txtRuangan.setText("");
                
                // Refresh tables
                loadAntrianAktif();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal: " + result.get("message"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Umur harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Terjadi kesalahan: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void ubahStatus(String status) {
        int row = tblAktif.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih antrian terlebih dahulu!");
            return;
        }

        int nomorAntrian = (int) modelAktif.getValueAt(row, 0);
        
        boolean success = controller.updateStatusAntrian(nomorAntrian, status);
        if (success) {
            loadAntrianAktif();
            loadAntrianSelesai();
            
            String message = status.equals("DILAYANI") ? 
                "Antrian " + nomorAntrian + " sedang dilayani" :
                "Antrian " + nomorAntrian + " telah selesai";
            JOptionPane.showMessageDialog(this, message);
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate status antrian",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= CLOCK =================
    private void startClock() {
        new Timer(1000, e ->
                lblWaktu.setText(
                        new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())
                )
        ).start();
    }
    
    @Override
    public void dispose() {
        // Tutup koneksi database saat frame ditutup
        if (controller != null) {
            controller.closeConnection();
        }
        super.dispose();
    }
}