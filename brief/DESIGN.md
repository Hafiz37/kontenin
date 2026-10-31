# DESIGN.md: Kontenin

Design brief v0.1 · 28 Oktober 2026 · Acuan: PRD-Kontenin v0.1 · Penulis: Senior UI/UX Designer

**Cakupan.** Dokumen ini hanya mengatur tampilan dan perilaku antarmuka untuk halaman dan komponen yang ada di PRD. Tidak ada halaman, fitur, atau komponen tambahan di luar kebutuhan PRD.

**Konvensi label.**
- **[PRD]** = berasal langsung dari PRD.
- **[USULAN]** = keputusan desain saya karena PRD tidak menentukan. Perlu dikonfirmasi (daftar di bagian 10B).
- **[OPEN]** = ambigu; tidak diputuskan di sini (bagian 10).

**Masukan dari pemilik (jawaban klarifikasi).**

| Topik | Jawaban |
|---|---|
| Kesan brand | Profesional dan tenang, seperti alat kerja yang rapi |
| Referensi visual | Tidak ada; diserahkan ke desainer |
| Warna utama | Hijau `#7F997E` |
| Perangkat dan konteks | Sering dipakai satu tangan, di luar ruangan |
| Aksesibilitas | Kontras minimal WCAG AA; AAA untuk teks penting (nominal dan tanggal) |

**Temuan penting soal warna utama.** `#7F997E` di atas putih hanya mencapai kontras **3,11:1**. Itu lolos untuk komponen UI besar tetapi **gagal AA untuk teks**, dan teks putih di atasnya juga gagal. Karena itu `#7F997E` dipakai sebagai **warna brand pada permukaan besar dengan isi gelap**, sedangkan teks hijau dan tombol berisi putih memakai turunan yang lebih gelap, `#2C452C`. Rinciannya di bagian 2.

---

## 1. Tujuan Desain dan Kesan yang Dibangun

### 1.1 Tujuan

Kontenin adalah alat kerja offline untuk WCC freelance [PRD]. Desain harus membantu tiga hal, sesuai masalah di PRD:

1. **Tidak ada job yang terlewat atau bentrok.** Tanggal, jam, dan peringatan bentrok adalah informasi paling menonjol.
2. **Detail job mudah ditemukan saat dibutuhkan** (alamat, shareloc, catatan, fee) tanpa menggali chat.
3. **Keuangan terbaca sekilas.** Sisa tagihan dan rekap tidak butuh perhitungan sendiri.

### 1.2 Konteks pemakaian

Dipakai satu tangan, sering di luar ruangan dengan layar terkena cahaya terang, kadang saat sedang sibuk bekerja. Konsekuensi untuk desain:

- Informasi penting harus **terbaca dalam sekali lirik** dan **tahan silau** (kontras tinggi, teks tidak tipis).
- Aksi utama harus **terjangkau ibu jari** (separuh bawah layar).
- Target sentuh **besar dan berjarak**, supaya tidak salah ketuk saat bergerak.

### 1.3 Kesan: profesional dan tenang, seperti alat kerja yang rapi

| Kesan | Artinya di tampilan |
|---|---|
| **Profesional** | Tata letak konsisten, tipografi bersih, angka rapi sejajar, tanpa dekorasi |
| **Tenang** | Dominan netral hangat dan hijau lembut. Kuning dan merah hanya muncul saat butuh perhatian |
| **Rapi** | Banyak ruang kosong, hierarki jelas, satu aksi utama per layar |
| **Dapat diandalkan** | Pesan jelas, status selalu berupa teks (bukan warna saja), tidak ada kejutan |

### 1.4 Prinsip desain

1. **Tanggal dan jam dulu.** Itu yang dicari pengguna paling sering.
2. **Satu aksi utama per layar**, diletakkan di zona jempol.
3. **Warna bermakna.** Hijau = identitas dan aksi; kuning = perlu perhatian (belum lunas); merah = error atau hapus. Tidak dipakai untuk dekorasi.
4. **Peringatan menginformasikan, tidak memaksa.** Bentrok tidak memblokir penyimpanan [PRD FR-1.8].
5. **Jangan hanya mengandalkan warna.** Setiap status punya teks dan/atau ikon.

### 1.5 Yang dihindari

Nuansa romantis berlebihan (ornamen, bunga, emas, script font), ilustrasi dekoratif, gradien, bayangan tebal, dan animasi yang tidak membawa informasi. Alasannya: aplikasi ini alat kerja dan harus terbaca jelas di luar ruangan.

---

## 2. Palet Warna

Semua pasangan di bawah sudah dihitung rasio kontrasnya (rumus WCAG 2.x). Dasar teknis: Material 3 dengan skema warna kustom. Material 3 sendiri masih asumsi yang belum disetujui di PRD (OQ-9); jika berubah, nilai hex di sini tetap bisa dipakai.

### 2.1 Mode terang (Light)

| Token | Hex | Dipakai untuk | Kontras |
|---|---|---|---|
| `brand` | `#7F997E` | Wadah FAB, ikon aplikasi, aksen dekoratif besar (pita header kosong state). **Bukan untuk teks, bukan untuk tombol berisi putih.** | 3,11:1 vs putih |
| `on-brand` | `#111A11` | Ikon/teks di atas `brand` (ikon plus di FAB) | 5,73:1 vs `brand` |
| `primary` | `#2C452C` | Tombol utama (isi), teks hijau/tautan, indikator tab, ikon aktif, cincin fokus | 10,53:1 vs putih |
| `on-primary` | `#FFFFFF` | Teks di atas `primary` | 10,53:1 |
| `primary-container` | `#E3EBE2` | Latar tombol tonal, filter chip terpilih, segmen terpilih, kartu ringkasan Rekap | |
| `on-primary-container` | `#2C452C` | Teks/ikon di atas `primary-container` | 8,65:1 |
| `background` | `#F6F7F5` | Latar halaman | |
| `surface` | `#FFFFFF` | Kartu, dialog, app bar, bottom nav, kolom input | |
| `surface-variant` | `#ECEFEA` | Latar kolom nonaktif, baris Per Sumber Job selang-seling, area pembeda | |
| `on-surface` | `#1A1D1A` | Teks utama: nama, **tanggal, jam, nominal** | 17,01:1 vs putih (AAA) |
| `on-surface-variant` | `#444B44` | Teks sekunder: label, caption, sumber job | 8,99:1 vs putih (AAA) |
| `outline` | `#6B746A` | Garis tepi kolom input dan chip tidak terpilih | 4,51:1 vs `background` (butuh ≥3) |
| `outline-variant` | `#D5DAD3` | Pemisah dekoratif dan garis kartu (tidak membawa informasi) | tidak dipersyaratkan |
| `warning-container` | `#FFF0CC` | Latar chip "Belum lunas" / "Kurang Rp X" | |
| `on-warning` | `#5C3A00` | Teks chip peringatan | 9,02:1 vs `warning-container` |
| `warning-accent` | `#9A5B00` | Garis tepi chip, ikon peringatan | 5,43:1 vs putih |
| `error` | `#B3261E` | Teks error kolom, garis tepi kolom error, tombol Hapus (isi) | 6,54:1 vs putih (AA) |
| `on-error` | `#FFFFFF` | Teks di atas tombol Hapus | 6,54:1 |
| `error-container` | `#FDE7E5` | Latar banner error | |
| `on-error-container` | `#7A1712` | Teks di atas `error-container` | 9,06:1 |
| `inverse-surface` | `#2A2F2A` | Latar snackbar | |
| `inverse-on-surface` | `#F1F4F0` | Teks snackbar | 12,31:1 |
| `inverse-primary` | `#B9D6B8` | Tombol aksi snackbar ("Coba lagi") | 8,69:1 |
| `scrim` | `#000000` @ 32% | Latar di belakang dialog | |

