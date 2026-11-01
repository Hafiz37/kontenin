# Kontenin

Aplikasi Android native untuk Wedding Content Creator (WCC) freelance: mencatat job, pengingat otomatis, peringatan bentrok jadwal, rekap fee & piutang, serta backup data.

**Status**: 🚧 Frontend Development (~60% completion)  
**Fase**: FE-01 hingga FE-12 selesai, FE-13 hingga FE-19 dalam progress

---

## 📱 Tentang Aplikasi

**Kontenin** adalah aplikasi offline-first untuk mencatat job wedding content creator dengan fitur:
- ✅ CRUD job dengan validasi lengkap
- ✅ Autocomplete acara dan sumber job
- ✅ Peringatan jadwal bentrok (non-blocking)
- ✅ Pencatatan pembayaran (DP hingga lunas)
- ⏳ Rekap per periode dan per sumber job
- ⏳ Daftar piutang lintas periode
- ⏳ Pengingat otomatis (H-7, H-3, H-1, 3 jam sebelum acara)
- ⏳ Ekspor/Impor backup JSON

---

## 🛠 Teknologi

| Komponen | Stack |
|----------|-------|
| **Bahasa** | Kotlin 100% |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Arsitektur** | MVVM (ViewModel + Repository pattern) |
| **Concurrency** | Kotlin Coroutines + Flow |
| **Dependency Injection** | Manual (AppContainer) |
| **Database** | ⏳ Room (belum diimplementasikan, pakai demo in-memory) |
| **Alarm** | ⏳ AlarmManager (belum, pakai simulasi) |
| **Backup** | ⏳ Storage Access Framework + JSON (belum) |
| **Testing** | JUnit 4 + Compose UI Test + Espresso |

---

## 🚀 Quick Start

### Prasyarat
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17 (bundled di Android Studio: `/opt/android-studio/jbr`)
- Android SDK 26+ (targetSdk 34)
- Emulator atau device fisik Android 8.0+

### Clone dan Build

```bash
# Clone repository
git clone <repository-url>
cd kontenin

# Build dengan Gradle wrapper (offline mode jika dependency sudah ada)
JAVA_HOME=/opt/android-studio/jbr ./gradlew --offline :app:assembleDebug

# Atau tanpa offline jika pertama kali
./gradlew :app:assembleDebug

# Install di device/emulator terhubung
./gradlew :app:installDebug
```

### Menjalankan Tests

```bash
# Unit tests (logika murni: validasi, formatter, conflict detection)
./gradlew :app:testDebugUnitTest

# Lint check
./gradlew :app:lintDebug

# UI tests (butuh emulator/device)
./gradlew :app:connectedDebugAndroidTest
```

**Status Test Saat Ini**:
- ✅ Unit tests: 24/24 lulus
- ✅ Lint: 0 errors, 43 warnings (non-blocking)
- ⚠️ UI tests: 3/6 lulus (blocker: FE-13–FE-17 belum lengkap)

---

## 📂 Struktur Proyek

