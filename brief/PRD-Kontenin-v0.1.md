# PRD: Kontenin

Versi 0.1 (draft) · 28 Oktober 2026 · Penulis: Senior PM · Sumber: brief-kontenin.md dan jawaban klarifikasi pemilik

**Ringkasan.** Kontenin adalah aplikasi Android offline untuk freelancer Wedding Content Creator (WCC): mencatat job, mengingatkan otomatis, memperingatkan jadwal bentrok, mencatat pembayaran, merekap fee dan piutang, serta mencadangkan data ke file. Fase 1: tugas kuliah dan pemakaian pribadi (cakupan PRD ini). Fase 2: rilis Play Store untuk WCC lain (belum didefinisikan, lihat bagian 10 dan 12).

**Konvensi.** [BRIEF] = diambil dari brief. [REKOMENDASI] = usulan PM, menunggu konfirmasi pemilik. [OPEN] = pertanyaan terbuka di bagian 10; tidak diputuskan sepihak.

## 1. Problem Statement

**Yang dirugikan:** WCC freelance yang menerima job dari banyak sumber (vendor sendiri dan vendor lain) dan mengelola semuanya sendiri tanpa admin.

**Masalah dan dampak:**

1. Detail job tersebar di chat dan catatan. Alamat, lokasi, rundown, dan kontak PIC sulit dicari saat dibutuhkan.
2. Jadwal terlewat atau bentrok. Data pemilik: rata-rata 10 job per bulan dan 3–5 job bentrok [OPEN OQ-1: periode pengukuran belum jelas]. Bentrok adalah masalah terukur terbesar saat ini.
3. Fee belum dibayar mudah terlupa. Data pemilik: keterlambatan hanya 1–3 hari, tidak ada tunggakan panjang. Artinya nilai fitur piutang bagi pemilik saat ini rendah; nilainya bagi WCC lain belum terbukti [OPEN OQ-2].
4. Penghasilan per periode dan per sumber job tidak terlihat.
5. Catatan penghasilan hilang jika HP rusak atau hilang.

Biaya konkret satu job yang terlewat atau bentrok (rupiah, reputasi) belum pernah diukur.

## 2. Target User dan Persona

**Target:** WCC freelance individu, pengguna Android, Indonesia, satu orang satu perangkat. **Bukan target:** studio dengan tim, event organizer, vendor pernikahan lengkap.

### Persona 1: Pemilik (pengguna fase 1, berbasis fakta)

| Atribut | Isi |
|---|---|
| Profil | WCC freelance, mahasiswa, sekaligus pembuat aplikasi |
| Volume | ±10 job per bulan |
| Sumber job | Pribadi dan vendor lain |
| Masalah utama | Job bentrok 3–5 kali; detail job di chat dan catatan |
| Pembayaran | Fee cair dengan keterlambatan 1–3 hari |
| Kebutuhan | Pengingat yang pasti berbunyi, peringatan bentrok, rekap penghasilan |
| Perangkat | [OPEN OQ-12: merk, model, versi Android belum disebutkan] |

### Persona 2: WCC lain (pengguna fase 2, **hipotesis, belum divalidasi**)

| Atribut | Isi |
|---|---|
| Profil | WCC freelance yang menerima job dari beberapa vendor |
| Volume | Tidak diketahui; bisa lebih tinggi dari pemilik |
| Perangkat | Beragam, termasuk merk dengan manajemen baterai agresif (Xiaomi, Oppo, Vivo, Realme; brief 9.5) |
| Kemungkinan perbedaan | Zona waktu bukan WIB, piutang lebih lama, pengetahuan teknis lebih rendah (tidak paham izin alarm dan pengaturan baterai) |
| Validasi | Wawancara minimal 3 WCC sebelum fitur fase 2 dikunci [OPEN OQ-3] |

## 3. Goals dan Non-goals

**Goals (versi 1):**

- G1. Semua data job tersimpan di satu tempat dan bisa diubah, diduplikat, dihapus.
- G2. Setiap job mendapat 4 pengingat otomatis (H-7, H-3, H-1 pukul 08.00, 3 jam sebelum acara) tanpa pengaturan.
- G3. Pengguna diperingatkan sebelum menyimpan job yang bentrok.
- G4. Pembayaran tercatat dan sisa tagihan terlihat per job.
- G5. Rekap per periode, per sumber job, dan piutang lintas periode akurat.
- G6. Seluruh data bisa diekspor dan diimpor kembali tanpa kehilangan isi.

**Non-goals (versi 1) [BRIEF]:**