### 2.2 Mode gelap (Dark), mengikuti sistem [PRD]

| Token | Hex | Dipakai untuk | Kontras |
|---|---|---|---|
| `brand` | `#7F997E` | Wadah FAB | 6,02:1 vs `background` |
| `on-brand` | `#111A11` | Ikon di FAB | 5,73:1 |
| `primary` | `#A9C4A8` | Tombol utama, teks hijau, indikator, cincin fokus | 9,15:1 vs `surface` |
| `on-primary` | `#0F1A0F` | Teks di atas `primary` | 9,49:1 |
| `primary-container` | `#2A3B29` | Chip/segmen terpilih, kartu ringkasan | |
| `on-primary-container` | `#D5E6D3` | Teks di atasnya | 9,16:1 |
| `background` | `#101310` | Latar halaman | |
| `surface` | `#181C18` | Kartu, dialog, app bar, nav | |
| `surface-variant` | `#232823` | Latar kolom nonaktif, pembeda | |
| `on-surface` | `#E4E8E3` | Teks utama | 13,92:1 vs `surface` |
| `on-surface-variant` | `#B9C1B8` | Teks sekunder | 9,34:1 vs `surface` |
| `outline` | `#8A938A` | Garis tepi kolom dan chip | 5,44:1 vs `surface` |
| `outline-variant` | `#3A423A` | Pemisah dekoratif | |
| `warning-container` | `#4A3600` | Latar chip peringatan | |
| `on-warning` | `#FFDEA3` | Teks chip | 8,92:1 |
| `warning-accent` | `#F2B84B` | Garis tepi dan ikon peringatan | 6,45:1 vs `warning-container` |
| `error` | `#F2B8B5` | Teks/garis error, tombol Hapus | 10,10:1 vs `surface` |
| `on-error` | `#601410` | Teks di atas tombol Hapus | 7,66:1 |
| `error-container` | `#5E1A16` | Latar banner error | |
| `on-error-container` | `#FFD9D6` | Teks di atasnya | 9,87:1 |
| `inverse-surface` | `#E4E8E3` | Latar snackbar | |
| `inverse-on-surface` | `#1A1D1A` | Teks snackbar | 13,73:1 |
| `inverse-primary` | `#2C452C` | Aksi snackbar | 8,50:1 |

### 2.3 Aturan pemakaian warna

1. **Satu warna interaktif utama: hijau `primary`.** Tombol utama, tautan, tab aktif, ikon aktif.
2. **`brand` `#7F997E` hanya sebagai wadah besar dengan isi `on-brand`.** Jangan jadikan warna teks, garis tipis, atau latar tombol berisi putih (gagal kontras).
3. **Kuning hanya untuk status pembayaran belum lunas** [PRD FR-3.4: chip warna Warning]. Jangan dipakai untuk hal lain.
4. **Merah hanya untuk error validasi dan aksi Hapus.** Bentrok tidak dipakai merah penuh: dialog bentrok memakai ikon peringatan kuning dengan judul netral, karena tidak memblokir.
5. **Nominal, tanggal, dan jam selalu `on-surface`** (AAA). Tidak diwarnai hijau/kuning/merah.
6. **Disabled:** isi `on-surface` @ 38%, latar `on-surface` @ 12%. Komponen nonaktif dikecualikan dari persyaratan kontras WCAG, tetapi tetap harus terbaca sebagai nonaktif lewat bentuk (bukan hanya warna).
7. **Jangan pernah memakai warna sebagai satu-satunya pembeda** (lihat bagian 8).

---

## 3. Tipografi

**Font: Inter** (lisensi terbuka, di-bundle di aplikasi), cadangan Roboto/sistem. Alasan: huruf tegas dan terbaca di bawah silau, mendukung **angka tabular** (`tnum`) sehingga kolom rupiah sejajar. Semua nominal, tanggal, dan jam memakai `fontFeatureSettings = "tnum"`. Jika pemilik lebih suka font sistem (tanpa bundling), lihat D-8.

Satuan `sp` (ikut ukuran font sistem). Bobot minimum **Regular 400**; tidak memakai Light/Thin karena memburuk saat silau.

| Peran | Ukuran / Tinggi baris | Bobot | Dipakai untuk |
|---|---|---|---|
| Judul layar | 22 / 28 sp | SemiBold 600 | Judul top app bar |
| Angka besar | 28 / 36 sp | Bold 700, tabular | Total di kartu ringkasan Rekap, total piutang |
| Heading seksi | 18 / 24 sp | SemiBold 600 | "Pembayaran", "Per Sumber Job", "Piutang", judul dialog |
| Tanggal & jam (kartu dan detail) | 20 / 28 sp | SemiBold 600, tabular | Baris tanggal dan rentang jam, titik pandang pertama |
| Judul kartu | 16 / 24 sp | SemiBold 600 | Nama pasangan ("Andi & Sari") |
| Body | 16 / 24 sp | Regular 400 | Isi, nilai kolom, teks dialog, alamat, catatan |
| Nominal | 16 / 24 sp | SemiBold 600, tabular | Fee, dibayar, sisa |
| Body kecil | 14 / 20 sp | Regular 400 | Info sekunder (acara, sumber job di kartu), teks bantu |
| Caption | 13 / 18 sp | Medium 500 | Label kolom kecil, penghitung karakter, teks chip |
| Label tombol | 16 / 24 sp | Medium 500 | Tombol, tab, label nav |

Aturan:
- **Tidak ada teks di bawah 13 sp.** Info yang penting tidak boleh di caption.
- Dukung skala font sistem sampai **200%**. Layout harus menyesuaikan (tinggi baris fleksibel, teks wrap, bukan terpotong). Tombol tidak memakai tinggi tetap yang memotong label.
- Teks rata kiri. Nominal di kolom ringkasan rata kanan agar sejajar.
- Format [PRD]: tanggal `dd MMM yyyy` (18 Okt 2026), jam `HH.mm` (10.00), rentang `10.00 - 14.00`, rupiah locale id-ID (`Rp 3.500.000`).
- Judul pasangan ditampilkan "Andi & Sari" [PRD FR-1.4]. Batas panjang 50 + 50 karakter: maksimal 2 baris di kartu, lalu elipsis; di Detail ditampilkan penuh.

---

## 4. Spacing, Grid, dan Radius Sudut

### 4.1 Skala spacing (basis 4 dp)

