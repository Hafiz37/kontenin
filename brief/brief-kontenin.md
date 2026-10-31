# Brief Proyek: Kontenin

> **Pembaca**: pemilik proyek dan AI coding assistant.
> **Bahasa**: Indonesia; istilah teknis tetap Inggris.
> **Tanda `[ASUMSI]`**: keputusan default yang belum dikonfirmasi. Ubah jika tidak sesuai.
> **Prinsip utama**: aplikasi harus **sederhana dan tidak ribet**. Jika ragu antara dua cara, pilih yang lebih sederhana.

---

## 0. Instruksi untuk AI Coding Assistant

- Bangun aplikasi **native Android** di **Android Studio** dengan **Kotlin** dan **Jetpack Compose** `[ASUMSI]`.
- Aplikasi **sepenuhnya offline dan lokal**: database **Room** di perangkat. **Tanpa login, tanpa server, tanpa Firebase, tanpa izin `INTERNET`.**
- Ikuti struktur, skema data, dan daftar layar di brief ini. Jangan menambahkan fitur dari Bagian 10 (Batasan) kecuali diminta.
- **Jika ada yang membingungkan, jangan menebak dan jangan lanjut menulis kode untuk bagian itu.** Berhenti, tanyakan ke pemilik proyek, dan koordinasikan dulu. Berlaku untuk: instruksi ambigu atau bertentangan, informasi yang belum ada (versi library, nama paket), keputusan teknis dengan beberapa cara, dan perubahan yang menyimpang dari brief.
- Format pertanyaan:
  1. **Masalah**: apa yang membingungkan dan di bagian brief mana.
  2. **Pilihan jawaban**: 2 sampai 4 opsi berlabel A, B, C.
  3. **Plus dan minus** setiap opsi (waktu pengerjaan, kerumitan, keandalan notifikasi, kemudahan pemakaian).
  4. **Rekomendasi** beserta alasannya, sesuai prinsip aplikasi yang sederhana.
  5. Tunggu jawaban sebelum melanjutkan.
- Jika pertanyaannya kecil dan jawabannya jelas dari brief, tidak perlu bertanya.
- Hal yang hanya bisa dikerjakan pemilik proyek (uji di HP fisik, pengaturan baterai HP) ada di Bagian 12. Jangan berpura-pura sudah mengerjakannya.
- Kerjakan **satu tahap per permintaan** sesuai Bagian 14. Setiap tahap harus bisa di-build dan dijalankan sebelum lanjut.
- **Skema database harus final sebelum pemilik proyek memasukkan data asli** (lihat Bagian 7.2). Jika di tengah jalan terlihat skema perlu berubah, berhenti dan tanyakan.

---

## 1. Informasi Proyek

| Item | Isi |
|---|---|
| Nama aplikasi | **Kontenin** (untuk freelancer *Wedding Content Creator*, disingkat WCC) |
| Jenis proyek | Aplikasi pribadi |
| Pengguna | Satu orang: freelancer wedding content creator |
| Platform | Android native, `minSdk 26` `[ASUMSI]` |
| Bahasa aplikasi | Indonesia |
| Mata uang | Rupiah (Rp) |
| Zona waktu | Asia/Jakarta (WIB) |

## 2. Latar Belakang dan Masalah

Freelancer wedding content creator menerima job dari banyak sumber (vendor sendiri maupun vendor lain). Detail job biasanya tersebar di chat dan catatan, sehingga jadwal bisa terlewat atau bentrok, alamat dan lokasi sulit dicari, fee yang belum dibayar lupa ditagih, dan sulit mengetahui berapa penghasilan dalam periode tertentu.

**Rumusan masalah:**
1. Bagaimana menyimpan semua data job (pengantin, waktu, lokasi, sumber, fee, catatan) di satu tempat yang rapi?
2. Bagaimana mengingatkan pengguna sebelum hari acara tanpa perlu mengatur alarm satu per satu, dan memperingatkan jika dua job bentrok?
3. Bagaimana melihat rekap job dan fee per periode dan per sumber job, serta **fee yang masih harus ditagih dari semua periode**?
4. Bagaimana agar catatan penghasilan tidak hilang jika HP rusak atau hilang?

## 3. Solusi dan Tujuan

**Solusi:** Kontenin adalah aplikasi Android untuk mencatat **Job**. Setiap job otomatis mendapat empat pengingat (H-7, H-3, H-1, dan 3 jam sebelum acara). Aplikasi menyediakan pencatatan pembayaran (termasuk DP), rekap harian, mingguan, dan bulanan beserta rekap per sumber job, daftar piutang lintas periode, dan cadangan data berupa file.

**Tujuan:**
- Pengguna dapat menambah, melihat, mengubah, menduplikat, dan menghapus job.
- Pengguna menerima pengingat otomatis untuk setiap job tanpa pengaturan tambahan.
- Pengguna diperingatkan saat menyimpan job yang jadwalnya bentrok.
- Pengguna dapat mencatat jumlah yang sudah dibayar dan melihat sisa tagihan.
- Pengguna dapat melihat rekap job dan fee per periode dan per sumber job, serta semua fee yang belum lunas.
- Pengguna dapat mengekspor dan mengimpor seluruh data sebagai satu file.

