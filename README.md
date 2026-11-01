# Kontenin

**Kontenin** adalah aplikasi Android native yang dirancang khusus untuk memfasilitasi kebutuhan operasional seorang *Wedding Content Creator* (WCC) freelance dalam mencatat pekerjaan, mengatur jadwal peliputan, memonitor pembayaran, serta merekap pendapatan tanpa bergantung pada koneksi internet.

---

## 📌 Nama Proyek
- **Nama Aplikasi**: Kontenin
- **Target Pengguna**: Freelancer Wedding Content Creator (WCC) individu
- **Platform**: Android Native (`minSdk 26`, `targetSdk 34`)
- **Zona Waktu & Format**: Asia/Jakarta (WIB) & Rupiah (Rp)
- **Status Pengembangan**: Selesai Tahap Frontend (FE-01 s/d FE-20) / Menunggu Integrasi Backend

---

## 🔍 Rumusan Masalah
Seorang Wedding Content Creator freelance kerap menerima pekerjaan dari berbagai sumber (klien pribadi langsung, vendor fotografer/videografer, maupun Event Organizer) tanpa didukung oleh staf admin. Hal ini menimbulkan sejumlah kendala operasional:

1. **Informasi Tercecer**: Detail acara (nama pasangan pengantin, tanggal, rentang waktu, alamat, kontak PIC, tautan shareloc maps, hingga rundown acara) tersebar di berbagai obrolan aplikasi pesan singkat dan catatan pribadi.
2. **Risiko Jadwal Terlewat atau Bentrok**: Jadwal yang tidak tersentralisasi berisiko memicu penerimaan dua pekerjaan di rentang waktu yang sama (*overlapping*), serta keterlambatan persiapan tanpa adanya sistem pengingat otomatis.
3. **Pelacakan Fee & Piutang Tidak Rapi**: Pembayaran dari vendor seringkali berupa uang muka (DP) dengan pelunasan bertahap. Tanpa pencatatan yang jelas, penagihan sisa fee yang telah jatuh tempo sering terlupakan.
4. **Kesulitan Mengetahui Performa Penghasilan**: Sulit melihat total pendapatan bersih per periode (harian, mingguan, bulanan) maupun breakdown kontribusi pendapatan berdasarkan sumber job (vendor mana yang paling menguntungkan).
5. **Ketergantungan Perangkat & Risiko Kehilangan Data**: Catatan manual berisiko hilang apabila ponsel rusak, hilang, atau berganti perangkat tanpa adanya mekanisme pencadangan mandiri.

---

## 💡 Solusi yang Ditawarkan
Aplikasi **Kontenin** menghadirkan solusi terintegrasi, praktis, dan *offline-first* dengan prinsip **sederhana dan tidak ribet**:

- **Pusat Pengelolaan Job Terpadu**: Satu tempat untuk mencatat semua detail pekerjaan lengkap dengan autocomplete isian, navigasi Maps satu sentuhan, dan fitur duplikasi data pengantin untuk acara bertahap (misal: Akad lalu Resepsi).
- **Deteksi Bentrok Jadwal Otomatis**: Memeriksa potensi irisan waktu antar-job secara *real-time* sebelum data disimpan dan memberikan dialog konfirmasi non-blocking.
- **Pengingat Berlapis (Simulasi / Alarm Terjadwal)**: Menjadwalkan pengingat otomatis di titik kritis (H-7, H-3, H-1 pukul 08:00, serta 3 jam sebelum acara dimulai) tanpa perlu konfigurasi manual satu per satu.
- **Pencatatan Pembayaran & Tagihan Piutang**: Memonitor status pembayaran (Belum Bayar, Kurang, Lunas) dengan validasi nominal, serta menyediakan daftar piutang khusus untuk job yang telah selesai dikerjakan namun belum lunas.
- **Rekapitulasi Finansial & Analisis Sumber**: Ringkasan performa finansial per periode (Hari Ini, Minggu Ini, Bulan Ini) dan perincian kontribusi fee per sumber job/vendor.
- **Cadangan Data Mandiri (Backup & Restore)**: Fitur ekspor dan impor seluruh basis data ke dalam satu berkas format JSON untuk keamanan jangka panjang tanpa membutuhkan server eksternal.

---

## 📖 Deskripsi Proyek
Kontenin dibangun dengan arsitektur modern Android yang berpegang pada prinsip *offline-first* dan *privacy-first*. Aplikasi ini tidak memerlukan pendaftaran akun, login, maupun koneksi internet (tanpa izin `INTERNET`). 

Aplikasi memiliki lima alur layar utama:
1. **Beranda**: Menampilkan daftar job terorganisir dalam tab *Mendatang* (diurutkan waktu terdekat) dan *Riwayat*, dilengkapi filter cepat *Belum Lunas*.
2. **Form Job**: Antarmuka pencatatan/pengeditan pekerjaan yang dilengkapi pemilih tanggal, waktu mulai, durasi, autocomplete acara dan sumber job, format input mata uang Rupiah, serta dialog konfirmasi bentrok dan pembatalan draft.
3. **Detail Job**: Tampilan komprehensif seluruh data pasangan, alamat, tautan Google Maps langsung, penyesuaian nominal pembayaran cepat (*inline dialog*), fitur duplikat, dan penghapusan data dengan konfirmasi keamanan.
4. **Rekap & Piutang**: Dashboard analitik penghasilan dengan segmented control periode, visualisasi angka nominal tabular (28sp bold), daftar distribusi vendor, serta seksi daftar tagihan piutang lintas periode.
5. **Pengaturan & Cadangan**: Pusat kendali status izin sistem (Notifikasi & Alarm Tepat Waktu), dialog panduan baterai/autostart vendor, pengujian pengingat, serta flow ekspor dan impor berkas JSON.

