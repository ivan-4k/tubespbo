package service;

import model.Pasien;
import model.Dokter;
import model.Antrian;
import java.sql.Timestamp;
import java.util.*;

public class AntrianServiceDefault implements AntrianService {
    
    private Queue<Antrian> daftarAntrian;
    private int nomorOtomatis;
    private List<Dokter> daftarDokter;
    
    public AntrianServiceDefault() {
        this.daftarAntrian = new LinkedList<>();
        this.daftarDokter = new ArrayList<>();
        this.nomorOtomatis = 1;
        initializeDokter();
    }
    
    private void initializeDokter() {
        daftarDokter.add(new Dokter(1, "Dr. Andi Wijaya", "Umum", "101", "08:00-16:00"));
        daftarDokter.add(new Dokter(2, "Dr. Siti Rahayu", "Anak", "102", "09:00-17:00"));
        daftarDokter.add(new Dokter(3, "Dr. Bambang Sutrisno", "Bedah", "103", "10:00-18:00"));
    }
    
    @Override
    public int tambahAntrian(Pasien pasien, Dokter dokter) {
        int nomorAntrian = nomorOtomatis++;
        
        // Buat timestamp sekarang untuk waktu ambil
        Timestamp waktuAmbil = new Timestamp(System.currentTimeMillis());
        
        // Buat antrian dengan timestamp yang sesuai
        Antrian antrian = new Antrian(
            nomorAntrian, 
            pasien, 
            dokter, 
            "MENUNGGU", 
            waktuAmbil,  // waktu ambil = sekarang
            null,        // waktu mulai = null (belum mulai)
            null         // waktu selesai = null (belum selesai)
        );
        
        daftarAntrian.offer(antrian);
        return nomorAntrian;
    }
    
    @Override
    public Antrian panggilAntrian() {
        if (daftarAntrian.isEmpty()) {
            return null;
        }
        
        Antrian antrian = daftarAntrian.poll();
        
        if (antrian != null) {
            antrian.setStatus("DILAYANI");
            antrian.setWaktuMulai(new Timestamp(System.currentTimeMillis()));
            System.out.println("Memanggil antrian: " + antrian);
        }
        
        return antrian;
    }
    
    @Override
    public boolean selesaikanAntrian(int nomorAntrian) {
        // Cari antrian yang sedang dilayani
        for (Antrian antrian : daftarAntrian) {
            if (antrian.getNomorAntrian() == nomorAntrian && 
                "DILAYANI".equals(antrian.getStatus())) {
                antrian.setStatus("SELESAI");
                antrian.setWaktuSelesai(new Timestamp(System.currentTimeMillis()));
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Queue<Antrian> lihatAntrian() {
        return new LinkedList<>(daftarAntrian);
    }
    
    @Override
    public List<Dokter> getDaftarDokter() {
        return new ArrayList<>(daftarDokter);
    }
    
    @Override
    public List<Antrian> getAntrianAktif() {
        List<Antrian> aktif = new ArrayList<>();
        for (Antrian antrian : daftarAntrian) {
            if ("MENUNGGU".equals(antrian.getStatus()) || 
                "DILAYANI".equals(antrian.getStatus())) {
                aktif.add(antrian);
            }
        }
        return aktif;
    }
    
    @Override
    public List<Antrian> getAntrianSelesai() {
        List<Antrian> selesai = new ArrayList<>();
        for (Antrian antrian : daftarAntrian) {
            if ("SELESAI".equals(antrian.getStatus())) {
                selesai.add(antrian);
            }
        }
        return selesai;
    }
    
    @Override
    public boolean updateStatusAntrian(int nomorAntrian, String status) {
        for (Antrian antrian : daftarAntrian) {
            if (antrian.getNomorAntrian() == nomorAntrian) {
                antrian.setStatus(status);
                
                // Update timestamp sesuai status
                if ("DILAYANI".equals(status)) {
                    antrian.setWaktuMulai(new Timestamp(System.currentTimeMillis()));
                } else if ("SELESAI".equals(status)) {
                    antrian.setWaktuSelesai(new Timestamp(System.currentTimeMillis()));
                }
                
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getNomorAntrianBerikutnya() {
        return nomorOtomatis;
    }
    
    public int getJumlahAntrian() {
        return daftarAntrian.size();
    }
}