- Cloud, sinkronisasi, login, akun, multi-perangkat, multi-pengguna.
- Cadangan otomatis atau terjadwal; impor bersifat mengganti, bukan menggabungkan.
- Jadwal pengingat per job, pengingat menagih fee.
- Kalender bulanan, widget, ekspor PDF/CSV, foto atau lampiran.
- Riwayat pembayaran per transaksi (hanya total dibayar).
- Tampilan khusus bentrok; peringatan tidak memblokir penyimpanan.
- Rekap untuk periode selain hari ini, minggu ini, bulan ini.
- Berbagi jadwal ke orang lain.

## 4. User Stories

| ID | Cerita | FR |
|---|---|---|
| US-1 | Sebagai WCC, saya ingin mencatat job (pengantin, waktu, lokasi, sumber, fee, catatan), supaya semua detail ada di satu tempat. | 1 |
| US-2 | Sebagai WCC, saya ingin melihat job mendatang urut dari yang terdekat, supaya tahu apa yang harus dikerjakan berikutnya. | 1 |
| US-3 | Sebagai WCC, saya ingin membuka lokasi acara di Maps dengan satu ketukan, supaya tidak mencari alamat di chat. | 1 |
| US-4 | Sebagai WCC, saya ingin menduplikat job, supaya tidak mengetik ulang data pasangan yang sama untuk acara berbeda. | 1 |
| US-5 | Sebagai WCC, saya ingin diperingatkan saat jadwal baru bentrok, supaya tidak menerima dua job di waktu yang sama. | 1 |
| US-6 | Sebagai WCC, saya ingin mengisi acara dan sumber job dengan saran dari isian lama, supaya cepat dan ejaannya seragam di rekap. | 2 |
| US-7 | Sebagai WCC, saya ingin mencatat DP dan pelunasan, supaya tahu sisa fee tiap job. | 3 |
| US-8 | Sebagai WCC, saya ingin menandai job lunas dengan satu ketukan, supaya pencatatan tidak merepotkan. | 3 |
| US-9 | Sebagai WCC, saya ingin pengingat otomatis sebelum acara, supaya tidak perlu memasang alarm satu per satu. | 4 |
| US-10 | Sebagai WCC, saya ingin pengingat tetap berbunyi setelah HP restart, supaya tidak terlewat karena alasan teknis. | 4 |
| US-11 | Sebagai WCC, saya ingin mengetuk notifikasi dan langsung melihat detail job, supaya tidak mencari manual. | 4 |
| US-12 | Sebagai WCC, saya ingin rekap hari ini, minggu ini, bulan ini, supaya tahu jumlah job dan penghasilan periode berjalan. | 5 |
| US-13 | Sebagai WCC, saya ingin rekap per sumber job, supaya tahu sumber mana yang paling menghasilkan. | 5 |
| US-14 | Sebagai WCC, saya ingin daftar fee yang belum lunas dari semua periode, supaya tahu siapa yang harus ditagih. | 5 |
| US-15 | Sebagai WCC, saya ingin menguji pengingat dari Pengaturan, supaya yakin HP saya tidak memblokir notifikasi. | 6 |
| US-16 | Sebagai WCC, saya ingin mengekspor dan mengimpor seluruh data sebagai file, supaya catatan tidak hilang jika HP rusak. | 7 |

## 5. Daftar Fitur

| Fase | Fitur |
|---|---|
| **MVP** | FR-1 Kelola job (CRUD, duplikat, peringatan bentrok, Buka Maps) · FR-2 Autocomplete acara dan sumber job · FR-3 Pembayaran · FR-4 Pengingat otomatis · FR-5 Rekap, per sumber job, piutang · FR-6 Pengaturan (izin, petunjuk baterai, Uji Pengingat) · FR-7 Ekspor dan Impor |
| **v2** | Rekap dengan pemilihan bulan lain (prioritas Tinggi di brief; disetujui pemilik) |
| **Later** | Pengingat menagih fee · widget job terdekat · kalender bulanan · ekspor rekap PDF/CSV · riwayat pembayaran per transaksi · jam pengingat harian dapat diatur · pengingat berkala untuk ekspor cadangan · cadangan otomatis ke Google Drive |
| **Prasyarat rilis publik** | Lihat bagian 12. Bukan fitur; pekerjaan yang harus selesai sebelum Play Store. |

Prioritas pemotongan di dalam MVP jika waktu habis belum ditetapkan [OPEN OQ-4].

## 6. Functional Requirements (MVP)

### FR-1 Kelola Job

