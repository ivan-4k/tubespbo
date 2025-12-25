package model;

public class Pasien {
    private int idPasien;
    private String nama;

    public Pasien(int idPasien, String nama) {
        this.idPasien = idPasien;
        this.nama = nama;
    }

    public int getIdPasien() {
        return idPasien;
    }

    public String getNama() {
        return nama;
    }
}