## 4. Ruang Lingkup

Lima fitur utama: **(1) kelola job (autocomplete, duplikat, peringatan bentrok), (2) pengingat otomatis, (3) pembayaran, (4) rekap dan piutang, (5) cadangan data.**

---

## 5. Istilah

- **Job**: satu pekerjaan meliput satu acara pernikahan (satu baris data).
- **Acara**: jenis acara pada job, contoh: tedak sinten, resepsi, akad. Diisi manual.
- **Sumber job**: dari mana job didapat, contoh: "Pribadi" atau nama vendor lain. Diisi manual.
- **Pengingat**: notifikasi terjadwal untuk satu job. Setiap job punya empat pengingat otomatis.
- **Periode rekap**: Hari Ini, Minggu Ini, atau Bulan Ini.
- **Dibayar**: jumlah rupiah yang sudah diterima untuk satu job (termasuk DP).
- **Lunas**: `dibayar >= fee` (dihitung, tidak disimpan). **Sisa** = `fee - dibayar`.
- **Piutang**: job yang acaranya **sudah lewat** dan belum lunas `[ASUMSI]`.
- **Bentrok**: dua job yang rentang waktunya saling tumpang tindih.
- **Cadangan**: file JSON berisi seluruh data job, dibuat lewat fitur Ekspor.

## 6. Kebutuhan Fungsional

### FR-1 Job (tambah, lihat, ubah, duplikat, hapus)
- Field job lengkap ada di Bagian 7.
- **Beranda** menampilkan daftar job dengan dua tab: **Mendatang** (urut dari yang terdekat) dan **Riwayat** (urut dari yang terbaru). Ada satu **filter chip "Belum lunas"** yang berlaku pada tab yang sedang aktif.
- Job dianggap **Mendatang** jika waktu selesainya (jam mulai + durasi) belum lewat.
- **Detail job** menampilkan semua isi (termasuk catatan), jam ditampilkan sebagai rentang (contoh `10.00 - 14.00`, dihitung otomatis, tidak disimpan).
- Tombol **Buka Maps** di detail membuka link `shareloc` lewat `ACTION_VIEW`. Jika `shareloc` kosong, tombol disembunyikan. Jika tidak ada aplikasi yang bisa membuka, tampilkan pesan.
- Tombol cepat **Tandai Lunas** (mengisi `dibayar = fee`) dan **Ubah Pembayaran** (dialog kecil untuk mengisi `dibayar`) di detail.
- **Duplikat**: tombol di detail membuka Form Job dalam mode tambah, terisi dari job tersebut. Yang **disalin**: nama pengantin 1 dan 2, jam mulai, durasi, alamat, shareloc, sumber job, fee, catatan. Yang **dikosongkan**: tanggal (wajib dipilih ulang), acara, dan `dibayar` (kembali 0). Hasil duplikat adalah job baru; job asli tidak berubah `[ASUMSI]`.
- **Peringatan bentrok**: saat menekan Simpan (baru maupun ubah), aplikasi mengecek job lain yang bentrok. Jika ada, tampilkan dialog berisi daftar job yang bentrok (nama, acara, rentang jam) dengan tombol **Tetap Simpan** dan **Kembali**. Peringatan tidak memblokir penyimpanan.
  - Aturan bentrok: waktu diubah ke menit absolut (`tanggal * 1440 + jamMulaiMenit`), lalu dua job bentrok jika `mulaiA < selesaiB && mulaiB < selesaiA`. Job yang berurutan tanpa jeda (selesai A sama dengan mulai B) **tidak** dianggap bentrok.
  - Karena durasi bisa sampai 24 jam, ambil kandidat dari tanggal -1 sampai +1 hari. Saat mengubah job, job itu sendiri dikecualikan.
- Hapus dengan dialog konfirmasi. Pengingat job ikut dibatalkan.
- Tanggal masa lalu boleh diisi (untuk mencatat job yang sudah lewat).

### FR-2 Autocomplete (acara dan sumber job)
- Kolom **acara** dan **sumber job** tetap input teks bebas, tetapi menampilkan **saran** dari isian sebelumnya saat difokuskan atau diketik (maksimal 5 saran).
- Saran diambil dari data job yang sudah ada (tanpa tabel khusus), tanpa membedakan huruf besar kecil, diurutkan dari yang paling sering dipakai.
- Saat menyimpan: `trim` dan rapikan spasi ganda. Jika teks sama dengan isian lama tanpa memandang huruf besar kecil, **pakai ejaan isian lama** (agar "resepsi" dan "Resepsi" tidak dihitung sebagai dua sumber berbeda di rekap).
- Memilih saran mengisi kolom, pengguna tetap boleh mengedit.

### FR-3 Pembayaran
- Satu field **`dibayar`** (rupiah) per job, default 0. Lunas dan sisa **dihitung** dari `fee` dan `dibayar`, tidak disimpan terpisah.
- Di Form Job: kolom **"Sudah dibayar"** (opsional, default 0) dan tombol kecil **"Lunas"** yang mengisi kolom itu dengan nilai fee.
- Di Detail Job: tombol **Tandai Lunas** (tampil jika belum lunas) dan **Ubah Pembayaran** (dialog satu kolom, validasi sama dengan Form).
- Kartu job menampilkan chip (warna Warning) sesuai status:
  - `dibayar = 0` (dan `fee > 0`): chip **"Belum lunas"**.
  - `0 < dibayar < fee`: chip **"Kurang Rp 500.000"** (nilai sisa).
  - Lunas: tanpa chip.