- 1.1 Tambah, lihat, ubah, hapus job dengan field di bagian 7. Tanggal masa lalu boleh diisi.
- 1.2 Validasi (pesan error Bahasa Indonesia di bawah kolom; Simpan tidak menyimpan jika tidak valid):
  - Nama pengantin 1 dan 2: wajib, 1–50 karakter setelah trim.
  - Tanggal dan jam mulai: wajib.
  - Durasi: wajib, 0,5–24 jam, kelipatan 0,5; terima koma dan titik; nilai awal form 4 jam [ASUMSI, OQ-9].
  - Acara dan sumber job: wajib, 1–50 karakter.
  - Alamat: opsional, maks 300 karakter. Catatan: opsional, maks 1000 karakter.
  - Shareloc: opsional; jika diisi harus diawali http:// atau https://.
  - Fee: wajib, bilangan bulat ≥ 0, pemisah ribuan saat mengetik.
  - Dibayar: opsional (default 0), bilangan bulat, 0 ≤ dibayar ≤ fee. Jika fee diturunkan di bawah dibayar, error di kolom dibayar.
- 1.3 Beranda: tab **Mendatang** (urut terdekat) dan **Riwayat** (urut terbaru), satu filter chip “Belum lunas” untuk tab aktif, FAB tambah. Job berstatus Mendatang selama waktu selesai (mulai + durasi) belum lewat.
- 1.4 Kartu job: nama (“Andi & Sari”), acara, tanggal, rentang jam, sumber job, fee, chip pembayaran (lihat FR-3).
- 1.5 Detail job: semua isi termasuk catatan; jam sebagai rentang (mis. 10.00 - 14.00) dihitung otomatis, tidak disimpan.
- 1.6 Tombol **Buka Maps** membuka shareloc lewat ACTION_VIEW. Shareloc kosong: tombol disembunyikan. Tidak ada aplikasi pembuka: pesan “Tidak ada aplikasi untuk membuka link ini”.
- 1.7 **Duplikat** membuka Form Job mode tambah. Disalin: nama 1 dan 2, jam mulai, durasi, alamat, shareloc, sumber job, fee, catatan. Dikosongkan: tanggal (wajib pilih ulang), acara, dibayar (0). Job asli tidak berubah [ASUMSI, OQ-9].
- 1.8 **Peringatan bentrok** saat Simpan (baru atau ubah):
  - Menit absolut = tanggal × 1440 + jamMulaiMenit. Bentrok jika mulaiA < selesaiB dan mulaiB < selesaiA.
  - Selesai A = mulai B (berurutan tanpa jeda): bukan bentrok.
  - Kandidat dicek dari tanggal −1 sampai +1. Job yang sedang diubah dikecualikan.
  - Jika ada bentrok: dialog berisi daftar (nama, acara, rentang jam) dengan tombol **Tetap Simpan** dan **Kembali**. Tidak memblokir.
- 1.9 Hapus dengan dialog konfirmasi; 4 pengingat job ikut dibatalkan.

### FR-2 Autocomplete (acara dan sumber job)

- 2.1 Kolom tetap teks bebas; saat fokus atau mengetik tampil maksimal 5 saran dari data job yang ada (tanpa tabel khusus), tidak membedakan huruf besar kecil, urut dari paling sering dipakai.
- 2.2 Saat simpan: trim dan rapikan spasi ganda. Jika sama dengan isian lama tanpa memandang huruf besar kecil, pakai ejaan isian lama.
- 2.3 Memilih saran mengisi kolom; tetap bisa diedit.

### FR-3 Pembayaran

- 3.1 Satu field `dibayar` per job. Lunas = dibayar ≥ fee; sisa = fee − dibayar; keduanya dihitung, tidak disimpan.
- 3.2 Form Job: kolom “Sudah dibayar” dan tombol “Lunas” (mengisi dibayar = fee).
- 3.3 Detail Job: **Tandai Lunas** (tampil jika belum lunas) dan **Ubah Pembayaran** (dialog satu kolom, validasi sama dengan Form).
- 3.4 Chip (warna Warning): dibayar = 0 dan fee > 0 → “Belum lunas”; 0 < dibayar < fee → “Kurang Rp X”; lunas → tanpa chip.

### FR-4 Pengingat Otomatis

- 4.1 Setiap job yang disimpan (baru, ubah, duplikat) mendapat 4 pengingat. Tidak ada pengaturan per job.

