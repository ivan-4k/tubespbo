package model;

public class Dokter {

    private int idDokter;
    private String namaDokter;
    private String spesialis;
    private String ruangan;

    public Dokter(int idDokter, String namaDokter, String spesialis, String ruangan) {
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
        this.spesialis = spesialis;
        this.ruangan = ruangan;
    }

    public int getIdDokter() {
        return idDokter;
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public String getSpesialis() {
        return spesialis;
    }

    public String getRuangan() {
        return ruangan;
    }

    // HANYA UNTUK TAMPILAN COMBOBOX
    @Override
    public String toString() {
        return namaDokter + " | " + spesialis + " | " + ruangan;
    }
}