```
kontenin/
├── app/src/main/java/com/example/kontenin/
│   ├── MainActivity.kt                    # Entry point aplikasi
│   ├── AppContainer.kt                    # Manual DI container
│   │
│   ├── model/                             # Data models (Job, JobDraft)
│   ├── contract/                          # Gateway interfaces
│   ├── demo/                              # In-memory implementations untuk frontend
│   │
│   ├── presentation/                      # ViewModels & business logic
│   │   ├── home/                          # Beranda (tab, filter)
│   │   ├── jobform/                       # Form Job (validasi, autocomplete, bentrok)
│   │   ├── jobdetail/                     # Detail Job ⚠️ parsial
│   │   ├── recap/                         # Rekap & Piutang ⚠️ parsial
│   │   ├── settings/                      # Pengaturan ⚠️ parsial
│   │   ├── JobDisplayMapper.kt            # Transformasi Job → UI data
│   │   └── PaymentValidator.kt            # Validasi pembayaran bersama
│   │
│   ├── ui/                                # Compose UI
│   │   ├── theme/                         # Material 3 theming (Color, Type, Shape)
│   │   ├── components/                    # Reusable components
│   │   ├── screens/                       # Layar aplikasi
│   │   ├── navigation/                    # Navigation graph
│   │   └── KonteninApp.kt                 # Root composable
│   │
│   └── util/                              # Utilities (Clock, Formatters)
│
├── app/src/test/                          # Unit tests ✅
├── app/src/androidTest/                   # UI tests ⚠️
│
├── brief/                                 # Dokumentasi requirement
│   ├── brief-kontenin.md                  # Brief utama dari pemilik
│   ├── PRD-Kontenin-v0.1.md              # Product Requirements Document
│   ├── DESIGN.md                          # Design specification
│   └── Kontenin-Frontend-Tasks.md         # Task breakdown FE-01 hingga FE-20
│
└── docs/                                  # Dokumentasi teknis
    ├── frontend-contracts.md              # ✅ Kontrak gateway untuk integrasi
    ├── frontend-decisions.md              # ✅ Keputusan arsitektur
    ├── frontend-handoff.md                # ✅ Panduan integrasi backend
    ├── frontend-acceptance.md             # ✅ Checklist kriteria selesai
    ├── frontend-test-report.md            # ✅ Hasil testing
    └── frontend-qa-checklist.md           # ⚠️ Checklist QA per layar
```

---

## 📖 Dokumentasi

### Untuk Developer Frontend
- **[Frontend Contracts](docs/frontend-contracts.md)** — Interface gateway dan ownership state
- **[Frontend Decisions](docs/frontend-decisions.md)** — Keputusan arsitektur dan pattern
- **[Test Report](docs/frontend-test-report.md)** — Hasil testing dan blocker

### Untuk Backend Integration
- **[Frontend Handoff](docs/frontend-handoff.md)** — Panduan integrasi backend (pemetaan kontrak → Room/AlarmManager/SAF)
- **[Frontend Acceptance](docs/frontend-acceptance.md)** — Kriteria selesai dan status acceptance

### Untuk Pemilik Proyek
- **[Brief](brief/brief-kontenin.md)** — Requirement dan instruksi AI
- **[PRD](brief/PRD-Kontenin-v0.1.md)** — Spesifikasi fitur dan use case
- **[DESIGN](brief/DESIGN.md)** — Spesifikasi visual dan UX

---

## 🎯 Progress Task Frontend

| Task | Status | Keterangan |
|------|--------|------------|
| FE-01 | ✅ | Setup proyek, Kotlin, Compose, Material 3, DI manual |
| FE-02 | ✅ | Model, kontrak gateway, demo in-memory |
| FE-03 | ✅ | Tema Roboto, palet warna light/dark |
| FE-04 | ✅ | Komponen UI bersama (button, field, chip, dialog) |
| FE-05 | ✅ | Navigasi bottom bar dan kerangka 5 layar |
| FE-06 | ✅ | Formatter waktu/rupiah, JobCard, date picker converter |
| FE-07 | ✅ | Beranda: tab Mendatang/Riwayat + filter Belum Lunas |
| FE-08 | ✅ | Form Job: semua field + SavedStateHandle |
| FE-09 | ✅ | Validasi form lengkap + tombol Lunas |
| FE-10 | ✅ | Autocomplete acara/sumber job + normalisasi ejaan |
| FE-11 | ✅ | Simpan/ubah/duplikat job |
| FE-12 | ✅ | Dialog bentrok non-blocking |
| FE-13 | ⚠️ | Detail Job (struktur ada, UI perlu finalisasi) |
| FE-14 | ⚠️ | Pembayaran di Detail (validator ada, UI perlu finalisasi) |
| FE-15 | ⚠️ | Rekap + Per Sumber + Piutang (calculator ada, UI perlu finalisasi) |
| FE-16 | ⚠️ | Pengaturan + pengingat simulasi (struktur ada, UI perlu finalisasi) |
| FE-17 | ❌ | UI Ekspor/Impor (belum diimplementasikan) |
| FE-18 | ❌ | Polish aksesibilitas dan layout (belum dijalankan) |
| FE-19 | ❌ | Verifikasi alur (3/6 test gagal, blocker FE-13–FE-17) |
| FE-20 | ⚠️ | Dokumentasi handoff (dokumen dibuat, menunggu completion) |