| Pengingat | Waktu | Judul |
|---|---|---|
| H-7 | 7 hari sebelum tanggal acara, 08.00 WIB | H-7: Resepsi Andi & Sari |
| H-3 | 3 hari sebelum, 08.00 | H-3: … |
| H-1 | 1 hari sebelum, 08.00 | H-1: … |
| 3 jam | Tepat 3 jam sebelum jam mulai | 3 jam lagi: … |

- 4.2 Jam 08.00 adalah satu konstanta di kode [ASUMSI].
- 4.3 Isi notifikasi: tanggal, rentang jam, lalu alamat (dilewati jika kosong). Contoh: “Besok, Sab 18 Okt 2026, 10.00 - 14.00. Gedung X”.
- 4.4 Pengingat yang waktunya sudah lewat saat simpan dilewati, tidak dibunyikan langsung. Job yang acaranya sudah lewat tidak punya pengingat.
- 4.5 Mekanisme: alarm via AlarmManager ke BroadcastReceiver; requestCode = id job × 4 + indeks (0 H-7, 1 H-3, 2 H-1, 3 tiga jam); PendingIntent FLAG_IMMUTABLE, dibuat oleh satu fungsi yang sama untuk jadwal dan pembatalan. Semua logika dalam satu kelas ReminderScheduler: schedule(job), cancel(jobId), rescheduleAll().
- 4.6 Saat alarm berbunyi, receiver membaca job terbaru dari database. Job tidak ada: tidak menampilkan apa pun.
- 4.7 Notifikasi: channel `pengingat_job` (nama “Pengingat Job”, importance HIGH); ID notifikasi = requestCode; ketukan membuka Detail Job. Job sudah dihapus: “Job tidak ditemukan”, kembali ke Beranda.
- 4.8 Pemicu jadwal: simpan, ubah (batalkan lama lalu jadwalkan), hapus (batalkan), impor, BOOT_COMPLETED, MY_PACKAGE_REPLACED, dan setiap aplikasi dibuka: rescheduleAll(). Status lunas tidak memengaruhi pengingat.
- 4.9 Izin: POST_NOTIFICATIONS diminta saat pertama menyimpan job (dengan penjelasan singkat). Izin alarm tepat waktu belum diberikan: pakai cadangan `setAndAllowWhileIdle`, banner di Pengaturan. Izin notifikasi ditolak: job tetap tersimpan, banner di Pengaturan. USE_EXACT_ALARM tidak dideklarasikan [ASUMSI].

### FR-5 Rekap dan Piutang

- 5.1 Periode: Hari Ini, Minggu Ini, Bulan Ini. Dihitung dari **tanggal acara**, bukan tanggal pembayaran. Minggu mulai Senin [ASUMSI, OQ-9].
- 5.2 Isi per periode: jumlah job, total fee, total dibayar, total sisa, daftar job (ketuk membuka Detail), dan **Per Sumber Job** (jumlah job, total fee, total sisa).
- 5.3 **Piutang** (tidak terpengaruh pilihan periode): total sisa, jumlah job, daftar urut tanggal acara terlama dulu. Definisi: acara sudah lewat dan belum lunas [ASUMSI, OQ-5].
- 5.4 Kondisi kosong: “Belum ada job pada periode ini” dengan semua total Rp 0; “Tidak ada fee yang perlu ditagih” dengan total Rp 0.

### FR-6 Pengaturan

- 6.1 Status izin notifikasi dan alarm tepat waktu, dengan tombol ke pengaturan sistem jika belum aktif.
- 6.2 Petunjuk baterai dan autostart.
- 6.3 **Uji Pengingat** (semua build): menjadwalkan satu notifikasi uji beberapa detik ke depan lewat jalur alarm yang sama dengan pengingat asli.
- 6.4 Tombol Ekspor Data, Impor Data, dan versi aplikasi.

### FR-7 Cadangan Data

- 7.1 **Ekspor**: satu file JSON berisi seluruh job via ACTION_CREATE_DOCUMENT, tanpa izin tambahan. Nama saran: `kontenin-backup-YYYYMMDD.json`.
- 7.2 **Impor** via ACTION_OPEN_DOCUMENT: (a) baca dan validasi seluruh file sebelum mengubah apa pun; satu job tidak valid berarti seluruh impor ditolak; (b) dialog konfirmasi “Semua data saat ini akan diganti dengan isi file (N job).”; (c) batalkan alarm lama, ganti isi tabel `jobs` dalam satu transaksi Room, panggil rescheduleAll().
- 7.3 Validasi file: `app` = “kontenin”; `schemaVersion` tidak lebih baru dari yang dikenal aplikasi; semua field wajib ada dengan tipe benar; aturan validasi FR-1.2 terpenuhi; `id` dipertahankan.
- 7.4 Auto Backup Android tetap aktif (bonus, bukan pengganti Ekspor).
- 7.5 Pesan: “Impor berhasil: N job”, “File cadangan tidak valid”, “Gagal mengekspor data” (dengan coba lagi).