- Rekap menampilkan total fee, total sudah dibayar, dan total belum dibayar (sisa).

### FR-4 Pengingat otomatis
- Setiap job yang disimpan otomatis mendapat **4 pengingat**: H-7, H-3, H-1, dan 3 jam sebelum jam mulai. Tidak ada pengaturan per job.
- Rincian di Bagian 9.

### FR-5 Rekap dan Piutang
- Pilihan periode: **Hari Ini**, **Minggu Ini**, **Bulan Ini**.
- Isi per periode: jumlah job, total fee, total sudah dibayar, total belum dibayar (sisa), daftar job pada periode itu, dan bagian **Per Sumber Job** (jumlah job, total fee, dan total sisa tiap sumber).
- Rekap dihitung berdasarkan **tanggal acara** job, bukan tanggal pembayaran.
- Minggu dimulai hari **Senin** `[ASUMSI]`.
- Mengetuk job pada daftar rekap membuka detail job.
- Bagian **Piutang** (tidak terpengaruh pilihan periode): menampilkan total sisa dan jumlah job piutang, lalu daftar job piutang diurutkan dari **tanggal acara terlama** agar yang paling lama menunggu muncul paling atas. Mengetuk job membuka detail. Definisi piutang ada di Bagian 5.

### FR-6 Pengaturan
- Status izin notifikasi dan izin alarm tepat waktu, beserta tombol menuju pengaturannya jika belum aktif.
- Petunjuk baterai dan autostart (Bagian 9.5).
- Tombol **Uji Pengingat** (ada di semua build, bukan hanya debug): menjadwalkan satu notifikasi uji beberapa detik ke depan lewat jalur alarm yang sama dengan pengingat asli.
- Tombol **Ekspor Data** dan **Impor Data** (FR-7).
- Versi aplikasi.

### FR-7 Cadangan data (Ekspor dan Impor)
- **Ekspor**: membuat satu file JSON berisi seluruh job lewat Storage Access Framework (`ACTION_CREATE_DOCUMENT`, **tanpa izin tambahan**). Nama file saran: `kontenin-backup-YYYYMMDD.json`. Pengguna memilih lokasi simpan (HP, Google Drive, dan sebagainya).
- **Impor**: memilih file lewat `ACTION_OPEN_DOCUMENT`. Alurnya:
  1. Baca dan validasi file **seluruhnya** sebelum mengubah apa pun. Jika ada yang tidak valid, batalkan semua dan tampilkan pesan; data lama tidak berubah.
  2. Tampilkan dialog konfirmasi: "Semua data saat ini akan diganti dengan isi file (N job)."
  3. Batalkan semua alarm job lama, ganti seluruh isi tabel `jobs` dalam **satu transaksi Room**, lalu panggil `rescheduleAll()`.
- Impor bersifat **mengganti seluruh data**, bukan menggabungkan `[ASUMSI]`.
- Format file dan aturan validasi ada di Bagian 7.3.
- Sebagai lapisan tambahan, biarkan **Auto Backup Android** aktif (`android:allowBackup="true"`, bawaan). Ini bukan pengganti Ekspor, hanya bonus gratis. Setelah data dipulihkan oleh sistem, `rescheduleAll()` saat aplikasi dibuka menjadwalkan ulang pengingat.

---

## 7. Data

### 7.1 Tabel Room `jobs`

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | Long | Primary key, auto generate |
| `namaPengantin1` | String | Wajib |
| `namaPengantin2` | String | Wajib |
| `tanggal` | Long | Tanggal acara, disimpan sebagai `LocalDate.toEpochDay()` |
| `jamMulaiMenit` | Int | Jam mulai sebagai menit sejak 00.00 (0 sampai 1439) |
| `durasiMenit` | Int | Lama job dalam menit (kelipatan 30; input pengguna dalam jam, mis. 2,5) |
| `acara` | String | Input manual + autocomplete |
| `alamat` | String | Opsional, boleh kosong |
| `shareloc` | String | Opsional, link Google Maps |
| `sumberJob` | String | Input manual + autocomplete |
| `fee` | Long | Rupiah, bilangan bulat, minimal 0 |
| `dibayar` | Long | Rupiah yang sudah diterima, default 0, `0 <= dibayar <= fee` |
| `catatan` | String | Opsional, default string kosong. Rundown, permintaan klien, kontak PIC, dan sebagainya |
| `createdAt` | Long | Epoch millis |
| `updatedAt` | Long | Epoch millis |

- Buat **index** pada `tanggal`.
- Jam selesai, status lunas, dan sisa **tidak disimpan**; dihitung dari field di atas.
- Waktu mulai acara = `tanggal` + `jamMulaiMenit` pada zona `Asia/Jakarta`.
- Tidak perlu tabel terpisah untuk acara maupun sumber job.

### 7.2 Aturan skema dan migrasi