**Progress**: ~60% (12/20 selesai penuh, 5/20 parsial, 3/20 belum)

---

## ⚠️ Catatan Penting

### Data Tidak Persisten
**Aplikasi fase frontend ini menggunakan data demo in-memory**. Semua perubahan akan hilang saat aplikasi ditutup/restart. Ini **by design** untuk fase frontend agar tidak mencampurkan implementasi UI dengan backend.

### Package Name Sementara
`com.example.kontenin` adalah placeholder untuk MVP lokal. Package final untuk rilis publik akan ditentukan nanti.

### Backend Belum Ada
- ✅ **Sudah ada**: UI, validasi, kontrak gateway, demo gateway
- ❌ **Belum ada**: Room database, AlarmManager, Storage Access Framework, permission handling

### Testing Terbatas
- ✅ Unit test: logika murni (validasi, formatter, conflict detection)
- ⚠️ UI test: sebagian lulus, sebagian blocker fitur FE-13–FE-17
- ❌ Alarm reliability: belum diuji (menunggu backend)
- ❌ Poco X3 GT: belum diuji di perangkat fisik

---

## 🚦 Roadmap

### ✅ Fase 1: Frontend Foundation (Selesai)
- Setup proyek, tema, komponen
- Navigasi dan kerangka layar
- Model dan kontrak gateway

### 🚧 Fase 2: Frontend Features (Saat Ini)
- ✅ Beranda dan Form Job lengkap
- ⏳ Detail, Rekap, Pengaturan (finalisasi UI)
- ⏳ Ekspor/Impor UI
- ⏳ Polish dan aksesibilitas

### 📅 Fase 3: Backend Integration (Belum Mulai)
- Room database + DAO + Entity
- AlarmManager + ReminderScheduler + BroadcastReceiver
- Storage Access Framework + JSON serialization
- Permission handling (POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM)
- Ganti DemoGateway → implementasi nyata

### 📅 Fase 4: Testing & Polish (Belum Mulai)
- Uji alarm di Poco X3 GT (reboot, layar mati, doze mode)
- Round-trip backup: ekspor → hapus → install → impor
- Performance test 1000 job di perangkat fisik
- Aksesibilitas audit lengkap

### 📅 Fase 5: MVP Release (Belum Dijadwalkan)
- Package name final
- Ikon aplikasi dan splash screen
- Screenshot dan deskripsi untuk dokumentasi
- User testing dengan pemilik proyek

---

## 🐛 Known Issues & Blocker

### Blocker Kritis (Mencegah Handoff Backend)
1. **FE-13**: Detail Job belum UI lengkap (tampilan, Maps, action bar, hapus)
2. **FE-14**: Dialog Ubah Pembayaran belum terintegrasi
3. **FE-15**: Card ringkasan Rekap, Per Sumber, Piutang belum ditampilkan
4. **FE-16**: Status izin, banner, Uji Pengingat, petunjuk baterai belum UI
5. **FE-17**: Ekspor/Impor belum diimplementasikan sama sekali
6. **FE-18**: TalkBack audit, font 200% test, keyboard nav belum dijalankan
7. **FE-19**: 3 dari 6 UI test gagal

### Non-blocker (Can Wait)
- Animasi transisi antar layar
- Preview mode untuk screenshot
- Dark mode comparison screenshots

---

## 📞 Kontak & Support

- **Developer**: AI Software Engineer
- **Pemilik Proyek**: [Belum diisi]
- **Repository**: [Belum diisi]

Untuk pertanyaan tentang:
- **Requirement**: Lihat `brief/brief-kontenin.md`
- **Arsitektur**: Lihat `docs/frontend-decisions.md`
- **Integrasi Backend**: Lihat `docs/frontend-handoff.md`
- **Testing**: Lihat `docs/frontend-test-report.md`

---

## 📜 Lisensi

[Belum ditentukan] - Aplikasi pribadi untuk proyek kuliah dan pemakaian freelancer wedding content creator.

---

**Last Updated**: 1 November 2026  
**Version**: 0.1.0-alpha (Frontend Development)
