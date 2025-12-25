
CREATE DATABASE IF NOT EXISTS klinik;
USE klinik;

CREATE TABLE dokter (
    id_dokter INT AUTO_INCREMENT PRIMARY KEY,
    nama_dokter VARCHAR(100) NOT NULL,
    spesialis VARCHAR(100) NOT NULL,
    ruangan VARCHAR(20) NOT NULL
);

INSERT INTO dokter (nama_dokter, spesialis, ruangan) VALUES
('Dr. Andi', 'Umum', 'Ruang 101'),
('Dr. Sinta', 'Anak', 'Ruang 102');

CREATE TABLE pasien (
    id_pasien INT AUTO_INCREMENT PRIMARY KEY,
    nama_pasien VARCHAR(100) NOT NULL,
    alamat VARCHAR(150) NOT NULL,
    umur INT NOT NULL,
    jenis_keluhan VARCHAR(150) NOT NULL
);

CREATE TABLE antrian (
    id_antrian INT AUTO_INCREMENT PRIMARY KEY,
    nomor_antrian INT NOT NULL,
    id_pasien INT NOT NULL,
    id_dokter INT NOT NULL,
    status ENUM('MENUNGGU','DILAYANI','SELESAI') NOT NULL,
    waktu_ambil DATETIME,
    waktu_mulai DATETIME,
    waktu_selesai DATETIME,
    CONSTRAINT fk_pasien
        FOREIGN KEY (id_pasien) REFERENCES pasien(id_pasien)
        ON DELETE CASCADE,
    CONSTRAINT fk_dokter
        FOREIGN KEY (id_dokter) REFERENCES dokter(id_dokter)
        ON DELETE CASCADE
);