- Versi database awal: **1**. Set `exportSchema = true` dan simpan file skema di repository (Git).
- **Dilarang** memakai `fallbackToDestructiveMigration`. Perubahan skema setelah ada data asli harus lewat `Migration` yang ditulis eksplisit.
- Pemilik proyek **tidak memasukkan data asli** sebelum Tahap 7 selesai atau sebelum skema dinyatakan final.
- Setiap perubahan skema setelah itu: AI wajib bertanya dulu (Bagian 0), menulis `Migration`, dan memperbarui format cadangan (Bagian 7.3).

### 7.3 Format file cadangan (JSON)

```json
{
  "app": "kontenin",
  "schemaVersion": 1,
  "exportedAt": 1760000000000,
  "jobs": [
    {
      "id": 1,
      "namaPengantin1": "Andi",
      "namaPengantin2": "Sari",
      "tanggal": 20379,
      "jamMulaiMenit": 600,
      "durasiMenit": 240,
      "acara": "Resepsi",
      "alamat": "Gedung X",
      "shareloc": "https://maps.app.goo.gl/...",
      "sumberJob": "Pribadi",
      "fee": 1500000,
      "dibayar": 500000,
      "catatan": "",
      "createdAt": 1760000000000,
      "updatedAt": 1760000000000
    }
  ]
}
```

- `schemaVersion` mengikuti versi skema data. Impor **menolak** file dengan `app` yang salah atau `schemaVersion` lebih baru dari yang dikenal aplikasi.
- Validasi saat impor: semua field wajib ada dengan tipe benar, dan aturan Bagian 8 terpenuhi (termasuk `0 <= dibayar <= fee`). Satu job tidak valid berarti seluruh impor ditolak (semua atau tidak sama sekali).
- `id` dipertahankan dari file.
- Pakai `org.json` bawaan Android `[ASUMSI]`, tanpa library tambahan di aplikasi. Catatan: `org.json` di unit test JVM tidak berfungsi tanpa dependensi test tambahan. AI harus menanyakan pilihannya (Bagian 0): A) `org.json` + `testImplementation` org.json, B) `kotlinx.serialization`.

---

## 8. Aturan Validasi

| Field | Aturan |
|---|---|
| Nama pengantin 1 dan 2 | Wajib, 1 sampai 50 karakter setelah `trim` |
| Tanggal | Wajib (boleh masa lalu) |
| Jam mulai | Wajib |
| Durasi | Wajib, 0,5 sampai 24 jam, kelipatan 0,5. Terima koma dan titik sebagai pemisah desimal. Nilai awal form: 4 jam `[ASUMSI]` |
| Acara | Wajib, 1 sampai 50 karakter |
| Sumber job | Wajib, 1 sampai 50 karakter |
| Alamat | Opsional, maksimal 300 karakter |
| Shareloc | Opsional; jika diisi harus diawali `http://` atau `https://` |
| Fee | Wajib, angka bulat 0 atau lebih. Ditampilkan dengan pemisah ribuan saat mengetik |
| Dibayar | Opsional (default 0), angka bulat, `0 <= dibayar <= fee`, pemisah ribuan saat mengetik. Jika fee diturunkan di bawah dibayar, tampilkan error pada kolom dibayar |
| Catatan | Opsional, maksimal 1000 karakter |

Pesan error ditampilkan di bawah kolom yang salah, dalam bahasa Indonesia.

---

## 9. Aturan Pengingat (Rinci)

### 9.1 Jadwal pengingat

Waktu acara = `tanggal` + `jam mulai` (WIB).

| Pengingat | Waktu bunyi | Contoh judul |
|---|---|---|
| H-7 | 7 hari sebelum tanggal acara, pukul **08.00** | `H-7: Resepsi Andi & Sari` |
| H-3 | 3 hari sebelum tanggal acara, pukul **08.00** | `H-3: Resepsi Andi & Sari` |
| H-1 | 1 hari sebelum tanggal acara, pukul **08.00** | `H-1: Resepsi Andi & Sari` |
| 3 jam | Tepat 3 jam sebelum jam mulai | `3 jam lagi: Resepsi Andi & Sari` |

- Jam 08.00 untuk H-7, H-3, H-1 didefinisikan sebagai **satu konstanta** di kode `[ASUMSI]` agar mudah diubah.
- **Isi notifikasi**: tanggal dan rentang jam, lalu alamat. Contoh: `Besok, Sab 18 Okt 2026, 10.00 - 14.00. Gedung X`. Jika alamat kosong, bagian alamat dilewati.
- Pengingat yang **waktunya sudah lewat saat job disimpan dilewati**, tidak dibunyikan langsung. Contoh: job dibuat untuk besok, maka H-7 dan H-3 dilewati.
- Tidak ada pengingat untuk job yang acaranya sudah lewat.

### 9.2 Mekanisme

