package model;

import java.sql.Timestamp;

public class Antrian {

    private int nomorAntrian;
    private Pasien pasien;
    private Dokter dokter;
    private String status;
    private Timestamp waktuAmbil;
    private Timestamp waktuMulai;
    private Timestamp waktuSelesai;

    // ================= CONSTRUCTOR =================
    public Antrian(int nomorAntrian, Pasien pasien, Dokter dokter,
                String status, Timestamp waktuAmbil,
                Timestamp waktuMulai, Timestamp waktuSelesai) {

        this.nomorAntrian = nomorAntrian;
        this.pasien = pasien;
        this.dokter = dokter;
        this.status = status;
        this.waktuAmbil = waktuAmbil;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    // ================= GETTER =================

    public int getNomorAntrian() {
        return nomorAntrian;
    }

    public Pasien getPasien() {
        return pasien;
    }

    public Dokter getDokter() {
        return dokter;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getWaktuAmbil() {
        return waktuAmbil;
    }

    public Timestamp getWaktuMulai() {
        return waktuMulai;
    }

    public Timestamp getWaktuSelesai() {
        return waktuSelesai;
    }

    // ================= SETTER =================

    public void setNomorAntrian(int nomorAntrian) {
        this.nomorAntrian = nomorAntrian;
    }

    public void setPasien(Pasien pasien) {
        this.pasien = pasien;
    }

    public void setDokter(Dokter dokter) {
        this.dokter = dokter;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWaktuMulai(Timestamp waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public void setWaktuSelesai(Timestamp waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    @Override
    public String toString() {
        return "Antrian #" + nomorAntrian + 
               " - " + pasien.getNamaPasien() + 
               " - " + dokter.getNamaDokter() + 
               " - Status: " + status;
    }
}
