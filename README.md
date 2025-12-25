# Sistem Antrian Klinik

Project Tugas Besar PBO (Kelompok 6)
Anggota:
1. Ivan Febriansyah Safari (2403077)
2. Suliswatun Hasanah (2403069)
3. Kusti Rahmawati (2403016)

## Deskripsi
Aplikasi Sistem Antrian Klinik berbasis Java yang digunakan untuk mengelola antrian pasien
dengan antarmuka grafis (Java Swing). Aplikasi ini menerapkan konsep arsitektur 2-tier,
di mana aplikasi langsung terhubung ke database MySQL menggunakan JDBC.

## Fitur
- Input data pasien melalui form
- Pengambilan nomor antrian otomatis
- Pengelompokan antrian berdasarkan status (Menunggu)
- Pemilihan dokter spesialis
- Tampilan tanggal dan waktu secara realtime
- Penyimpanan data ke database MySQL

## Struktur Folder
- `src/database` : koneksi database
- `src/view` : tampilan aplikasi (Java Swing)
- `lib` : library pendukung (.jar)
- `.vscode` : konfigurasi Visual Studio Code
- `database` : file SQL database

## Cara Menjalankan
1. Buka project di Visual Studio Code
2. Pastikan XAMPP (MySQL) sudah berjalan
3. Import database menggunakan file `database/antrian.sql`
4. Pastikan library JDBC (mysql-connector) sudah ada di folder `lib`
5. Jalankan file `MainApp.java`

## Teknologi
- Java
- Java Swing
- MySQL (XAMPP)
- JDBC
- Visual Studio Code
