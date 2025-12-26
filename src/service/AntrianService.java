package service;

import model.Pasien;
import model.Dokter;
import model.Antrian;
import java.util.List;
import java.util.Queue;

public interface AntrianService {
    int tambahAntrian(Pasien pasien, Dokter dokter);
    
    Antrian panggilAntrian();
    
    Queue<Antrian> lihatAntrian();
    
    // Additional methods
    List<Dokter> getDaftarDokter();
    List<Antrian> getAntrianAktif();
    List<Antrian> getAntrianSelesai();
    boolean updateStatusAntrian(int nomorAntrian, String status);
    int getNomorAntrianBerikutnya();
    boolean selesaikanAntrian(int nomorAntrian);
}