| Token | dp | Dipakai untuk |
|---|---|---|
| `space-1` | 4 | Jarak ikon-teks rapat |
| `space-2` | 8 | **Jarak minimum antar target sentuh**; jarak chip |
| `space-3` | 12 | Padding dalam chip dan baris |
| `space-4` | 16 | **Margin layar**, padding kartu, jarak antar kolom form |
| `space-5` | 24 | Jarak antar seksi |
| `space-6` | 32 | Jarak besar (di atas tombol aksi akhir) |

### 4.2 Grid

- Target: **HP Android potret** (satu orang satu perangkat [PRD]). Lebar kompak (< 600 dp): **4 kolom, margin 16 dp, gutter 16 dp.** Konten satu kolom.
- Lebar konten maksimal **600 dp**, rata tengah, jika layar lebih lebar. Tidak ada tata letak khusus tablet/landscape karena tidak ada di PRD (D-9).
- Daftar kartu: satu kolom penuh lebar, jarak antar kartu 12 dp.
- **Zona jempol:** aksi utama selalu di 40% bawah layar (FAB, tombol Simpan, bottom bar). Elemen yang hanya dibaca (tanggal, total) boleh di atas.

### 4.3 Radius sudut

| Token | dp | Dipakai untuk |
|---|---|---|
| `radius-sm` | 8 | Chip, snackbar, item dropdown autocomplete |
| `radius-md` | 12 | **Tombol, kolom input, kartu, segmented button** |
| `radius-lg` | 16 | FAB, kartu ringkasan Rekap |
| `radius-xl` | 24 | Dialog |
| `radius-full` | 999 | Indikator nav, indikator tab |

Radius sedang (12 dp) sengaja dipilih, bukan pil penuh, agar terkesan rapi dan serius.

### 4.4 Elevasi

Datar. Pemisahan lewat warna permukaan dan garis tipis `outline-variant` 1 dp, bukan bayangan. Satu-satunya elevasi: FAB (2 dp) dan dialog (scrim + 3 dp).

---

## 5. Komponen UI dan State

Hanya komponen yang dibutuhkan fitur di PRD. Basis: pola Material 3.

### 5.1 Aturan state umum

| State | Perilaku |
|---|---|
| **Default** | Sesuai tabel masing-masing |
| **Hover** | Hanya berlaku bila ada penunjuk (mouse, stylus, ChromeOS). Di layar sentuh tidak muncul. Tambahkan lapisan `on-surface` 8% |
| **Pressed** | Pengganti hover di layar sentuh: ripple + lapisan 12%. Wajib ada |
| **Focus** | Cincin 2 dp `primary`, jarak 2 dp (kontras 9,8:1 vs `background`). Berlaku untuk keyboard/switch access/TalkBack |
| **Disabled** | Isi 38%, latar 12%. Tanpa ripple. Tidak menghilangkan aksi; jika alasan nonaktif tidak jelas, beri teks bantu |
| **Error** | Garis tepi + label + teks bantu + ikon `error`. Selalu ada teks, bukan hanya warna |

### 5.2 Daftar komponen dan state

| Komponen | Default | Hover | Disabled | Error |
|---|---|---|---|---|
| **Tombol utama** (isi `primary`, teks putih; tinggi 56 dp, penuh lebar di form/dialog) | Isi `primary` | Lapisan putih 8% | Isi `on-surface` 12%, teks 38% | Tidak berlaku. Gagal aksi ditampilkan lewat snackbar |
| **Tombol tonal** (isi `primary-container`; 48-56 dp): Lunas, Tandai Lunas, Ubah Pembayaran, Buka Maps | Isi `primary-container`, teks `on-primary-container` | Lapisan 8% | Isi 12%, teks 38% | Tidak berlaku |
| **Tombol outline** (garis `outline`, teks `primary`; 48 dp): Duplikat, Kembali, Ubah | Latar transparan | Lapisan `primary` 8% | Garis dan teks 38% | Tidak berlaku |
| **Tombol teks**: Batal, tautan | Teks `primary` | Lapisan 8% | Teks 38% | Tidak berlaku |
| **Tombol destruktif** (Hapus): isi `error` + teks `on-error` (di dialog); outline `error` + teks `error` (di halaman) | Seperti kiri | Lapisan 8% | 38% | Tidak berlaku |
| **Kolom teks** (tinggi 56 dp, label di atas, garis `outline` 1 dp; fokus 2 dp `primary`) | Label `on-surface-variant`, isi `on-surface` | Garis menebal ke `on-surface` | Latar `surface-variant`, isi 38% | Garis dan label `error` 2 dp, ikon `!`, teks bantu `error` di bawah |
| **Kolom uang** (awalan "Rp", pemisah ribuan saat mengetik, keyboard numerik, tabular) | Seperti kolom teks | Seperti kolom teks | Seperti kolom teks | Pesan dari validasi PRD (mis. dibayar > fee) |
| **Kolom tanggal dan jam** (kolom hanya-baca, ketuk membuka picker sistem) | Teks bantu "Pilih tanggal" | Seperti kolom teks | Tidak dipakai | "Pilih tanggal acara" bila kosong saat Simpan |
| **Autocomplete** (kolom teks + daftar maks 5 saran, item 48 dp, `radius-sm`) | Daftar muncul saat fokus/mengetik; tetap teks bebas | Item: lapisan 8% | Mengikuti kolom | Mengikuti kolom. Tidak ada saran = daftar tidak muncul |
| **Kartu job** (permukaan `surface`, garis `outline-variant`, `radius-md`, padding 16, seluruh kartu dapat diketuk ≥ 48 dp) | Lihat 6.1 | Lapisan 8% | Tidak berlaku | Tidak berlaku |
| **Chip pembayaran** (status, tidak interaktif; tinggi 28 dp; `warning-container` + garis `warning-accent` + ikon + teks) | "Belum lunas" atau "Kurang Rp X"; hilang jika lunas | Tidak berlaku | Tidak berlaku | Tidak berlaku |
| **Filter chip** ("Belum lunas"; tinggi 48 dp area sentuh) | Latar `surface`, garis `outline`, teks `on-surface` | Lapisan 8% | Tidak dipakai | Tidak berlaku |
| &nbsp;&nbsp;↳ terpilih | `primary-container` + garis `primary` + ikon centang + teks | Lapisan 8% | | |
| **Tab** (Mendatang, Riwayat; 48 dp) | Teks `on-surface-variant` | Lapisan 8% | Tidak dipakai | Tidak berlaku |
| &nbsp;&nbsp;↳ terpilih | Teks `primary` SemiBold + indikator 3 dp `primary` | | | |
| **Segmented button** (Hari Ini / Minggu Ini / Bulan Ini; 48 dp) | Garis `outline`, teks `on-surface` | Lapisan 8% | Tidak dipakai | Tidak berlaku |
| &nbsp;&nbsp;↳ terpilih | `primary-container` + ikon centang + teks `on-primary-container` | | | |
| **FAB tambah** (56 dp, `radius-lg`, isi `brand`, ikon plus `on-brand`) | Pojok kanan bawah, 16 dp dari tepi, di atas bottom nav | Lapisan 8% | Tidak dipakai | Tidak berlaku |
| **Top app bar** (64 dp, `surface`, judul 22 sp; tombol kembali/tutup 48 dp) | Judul rata kiri | Lapisan pada tombol 8% | Tidak berlaku | Tidak berlaku |
| **Bottom navigation** (80 dp, 3 item, label selalu terlihat, indikator `primary-container`) [USULAN] | Ikon + label `on-surface-variant` | Lapisan 8% | Tidak berlaku | Tidak berlaku |
| &nbsp;&nbsp;↳ terpilih | Ikon `on-primary-container` di indikator pil, label `primary` SemiBold | | | |
| **Bottom action bar** (halaman Detail; 80 dp, 3 aksi ikon + label) [USULAN] | Ubah, Duplikat (`primary`); Hapus (`error`) | Lapisan 8% | Tidak berlaku | Tidak berlaku |
| **Dialog** (`surface`, `radius-xl`, padding 24, scrim 32%; tombol di bawah, tersusun vertikal bila label panjang) | Judul 18 sp, isi 16 sp | Pada tombol | Tombol utama 38% saat memproses | Pesan error di dalam dialog (mis. Ubah Pembayaran) |
| **Snackbar** (`inverse-surface`, `radius-sm`, di atas bottom nav, aksi ≥ 48 dp) | Teks + aksi opsional "Coba lagi" | Lapisan pada aksi | Tidak berlaku | Dipakai untuk kegagalan (lihat 7.3) |
| **Banner izin** (di Pengaturan; ikon + teks + tombol) | Warna `warning-container` (izin belum aktif) | Pada tombol | Tidak berlaku | Warna `error-container` bila gagal total (tidak ada skenario ini di PRD; tidak dipakai) |
| **Baris Pengaturan** (min 56 dp; ikon, judul, status teks, aksi) | Status berupa teks "Aktif" / "Belum aktif" + ikon | Lapisan 8% | Tidak berlaku | Tidak berlaku |
| **Baris ringkasan** (Rekap: label kiri, nilai kanan, tabular) | Label `on-surface-variant`, nilai `on-surface` | Tidak berlaku | Tidak berlaku | Tidak berlaku |

