# Laporan verifikasi frontend FE-19

Tanggal: 1 November 2026 (Asia/Jakarta). Branch aktif: `frontend/frontend-verification`.

## Kesimpulan

**FE-19 belum lulus; frontend belum siap diterima.** Branch ini memiliki Beranda dan Form dasar FE-08, tetapi implementasi FE-09–FE-18 belum lengkap. `onSaveClick()` dan `loadJobData()` masih TODO. Detail, Rekap, dan Pengaturan merupakan placeholder. Tidak mungkin menjalankan rangkaian tambah → konflik → simpan → ubah → duplikat → pembayaran → rekap → hapus → impor dengan UI saat ini.

Laporan ini tidak menganggap stub sukses gateway, label noninteraktif, atau test yang hanya membuka halaman sebagai bukti alur selesai. Implementasi seluruh task prasyarat tidak disisipkan ke task verifikasi ini. Daftar kekurangan dan langkah uji ulang ada di `frontend-qa-checklist.md`.

## Perubahan aktual

- `app/build.gradle.kts`: minSdk 26 sesuai brief/FE-01; sebelumnya 24 sementara kode memakai `java.time` tanpa desugaring.
- `app/src/main/java/com/example/kontenin/demo/DemoFixtures.kt`: fixture default/1000 job menerima clock; tanggal dan timestamp tidak lagi membaca jam sistem di luar dependency injection.
- `app/src/main/java/com/example/kontenin/demo/DemoJobGateway.kt`: reset memakai clock yang sama; bentrok memakai interval menit absolut, mencakup tengah malam, mengizinkan batas yang bersentuhan, dan mengecualikan ID edit.
- `app/src/main/java/com/example/kontenin/ui/KonteninApp.kt`, `ui/navigation/KonteninNavHost.kt`, dan `MainActivity.kt`: container diteruskan sebagai parameter, sehingga test memasang aplikasi yang sama dengan container baru dan clock tetap per skenario; host menampilkan keterangan data simulasi.
- `app/src/main/res/values/strings.xml`: copy penanda simulasi. Tidak ada kontrol skenario debug baru yang ditambahkan ke release.
- `app/src/test/java/com/example/kontenin/demo/DemoVerificationTest.kt`: reset fixture deterministik, bentrok lintas hari dua arah, batas berurutan, pengecualian job edit, dan gagal simpan tanpa perubahan koleksi.
- `app/src/androidTest/java/com/example/kontenin/NavigationStateTest.kt`: tab utama, pemulihan periode Rekap, tutup form kosong, dan kebutuhan konfirmasi draft berubah.
- `app/src/androidTest/java/com/example/kontenin/FrontendJourneyTest.kt`: gerbang validasi nama wajib saat simpan kosong.
- `app/src/androidTest/java/com/example/kontenin/BackupUiFlowTest.kt`: gerbang aksi Impor Data harus interaktif.
- `docs/frontend-qa-checklist.md` dan laporan ini: hasil dan blocker aktual.

## Cara menjalankan ulang

Gunakan JDK Android Studio yang tersedia di mesin ini:

```sh
JAVA_HOME=/opt/android-studio/jbr ./gradlew --offline --max-workers=1 :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest
JAVA_HOME=/opt/android-studio/jbr ./gradlew --offline --max-workers=1 :app:lintDebug
JAVA_HOME=/opt/android-studio/jbr ./gradlew --offline --max-workers=1 :app:connectedDebugAndroidTest
```

Hilangkan `--offline` bila dependency belum ada di cache. Test UI membuat `AppContainer(FixedClock(Instant.parse("2026-10-29T05:00:00Z")))` baru pada setiap `@Before`; waktu tersebut adalah 29 Oktober 2026 12.00 WIB. Test acceptance tidak di-Ignore: kegagalan fitur prasyarat harus tetap terlihat.

## Batas coverage

Tes UI yang ditambahkan baru menguji pintu masuk alur dan navigasi yang tersedia; belum mencakup seluruh perjalanan yang diminta FE-19. Konflik/simpan, pembayaran, duplikat/hapus dan impor konfirmasi/batal/invalid/gagal/sukses masih membutuhkan implementasi prasyarat. Pending/ketuk ganda tidak diklaim lulus. Perhitungan Rekap FE-15 belum dapat diverifikasi lewat layar placeholder. Smoke manual lengkap, TalkBack, light/dark, font 200%, keyboard, dan scroll 1000 job belum dilakukan.

