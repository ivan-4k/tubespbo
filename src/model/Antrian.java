package model;

public class Antrian {
    private int nomorAntrian;
    private String status;

    public Antrian(int nomorAntrian, String status) {
        this.nomorAntrian = nomorAntrian;
        this.status = status;
    }

    public int getNomorAntrian() {
        return nomorAntrian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