### 5.3 Catatan khusus komponen

- **Tombol "Lunas"** di Form [PRD FR-3.2] adalah tombol tonal di bawah kolom "Sudah dibayar". Perilaku saat fee kosong **belum ditentukan** (OQ-8): tampilan nonaktif atau aktif tidak diputuskan di brief ini.
- **Pembayaran sebagian:** hanya ada satu angka "dibayar" [PRD]. Tidak ada riwayat transaksi, sehingga tidak ada komponen daftar pembayaran.
- **Penghitung karakter** ditampilkan hanya pada kolom Alamat (300) dan Catatan (1000), muncul saat sisa < 20% [USULAN].
- **Kolom durasi:** jam dengan koma/titik, langkah 0,5, awal "4" [PRD, asumsi OQ-9]. Satuan "jam" sebagai akhiran.

---

## 6. Layout Tiap Halaman Utama

Halaman di PRD: **Beranda, Form Job, Detail Job, Rekap, Pengaturan.** Piutang dan Per Sumber Job adalah bagian dari Rekap [PRD FR-5]; petunjuk baterai bagian dari Pengaturan [PRD FR-6]. Tidak dibuat halaman terpisah untuk keduanya (penempatan perlu dikonfirmasi, D-2 dan D-4).

**Navigasi utama [USULAN]:** bottom navigation 3 item (Beranda, Rekap, Pengaturan) agar terjangkau satu tangan. PRD hanya menyebut "navigasi" di Tahap 1 tanpa bentuk (D-1). Form Job dan Detail Job adalah layar turunan (tanpa bottom nav).

Semua wireframe di bawah berskala kasar untuk lebar 360 dp.

### 6.1 Beranda [PRD FR-1.3, 1.4]

```
┌──────────────────────────────────────┐
│ Kontenin                             │  Top app bar
├──────────────────────────────────────┤
│   Mendatang          Riwayat         │  Tab 48 dp
│   ━━━━━━━━━━                         │
│  [✓ Belum lunas]                     │  Filter chip
│ ┌──────────────────────────────────┐ │
│ │ 18 Okt 2026 · 10.00 - 14.00      │ │  20 sp, tabular
│ │ Andi & Sari                      │ │  16 sp SemiBold
│ │ Resepsi · Vendor A               │ │  14 sp sekunder
│ │ Rp 3.500.000                     │ │  nominal
│ │ [⚠ Kurang Rp 1.500.000]          │ │  chip kuning
│ └──────────────────────────────────┘ │
│ ┌──────────────────────────────────┐ │
│ │ 24 Okt 2026 · 08.00 - 12.00      │ │
│ │ Budi & Rina                      │ │
│ │ Akad · Pribadi                   │ │
│ │ Rp 2.000.000                     │ │  lunas: tanpa chip
│ └──────────────────────────────────┘ │
│                              ( + )   │  FAB 56 dp
├──────────────────────────────────────┤
│  Beranda     Rekap     Pengaturan    │  Bottom nav
└──────────────────────────────────────┘
```

- Tab **Mendatang** urut terdekat; tab **Riwayat** urut terbaru [PRD].
- Filter chip "Belum lunas" berlaku untuk tab aktif [PRD].
- Isi kartu (urutan visual) [USULAN, field dari PRD FR-1.4]: tanggal + rentang jam, nama pasangan, acara dan sumber job, fee, chip pembayaran. Tanggal dan jam di atas karena itulah yang dipindai pengguna.
- Seluruh kartu dapat diketuk, membuka Detail Job. Tidak ada tombol di dalam kartu (menghindari salah ketuk).
- Padding bawah daftar cukup agar kartu terakhir tidak tertutup FAB.
- Rentang jam lintas tengah malam (22.00 - 02.00): format belum ditentukan (OQ-7).

### 6.2 Form Job: tambah, ubah, duplikat [PRD FR-1.1, 1.2, 1.7, 3.2]

```
┌──────────────────────────────────────┐
│ ✕  Tambah Job                        │  Top app bar (tutup)
├──────────────────────────────────────┤
│ Pasangan                             │
│ [ Nama pengantin 1                 ] │
│ [ Nama pengantin 2                 ] │
│                                      │
│ Waktu                                │
│ [ Tanggal                      ▾  ]  │
│ [ Jam mulai                    ▾  ]  │
│ [ Durasi                    jam   ]  │
│                                      │
│ Acara dan sumber                     │
│ [ Acara                            ] │  autocomplete
│ [ Sumber job                       ] │  autocomplete
│                                      │
│ Lokasi                               │
│ [ Alamat (opsional)                ] │
│ [ Shareloc (opsional)              ] │
│                                      │
│ Pembayaran                           │
│ [ Fee                    Rp        ] │
│ [ Sudah dibayar          Rp        ] │
│ [ Lunas ]                            │  tonal
│                                      │
│ Catatan                              │
│ [ Catatan (opsional)               ] │
│                                      │
├──────────────────────────────────────┤
│ [            Simpan                ] │  sticky, 56 dp
└──────────────────────────────────────┘
```

