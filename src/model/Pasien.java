package model;

public class Pasien {

    private int idPasien;
    private String namaPasien;
    private String jenisKeluhan;
    private String alamat;
    private int umur;

    // ================= CONSTRUCTOR =================
    public Pasien(int idPasien, String namaPasien, String jenisKeluhan, String alamat, int umur) {
        this.idPasien = idPasien;
        this.namaPasien = namaPasien;
        this.jenisKeluhan = jenisKeluhan;
        this.alamat = alamat;
        this.umur = umur;
    }

    // ================= GETTER =================
    public int getIdPasien() {
        return idPasien;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public String getJenisKeluhan() {
        return jenisKeluhan;
    }

    public String getAlamat() {
        return alamat;
    }

    public int getUmur() {
        return umur;
    }

    // ================= SETTER (OPSIONAL) =================
    public void setNamaPasien(String namaPasien) {
        this.namaPasien = namaPasien;
    }

    public void setJenisKeluhan(String jenisKeluhan) {
        this.jenisKeluhan = jenisKeluhan;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }
}