- Default: **`AlarmManager.setExactAndAllowWhileIdle`** dengan `PendingIntent` ke sebuah `BroadcastReceiver`. Alasan: pengingat harus tepat waktu dan tidak boleh tertunda mode hemat baterai.
- `requestCode` unik per pengingat: `id job * 4 + indeks` (0 = H-7, 1 = H-3, 2 = H-1, 3 = 3 jam). Gunakan `FLAG_IMMUTABLE`.
- Untuk membatalkan alarm, `PendingIntent` harus **identik** dengan saat dijadwalkan (komponen, action, dan `requestCode` sama). Buat satu fungsi pembuat `PendingIntent` yang dipakai bersama oleh jadwal dan pembatalan.
- Pada saat alarm berbunyi, receiver membaca job dari Room berdasarkan `jobId` lalu menampilkan notifikasi dengan data **terbaru**. Jika job sudah tidak ada, tidak menampilkan apa pun. Gunakan `goAsync()` atau worker singkat untuk akses database.
- Bungkus semua logika alarm dalam satu kelas, misalnya `ReminderScheduler`, dengan fungsi `schedule(job)`, `cancel(jobId)`, dan `rescheduleAll()`.

**Keputusan terbuka (dikonfirmasi di awal Tahap 4, berdasarkan hasil uji awal Tahap 1):**

| Opsi | Mekanisme | Plus | Minus |
|---|---|---|---|
| A (default `[ASUMSI]`) | `setExactAndAllowWhileIdle` + izin `SCHEDULE_EXACT_ALARM` + cadangan `setAndAllowWhileIdle` | Presisi, tidak ada ikon tambahan | Di Android 14+ izin biasanya **tidak diberikan otomatis**, pemilik harus mengaktifkannya manual sekali |
| B | `setAlarmClock` | Tanpa izin, kebal Doze, paling andal | Ikon alarm selalu tampil di status bar selama ada pengingat tertunda |
| C | `setAndAllowWhileIdle` saja | Tanpa izin, paling sederhana | Waktu tidak presisi (bisa bergeser beberapa menit) |

Pengingat 08.00 dan "3 jam lagi" tidak membutuhkan presisi detik, jadi opsi C pun bisa diterima. Jika uji awal di HP pemilik berjalan baik dengan A, tetap pakai A.

| Peristiwa | Tindakan |
|---|---|
| Job disimpan (baru atau hasil duplikat) | Jadwalkan 4 pengingat (yang masih di masa depan) |
| Job diubah | Batalkan 4 pengingat lama, jadwalkan ulang |
| Job dihapus | Batalkan 4 pengingat |
| Impor data | Batalkan alarm semua job lama sebelum penggantian, lalu `rescheduleAll()` |
| HP restart (`BOOT_COMPLETED`) | `rescheduleAll()` |
| Aplikasi diperbarui (`MY_PACKAGE_REPLACED`) | `rescheduleAll()` |
| Aplikasi dibuka | `rescheduleAll()` (aman dijalankan berulang) |

`rescheduleAll()` menjadwalkan ulang semua job yang acaranya belum lewat. Status pembayaran tidak memengaruhi pengingat; pengingat tetap berjalan untuk job yang sudah lunas.

### 9.3 Notifikasi

- **Channel**: id `pengingat_job`, nama "Pengingat Job", importance `HIGH`.
- ID notifikasi = `requestCode` pengingat (agar tidak menumpuk duplikat).
- `PendingIntent` dengan `FLAG_IMMUTABLE` yang membuka layar **Detail Job** (kirim `jobId`).
- Jika job sudah dihapus saat notifikasi ditekan, tampilkan "Job tidak ditemukan" lalu kembali ke Beranda.

### 9.4 Izin

| Izin | Keterangan |
|---|---|
| `POST_NOTIFICATIONS` | Android 13+. Diminta saat pertama kali pengguna menyimpan job, dengan penjelasan singkat. |
| `SCHEDULE_EXACT_ALARM` | Android 12+. Cek `canScheduleExactAlarms()`. |
| `RECEIVE_BOOT_COMPLETED` | Untuk penjadwalan ulang setelah restart. |

- Jika izin alarm tepat waktu **belum diberikan**: gunakan `setAndAllowWhileIdle` sebagai cadangan (waktu kurang presisi), dan tampilkan banner di Pengaturan dengan tombol menuju layar izinnya. Setelah izin diberikan dan aplikasi dibuka, `rescheduleAll()` menjadwalkan ulang.
- Jika izin notifikasi ditolak: job tetap bisa disimpan; banner di Pengaturan menjelaskan dan menyediakan tombol menuju pengaturan notifikasi aplikasi.
- Jangan mendeklarasikan `USE_EXACT_ALARM` `[ASUMSI]` (dibatasi kebijakan Play Store; aplikasi ini untuk pemakaian pribadi).
- Jika opsi B atau C dipilih di Bagian 9.2, sesuaikan daftar izin dan tanyakan dulu sebelum mengubah manifest.

### 9.5 Catatan keandalan

Beberapa merk HP (Xiaomi, Oppo, Vivo, Realme, dan sejenisnya) mematikan aplikasi di latar belakang dan bisa membatalkan alarm. Pemilik proyek perlu mengatur aplikasi ke **"Tanpa batasan" baterai** dan mengaktifkan **Autostart** (lihat Bagian 12). Aplikasi menampilkan petunjuk singkat ini di Pengaturan, dan tombol **Uji Pengingat** (FR-6) memungkinkan pemilik memverifikasi kapan saja.

---

## 10. Batasan Versi Ini