- Satu kolom, label di atas kolom, urutan sesuai alur pikir pengguna (siapa → kapan → apa → di mana → berapa → catatan). Pengelompokan dengan heading seksi 18 sp adalah [USULAN].
- **Tombol Simpan menempel di bawah** (zona jempol), selalu terlihat saat form dengan keyboard terbuka.
- **Mode ubah:** judul "Ubah Job", kolom terisi. **Mode duplikat:** judul "Tambah Job" (PRD: mode tambah); kolom tersalin kecuali tanggal, acara, dan dibayar yang kosong/0 [PRD FR-1.7]. Penanda bahwa form hasil duplikasi tidak ada di PRD (D-6).
- **Validasi** [PRD FR-1.2]: pesan Bahasa Indonesia di bawah kolom; Simpan tidak menyimpan jika tidak valid. Waktu munculnya error [USULAN]: saat kolom ditinggalkan bila sudah diisi tapi salah, dan semua sekaligus saat Simpan, lalu gulir ke kolom pertama yang bermasalah.
- Fee diturunkan di bawah dibayar: error di kolom **dibayar** [PRD EC-7].
- Autocomplete: maks 5 saran, urut paling sering dipakai, tetap bisa mengetik bebas [PRD FR-2].
- Saat Simpan memproses: tombol menampilkan indikator kecil dan nonaktif (cegah ketuk ganda).

**Dialog peringatan bentrok** [PRD FR-1.8]:

```
┌────────────────────────────────┐
│ ⚠  Jadwal bertabrakan          │
│                                │
│ Job ini tumpang tindih dengan: │
│ ┌────────────────────────────┐ │
│ │ Budi & Rina                │ │
│ │ Akad · 08.00 - 12.00       │ │
│ └────────────────────────────┘ │
│                                │
│ [         Kembali            ] │  utama (isi)
│ [       Tetap Simpan         ] │  outline
└────────────────────────────────┘
```

- Daftar berisi nama, acara, rentang jam [PRD]. Judul dan kalimat pengantar adalah [USULAN copy].
- **Kembali** diberi bobot visual utama dan diletakkan di atas; **Tetap Simpan** tetap mudah dijangkau tetapi tidak menjadi default. Alasan: bentrok adalah masalah terukur terbesar pemilik (PRD bagian 1), tetapi peringatan tidak boleh terasa memblokir (D-7).
- Daftar bentrok belum menyertakan tanggal karena PRD tidak menyebutnya; kandidat dicek ±1 hari sehingga ini bisa membingungkan (D-5).

### 6.3 Detail Job [PRD FR-1.5, 1.6, 3.3]

```
┌──────────────────────────────────────┐
│ ←  Detail Job                        │
├──────────────────────────────────────┤
│ Andi & Sari                          │  22 sp
│ Resepsi · Vendor A                   │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ 18 Okt 2026                      │ │  20 sp, kartu `primary-container`
│ │ 10.00 - 14.00                    │ │
│ └──────────────────────────────────┘ │
│                                      │
│ Lokasi                               │
│ Gedung X, Jl. Mawar No. 1            │
│ [ ↗ Buka Maps ]                      │  tonal; tersembunyi bila shareloc kosong
│                                      │
│ Pembayaran                           │
│ Fee               Rp 3.500.000       │
│ Dibayar           Rp 2.000.000       │
│ Sisa              Rp 1.500.000       │
│ [⚠ Kurang Rp 1.500.000]              │
│ [ Tandai Lunas ]  [ Ubah Pembayaran ]│  tersembunyi bila lunas (Tandai)
│                                      │
│ Catatan                              │
│ Dress code putih. PIC: Mas Dimas ... │
│                                      │
├──────────────────────────────────────┤
│  ✎ Ubah     ⧉ Duplikat    🗑 Hapus   │  bottom action bar
└──────────────────────────────────────┘
```

- Semua isi ditampilkan, termasuk catatan [PRD]. Jam sebagai rentang yang dihitung otomatis [PRD].
- **Buka Maps:** tersembunyi bila shareloc kosong [PRD]. Bila tidak ada aplikasi pembuka, snackbar "Tidak ada aplikasi untuk membuka link ini" [PRD].
- **Tandai Lunas** hanya tampil bila belum lunas [PRD]. **Ubah Pembayaran** membuka dialog satu kolom (validasi sama dengan form) [PRD].
- Hapus selalu lewat dialog konfirmasi [PRD FR-1.9]; tidak ada undo [PRD EC-24], jadi dialog harus menyebut bahwa data tidak bisa dikembalikan.
- Letak aksi Ubah, Duplikat, Hapus di bottom bar adalah [USULAN] (PRD tidak menentukan, D-3). Hapus dipisahkan warna dan jarak ≥ 8 dp dari aksi lain.
- Kontak PIC: PRD menyebutnya sebagai masalah (bagian 1), tetapi tidak ada field khusus. Tidak ditampilkan terpisah; bila ada, tersimpan di Catatan (D-10).

### 6.4 Rekap [PRD FR-5]

```
┌──────────────────────────────────────┐
│ Rekap                                │
├──────────────────────────────────────┤
│ [Hari Ini][✓Minggu Ini][Bulan Ini]   │  segmented, 48 dp
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ Jumlah job              4        │ │  kartu `primary-container`
│ │ Total fee      Rp 12.000.000     │ │
│ │ Total dibayar  Rp 10.500.000     │ │
│ │ Total sisa      Rp 1.500.000     │ │  28 sp bold
│ └──────────────────────────────────┘ │
│                                      │
│ Per Sumber Job                       │
│  Vendor A   3 job  Rp 9jt   sisa Rp 1,5jt │
│  Pribadi    1 job  Rp 3jt   sisa Rp 0     │
│                                      │
│ Daftar Job                           │
│  [kartu job ringkas, ketuk → Detail] │
│                                      │
│ ──────────────────────────────────── │
│ Piutang (semua periode)              │
│ Total sisa  Rp 1.500.000 · 1 job     │
│  [kartu job, terlama dulu]           │
├──────────────────────────────────────┤
│  Beranda     Rekap     Pengaturan    │
└──────────────────────────────────────┘
```

- Periode dipilih dengan segmented button; **dihitung dari tanggal acara** [PRD 5.1].
- Isi: jumlah job, total fee, total dibayar, total sisa, daftar job, Per Sumber Job (jumlah job, total fee, total sisa) [PRD 5.2]. Urutan seksi di layar adalah [USULAN] (D-2).
- **Piutang tidak dipengaruhi pilihan periode** [PRD 5.3]. Karena itu dipisahkan dengan pembatas dan heading eksplisit "(semua periode)" agar tidak disangka mengikuti segmented button.
- Daftar job di Rekap memakai kembali **kartu job yang sama** (varian padat tanpa sumber job), bukan komponen baru.
- Semua nominal ditulis penuh (`Rp 1.500.000`) di kartu dan baris; singkatan "jt" di wireframe hanya karena keterbatasan lebar gambar. Jangan menyingkat nominal di aplikasi (keterbacaan dan aksesibilitas).
- Definisi "acara sudah lewat" untuk piutang dan ambang hari belum ditetapkan (OQ-5).

### 6.5 Pengaturan [PRD FR-6, FR-7]