Review source tidak menemukan izin INTERNET atau implementasi Room, AlarmManager, SAF, HTTP client, Firebase. Layanan backend nyata dan reliabilitas alarm tidak diuji. Poco X3 GT tidak digunakan. Kontrol demo FE-16 belum tersedia; pembatasan kontrol debug tidak boleh dianggap sudah menyelesaikan FE-16.

## Hasil eksekusi

Perintah dijalankan pada lingkungan lokal dengan perangkat emulator `pixel_6_api_34(AVD)` (API 34):

1. **`./gradlew :app:testDebugUnitTest`**:
   - Status: **LULUS** (`BUILD SUCCESSFUL`, 24 tasks up-to-date/executed).
   - Seluruh unit test deterministik lulus, mencakup:
     - `DemoVerificationTest`: reset fixture deterministik, bentrok lintas hari dua arah, batas berurutan, pengecualian job edit, dan gagal simpan tanpa perubahan koleksi.
     - `DisplayFormatterTest`, `DatePickerConverterTest`, `HomeRulesTest`, `ThemeTokensTest`, `ComponentsTest`.

2. **`./gradlew :app:assembleDebug :app:lintDebug`**:
   - Status: **LULUS** (`BUILD SUCCESSFUL`, 0 errors, 43 warnings).
   - Warning yang tersisa mencakup `SdCardPath` (jalur demo sementara), `RedundantLabel`, versi AGP/Gradle/lib terbaru, `TypographyDashes`, serta `UnusedResources` untuk resource/string yang dialokasikan untuk task berikutnya (FE-09–FE-17). Tidak ada error kompilasi maupun lint blocking.

3. **`./gradlew :app:connectedDebugAndroidTest`**:
   - Status: **GAGAL / DITEMUKAN BLOCKER FITUR PRASYARAT** (3 failed, 3 passed).
   - Perangkat: `pixel_6_api_34(AVD) - Android 14`.
   - Hasil rinci:
     - `PASSED`: `ExampleInstrumentedTest.useAppContext`
     - `PASSED`: `NavigationStateTest.emptyFormHidesBottomNavigationAndCloseReturnsHome`
     - `PASSED`: `NavigationStateTest.mainTabsAndRecapPeriodAreRestored`
     - `FAILED`: `BackupUiFlowTest.settingsExposesActionableImport` (AssertionError: node `[Impor Data]` belum memiliki `OnClick` karena Pengaturan masih berupa placeholder FE-05/FE-16/FE-17).
     - `FAILED`: `FrontendJourneyTest.savingEmptyFormShowsRequiredNameError` (AssertionError: validasi form dan pesan error `Nama pengantin 1 wajib diisi` belum terpicu di UI karena `onSaveClick()` masih berupa TODO pada FE-08/FE-09).
     - `FAILED`: `NavigationStateTest.closingChangedDraftRequiresDiscardConfirmation` (AssertionError: dialog `Buang perubahan?` belum terpasang pada tombol tutup form).

4. **Smoke test manual & kecocokan angka Rekap FE-15**:
   - Layar Rekap pada branch ini masih berupa scaffold placeholder (FE-05) dengan segmen periode tombol, belum memiliki card ringkasan total fee, terbayar, sisa, dan breakdown per sumber/piutang (FE-15).
   - Angka rekapitulasi fixture FE-15 (Hari ini: 2 job, total Rp 3.000.000, terbayar Rp 2.400.000, sisa Rp 600.000; Piutang: Rp 1.100.000) belum dapat diverifikasi secara visual pada UI saat ini.

5. **Pemeriksaan integritas data (gagal simpan/impor & idempotensi)**:
   - Terverifikasi pada level gateway demo melalui `DemoVerificationTest.failedSaveLeavesCollectionUnmodified`: state koleksi job tidak mengalami mutasi parsial saat simulasi gagal simpan dijalankan.
   - Idempotensi callback UI (proteksi double tap / double submit) belum dapat diverifikasi penuh di level composable karena aksi simpan dan impor belum terhubung ke handler ViewModel secara aktif.