- Data hanya ada di HP. Cadangan dilakukan **manual** lewat Ekspor/Impor file (plus Auto Backup Google jika aktif di HP). Tidak ada cloud, sinkronisasi otomatis, atau cadangan terjadwal.
- Impor selalu **mengganti** seluruh data; tidak ada penggabungan.
- Tidak ada login atau akun.
- Satu pengguna, satu perangkat.
- Jadwal pengingat tetap (H-7, H-3, H-1, 3 jam); tidak bisa diubah per job.
- Tidak ada kalender bulanan, widget, atau ekspor PDF.
- Deteksi bentrok hanya berupa **peringatan saat menyimpan**; tidak memblokir dan tidak ada tampilan khusus bentrok.
- Tidak ada foto atau lampiran pada job (hanya teks catatan).
- Pembayaran hanya berupa **total yang sudah dibayar** per job; tidak ada riwayat per transaksi (tanggal tiap cicilan) dan tidak ada pengingat menagih.
- Rekap per periode hanya untuk periode berjalan (hari ini, minggu ini, bulan ini), tanpa memilih periode lain. Bagian Piutang mencakup semua tanggal.
- Tidak ada fitur berbagi atau mengirim jadwal ke orang lain.

## 11. Pengembangan Lanjutan

| Prioritas | Pengembangan |
|---|---|
| Tinggi | Rekap dengan pemilihan bulan lain (riwayat) |
| Sedang | Pengingat menagih fee yang belum lunas |
| Sedang | Widget layar utama berisi job terdekat |
| Sedang | Tampilan kalender bulanan |
| Sedang | Ekspor rekap bulanan ke PDF atau CSV |
| Sedang | Riwayat pembayaran per transaksi (tanggal tiap cicilan) |
| Rendah | Jam pengingat harian (08.00) dapat diatur |
| Rendah | Pengingat berkala untuk melakukan ekspor cadangan |
| Rendah | Cadangan otomatis ke Google Drive |

---

## 12. Setup dan Pengujian (dikerjakan pemilik proyek, bukan AI)

### 12.1 Persiapan
1. Install **Android Studio** versi stabil terbaru.
2. Siapkan **HP Android fisik** dengan USB debugging aktif. Pengujian pengingat **harus** di HP fisik, bukan hanya emulator.
3. Tentukan **package name** (contoh `com.contoh.kontenin`) dan beritahu AI.
4. Tidak ada setup Firebase atau akun apa pun.

### 12.2 Pengaturan HP agar pengingat andal
- Pengaturan HP, Aplikasi, Kontenin, Baterai: pilih **Tanpa batasan** (atau "Jangan optimalkan").
- Aktifkan **Autostart** / "Mulai otomatis" (khusus merk tertentu).
- Izinkan **Notifikasi**.
- Izinkan **Alarm dan pengingat** (Android 12+). Di Android 14+ ini hampir pasti perlu diaktifkan manual.

### 12.3 Uji awal setelah Tahap 1 (uji alarm)
Tujuannya mengetahui sejak awal apakah HP Anda andal untuk alarm.
- [ ] Tekan tombol uji alarm (jadwal 1 menit ke depan), **matikan layar**, tunggu. Notifikasi harus muncul.
- [ ] Ulangi dengan aplikasi ditutup dari recent apps.
- [ ] Ulangi setelah izin alarm tepat waktu dimatikan, pastikan tidak crash dan cadangan tetap berbunyi (mungkin telat).
- [ ] Catat hasilnya. Jika notifikasi sering tidak muncul, bawa hasil ini ke keputusan terbuka di Bagian 9.2.

### 12.4 Uji setelah Tahap 4 (pengingat)
- [ ] Buat job dengan acara 10 menit ke depan lalu pastikan notifikasi "3 jam" yang dilewati tidak muncul dan tidak ada crash.
- [ ] Gunakan tombol **Uji Pengingat** di Pengaturan dan pastikan notifikasi muncul saat layar mati.
- [ ] Restart HP, lalu pastikan pengingat tetap terjadwal (cek dengan tombol uji atau job dekat).
- [ ] Ubah jam job lalu pastikan pengingat lama tidak berbunyi.
- [ ] Hapus job lalu pastikan notifikasi tidak muncul.

### 12.5 Uji setelah Tahap 6 (cadangan)
- [ ] Ekspor data, lalu pindahkan file ke tempat lain (mis. Google Drive).
- [ ] Hapus aplikasi (atau pakai HP lain), pasang ulang, lalu impor file. Semua job harus kembali, dan pengingat job mendatang aktif lagi.
- [ ] Coba impor file yang rusak atau bukan cadangan Kontenin. Aplikasi harus menolak dengan pesan, dan data lama tetap utuh.

### 12.6 Kebiasaan pemakaian
- Jangan mulai memasukkan data asli sebelum Tahap 7 selesai dan skema final (Bagian 7.2).
- Setelah mulai dipakai: **ekspor cadangan sebelum memperbarui aplikasi** dan setelah selesai memasukkan banyak job. Simpan file cadangan di luar HP.

---

## 13. Layar, Desain, dan Kondisi Khusus

### 13.1 Daftar layar

