package model;

import java.sql.Timestamp;

public class Antrian {

    private int idAntrian;
    private int nomorAntrian;
    private int idPasien;
    private int idDokter;
    private String status;
    private Timestamp waktuAmbil;
    private Timestamp waktuMulai;
    private Timestamp waktuSelesai;

    // ================= CONSTRUCTOR =================
    public Antrian(int idAntrian, int nomorAntrian, int idPasien, int idDokter,
                   String status, Timestamp waktuAmbil,
                   Timestamp waktuMulai, Timestamp waktuSelesai) {

        this.idAntrian = idAntrian;
        this.nomorAntrian = nomorAntrian;
        this.idPasien = idPasien;
        this.idDokter = idDokter;
        this.status = status;
        this.waktuAmbil = waktuAmbil;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    // ================= GETTER =================
    public int getIdAntrian() {
        return idAntrian;
    }

    public int getNomorAntrian() {
        return nomorAntrian;
    }

    public int getIdPasien() {
        return idPasien;
    }

    public int getIdDokter() {
        return idDokter;
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
    public void setStatus(String status) {
        this.status = status;
    }

    public void setWaktuMulai(Timestamp waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public void setWaktuSelesai(Timestamp waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }
}
