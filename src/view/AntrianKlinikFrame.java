package view;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AntrianKlinikFrame extends JFrame {
    void setWarnaStatus(JTable table, int kolomStatus) {
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String status = table.getValueAt(row, kolomStatus).toString();

            if (status.equalsIgnoreCase("DILAYANI")) {
                c.setBackground(new Color(200, 255, 200)); // hijau
            } else if (status.equalsIgnoreCase("MENUNGGU")) {
                c.setBackground(new Color(255, 255, 200)); // kuning
            } else {
                c.setBackground(Color.WHITE);
            }

            if (isSelected) {
                c.setBackground(new Color(184, 207, 229)); // default selection
            }

            return c;
        }
    });
}


    // ===== FORM =====
    JTextField txtNama, txtUmur, txtAlamat, txtKeluhan, txtSpesialis, txtRuangan;
    JComboBox<String> cbDokter;
    JLabel lblWaktu;

    // ===== TABLE =====
    JTable tblAktif, tblSelesai;
    DefaultTableModel modelAktif, modelSelesai;

    public AntrianKlinikFrame() {
        setTitle("Sistem Antrian Klinik");
        setSize(1350, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(initHeader(), BorderLayout.NORTH);
        add(initForm(), BorderLayout.WEST);
        add(initTables(), BorderLayout.CENTER);

        loadDokter();
        startClock();
        loadAntrianAktif();
        loadAntrianSelesai();
    }

    // ================= HEADER =================
    JPanel initHeader() {
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
    JPanel initForm() {
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
        txtNama.setMaximumSize(size);
        txtUmur.setMaximumSize(size);
        txtAlamat.setMaximumSize(size);
        txtKeluhan.setMaximumSize(size);
        txtSpesialis.setMaximumSize(size);
        txtRuangan.setMaximumSize(size);
        cbDokter.setMaximumSize(size);

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
    JPanel initTables() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        // ===== ANTRIAN AKTIF =====
        modelAktif = new DefaultTableModel(
            new String[]{
                "No", "Nama", "Umur", "Alamat", "Keluhan",
                "Dokter", "Spesialis", "Ruangan", "Status"
            }, 0
        );
        tblAktif = new JTable(modelAktif);
        setWarnaStatus(tblAktif, 8);
        JScrollPane spAktif = new JScrollPane(tblAktif);
        spAktif.setBorder(BorderFactory.createTitledBorder("Antrian Aktif"));

        JButton btnLayani = new JButton("Layani");
        JButton btnSelesai = new JButton("Selesai");

        btnLayani.addActionListener(e -> ubahStatus("DILAYANI"));
        btnSelesai.addActionListener(e -> ubahStatus("SELESAI"));

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnLayani);
        btnPanel.add(btnSelesai);

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(spAktif, BorderLayout.CENTER);
        atas.add(btnPanel, BorderLayout.SOUTH);

        // ===== ANTRIAN SELESAI =====
        modelSelesai = new DefaultTableModel(
            new String[]{
                "No", "Nama", "Umur", "Alamat",
                "Keluhan", "Dokter", "Selesai"
            }, 0
        );
        JScrollPane spSelesai = new JScrollPane(tblSelesai);
spSelesai.setVerticalScrollBarPolicy(
    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
);
spSelesai.setBorder(
    BorderFactory.createTitledBorder("Antrian Selesai")
);


        panel.add(atas);
        panel.add(spSelesai);

        return panel;
    }

    // ================= DATA =================
    void loadDokter() {
        cbDokter.removeAllItems();
        try (Connection c = DatabaseConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM dokter");
            while (rs.next()) {
                cbDokter.addItem(
                    rs.getInt("id_dokter") + " | " +
                    rs.getString("nama_dokter") + " | " +
                    rs.getString("spesialis") + " | " +
                    rs.getString("ruangan")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void tampilkanDetailDokter() {
        if (cbDokter.getSelectedItem() == null) return;
        String[] data = cbDokter.getSelectedItem().toString().split("\\|");
        txtSpesialis.setText(data[2].trim());
        txtRuangan.setText(data[3].trim());
    }

    void loadAntrianAktif() {
    modelAktif.setRowCount(0);
    try (Connection c = DatabaseConnection.getConnection()) {
        ResultSet rs = c.createStatement().executeQuery(
            "SELECT a.nomor_antrian, p.nama_pasien, p.umur, p.alamat, p.jenis_keluhan, " +
            "d.nama_dokter, d.spesialis, d.ruangan, a.status " +
            "FROM antrian a " +
            "JOIN pasien p ON a.id_pasien=p.id_pasien " +
            "JOIN dokter d ON a.id_dokter=d.id_dokter " +
            "WHERE a.status IN ('MENUNGGU','DILAYANI') " +
            "ORDER BY " +
            "CASE a.status " +
            "  WHEN 'MENUNGGU' THEN 1 " +
            "  WHEN 'DILAYANI' THEN 2 " +
            "END, a.nomor_antrian ASC"
        );

        while (rs.next()) {
            modelAktif.addRow(new Object[]{
                rs.getInt(1),      // No
                rs.getString(2),   // Nama
                rs.getInt(3),      // Umur
                rs.getString(4),   // Alamat
                rs.getString(5),   // Keluhan
                rs.getString(6),   // Dokter
                rs.getString(7),   // Spesialis
                rs.getString(8),   // Ruangan
                rs.getString(9)    // Status
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    void loadAntrianSelesai() {
        modelSelesai.setRowCount(0);
        try (Connection c = DatabaseConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery(
                "SELECT a.nomor_antrian, p.nama_pasien, p.umur, p.alamat, p.jenis_keluhan, " +
                "d.nama_dokter, a.waktu_selesai " +
                "FROM antrian a " +
                "JOIN pasien p ON a.id_pasien=p.id_pasien " +
                "JOIN dokter d ON a.id_dokter=d.id_dokter " +
                "WHERE a.status='SELESAI'"
            );
            while (rs.next()) {
                modelSelesai.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getTimestamp(7)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOGIC =================
    void ambilAntrian() {
        if (txtNama.getText().isEmpty() ||
            txtUmur.getText().isEmpty() ||
            txtAlamat.getText().isEmpty() ||
            txtKeluhan.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                "Lengkapi semua data pasien!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int umur;
        try {
            umur = Integer.parseInt(txtUmur.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Umur harus berupa angka!",
                "Validasi",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try (Connection c = DatabaseConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO pasien(nama_pasien, umur, alamat, jenis_keluhan) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, txtNama.getText());
            ps.setInt(2, umur);
            ps.setString(3, txtAlamat.getText());
            ps.setString(4, txtKeluhan.getText());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int idPasien = rs.getInt(1);

            ResultSet r = c.createStatement().executeQuery(
                "SELECT IFNULL(MAX(nomor_antrian),0)+1 FROM antrian"
            );
            r.next();
            int nomor = r.getInt(1);

            int idDokter = Integer.parseInt(
                cbDokter.getSelectedItem().toString().split("\\|")[0].trim()
            );

            PreparedStatement ps2 = c.prepareStatement(
                "INSERT INTO antrian(nomor_antrian,id_pasien,id_dokter,status,waktu_ambil) " +
                "VALUES (?,?,?,?,NOW())"
            );
            ps2.setInt(1, nomor);
            ps2.setInt(2, idPasien);
            ps2.setInt(3, idDokter);
            ps2.setString(4, "MENUNGGU");
            ps2.executeUpdate();

            loadAntrianAktif();

            txtNama.setText("");
            txtUmur.setText("");
            txtAlamat.setText("");
            txtKeluhan.setText("");
            txtSpesialis.setText("");
            txtRuangan.setText("");
            cbDokter.setSelectedIndex(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ubahStatus(String status) {
        int row = tblAktif.getSelectedRow();
        if (row == -1) return;

        int nomor = (int) modelAktif.getValueAt(row, 0);

        try (Connection c = DatabaseConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "UPDATE antrian SET status=?, waktu_" +
                (status.equals("DILAYANI") ? "mulai" : "selesai") +
                "=NOW() WHERE nomor_antrian=?"
            );
            ps.setString(1, status);
            ps.setInt(2, nomor);
            ps.executeUpdate();

            loadAntrianAktif();
            loadAntrianSelesai();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CLOCK =================
    void startClock() {
        new Timer(1000, e ->
            lblWaktu.setText(
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())
            )
        ).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AntrianKlinikFrame().setVisible(true));
    }
}