## 7. Sketsa Data Model

**Entitas tunggal: `Job` (tabel Room `jobs`).** Tidak ada tabel acara atau sumber job; keduanya string bebas.

| Field | Tipe | Wajib | Keterangan |
|---|---|---|---|
| id | Long | ya | PK, auto generate |
| namaPengantin1 | String | ya | 1–50 |
| namaPengantin2 | String | ya | 1–50 |
| tanggal | Long | ya | LocalDate.toEpochDay() |
| jamMulaiMenit | Int | ya | 0–1439 |
| durasiMenit | Int | ya | 30–1440, kelipatan 30 |
| acara | String | ya | 1–50, autocomplete |
| alamat | String | tidak | maks 300 |
| shareloc | String | tidak | http(s) |
| sumberJob | String | ya | 1–50, autocomplete |
| fee | Long | ya | ≥ 0 |
| dibayar | Long | ya | default 0; 0 ≤ dibayar ≤ fee |
| catatan | String | tidak | maks 1000, default kosong |
| createdAt, updatedAt | Long | ya | epoch millis |

- Index pada `tanggal`.
- **Dihitung, tidak disimpan:** jam selesai, status lunas, sisa, rentang jam, status Mendatang/Riwayat, piutang.
- Waktu mulai acara = tanggal + jamMulaiMenit pada Asia/Jakarta.
- **File cadangan (JSON):** `app`, `schemaVersion` (awal 1), `exportedAt`, `jobs[]` berisi seluruh field di atas.
- Aturan skema: versi awal 1; `exportSchema = true` dan file skema di Git; dilarang `fallbackToDestructiveMigration`; perubahan skema wajib Migration eksplisit dan pembaruan format cadangan. Skema harus final sebelum data asli dimasukkan.

## 8. Edge Case dan Failure State

| ID | Situasi | Perilaku |
|---|---|---|
| EC-1 | Job disimpan mepet acara (H-7/H-3/H-1 sudah lewat) | Pengingat lewat dilewati, tanpa bunyi langsung. Pemberitahuan ke pengguna tidak ditentukan [OQ-6] |
| EC-2 | Acara kurang dari 3 jam lagi | Tidak ada pengingat sama sekali; perilaku sama dengan EC-1 [OQ-6] |
| EC-3 | Job masa lalu | Boleh disimpan, tanpa pengingat |
| EC-4 | Durasi melewati tengah malam | Bentrok dihitung dengan menit absolut; kandidat ±1 hari. Tampilan rentang jam lintas hari tidak ditentukan [OQ-7]. Rekap memakai tanggal mulai |
| EC-5 | Job berurutan tanpa jeda | Bukan bentrok |
| EC-6 | Mengubah job | Job itu dikecualikan dari cek bentrok; pengingat lama dibatalkan |
| EC-7 | Fee diturunkan di bawah dibayar | Error di kolom dibayar, tidak tersimpan |
| EC-8 | Fee 0 | Lunas, tanpa chip, bukan piutang. Tombol “Lunas” saat fee kosong tidak ditentukan [OQ-8] |
| EC-9 | Izin notifikasi ditolak | Job tetap tersimpan; banner di Pengaturan |
| EC-10 | Izin alarm tepat waktu belum diberikan | Pakai setAndAllowWhileIdle; banner di Pengaturan; tidak crash |
| EC-11 | Alarm berbunyi, job sudah dihapus | Tidak ada notifikasi |
| EC-12 | Notifikasi diketuk, job sudah dihapus | “Job tidak ditemukan”, ke Beranda |
| EC-13 | HP restart, aplikasi diperbarui, aplikasi dibuka | rescheduleAll() |
| EC-14 | Merk HP mematikan aplikasi di latar | Petunjuk di Pengaturan + Uji Pengingat. Tidak bisa dijamin oleh aplikasi |
| EC-15 | Zona waktu atau jam sistem berubah | Tidak ditentukan; waktu dikunci Asia/Jakarta [OQ-10] |
| EC-16 | Impor file rusak, bukan cadangan Kontenin, atau schemaVersion lebih baru | Ditolak dengan “File cadangan tidak valid”; data lama tidak berubah |
| EC-17 | Impor gagal di tengah | Transaksi rollback. Alarm lama sudah dibatalkan sebelum transaksi, jadi rescheduleAll() wajib dipanggil juga pada jalur gagal [REKOMENDASI] |
| EC-18 | Ekspor gagal (penyimpanan penuh, dibatalkan) | Snackbar dengan coba lagi |
| EC-19 | Simpan ke database gagal | Snackbar dengan coba lagi |
| EC-20 | Data dipulihkan oleh Auto Backup | rescheduleAll() saat aplikasi dibuka |
| EC-21 | Shareloc tidak bisa dibuka | Pesan error; tombol disembunyikan jika kosong |
| EC-22 | Date picker mengembalikan UTC tengah malam | Konversi ke LocalDate memakai UTC (bukan Asia/Jakarta); fungsi murni dengan unit test |
| EC-23 | Ejaan beda huruf besar kecil (“resepsi” vs “Resepsi”) | Disatukan ke ejaan lama |
| EC-24 | Hapus job | Tidak ada undo |
| EC-25 | Dua job identik | Tidak ada deteksi duplikat selain peringatan bentrok |