```
┌──────────────────────────────────────┐
│ Pengaturan                           │
├──────────────────────────────────────┤
│ ┌──────────────────────────────────┐ │
│ │ ⚠ Izin notifikasi belum aktif    │ │  banner (hanya bila bermasalah)
│ │   Pengingat tidak akan tampil.   │ │
│ │   [ Buka Pengaturan ]            │ │
│ └──────────────────────────────────┘ │
│                                      │
│ Izin                                 │
│  Notifikasi             ✓ Aktif      │
│  Alarm tepat waktu      ⚠ Belum aktif│  [Buka Pengaturan]
│                                      │
│ Pengingat                            │
│  Petunjuk baterai dan autostart   >  │
│  [ Uji Pengingat ]                   │  tonal
│                                      │
│ Data                                 │
│  [ Ekspor Data ]                     │
│  [ Impor Data ]                      │
│                                      │
│ Tentang                              │
│  Versi aplikasi            0.1       │
├──────────────────────────────────────┤
│  Beranda     Rekap     Pengaturan    │
└──────────────────────────────────────┘
```

- Status izin selalu berupa **teks + ikon** ("Aktif" / "Belum aktif"), tidak hanya warna [PRD 6.1].
- Banner muncul hanya bila notifikasi ditolak atau alarm tepat waktu belum diberikan [PRD FR-4.9, EC-9, EC-10]. Tidak menutupi Pengaturan; tidak ada banner di Beranda (tidak ada di PRD).
- Petunjuk baterai/autostart: bentuk (halaman, bagian yang membuka, atau dialog) dan isi per merk belum ditentukan di PRD (D-4).
- **Uji Pengingat** tersedia di semua build [PRD 6.3].
- **Impor** memakai dialog konfirmasi: "Semua data saat ini akan diganti dengan isi file (N job)." [PRD 7.2], dengan tombol utama bernada hati-hati (label "Ganti Data" [USULAN copy]) dan **Batal**.
- Dialog izin notifikasi: sebelum permintaan sistem muncul pertama kali saat menyimpan job, tampil penjelasan singkat [PRD FR-4.9]; teks di 7.3.

---

## 7. Empty State, Loading, dan Pesan Error

Aplikasi sepenuhnya offline dan data lokal, sehingga loading sangat singkat dan tidak ada error jaringan.

### 7.1 Empty state

Format: ikon garis sederhana 48 dp warna `on-surface-variant` + satu kalimat judul (16 sp SemiBold) + satu kalimat penjelas bila perlu (14 sp). Tanpa ilustrasi dekoratif. Penjelasan yang diberi aksi memakai tombol tonal di zona tengah-bawah.

| Konteks | Teks | Sumber |
|---|---|---|
| Rekap, periode tanpa job | "Belum ada job pada periode ini" + semua total **Rp 0** | [PRD 5.4] |
| Rekap, piutang kosong | "Tidak ada fee yang perlu ditagih" + total **Rp 0** | [PRD 5.4] |
| Beranda, tab Mendatang kosong | "Belum ada job mendatang" + tombol "Tambah Job" | [USULAN copy] |
| Beranda, tab Riwayat kosong | "Belum ada riwayat job" | [USULAN copy] |
| Beranda, filter "Belum lunas" tanpa hasil | "Semua job di tab ini sudah lunas" | [USULAN copy] |
| Per Sumber Job kosong | Seksi disembunyikan bila periode kosong (mengikuti "Belum ada job...") | [USULAN] |

### 7.2 Loading

| Situasi | Perilaku | Sumber |
|---|---|---|
| Membuka Beranda/Rekap/Detail (Room, lokal) | Tampil langsung. Jika > 300 ms, tampilkan 3 kartu kerangka (skeleton) statis, tanpa animasi berkedip | [USULAN] |
| Menyimpan job | Tombol Simpan: indikator kecil + nonaktif sampai selesai | [USULAN] |
| Ekspor/Impor data | Dialog tak dapat dibatalkan dengan indikator bergaris dan teks "Memproses..." hanya jika > 500 ms; tombol Ekspor/Impor nonaktif selama proses | [USULAN] |
| Uji Pengingat | Snackbar "Notifikasi uji akan muncul beberapa detik lagi" | [USULAN copy] |

Tidak ada pull-to-refresh (data lokal, tidak ada sinkronisasi).

### 7.3 Pesan dan error

Gaya bahasa: Bahasa Indonesia, kalimat singkat, tenang, menyebut apa yang terjadi dan apa langkah berikutnya. Tidak menyalahkan pengguna. Tidak ada kode teknis dan tidak ada exception mentah di UI [PRD NFR].

**Sistem (dari PRD):**

| Situasi | Tampilan | Teks |
|---|---|---|
| Tidak ada aplikasi pembuka link | Snackbar | "Tidak ada aplikasi untuk membuka link ini" |
| Shareloc tidak bisa dibuka (EC-21) | Pesan error; tombol disembunyikan jika kosong | Teks belum ditetapkan di PRD (D-11) |
| Notifikasi diketuk, job sudah dihapus (EC-12) | Layar/ snackbar lalu ke Beranda | "Job tidak ditemukan" |
| Impor berhasil | Snackbar | "Impor berhasil: N job" |
| File cadangan salah/rusak/skema lebih baru (EC-16) | Dialog atau snackbar; data lama tidak berubah | "File cadangan tidak valid" |
| Ekspor gagal (EC-18) | Snackbar + "Coba lagi" | "Gagal mengekspor data" |
| Simpan ke database gagal (EC-19) | Snackbar + "Coba lagi" | "Gagal menyimpan job" [USULAN copy] |
| Konfirmasi impor | Dialog | "Semua data saat ini akan diganti dengan isi file (N job)." |

**Validasi kolom** [PRD FR-1.2 menentukan aturan; teks berikut [USULAN copy]]:

| Kolom | Pesan |
|---|---|
| Nama pengantin 1 / 2 (kosong) | "Nama pengantin 1 wajib diisi" / "Nama pengantin 2 wajib diisi" |
| Nama > 50 karakter | "Maksimal 50 karakter" |
| Tanggal | "Pilih tanggal acara" |
| Jam mulai | "Pilih jam mulai" |
| Durasi | "Isi durasi 0,5 sampai 24 jam, kelipatan 0,5" |
| Acara / Sumber job | "Acara wajib diisi" / "Sumber job wajib diisi" |
| Alamat > 300 | "Maksimal 300 karakter" |
| Shareloc | "Link harus diawali http:// atau https://" |
| Fee | "Fee wajib diisi" |
| Dibayar > fee | "Tidak boleh lebih dari fee" |
| Catatan > 1000 | "Maksimal 1000 karakter" |

**Dialog hapus** [PRD FR-1.9]: judul "Hapus job ini?", isi "Job Andi & Sari dan 4 pengingatnya akan dihapus. Tindakan ini tidak bisa dibatalkan." (tidak ada undo, PRD EC-24). Tombol: **Batal** (utama), **Hapus** (destruktif). [USULAN copy]

**Penjelasan izin notifikasi** [PRD FR-4.9, USULAN copy]: judul "Izinkan notifikasi", isi "Kontenin memakai notifikasi untuk mengingatkan jadwal job Anda sebelum acara." Tombol: **Lanjut** (utama), **Nanti**.