| No | Layar | Isi utama |
|---|---|---|
| 1 | Beranda | Tab Mendatang dan Riwayat, filter chip "Belum lunas", kartu job, tombol tambah (`FloatingActionButton`) |
| 2 | Form Job | Semua field Bagian 7 (termasuk dibayar dan catatan), autocomplete acara dan sumber job, tombol "Lunas", tombol Simpan (dipakai untuk tambah, ubah, dan duplikat), dialog peringatan bentrok |
| 3 | Detail Job | Semua isi, tombol Buka Maps, Tandai Lunas, Ubah Pembayaran, Duplikat, ubah, hapus |
| 4 | Rekap | Pilihan Hari Ini / Minggu Ini / Bulan Ini, ringkasan, daftar job, Per Sumber Job, Piutang |
| 5 | Pengaturan | Status izin notifikasi dan alarm, petunjuk baterai, Uji Pengingat, Ekspor Data, Impor Data, versi |

Bottom navigation: **Beranda, Rekap, Pengaturan**.

### 13.2 Desain `[ASUMSI]`
- **Material 3** bawaan Compose, tanpa komponen custom yang tidak perlu. Light dan dark mode mengikuti sistem.
- Warna: Primary `#9C4A6B`, Secondary `#C9A24B`, Background `#FAF7F5`, Surface `#FFFFFF`, Error `#D93025`, Warning `#F59E0B`, Success `#16A34A`. Didefinisikan hanya di `ui/theme`.
- Jarak kelipatan 4 dp (padding layar 16 dp), sudut membulat 12 dp, area sentuh minimal 48 dp.
- Kartu job: nama pengantin ("Andi & Sari"), acara, tanggal dan rentang jam, sumber job, fee, dan chip pembayaran (warna Warning): **"Belum lunas"** atau **"Kurang Rp X"**; tanpa chip jika lunas.
- Tanggal `dd MMM yyyy` (contoh `18 Okt 2026`), jam `HH.mm`, rupiah `Rp 1.500.000` (locale `id-ID`).
- Semua teks di `strings.xml`, singkat dan ramah.

### 13.3 Kondisi kosong dan error

| Situasi | Perilaku |
|---|---|
| Daftar Mendatang kosong | Ikon dan teks "Belum ada job mendatang", tombol tambah |
| Riwayat kosong | "Belum ada riwayat job" |
| Filter "Belum lunas" tanpa hasil | "Semua job sudah lunas" |
| Rekap tanpa job | "Belum ada job pada periode ini" dan semua total tampil Rp 0 |
| Piutang kosong | "Tidak ada fee yang perlu ditagih" dan total Rp 0 |
| Shareloc kosong | Tombol Buka Maps disembunyikan |
| Tidak ada aplikasi pembuka link | Pesan "Tidak ada aplikasi untuk membuka link ini" |
| Input tidak valid | Pesan di bawah kolom, tombol Simpan tidak menyimpan |
| Jadwal bentrok saat Simpan | Dialog peringatan berisi job yang bentrok; pilihan Tetap Simpan atau Kembali |
| Izin notifikasi ditolak | Banner di Pengaturan; job tetap bisa disimpan |
| Izin alarm tepat waktu belum diberikan | Banner di Pengaturan; pengingat memakai cadangan |
| Gagal menyimpan | Snackbar dengan opsi coba lagi |
| Ekspor gagal | Snackbar "Gagal mengekspor data" dengan opsi coba lagi |
| File impor tidak valid | Pesan "File cadangan tidak valid"; data tidak berubah |
| Impor berhasil | Pesan "Impor berhasil: N job" |

---

## 14. Rencana Pengerjaan dan Kriteria Selesai

### 14.1 Tahap

| Tahap | Pekerjaan | Keluaran yang dapat diuji |
|---|---|---|
| 1 | Setup proyek, tema, navigasi, database Room, **uji alarm awal** | Aplikasi berpindah layar; notifikasi uji muncul 1 menit kemudian saat layar mati |
| 2 | CRUD job (form, daftar, detail) + pembayaran + catatan | Tambah, lihat, ubah, hapus job; catat dibayar; tandai lunas |
| 3 | Autocomplete, duplikat job, peringatan bentrok | Saran muncul dan ejaan seragam; duplikat bekerja; dialog bentrok muncul |
| 4 | Pengingat otomatis (AlarmManager, notifikasi, boot) + Uji Pengingat | 4 notifikasi per job muncul pada waktunya |
| 5 | Rekap + Per Sumber Job + Piutang | Total, daftar, dan piutang sesuai |
| 6 | Cadangan data (Ekspor dan Impor JSON) | Ekspor, hapus, impor: semua data dan pengingat kembali |
| 7 | Pengaturan, rapikan kondisi kosong dan error, uji akhir | Semua kriteria selesai terpenuhi |