## 9. Success Metrics

Aplikasi tanpa INTERNET tidak punya analitik. Pada fase 1 semua metrik diukur manual (checklist dan catatan pemilik). Target bertanda **usulan** belum dikonfirmasi.

| Metrik | Baseline | Target | Cara ukur |
|---|---|---|---|
| Job terlewat karena lupa | Belum diukur [OQ-1] | 0 pada job yang punya pengingat (usulan) | Catatan pemilik 1 bulan pemakaian |
| Pengingat tampil sesuai jadwal | — | 4 dari 4 per job pada HP pemilik; toleransi waktu belum didefinisikan [OQ-11] | Checklist 12.3, 12.4 brief |
| Job bentrok yang lolos tanpa peringatan | 3–5 bentrok, tanpa peringatan | 0: semua pasangan overlap memunculkan peringatan; 0 false positive pada job berurutan | Unit test + uji manual |
| Akurasi rekap dan piutang | — | 100% sama dengan hitungan manual | Unit test + cocokkan dengan catatan pemilik |
| Round-trip cadangan | — | Ekspor → hapus → impor mengembalikan 100% job dan pengingat | Checklist 12.5 brief |
| Adopsi | Data di chat/catatan | Semua job baru satu bulan (±10) dicatat di Kontenin (usulan) | Catatan pemilik |
| Stabilitas | — | 0 crash pada kasus izin ditolak dan data kosong | Checklist 14.2 brief |

Metrik fase 2 (retensi, crash-free rate) belum didefinisikan [OQ-13].

## 10. Open Questions