**Peringatan pengingat yang dilewati** (EC-1, EC-2): PRD belum memutuskan apakah pengguna diberi tahu (OQ-6). Tidak ada pesan yang dirancang sampai diputuskan.

---

## 8. Aturan Aksesibilitas

Target pemilik: **WCAG 2.2 AA minimum; AAA untuk teks penting (nominal, tanggal, jam)**; target sentuh ≥ 48 dp [PRD]. TalkBack belum diputuskan di PRD (OQ-19); aturan di bawah mengusulkan pemenuhannya sebagai baseline desain.

### 8.1 Kontras

| Elemen | Persyaratan | Cara dipenuhi |
|---|---|---|
| **Nominal, tanggal, jam, rentang jam** | **AAA (≥ 7:1)** | Selalu `on-surface` (17:1 terang, 13,9:1 gelap); tidak berwarna |
| Teks sekunder (label, sumber job, caption) | AAA dicapai | `on-surface-variant` 8,99:1 terang, 9,34:1 gelap |
| Teks hijau/tautan, tombol utama | AA, dicapai AAA | `primary` 10,53:1 / 9,15:1 |
| Teks error | AA minimal | 6,54:1 terang (AA), 10,10:1 gelap. Di luar teks penting, jadi AA cukup |
| Isi di atas `brand` (ikon FAB) | AA komponen UI | `on-brand` 5,73:1 |
| Garis tepi kolom, chip, tombol outline | ≥ 3:1 | `outline` 4,51:1 terang |
| Indikator tab, centang, fokus | ≥ 3:1 vs sekitarnya | `primary` ≥ 8,6:1 |
| Disabled | Dikecualikan WCAG | Tetap dibedakan lewat bentuk: tidak ada ripple, label tetap terbaca |
| Mode gelap | Sama dengan mode terang | Nilai tertera di 2.2 |

Dilarang: teks `#7F997E` di atas putih/`background` (2,89–3,11:1, gagal). Teks putih di atas `#7F997E` (3,11:1, gagal).

### 8.2 Ukuran target sentuh dan jarak

- **Minimum 48 × 48 dp** untuk semua target interaktif [PRD].
- **Rekomendasi 56 dp** untuk aksi utama: tombol Simpan, tombol dialog utama, kolom input, FAB. Alasan: dipakai satu tangan, sambil bergerak, di luar ruangan.
- **Jarak antar target ≥ 8 dp.** Antara aksi destruktif dan aksi lain ≥ 16 dp bila berdampingan secara vertikal.
- Area ketuk boleh lebih besar dari tampilan visual (padding tak terlihat) bila ikon kecil.
- Chip pembayaran bersifat informasi, tidak interaktif. Jika filter chip 32 dp visual, area sentuh tetap 48 dp.
- Aksi utama di zona jempol (40% bawah); tidak ada aksi penting hanya di pojok kiri atas.

### 8.3 Tidak hanya warna

- Status bayar selalu **teks** ("Belum lunas", "Kurang Rp X") + ikon; bukan hanya latar kuning.
- Error kolom: ikon `!` + teks bantu + garis menebal; bukan hanya merah.
- Tab/segmen terpilih: centang atau indikator + bobot huruf, bukan hanya warna.
- Status izin di Pengaturan: ikon centang/peringatan + kata "Aktif"/"Belum aktif".

### 8.4 Teks dan layout adaptif

- Teks mengikuti skala font sistem hingga **200%**; tidak ada teks terpotong atau tertimpa; tombol tumbuh tingginya.
- Lebar tombol di dialog: penuh, tersusun vertikal bila label panjang.
- Bobot teks minimum Regular. Tidak ada teks abu-abu muda atau tipis.
- Layar potret dikunci tidak diwajibkan [PRD diam]; layout tetap tidak rusak saat diputar (konten dibatasi 600 dp).

### 8.5 TalkBack dan semantik [USULAN, menunggu OQ-19]

- Setiap kartu job dibacakan sebagai satu kesatuan, urut: "[tanggal], [jam], [nama], [acara], [sumber], fee [nominal] rupiah, [status bayar]". Aksi kartu: "ketuk dua kali untuk membuka detail".
- Nominal dibacakan sebagai angka rupiah ("tiga juta lima ratus ribu rupiah"), bukan karakter per karakter. Awalan "Rp" tidak dibaca "R P".
- FAB: label "Tambah job". Tombol ikon di top bar punya `contentDescription`: "Kembali", "Tutup".
- Chip filter dan segmen mengumumkan status "dipilih/tidak dipilih".
- Kolom error mengumumkan pesan error saat fokus.
- Dialog memindahkan fokus ke judul dan mengembalikan fokus ke pemicu saat ditutup.
- Urutan fokus mengikuti urutan visual; bottom bar dan FAB terakhir.

### 8.6 Pengurangan gerak

Animasi hanya transisi bawaan Material yang singkat (< 200 ms). Hormati pengaturan "Hapus animasi" sistem. Tidak ada animasi dekoratif atau berkedip.

---

## 9. Do & Don't

| Do | Don't |
|---|---|
| Tampilkan **tanggal dan jam** paling menonjol di kartu dan detail | Jangan menyembunyikan tanggal/jam di caption kecil |
| Pakai **hijau `#2C452C`** untuk teks dan tombol berisi putih | Jangan memakai `#7F997E` sebagai warna teks atau latar tombol berisi putih (kontras 3,11:1) |
| Pakai `#7F997E` sebagai wadah besar (FAB) dengan ikon `#111A11` | Jangan mengecilkan `#7F997E` jadi garis/teks tipis |
| Tulis nominal **penuh** (`Rp 1.500.000`) dengan angka tabular | Jangan menyingkat ("1,5jt") atau memakai angka proporsional yang tidak sejajar |
| Letakkan aksi utama di **40% bawah** layar | Jangan menaruh aksi penting di pojok kiri atas |
| Beri target sentuh **≥ 48 dp**, aksi utama 56 dp, jarak ≥ 8 dp | Jangan menumpuk dua tombol tanpa jarak |
| Gunakan kuning **hanya** untuk status belum lunas | Jangan memakai kuning/merah sebagai dekorasi |
| Tulis status dengan **teks + ikon** | Jangan membedakan status dengan warna saja |
| Tampilkan peringatan bentrok sebagai dialog yang jelas **tanpa memblokir** [PRD] | Jangan memblokir Simpan atau memakai merah penuh untuk bentrok |
| Konfirmasi Hapus dan Impor dengan teks yang menyebut konsekuensinya | Jangan menjalankan hapus/impor tanpa konfirmasi; jangan menjanjikan undo (tidak ada) |
| Pakai Bahasa Indonesia sederhana, kalimat pendek | Jangan menampilkan pesan teknis atau exception mentah |
| Pakai ruang kosong dan hierarki sederhana | Jangan memakai gradien, bayangan tebal, atau ornamen pernikahan |
| Dukung terang dan gelap mengikuti sistem | Jangan membuat pengaturan tema sendiri (tidak ada di PRD) |
| Rancang untuk skala font 200% | Jangan memakai tinggi tetap yang memotong teks |
| Pakai ulang komponen yang sama antar halaman | Jangan menambah halaman atau komponen di luar PRD |

