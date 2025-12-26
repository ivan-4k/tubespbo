package model;

public class Dokter {
    private int idDokter;
    private String namaDokter;
    private String spesialis;
    private String ruangan;

    // Constructors
    public Dokter() {}

    public Dokter(int idDokter, String namaDokter, String spesialis, String ruangan, String jamPraktek) {
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
        this.spesialis = spesialis;
        this.ruangan = ruangan;
    }

    // Getters dan Setters sesuai UML
    public int getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(int idDokter) {
        this.idDokter = idDokter;
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public void setNamaDokter(String namaDokter) {
        this.namaDokter = namaDokter;
    }

    public String getSpesialis() {
        return spesialis;
    }

    public void setSpesialis(String spesialis) {
        this.spesialis = spesialis;
    }

    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public String getNama() {
        return namaDokter;
    }

    @Override
    public String toString() {
        return namaDokter + " - " + spesialis + " (" + ruangan + ")";
    }
}