| ID | Pertanyaan | Dampak | Jawaban |
|---|---|---|---|
| OQ-1 | "3–5 job bentrok": per bulan atau total? Berapa kali jadwal pernah terlewat? | Baseline metrik | **3-5 job bentrok per bulan** |
| OQ-2 | Apakah WCC lain juga punya masalah tagihan, atau hanya kebutuhan pemilik? | Prioritas fitur piutang untuk fase 2 | **Prioritas rendah, fokus fase 1** |
| OQ-3 | Siapa yang diwawancarai untuk memvalidasi persona 2, dan kapan? | Scope fase 2 | **Tidak ada wawancara (proyek kuliah, fokus implementasi)** |
| OQ-4 | Jika waktu tidak cukup, fitur MVP mana yang dipotong lebih dulu? | Perencanaan tahap | **1) Piutang → 2) Per Sumber Job → 3) Filter "Belum lunas" → 4) Duplikat. Ekspor/Impor JANGAN potong (backup kritis)** |
| OQ-5 | "Acara sudah lewat" untuk piutang: setelah waktu selesai, atau setelah tanggal lewat? Perlu ambang hari (mis. keterlambatan lazim 1–3 hari membuat daftar piutang berisik)? | FR-5.3 | **Piutang muncul setelah jam selesai acara (jam mulai + durasi)** |
| OQ-6 | Perlukah memberi tahu pengguna bahwa sebagian pengingat dilewati saat job disimpan mepet? | EC-1, EC-2 | **Ya, snackbar setelah Simpan: "Beberapa pengingat dilewati karena acara terlalu dekat" (4 detik, auto dismiss)** |
| OQ-7 | Tampilan rentang jam untuk job lintas tengah malam (22.00 - 02.00)? | FR-1.5, notifikasi | **Di kartu/detail: "22.00 - 02.00 (19 Okt)". Notifikasi: gunakan tanggal mulai** |
| OQ-8 | Perilaku tombol "Lunas" saat fee kosong? | FR-3 | **Tombol nonaktif (disabled) saat fee = 0** |
| OQ-9 | Asumsi brief yang belum disetujui: durasi awal 4 jam, aturan duplikat, minggu mulai Senin, impor mengganti data, jam 08.00, Kotlin+Compose, Material 3 dan warna, DI manual | Seluruh FR terkait | **Setuju dengan semua asumsi** |
| OQ-10 | Zona waktu dikunci WIB. Pengguna WITA/WIT dan job di luar zona asal bagaimana? | Wajib dijawab sebelum rilis publik | **Fase 1: WIB saja. Multi-zona untuk fase 2 (rilis publik)** |
| OQ-11 | Toleransi waktu pengingat yang dianggap "tepat waktu" (detik, 1 menit, 10 menit)? | Kriteria uji, pilihan alarm | **≤ 5 menit (realistis untuk alarm Android)** |
| OQ-12 | Merk, model, dan versi Android HP uji pemilik? | Keputusan alarm, uji Tahap 1 | **Poco X3 GT** |
| OQ-13 | Metrik, target, dan cara ukur fase 2 (tanpa SDK analitik)? | Rencana rilis | **Manual: catatan pemilik + form feedback sederhana** |
| OQ-14 | Apakah rilis Play Store masuk dalam proyek kuliah ini atau proyek terpisah? Kapan? | Seluruh bagian 12 | **Belum diputuskan, fokus MVP lokal dulu** |
| OQ-15 | Mengapa WCC tidak cukup memakai kalender bawaan HP + spreadsheet? Diferensiasi Kontenin? | Kelayakan rilis publik | **Semua (satu tempat + deteksi bentrok + reminder terstruktur)** |
| OQ-16 | Fee tambahan/tip dari klien melebihi fee tercatat: bagaimana mencatatnya (saat ini dibayar ≤ fee)? | Validasi FR-3 | **Perlu field terpisah "Tip/Fee Tambahan" (fase 2). Fase 1: validasi dibayar ≤ fee tetap berlaku** |
| OQ-17 | Job dibatalkan atau ditunda: hapus saja, atau perlu status? Saat ini tidak ada status. | Model data (perubahan skema) | **Fase 1: Hapus saja (cukup). Status job masuk fase 2 jika terbukti perlu** |
| OQ-18 | Mengganti nama sumber job atau acara secara massal? Saat ini harus mengubah tiap job. | Rekap per sumber | **Fase 2. Terlalu kompleks untuk fase 1 (fitur kelola master data)** |
| OQ-19 | Berapa jumlah job maksimum yang harus tetap lancar? Brief tidak menyebut batas. | NFR performa | **Batas 1000 job. Lazy loading wajib. TalkBack baseline fase 1** |

## 11. Keputusan Teknis (Rekomendasi PM, menunggu konfirmasi pemilik)

| Keputusan | Rekomendasi | Alasan | Pertimbangan |
|---|---|---|---|
| Mekanisme alarm | **Opsi A**: setExactAndAllowWhileIdle + izin SCHEDULE_EXACT_ALARM + cadangan setAndAllowWhileIdle, dengan **gerbang uji Tahap 1**: jika pada HP uji notifikasi tidak muncul andal, pindah ke opsi B (setAlarmClock) | Pengingat 08.00 dan 3 jam tidak butuh presisi detik; A turun mulus ke perilaku opsi C jika izin tidak ada, tanpa ikon alarm permanen | Di Android 14+ izin tidak diberikan otomatis. Untuk rilis publik, pengguna awam bisa tidak mengaktifkannya sehingga pengingat jadi tidak presisi. Kebijakan Play Store untuk SCHEDULE_EXACT_ALARM dan USE_EXACT_ALARM harus diperiksa sebelum rilis [OQ-14] |
| Library JSON | **kotlinx.serialization** | Berjalan di unit test JVM tanpa dependensi tambahan, skema bertipe, lebih aman untuk format cadangan yang akan dipakai banyak pengguna | Menambah satu plugin dan satu library; org.json butuh testImplementation tambahan dan validasi manual |
| minSdk | **26** (sesuai brief) | Cukup luas untuk target | Cek sebaran versi Android pasar target sebelum rilis |
| Arsitektur lain | Sesuai brief: Kotlin, Compose, Material 3, MVVM, Room (KSP), Navigation Compose, java.time, DI manual | Selaras prinsip sederhana | — |

## 12. Persyaratan Non-fungsional dan Prasyarat Rilis Publik