### 14.2 Kriteria selesai
- [ ] Job dapat dibuat, dilihat, diubah, dan dihapus dengan semua field Bagian 7.
- [ ] Jam selesai tampil otomatis dari jam mulai + durasi.
- [ ] Tombol Buka Maps membuka link shareloc.
- [ ] Autocomplete acara dan sumber job bekerja, dan ejaan beda huruf besar kecil disatukan.
- [ ] Duplikat job menyalin dan mengosongkan field sesuai FR-1, dan job asli tidak berubah.
- [ ] Menyimpan job yang bentrok menampilkan peringatan; job berurutan tanpa jeda tidak dianggap bentrok.
- [ ] Jumlah dibayar dapat diisi dari form dan dari dialog di detail; chip "Belum lunas" dan "Kurang Rp X" tampil benar; Tandai Lunas mengisi dibayar sama dengan fee.
- [ ] Setiap job baru otomatis mendapat pengingat H-7, H-3, H-1 (08.00), dan 3 jam sebelum acara; pengingat yang sudah lewat dilewati.
- [ ] Mengubah atau menghapus job membatalkan pengingat lama.
- [ ] Pengingat tetap berjalan setelah HP restart dan notifikasi muncul saat layar mati.
- [ ] Tombol Uji Pengingat di Pengaturan memunculkan notifikasi.
- [ ] Mengetuk notifikasi membuka detail job yang benar.
- [ ] Rekap Hari Ini, Minggu Ini, Bulan Ini menampilkan jumlah job, total fee, sudah dibayar, belum dibayar, dan daftar job yang benar.
- [ ] Rekap Per Sumber Job menampilkan jumlah job, total fee, dan sisa tiap sumber.
- [ ] Bagian Piutang menampilkan semua job lewat yang belum lunas (lintas periode), terlama dulu, dengan total sisa yang benar.
- [ ] Ekspor menghasilkan file JSON; impor file itu mengembalikan seluruh data dan menjadwalkan ulang pengingat; file tidak valid ditolak tanpa mengubah data.
- [ ] Skema Room memakai `exportSchema = true`, tanpa `fallbackToDestructiveMigration`.
- [ ] Aplikasi tidak crash saat izin ditolak atau data kosong.

---

## 15. Aturan Teknis

| Bagian | Pilihan |
|---|---|
| Bahasa dan UI | Kotlin + Jetpack Compose, Material 3 |
| Arsitektur | MVVM (ViewModel + Repository), `StateFlow`, coroutines |
| Database | Room (dengan KSP), lokal saja, `exportSchema = true` |
| Pengingat | `AlarmManager` + `BroadcastReceiver` (Bagian 9) |
| Navigasi | Navigation Compose |
| Tanggal dan waktu | `java.time` dengan `ZoneId.of("Asia/Jakarta")` |
| Cadangan | JSON via `org.json` + Storage Access Framework (Bagian 6 FR-7 dan 7.3) `[ASUMSI]` |

Struktur paket yang disarankan:

```
com.contoh.kontenin
├── data        (entity Job, DAO, database, repository)
├── ui          (screens, components, navigation, theme)
├── viewmodel
├── reminder    (ReminderScheduler, ReminderReceiver, BootReceiver, notification helper)
├── backup      (ekspor/impor JSON, validasi file cadangan)
└── util        (format tanggal, jam, rupiah, penghitung rekap, deteksi bentrok)
```

Aturan:
- **Versi library**: pakai versi **stabil terbaru** dan cek dokumentasi resmi; jangan mengandalkan ingatan untuk nomor versi atau nama API (terutama izin alarm dan notifikasi, yang berubah antar versi Android). Gunakan **version catalog**.
- **Izin di manifest**: `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `RECEIVE_BOOT_COMPLETED`. **Tanpa** `INTERNET`. Izin ini sudah dideklarasikan sejak Tahap 1 karena dipakai uji alarm awal. Ekspor/impor tidak butuh izin tambahan.
- **Logika murni dipisah** dari Android agar mudah diuji: penghitung waktu pengingat, penghitung rekap (rentang periode, total, per sumber, piutang), deteksi bentrok, normalisasi teks autocomplete, validasi, konversi tanggal dari date picker, validasi dan pemetaan file cadangan.
- **Date picker**: `DatePicker` Material 3 mengembalikan milidetik **UTC tengah malam**. Konversi ke `LocalDate` harus memakai UTC, bukan `Asia/Jakarta`, agar tanggal tidak mundur satu hari. Buat fungsi murni dengan unit test.
- **Error**: bungkus hasil operasi dalam tipe hasil sederhana (misalnya `sealed class Result`). Jangan biarkan exception mentah sampai ke UI.
- **Database**: ikuti Bagian 7.2. Tanpa `fallbackToDestructiveMigration`; perubahan skema wajib lewat `Migration` dan pembaruan format cadangan.
- **Tanpa framework DI** (Hilt/Koin); buat dependensi manual `[ASUMSI]`.
- Semua teks di `strings.xml`.
- **Git**: inisialisasi repository, `.gitignore` standar Android, commit tiap tahap selesai.
- Unit test hanya untuk logika murni: waktu pengingat (termasuk yang dilewati), rentang periode rekap (awal minggu Senin, akhir bulan), agregasi per sumber dan piutang, deteksi bentrok (termasuk job berurutan dan melewati tengah malam), normalisasi autocomplete, validasi (termasuk `dibayar <= fee`), konversi tanggal date picker, serta serialisasi dan validasi file cadangan.
- Kerjakan hanya tahap yang diminta; jangan merombak tahap sebelumnya tanpa memberi tahu.