---

## 🛠 Teknologi yang Digunakan

Aplikasi ini menggunakan ekosistem pengembangan Android native termutakhir:

| Lapisan / Komponen | Teknologi | Keterangan & Peran |
|---|---|---|
| **Bahasa Pemrograman** | **Kotlin 1.9+** | Bahasa utama 100% untuk seluruh logika dan antarmuka. |
| **UI Toolkit** | **Jetpack Compose** | Deklaratif UI modern Android menggantikan sistem XML View tradisional. |
| **Design System** | **Material 3 (M3)** | Komponen Material Design 3 dengan palet warna custom dan tipografi sistem (Roboto). |
| **Pola Arsitektur** | **MVVM (Model-View-ViewModel)** | Pemisahan logika bisnis dari UI untuk kemudahan pengujian dan perawatan. |
| **Dependency Injection** | **Manual DI (`AppContainer`)** | Kontainer dependensi terpusat tanpa library reflektif berat seperti Dagger/Hilt. |
| **Manajemen Asinkron** | **Kotlin Coroutines & StateFlow** | Aliran data reaktif dan eksekusi non-blocking yang aman terhadap siklus hidup (lifecycle-aware). |
| **Navigasi** | **Navigation Compose** | Manajemen rute navigasi antar layar dan passing argumen berbasis tipe. |
| **Form & State Retention** | **SavedStateHandle** | Menjaga integritas data input form saat terjadi konfigurasi ulang (rotasi layar atau process death). |
| **Testing Framework** | **JUnit 4, Compose UI Test, Mockk** | Unit test untuk logika bisnis/validator dan instrumented UI test untuk acceptance alur. |
| **Platform Target** | **Android SDK 26 - 34** | Mendukung Android 8.0 (Oreo) hingga Android 14 tanpa dependensi pihak ketiga yang tidak perlu. |

*(Catatan: Pada tahap handoff backend berikutnya, lapisan data in-memory demo akan digantikan dengan **Room Database**, **Android AlarmManager**, serta **Storage Access Framework (SAF)**).*

---

## 🚀 Panduan Build & Pengujian

### Prasyarat
- JDK 17 (disarankan JDK bawaan Android Studio: `/opt/android-studio/jbr`)
- Android Studio Hedgehog (2023.1.1) atau versi lebih baru
- Android SDK Platform 34

### Perintah Gradle

```bash
# Build APK Debug
JAVA_HOME=/opt/android-studio/jbr ./gradlew :app:assembleDebug

# Menjalankan seluruh Unit Test (100% PASS)
JAVA_HOME=/opt/android-studio/jbr ./gradlew :app:testDebugUnitTest

# Memeriksa kualitas kode & aturan Lint (0 Errors)
JAVA_HOME=/opt/android-studio/jbr ./gradlew :app:lintDebug

# Mengompilasi APK Instrumented Test
JAVA_HOME=/opt/android-studio/jbr ./gradlew :app:assembleDebugAndroidTest
```

---

## 📂 Struktur Direktori Proyek

```
app/src/main/java/com/example/kontenin/
├── MainActivity.kt                     # Entry point Activity
├── AppContainer.kt                     # Manual Dependency Injection container
│
├── contract/                           # Definisi antarmuka Gateway (Job, Reminder, Backup, ExternalAction)
├── demo/                               # Implementasi in-memory & data fixtures untuk fase frontend
├── model/                              # Model domain entitas (Job, JobDraft)
│
├── presentation/                       # Lapisan ViewModel & aturan logika murni
│   ├── home/                           # HomeViewModel, HomeUiState, HomeRules
│   ├── jobform/                        # JobFormViewModel, FormValidator, AutocompleteRules
│   ├── jobdetail/                      # JobDetailViewModel, JobDetailUiState
│   ├── recap/                          # RecapViewModel, RecapCalculator, RecapUiState
│   └── settings/                       # SettingsViewModel, BackupController, BackupUiState
│
├── ui/                                 # Lapisan Presentasi Jetpack Compose
│   ├── theme/                          # Token tema (Color, Type, Shape, Dimensions)
│   ├── components/                     # Komponen bersama (Buttons, TextField, MoneyField, Chips, Dialogs)
│   ├── navigation/                     # KonteninNavHost, Route navigation graph
│   └── screens/                        # Layar aplikasi (Home, JobForm, JobDetail, Recap, Settings)
│
└── util/                               # Utility helpers (DisplayFormatter, RupiahFormatter, Clock)
```

---

## 📄 Dokumentasi Terkait
- **[Brief Proyek](brief/brief-kontenin.md)** — Kebutuhan operasional dan instruksi dasar
- **[Product Requirements Document (PRD)](brief/PRD-Kontenin-v0.1.md)** — Spesifikasi rinci seluruh fitur dan edge cases
- **[Design Specification](brief/DESIGN.md)** — Panduan visual, tipografi, warna, dan komponen
- **[Kontrak Gateway Frontend](docs/frontend-contracts.md)** — Spesifikasi kontrak integrasi backend
- **[Panduan Handoff Backend](docs/frontend-handoff.md)** — Roadmap implementasi database dan alarm
- **[Checklist Penerimaan Frontend](docs/frontend-acceptance.md)** — Kriteria penerimaan dan status verifikasi FE-01–FE-20

---

**Versi**: 0.1.0-alpha  
**Lisensi**: Proyek Tugas Kuliah Pemrograman Bergerak & Personal WCC Helper Tool