**Non-fungsional [BRIEF]:**

- Offline penuh; tanpa izin INTERNET; tanpa login, server, Firebase.
- Bahasa Indonesia; mata uang Rp; format tanggal `dd MMM yyyy`, jam `HH.mm`, rupiah locale id-ID.
- Light dan dark mode mengikuti sistem; area sentuh minimal 48 dp.
- Semua teks di `strings.xml`; tidak ada exception mentah sampai ke UI.
- Unit test hanya untuk logika murni: waktu pengingat, rentang rekap, agregasi, bentrok, normalisasi autocomplete, validasi, konversi date picker, serialisasi cadangan.
- Performa, aksesibilitas (TalkBack), dan ukuran data: belum ditentukan [OQ-19].

**Prasyarat rilis Play Store (milestone terpisah, belum dijadwalkan, bergantung OQ-14):**

- Package name final (placeholder `com.contoh.kontenin` tidak bisa dipakai; setelah rilis tidak bisa diganti).
- Kebijakan privasi dan pengisian Data Safety. Data berisi nama pasangan dan kontak PIC (data pihak ketiga) walau tidak keluar dari perangkat.
- Keputusan izin alarm sesuai kebijakan Play Store (bagian 11).
- Zona waktu multi-wilayah (OQ-10).
- Setelah ada pengguna publik, perubahan skema hanya lewat Migration; format cadangan harus kompatibel mundur.
- Pengukuran stabilitas tanpa SDK analitik (OQ-13).
- Persyaratan teknis Play Store terbaru (targetSdk, format paket) dicek saat itu; tidak ditetapkan di PRD ini.

## 13. Rencana Tahap dan Definisi Selesai

Definisi selesai (disetujui pemilik): **seluruh checklist kriteria selesai (brief 14.2) terpenuhi.** Tidak ada syarat tambahan. Tenggat belum disebutkan [OQ-14].

| Tahap | Pekerjaan | Keluaran teruji |
|---|---|---|
| 1 | Setup, tema, navigasi, Room, uji alarm awal | Notifikasi uji muncul 1 menit kemudian saat layar mati (gerbang keputusan alarm) |
| 2 | CRUD job + pembayaran + catatan | Tambah, lihat, ubah, hapus, catat dibayar, tandai lunas |
| 3 | Autocomplete, duplikat, peringatan bentrok | Saran, ejaan seragam, duplikat, dialog bentrok |
| 4 | Pengingat otomatis + Uji Pengingat | 4 notifikasi per job pada waktunya |
| 5 | Rekap, per sumber job, piutang | Total, daftar, piutang sesuai |
| 6 | Ekspor dan Impor | Ekspor, hapus, impor: data dan pengingat kembali |
| 7 | Pengaturan, kondisi kosong dan error, uji akhir | Semua kriteria selesai terpenuhi |

Data asli baru boleh dimasukkan setelah Tahap 7 selesai dan skema final.

## 14. Risiko

| Risiko | Dampak | Mitigasi |
|---|---|---|
| Alarm dimatikan sistem pada merk HP agresif | Pengingat gagal; nilai inti produk hilang | Uji awal Tahap 1, petunjuk baterai, Uji Pengingat, gerbang ke opsi B |
| Izin alarm tepat waktu tidak aktif (Android 14+) | Pengingat tidak presisi | Cadangan setAndAllowWhileIdle, banner izin |
| Pencadangan hanya manual | Data hilang jika pengguna lupa ekspor | Auto Backup aktif; pengingat ekspor berkala di Later |
| Perubahan skema setelah data asli masuk | Kehilangan atau korupsi data | Skema final sebelum data asli, Migration eksplisit, cadangan sebelum update |
| Rencana Play Store membuat asumsi fase 1 (WIB, satu pengguna) tidak berlaku | Pengerjaan ulang | Jawab OQ-10, OQ-14 lebih awal |
| Kebutuhan WCC lain belum divalidasi | Produk publik tidak dipakai | Wawancara (OQ-3, OQ-15) |
| Pengembang tunggal, proyek kuliah | Keterlambatan | Pengerjaan per tahap; prioritas pemotongan (OQ-4) |

## 15. Asumsi Belum Dikonfirmasi

Seluruh tanda [ASUMSI] di brief berstatus **belum dikonfirmasi** (pemilik: “belum semua disetujui”, tanpa rincian). Daftar dan keputusan ada di OQ-9, OQ-11, dan bagian 11. Pemilik diminta menandai satu per satu: setuju atau ubah.
