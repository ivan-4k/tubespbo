package view;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AntrianKlinikFrame extends JFrame {

    JTextField txtNama, txtKeluhan;
    JComboBox<String> cbDokter;
    JLabel lblWaktu;

    JTable tblAktif, tblSelesai;
    DefaultTableModel modelAktif, modelSelesai;

    public AntrianKlinikFrame() {
        setTitle("Sistem Antrian Klinik");
        setSize(1200, 650);
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
        form.setPreferredSize(new Dimension(320, 0));

        txtNama = new JTextField();
        txtKeluhan = new JTextField();
        cbDokter = new JComboBox<>();

        txtNama.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtKeluhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton btnAmbil = new JButton("Ambil Antrian");
        btnAmbil.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAmbil.addActionListener(e -> ambilAntrian());

        form.add(new JLabel("Nama Pasien"));
        form.add(txtNama);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Jenis Keluhan"));
        form.add(txtKeluhan);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Dokter Spesialis"));
        form.add(cbDokter);
        form.add(Box.createVerticalStrut(20));

        form.add(btnAmbil);

        return form;
    }

    // ================= TABLE PANEL =================
    JPanel initTables() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        // TABEL AKTIF
        modelAktif = new DefaultTableModel(
            new String[]{"No", "Nama", "Dokter", "Status"}, 0
        );
        tblAktif = new JTable(modelAktif);
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

        // TABEL SELESAI
        modelSelesai = new DefaultTableModel(
            new String[]{"No", "Nama", "Dokter", "Selesai"}, 0
        );
        tblSelesai = new JTable(modelSelesai);
        JScrollPane spSelesai = new JScrollPane(tblSelesai);
        spSelesai.setBorder(BorderFactory.createTitledBorder("Antrian Selesai"));

        panel.add(atas);
        panel.add(spSelesai);

        return panel;
    }

    // ================= DATA =================
    void loadDokter() {
        try (Connection c = DatabaseConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM dokter");
            while (rs.next()) {
                cbDokter.addItem(
                    rs.getInt("id_dokter") + " | " + rs.getString("nama_dokter")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadAntrianAktif() {
        modelAktif.setRowCount(0);
        try (Connection c = DatabaseConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery(
                "SELECT a.id_antrian,a.nomor_antrian,p.nama_pasien,d.nama_dokter,a.status " +
                "FROM antrian a JOIN pasien p ON a.id_pasien=p.id_pasien " +
                "JOIN dokter d ON a.id_dokter=d.id_dokter " +
                "WHERE a.status IN ('MENUNGGU','DILAYANI')"
            );
            while (rs.next()) {
                modelAktif.addRow(new Object[]{
                    rs.getInt(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)
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
                "SELECT a.nomor_antrian,p.nama_pasien,d.nama_dokter,a.waktu_selesai " +
                "FROM antrian a JOIN pasien p ON a.id_pasien=p.id_pasien " +
                "JOIN dokter d ON a.id_dokter=d.id_dokter " +
                "WHERE a.status='SELESAI'"
            );
            while (rs.next()) {
                modelSelesai.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getTimestamp(4)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOGIC =================
    void ambilAntrian() {
        try (Connection c = DatabaseConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO pasien(nama_pasien,jenis_keluhan) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, txtNama.getText());
            ps.setString(2, txtKeluhan.getText());
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
            txtKeluhan.setText("");
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
