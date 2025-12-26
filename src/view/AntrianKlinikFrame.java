package view;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import database.DatabaseConnection;
import model.Dokter;


public class AntrianKlinikFrame extends JFrame {

    // ================= FORM =================
    private JTextField txtNama, txtUmur, txtAlamat, txtKeluhan;
    private JTextField txtSpesialis, txtRuangan;
    private JComboBox<Dokter> cbDokter;
    private JLabel lblWaktu;

    // ================= TABLE =================
    private JTable tblAktif, tblSelesai;
    private DefaultTableModel modelAktif, modelSelesai;

    // ================= CONSTRUCTOR =================
    public AntrianKlinikFrame() {
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

    // ================= DATA =================
   private void loadDokter() {
    cbDokter.removeAllItems();

    try (Connection c = DatabaseConnection.getConnection();
         Statement s = c.createStatement();
         ResultSet rs = s.executeQuery("SELECT * FROM dokter")) {

        while (rs.next()) {
            Dokter d = new Dokter(
                rs.getInt("id_dokter"),
                rs.getString("nama_dokter"),
                rs.getString("spesialis"),
                rs.getString("ruangan")
            );
            cbDokter.addItem(d);
        }

        System.out.println("Total dokter dimuat: " + cbDokter.getItemCount());

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private void tampilkanDetailDokter() {
    Dokter d = (Dokter) cbDokter.getSelectedItem();
    if (d == null) return;

    txtSpesialis.setText(d.getSpesialis());
    txtRuangan.setText(d.getRuangan());
}


    private void loadAntrianAktif() {
        modelAktif.setRowCount(0);
        try (Connection c = DatabaseConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery(
                    "SELECT a.nomor_antrian, p.nama_pasien, p.umur, p.alamat, p.jenis_keluhan, " +
                    "d.nama_dokter, d.spesialis, d.ruangan, a.status " +
                    "FROM antrian a " +
                    "JOIN pasien p ON a.id_pasien=p.id_pasien " +
                    "JOIN dokter d ON a.id_dokter=d.id_dokter " +
                    "WHERE a.status IN ('MENUNGGU','DILAYANI') " +
                    "ORDER BY CASE a.status WHEN 'MENUNGGU' THEN 1 ELSE 2 END, a.nomor_antrian"
            );
            while (rs.next()) {
                modelAktif.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), rs.getString(8), rs.getString(9)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAntrianSelesai() {
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
                        rs.getInt(1), rs.getString(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getTimestamp(7)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOGIC =================
    private void ambilAntrian() {
        if (txtNama.getText().isEmpty() || txtUmur.getText().isEmpty()
                || txtAlamat.getText().isEmpty() || txtKeluhan.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data!");
            return;
        }

        try (Connection c = DatabaseConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO pasien(nama_pasien,umur,alamat,jenis_keluhan) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, txtNama.getText());
            ps.setInt(2, Integer.parseInt(txtUmur.getText()));
            ps.setString(3, txtAlamat.getText());
            ps.setString(4, txtKeluhan.getText());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int idPasien = rs.getInt(1);

            ResultSet r = c.createStatement().executeQuery(
                    "SELECT IFNULL(MAX(nomor_antrian),0)+1 FROM antrian");
            r.next();

            Dokter d = (Dokter) cbDokter.getSelectedItem();
if (d == null) {
    JOptionPane.showMessageDialog(this, "Pilih dokter terlebih dahulu!");
    return;
}
int idDokter = d.getIdDokter();


            PreparedStatement ps2 = c.prepareStatement(
                    "INSERT INTO antrian(nomor_antrian,id_pasien,id_dokter,status,waktu_ambil) " +
                    "VALUES (?,?,?,?,NOW())");
            ps2.setInt(1, r.getInt(1));
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ubahStatus(String status) {
        int row = tblAktif.getSelectedRow();
        if (row == -1) return;

        int nomor = (int) modelAktif.getValueAt(row, 0);
        try (Connection c = DatabaseConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE antrian SET status=?, waktu_" +
                    (status.equals("DILAYANI") ? "mulai" : "selesai") +
                    "=NOW() WHERE nomor_antrian=?");
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
    private void startClock() {
        new Timer(1000, e ->
                lblWaktu.setText(
                        new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())
                )
        ).start();
    }
}