---

## 10. Open Questions

### 10A. Pertanyaan terbuka dari PRD yang memengaruhi desain

Tidak diputuskan di brief ini; tampilan bagian terkait ditandai sebagai belum final.

| ID | Pertanyaan | Dampak desain |
|---|---|---|
| OQ-5 | "Acara sudah lewat" untuk piutang: setelah waktu selesai atau tanggal lewat? Perlu ambang hari? | Isi daftar Piutang; daftar bisa berisik jika keterlambatan 1–3 hari ikut tampil | **Piutang muncul setelah jam selesai acara (jam mulai + durasi)** |
| OQ-6 | Apakah pengguna perlu diberi tahu saat sebagian pengingat dilewati karena job disimpan mepet? | Perlu/tidaknya pesan di Form atau Snackbar setelah Simpan | **Ya, snackbar: "Beberapa pengingat dilewati karena acara terlalu dekat"** |
| OQ-7 | Tampilan rentang jam untuk job lintas tengah malam (22.00 - 02.00)? | Format di kartu, detail, notifikasi, dialog bentrok | **Kartu/detail: "22.00 - 02.00 (19 Okt)". Notifikasi: tanggal mulai** |
| OQ-8 | Perilaku tombol "Lunas" saat fee kosong? | State tombol Lunas di Form | **Tombol nonaktif (disabled) saat fee = 0** |
| OQ-9 | Asumsi brief yang belum disetujui: durasi awal 4 jam, aturan duplikat, minggu mulai Senin, impor mengganti data, jam 08.00, Material 3 dan warna | Nilai awal durasi, bidang yang disalin saat duplikat, label hari Rekap, teks dialog impor, dasar komponen | **Setuju dengan semua asumsi** |
| OQ-10 | Zona waktu dikunci WIB; pengguna WITA/WIT? | Apakah perlu penanda zona waktu di jam; saat ini tidak ada | **Fase 1: WIB saja. Multi-zona fase 2** |
| OQ-12 | Merk, model, versi Android HP pemilik? | Ukuran layar uji, perilaku izin alarm/notifikasi, isi petunjuk baterai | **Poco X3 GT** |
| OQ-16 | Fee tambahan/tip di atas fee tercatat? | Saat ini dibayar ≤ fee; tidak ada tampilan "lebih bayar" | **Fase 2. Fase 1: validasi dibayar ≤ fee tetap berlaku** |
| OQ-17 | Job dibatalkan/ditunda: hapus saja atau perlu status? | Tidak ada tampilan status job; saat ini hanya Hapus | **Fase 1: Hapus saja (cukup). Status job fase 2** |
| OQ-18 | Mengganti nama sumber job/acara secara massal? | Tidak ada layar kelola sumber/acara | **Fase 2 (terlalu kompleks untuk fase 1)** |
| OQ-19 | Batas jumlah job; kebutuhan performa dan TalkBack | Teknik daftar panjang; kewajiban TalkBack (8.5 mengusulkan baseline) | **Batas 1000 job. Lazy loading wajib. TalkBack baseline fase 1** |

### 10B. Keputusan desain yang saya ambil karena PRD diam (perlu konfirmasi)

| ID | Keputusan sementara | Mengapa perlu dikonfirmasi | Jawaban |
|---|---|---|---|
| D-1 | Navigasi utama: bottom navigation 3 item (Beranda, Rekap, Pengaturan) | PRD tidak menyebut bentuk navigasi; ini pilihan satu tangan | **Bottom navigation 3 item (Beranda, Rekap, Pengaturan)** |
| D-2 | Rekap: Piutang sebagai seksi terpisah di bagian bawah, urutan seksi: ringkasan → Per Sumber Job → Daftar Job → Piutang | Alternatif: piutang jadi segmen/halaman sendiri | **Ringkasan → Per Sumber → Daftar Job → Piutang** |
| D-3 | Detail Job: Ubah, Duplikat, Hapus di bottom action bar | PRD tidak menentukan letak aksi; alternatif: menu "⋮" di atas (kurang terjangkau) | **Bottom action bar (Ubah, Duplikat, Hapus)** |
| D-4 | Petunjuk baterai/autostart: baris di Pengaturan yang membuka halaman/dialog petunjuk | Bentuk dan isi per merk belum ada di PRD; perlu bahan teks | **Dialog dengan teks umum + link ke pengaturan** |
| D-5 | Daftar bentrok di dialog: nama, acara, rentang jam saja [PRD]. Perlu ditambah tanggal? | Kandidat dicek ±1 hari, tanggal membantu membedakan | **Tambahkan tanggal (18 Okt 2026 · Akad · 08.00-12.00)** |
| D-6 | Judul form duplikat "Tambah Job", tanpa penanda "hasil duplikat" | PRD menyebut mode tambah; penanda opsional | **Judul "Tambah Job (Disalin dari Andi & Sari)"** |
| D-7 | Dialog bentrok: "Kembali" jadi tombol utama, "Tetap Simpan" outline | Menekankan keamanan; alternatif: setara bobot | **"Kembali" utama, "Tetap Simpan" outline** |
| D-8 | Font Inter di-bundle | Alternatif: font sistem (Roboto) agar APK lebih kecil | **Roboto sistem (0KB, angka tabular juga bagus)** |
| D-9 | Tidak ada layout tablet/landscape; konten dibatasi 600 dp | Target hanya HP [PRD]; perlu konfirmasi bahwa itu cukup | **HP potret saja, konten max 600dp** |
| D-10 | Kontak PIC tidak punya field; dianggap tersimpan di Catatan | PRD menyebut PIC sebagai masalah tetapi model data tidak punya field | **Tetap di Catatan** |
| D-11 | Teks untuk "shareloc tidak bisa dibuka" (EC-21) belum ditentukan | Perlu teks final | **Sembunyikan tombol Maps jika link bermasalah** |
| D-12 | Nama hari (mis. "Sab") di kartu: tidak dipakai; format hanya `dd MMM yyyy` sesuai PRD | Contoh notifikasi PRD memakai nama hari; konsistensi perlu diputuskan | **Kartu: "Sab, 18 Okt 2026". Detail: "Sab, 18 Okt 2026". Notifikasi: "Sab: Resepsi Andi & Sari" (konsisten dengan contoh PRD)** |
| D-13 | Menutup Form saat ada perubahan belum disimpan: tidak ada dialog "buang perubahan" | PRD tidak menyebut; risiko kehilangan input | **Dialog jika ada perubahan: "Buang perubahan?"** |
| D-14 | Semua teks copy bertanda [USULAN copy] di bagian 6 dan 7 | Perlu persetujuan pemilik (mis. judul dialog, validasi, kosong state) | **Setujui semua copy di bagian 6 dan 7 DESIGN** |

### 10C. Ringkasan yang butuh keputusan terlebih dahulu

Untuk desain final, jawab dulu: **OQ-7, OQ-8, OQ-5, D-1, D-2, D-3, D-4.** Tujuh hal ini paling memengaruhi tata letak. Sisanya bisa dikerjakan paralel dengan nilai sementara di atas.
