# Kontenin — Task Frontend v1.0

Tanggal: 29 Oktober 2026  
Sumber: `PRD-Kontenin-v0.1.md` dan `DESIGN.md` yang dilampirkan.  
Tujuan: menyelesaikan **frontend terlebih dahulu**, baru melanjutkan backend. Dokumen ini adalah rencana pengerjaan, bukan laporan fitur yang sudah dibangun.

## 1. Lingkup dan cara menggunakan dokumen

Frontend Kontenin adalah **aplikasi Android native Kotlin + Jetpack Compose + Material 3**, bukan website. Arsitektur menggunakan MVVM, Navigation Compose, dan DI manual. Semua halaman dan interaksinya harus dapat dicoba dengan data demo sebelum backend dimulai.

Dalam proyek offline ini, istilah **backend** berarti implementasi data dan layanan lokal: Room/DAO/migrasi, penyimpanan permanen, AlarmManager/receiver, izin sistem nyata, Intent Maps/pengaturan, Storage Access Framework, serialisasi JSON, transaksi impor dan penjadwalan ulang. Tidak diperlukan server, API HTTP, login, Firebase, atau cloud.

**Termasuk frontend:** desain visual, komponen, navigasi, state/ViewModel, validasi input, formatter, aturan presentasi, interface integrasi, implementasi demo in-memory, semua alur sukses/gagal, dan pengujian frontend. Perhitungan bentrok/agregasi pada demo diperlukan supaya alur dapat diuji; bukan bukti backend sudah selesai.

**Tidak termasuk:** implementasi backend di atas, alarm sungguhan, backup file sungguhan, rilis Play Store, kalender bulanan, pemilihan bulan lain, tip, riwayat transaksi, atau fitur fase 2. Jangan memakai data pelanggan asli dalam build demo.

Kerjakan FE-01 sampai FE-20 secara berurutan. Setiap task bergantung pada task sebelumnya yang sudah digabungkan. Setelah testing task lulus, gabungkan branch ke branch dasar proyek (misalnya `main`), lalu buat branch task berikutnya dari hasil terbaru. Nama branch dan commit di bawah adalah usulan yang siap dipakai; jangan melakukan commit terhadap perubahan lain yang tidak terkait.

Setiap task memiliki delapan bagian: judul, subbab detail, pekerjaan dan tujuan, prompt AI, file dibuat, file diubah, testing steps, serta branch dan commit message. Tidak ada kode implementasi lengkap atau penilaian kompleksitas.

## 2. Aturan pembacaan sumber dan keputusan

Jawaban yang sudah terisi di PRD bagian 10 dan DESIGN bagian 10A/10B didahulukan dari kalimat lama yang masih bertuliskan belum diputuskan. Keputusan penting:

| Topik | Acuan pengerjaan |
|---|---|
| Font | Roboto sistem, bukan Inter yang di-bundle (D-8). |
| Navigasi | Beranda, Rekap, Pengaturan; Detail/Form sebagai layar turunan. |
| Waktu | Asia/Jakarta, minggu mulai Senin, durasi awal 4 jam. |
| Tanggal kartu/detail | Nama hari aktual + tanggal; hitung lewat kalender, jangan menyalin nama hari contoh dokumen. |
| Rentang lintas hari | Tambahkan tanggal selesai, misalnya `22.00 - 02.00 (19 Okt)`. |
| Duplikat | Judul dinamis `Tambah Job (Disalin dari Andi & Sari)`; tanggal/acara kosong, dibayar 0. |
| Keluar form | Tanyakan `Buang perubahan?` bila draft berubah. |
| Piutang | Job selesai dan belum lunas, semua periode. Untuk batas waktu tepat selesai, gunakan `sekarang >= selesai` sebagai keputusan implementasi frontend yang dicatat. |
| Tombol Lunas | Disabled saat fee 0; fee kosong/tidak valid juga disabled agar tidak menghasilkan nilai salah. |
| Bentrok | Tidak memblokir; tanggal ditampilkan; Kembali utama, Tetap Simpan outline. |
| Maps | Link kosong/bermasalah: tombol tersembunyi (D-11). Jika gagal setelah diketuk: pesan PRD lalu sembunyikan tombol untuk link tersebut. Tanpa pengecekan jaringan. |
| Pengingat dekat | Snackbar persis PRD OQ-6, durasi dasar 4 detik. |
| Aksesibilitas | TalkBack baseline, font 200%, target sentuh 48 dp, daftar lazy untuk 1000 job. |
| Layar | HP potret, konten maksimum 600 dp; tidak perlu desain tablet tersendiri. |
| Kontak PIC | Tetap di Catatan, bukan field baru. |

Judul notifikasi di D-12 berbeda dari pola H-7/H-3/H-1/3 jam pada FR-4. Catat konflik ini untuk spesifikasi backend; frontend hanya menguji event buka Detail dan status simulasi, sehingga tidak perlu menetapkan format notifikasi OS sekarang. Versi Android Poco X3 GT belum diberikan; catat versi aktual saat perangkat diuji.

Alur pengerjaan asli PRD mencampurkan Room/alarm di tahap awal. Dokumen ini sengaja mengurutkan ulang berdasarkan instruksi frontend dahulu. Akibatnya pengujian reliabilitas alarm perangkat baru dilakukan pada fase backend; kelulusan frontend tidak membuktikan alarm andal.

## 3. Konvensi file dan prompt AI

Daftar file berikut adalah **rencana struktur**, bukan hasil pemeriksaan repository; lampiran hanya berisi PRD dan desain. Gunakan file setara jika proyek sudah memiliki struktur, tanpa menduplikasi kelas/komponen.

| Alias | Path relatif dari root repository |
|---|---|
| `K/` | `app/src/main/java/com/contoh/kontenin/` |
| `R/` | `app/src/main/res/` |
| `T/` | `app/src/test/java/com/contoh/kontenin/` |
| `A/` | `app/src/androidTest/java/com/contoh/kontenin/` |

Ganti bagian package pada seluruh path bila package proyek aktual berbeda. `com.contoh.kontenin` hanya placeholder MVP lokal, bukan keputusan package rilis publik. File yang dibuat pada task awal menjadi file yang diubah pada task sesudahnya. Daftar file uji adalah rencana implementasi nanti; dokumen ini tidak menjalankan pengujian aplikasi.

**Instruksi umum untuk setiap prompt AI:** berikan PRD, DESIGN, dokumen task ini, dan akses repository kepada AI. AI harus membaca struktur aktual serta aturan repository, mengerjakan hanya task yang dipilih, mempertahankan pekerjaan sebelumnya, menggunakan `strings.xml`, dan menjaga scope frontend. Semua layanan nyata diganti adapter demo. AI harus melaporkan file aktual yang dibuat/diubah, hasil testing yang benar-benar dijalankan, serta kendala; tidak boleh mengklaim test lulus tanpa dijalankan. Prompt khusus setiap task di bawah dipakai bersama instruksi umum ini.

## 4. Urutan task

| ID | Task | Status awal |
|---|---|---|
| FE-01 | Siapkan proyek Android dan struktur frontend | Belum dikerjakan |
| FE-02 | Tetapkan model UI, kontrak, dan data demo bersama | Belum dikerjakan |
| FE-03 | Bangun tema, tipografi, dan token desain | Belum dikerjakan |
| FE-04 | Bangun komponen UI bersama | Belum dikerjakan |
| FE-05 | Siapkan navigasi dan kerangka halaman | Belum dikerjakan |
| FE-06 | Buat formatter waktu, rupiah, dan kartu job | Belum dikerjakan |
| FE-07 | Implementasikan Beranda, tab, dan filter | Belum dikerjakan |
| FE-08 | Bangun Form Job dan input dasar | Belum dikerjakan |
| FE-09 | Tambahkan validasi form dan tombol Lunas | Belum dikerjakan |
| FE-10 | Tambahkan autocomplete acara dan sumber job | Belum dikerjakan |
| FE-11 | Hubungkan simpan demo, ubah, duplikat, dan keluar form | Belum dikerjakan |
| FE-12 | Tambahkan dialog bentrok sebelum simpan | Belum dikerjakan |
| FE-13 | Implementasikan Detail Job, Maps, dan hapus | Belum dikerjakan |
| FE-14 | Implementasikan perubahan pembayaran di Detail | Belum dikerjakan |
| FE-15 | Implementasikan Rekap, Per Sumber, dan Piutang | Belum dikerjakan |
| FE-16 | Implementasikan Pengaturan dan simulasi pengingat | Belum dikerjakan |
| FE-17 | Implementasikan UI ekspor dan impor simulasi | Belum dikerjakan |
| FE-18 | Rapikan state, aksesibilitas, dan layout seluruh layar | Belum dikerjakan |
| FE-19 | Verifikasi alur frontend dari awal sampai akhir | Belum dikerjakan |
| FE-20 | Finalisasi dokumentasi dan serah terima ke fase backend | Belum dikerjakan |

---

## FE-01 — Siapkan proyek Android dan struktur frontend

### 1. Judul

Siapkan proyek Android dan struktur frontend.

### 2. Subbab detail

Acuan: PRD §11–12; DESIGN §10B D-9. Prasyarat: tidak ada; mulai dari lampiran PRD/desain.

- Inisialisasi aplikasi Kotlin dengan Jetpack Compose, Material 3, Navigation Compose, ViewModel, dan DI manual.
- Pisahkan paket ui, presentation, model, contract, demo, dan util; gunakan satu modul app agar proyek tetap sederhana.
- Tetapkan minSdk 26 sesuai rekomendasi PRD; catat versi toolchain aktual yang kompatibel di proyek, jangan memakai nomor versi tebakan.
- Gunakan package kerja com.contoh.kontenin bila belum ada package resmi; tandai sebagai sementara untuk MVP lokal. Tidak menambahkan izin INTERNET, Room, server, Firebase, atau alarm.

### 3. Apa yang dilakukan dan supaya apa

Menyediakan fondasi yang bisa dijalankan agar seluruh task berikutnya dapat ditinjau di emulator tanpa menunggu backend. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Bangun fondasi proyek Android Kontenin sesuai lingkup FE-01. Gunakan Kotlin, Compose, Material 3, MVVM dan DI manual. Buat struktur paket, aplikasi awal, serta konfigurasi test frontend. Gunakan versi kompatibel yang tersedia dan dokumentasikan pilihan. Jangan mengimplementasikan penyimpanan permanen atau layanan Android pengingat.

### 5. File yang dibuat dan bagaimana

- `settings.gradle.kts` — daftar modul dan repositori dependensi.
- `build.gradle.kts; gradle/libs.versions.toml; gradle.properties` — konfigurasi build dan versi dependensi.
- `gradlew; gradlew.bat; gradle/wrapper/gradle-wrapper.properties; gradle/wrapper/gradle-wrapper.jar` — wrapper dari generator Gradle resmi.
- `app/build.gradle.kts; app/proguard-rules.pro` — konfigurasi modul aplikasi.
- `app/src/main/AndroidManifest.xml` — launcher aplikasi tanpa izin INTERNET.
- `K/MainActivity.kt` — entry point Compose minimal.
- `R/values/strings.xml` — nama aplikasi dan teks awal.
- `.gitignore; README.md` — file yang diabaikan dan cara build/jalankan.
- `docs/frontend-decisions.md` — package sementara, toolchain, dan keputusan lingkup.

### 6. File yang diubah dan bagaimana

- Tidak ada untuk proyek baru. Bila proyek sudah ada, ubah file konfigurasi dan MainActivity yang setara; jangan membuat proyek kedua.

### 7. Testing steps

1. Jalankan Gradle sync dan :app:assembleDebug; hasilnya build berhasil.
2. Jalankan aplikasi pada emulator API 26 atau lebih baru; layar awal terbuka tanpa crash.
3. Periksa merged manifest; izin INTERNET tidak ada.
4. Pastikan proyek belum bergantung pada Room, HTTP client, atau scheduler alarm.

### 8. Branch & commit message

- Branch: `frontend/fe-01-bootstrap`
- Commit message: `chore(frontend): initialize Kontenin Android project`

---

## FE-02 — Tetapkan model UI, kontrak, dan data demo bersama

### 1. Judul

Tetapkan model UI, kontrak, dan data demo bersama.

### 2. Subbab detail

Acuan: PRD §7; FR-1–7. Prasyarat: FE-01 selesai dan sudah digabungkan.

- Buat model Job tanpa anotasi database: id, kedua nama, tanggal epoch day, jam mulai menit, durasi menit, acara, alamat, shareloc, sumber, fee, dibayar, catatan, createdAt, updatedAt. Gunakan Long untuk rupiah.
- Pisahkan JobDraft agar input tanggal/fee kosong dapat direpresentasikan tanpa dianggap nol. Nilai turunan tidak disimpan pada Job.
- Definisikan kontrak JobGateway untuk observasi daftar/detail, simpan, hapus, pembaruan pembayaran, dan pemeriksaan bentrok; ReminderGateway untuk status izin, permintaan izin, dan uji; BackupGateway untuk alur ekspor serta validasi/konfirmasi impor; ExternalActionGateway untuk Maps dan pengaturan sistem.
- Bangun implementasi demo in-memory yang dipakai bersama semua layar; sediakan sukses, gagal, tertunda, kosong, dan job hilang. Hasil operasi bertipe, tidak berupa exception mentah.
- Sediakan clock yang dapat dikendalikan, ID unik, seed tetap, dan reset eksplisit hanya lewat alat debug. Data berubah selama sesi, lalu kembali ke seed saat proses dimulai ulang.

### 3. Apa yang dilakukan dan supaya apa

Memastikan perubahan dari Form, Detail, Beranda, dan Rekap tersambung tanpa backend; kontrak menjadi titik integrasi nanti. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan FE-02 dengan model murni, kontrak gateway dan satu sumber data demo in-memory melalui DI manual. Pisahkan draft dari model tersimpan. Simulasikan operasi dan kegagalan deterministik, sertakan jam uji yang dapat diinjeksi. Jangan mengklaim data demo persisten. Semua layar berikutnya harus menggunakan instance gateway yang sama.

### 5. File yang dibuat dan bagaimana

- `K/model/Job.kt; K/model/JobDraft.kt` — data tersimpan dan input mentah.
- `K/contract/JobGateway.kt; K/contract/ReminderGateway.kt; K/contract/BackupGateway.kt; K/contract/ExternalActionGateway.kt` — batas operasi dan hasil bertipe, termasuk state izin dan hasil pengingat dilewati.
- `K/demo/DemoJobGateway.kt; K/demo/DemoReminderGateway.kt; K/demo/DemoBackupGateway.kt; K/demo/DemoExternalActionGateway.kt` — implementasi simulasi tanpa akses sistem nyata.
- `K/demo/DemoFixtures.kt; K/demo/DemoScenario.kt` — seed dan pilihan skenario.
- `K/util/AppClock.kt` — sumber waktu WIB yang dapat diganti saat uji.
- `K/AppContainer.kt` — instance gateway bersama dan factory ViewModel manual.
- `docs/frontend-contracts.md` — parameter, hasil, error, dan kepemilikan state setiap kontrak.

### 6. File yang diubah dan bagaimana

- `K/MainActivity.kt` — membuat dan meneruskan AppContainer dengan lifecycle yang tepat.
- `docs/frontend-decisions.md` — menjelaskan data demo, reset proses, dan batas kontrak.

### 7. Testing steps

1. Ubah satu job lewat gateway; observer daftar dan detail menerima nilai yang sama.
2. Mulai ulang proses; data kembali ke seed dan tidak ada klaim sudah tersimpan permanen.
3. Skenario gagal simpan/hapus tidak memodifikasi data; skenario tertunda selesai saat pemicu uji dilepas.
4. Pastikan fixture memuat job mendatang, sedang berlangsung, masa lalu, lintas tengah malam, fee nol, DP, lunas, nama panjang, dan 1000 job.

### 8. Branch & commit message

- Branch: `frontend/fe-02-demo-contracts`
- Commit message: `feat(frontend): add UI contracts and shared demo data`

---

## FE-03 — Bangun tema, tipografi, dan token desain

### 1. Judul

Bangun tema, tipografi, dan token desain.

### 2. Subbab detail

Acuan: DESIGN §2–4; §10B D-8. Prasyarat: FE-02 selesai dan sudah digabungkan.

- Gunakan seluruh palet light/dark DESIGN, termasuk token warning, brand, dan inverse snackbar.
- Gunakan Roboto/sistem sesuai D-8; jangan bundle Inter. Angka nominal, tanggal, dan jam menggunakan angka tabular.
- Terapkan spacing 4/8/12/16/24/32 dp, radius 8/12/16/24 dp, margin 16 dp, dan konten maksimal 600 dp.
- Ikuti tema sistem. Warna brand #7F997E hanya untuk permukaan besar dengan isi gelap #111A11; primary light #2C452C memakai teks putih. Hindari dynamic color yang mengganti palet.
- Ukuran teks minimum 13 sp, dukung skala 200%, dan tinggi elemen fleksibel.

### 3. Apa yang dilakukan dan supaya apa

Menjaga seluruh halaman konsisten dan tetap terbaca di luar ruangan maupun saat ukuran font diperbesar. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan tema FE-03 dari nilai DESIGN.md, prioritaskan keputusan D-8 Roboto sistem. Pisahkan warna, tipografi, dimensi dan bentuk. Buat preview light/dark yang menunjukkan teks, nominal, dan token warning. Jangan memakai warna brand untuk teks kecil atau teks putih di atasnya.

### 5. File yang dibuat dan bagaimana

- `K/ui/theme/Color.kt; K/ui/theme/Theme.kt` — ColorScheme dan token tambahan light/dark.
- `K/ui/theme/Type.kt; K/ui/theme/Shape.kt; K/ui/theme/Dimensions.kt` — tipografi, radius, dan ukuran.
- `K/ui/preview/ThemePreview.kt` — contoh tema pada kedua mode.

### 6. File yang diubah dan bagaimana

- `K/MainActivity.kt` — membungkus konten dengan KonteninTheme.
- `R/values/strings.xml` — label preview yang tampil di UI bila diperlukan.

### 7. Testing steps

1. Periksa preview dan perangkat dalam light/dark; tidak ada warna yang terbawa salah dari mode lain.
2. Aktifkan font sistem 200%; teks contoh dan nominal tidak terpotong.
3. Periksa kontras warna komponen sesuai pasangan dalam DESIGN, khususnya FAB dan tombol utama.

### 8. Branch & commit message

- Branch: `frontend/fe-03-design-system`
- Commit message: `feat(frontend): implement Kontenin design tokens and themes`

---

## FE-04 — Bangun komponen UI bersama

### 1. Judul

Bangun komponen UI bersama.

### 2. Subbab detail

Acuan: DESIGN §5, §7–8. Prasyarat: FE-03 selesai dan sudah digabungkan.

- Sediakan tombol primary, tonal, outline, destruktif; field teks/uang; heading; chip pembayaran; banner; dialog konfirmasi; loading dan empty state.
- Bedakan state default, pressed, focused, disabled, loading, dan error. Tombol interaktif memiliki target minimal 48 dp.
- Chip pembayaran bersifat informatif dan tidak interaktif; status tidak disampaikan lewat warna saja.
- Buat preview komponen untuk nama panjang, nominal besar, light/dark, dan font 200%. Komponen menerima state dan callback, tanpa mengakses gateway sendiri.

### 3. Apa yang dilakukan dan supaya apa

Menghindari perbedaan perilaku antarhalaman dan mempermudah perbaikan UI dari satu tempat. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Kerjakan FE-04 sebagai komponen Compose reusable dengan state dari parameter. Gunakan token FE-03, teks resource, semantik aksesibilitas, dan ukuran fleksibel. Buat gallery preview komponen normal, error, disabled dan loading. Belum perlu memasang seluruh komponen ke halaman fitur.

### 5. File yang dibuat dan bagaimana

- `K/ui/components/KonteninButtons.kt; K/ui/components/KonteninTextField.kt; K/ui/components/MoneyField.kt` — elemen input/aksi.
- `K/ui/components/PaymentChip.kt; K/ui/components/SectionHeading.kt; K/ui/components/PermissionBanner.kt` — informasi status.
- `K/ui/components/ConfirmationDialog.kt; K/ui/components/EmptyState.kt; K/ui/components/LoadingState.kt` — state umum.
- `K/ui/preview/ComponentGallery.kt` — gallery preview.

### 6. File yang diubah dan bagaimana

- `R/values/strings.xml` — label umum, aksi Batal/Kembali/Coba lagi, dan deskripsi ikon.

### 7. Testing steps

1. Ketuk tombol disabled; callback tidak dijalankan. Ketuk tombol aktif; feedback pressed terlihat.
2. Tampilkan error field; teks error terlihat dan dibaca TalkBack.
3. Periksa dialog dengan label panjang dan font 200%; tombol bisa tersusun vertikal tanpa tertutup.
4. Pastikan chip tidak diumumkan sebagai tombol dan target aksi minimal 48 dp.

### 8. Branch & commit message

- Branch: `frontend/fe-04-shared-components`
- Commit message: `feat(frontend): add reusable Compose UI components`

---

## FE-05 — Siapkan navigasi dan kerangka halaman

### 1. Judul

Siapkan navigasi dan kerangka halaman.

### 2. Subbab detail

Acuan: DESIGN §6; D-1, D-3. Prasyarat: FE-04 selesai dan sudah digabungkan.

- Buat navigasi Beranda, Rekap, Pengaturan dengan bottom navigation tiga item dan label selalu terlihat.
- Tambahkan route Detail berdasarkan jobId serta Form dengan mode tambah, ubah, atau duplikat. Lewatkan ID/mode, bukan objek Job besar.
- Sembunyikan bottom navigation di Form dan Detail. Sediakan area bottom action bar Detail dan sticky Simpan Form.
- Tangani system bar, safe inset, snackbar host, dan keyboard inset. Simpan state tab dan posisi scroll saat kembali.
- Gunakan placeholder berlabel untuk fitur yang belum dibuat, lalu ganti pada task selanjutnya.

### 3. Apa yang dilakukan dan supaya apa

Membentuk alur aplikasi yang dapat dinavigasi sebelum tiap fitur diisi. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan FE-05 memakai Navigation Compose dan scaffold. Pastikan back stack benar, bottom navigation hanya di tiga layar utama, route job menggunakan ID, serta snackbar tidak tertutup bottom bar. Jangan mengisi logika halaman sebelum task fitur terkait.

### 5. File yang dibuat dan bagaimana

- `K/ui/KonteninApp.kt; K/ui/navigation/Routes.kt; K/ui/navigation/KonteninNavHost.kt` — host aplikasi dan route.
- `K/ui/components/MainBottomBar.kt` — tiga tujuan utama.
- `K/ui/screens/home/HomeScreen.kt; K/ui/screens/recap/RecapScreen.kt; K/ui/screens/settings/SettingsScreen.kt` — kerangka layar utama.
- `K/ui/screens/jobform/JobFormScreen.kt; K/ui/screens/jobdetail/JobDetailScreen.kt` — kerangka layar turunan.

### 6. File yang diubah dan bagaimana

- `K/MainActivity.kt` — memasang KonteninApp.
- `R/values/strings.xml` — judul layar dan label navigasi.

### 7. Testing steps

1. Pindah Beranda → Rekap → Pengaturan lalu kembali; pilihan aktif sesuai layar.
2. Masuk Detail dan Form; bottom navigation hilang dan tombol kembali bekerja.
3. Tekan tab aktif berulang; tidak menumpuk layar identik.
4. Buka keyboard pada contoh field; area aksi tidak tertutup keyboard atau system bar.

### 8. Branch & commit message

- Branch: `frontend/fe-05-navigation`
- Commit message: `feat(frontend): add app navigation and screen scaffolds`

---

## FE-06 — Buat formatter waktu, rupiah, dan kartu job

### 1. Judul

Buat formatter waktu, rupiah, dan kartu job.

### 2. Subbab detail

Acuan: FR-1.4–1.5, FR-3.4; OQ-7; DESIGN D-12. Prasyarat: FE-05 selesai dan sudah digabungkan.

- Format rupiah penuh locale Indonesia, jam HH.mm, dan tanggal dengan nama hari aktual. Jangan menyalin pasangan hari/tanggal contoh yang tidak sesuai kalender.
- Hitung rentang waktu dari tanggal mulai, jam mulai menit, dan durasi; lintas hari tampilkan tanggal selesai, misalnya 22.00 - 02.00 (19 Okt).
- Buat kartu dengan tanggal/jam sebagai informasi dominan, nama pasangan, acara, sumber, fee, dan chip pembayaran. Nama pasangan maksimal dua baris di kartu.
- Fee nol dan lunas tidak memiliki chip; dibayar nol dengan fee positif bertuliskan Belum lunas; pembayaran sebagian bertuliskan Kurang Rp X.
- Sediakan varian padat untuk Rekap. Format tanggal picker UTC menjadi LocalDate melalui fungsi murni.

### 3. Apa yang dilakukan dan supaya apa

Menghasilkan tampilan data yang konsisten dan menghindari salah tanggal atau salah nominal di seluruh layar. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan FE-06 dengan formatter murni dan JobCard stateless. Gunakan java.time dengan Asia/Jakarta untuk waktu acara, tetapi konversi tanggal dari picker memakai UTC. Tambahkan unit test batas tanggal, lintas tengah malam, uang, dan status pembayaran. Jangan menyimpan nilai turunan di Job.

### 5. File yang dibuat dan bagaimana

- `K/util/DateTimeFormatter.kt; K/util/RupiahFormatter.kt; K/util/DatePickerConverter.kt` — formatter dan konversi murni.
- `K/presentation/JobDisplayMapper.kt` — nilai presentasi turunan.
- `K/ui/components/JobCard.kt` — kartu standar dan padat.
- `T/util/DisplayFormatterTest.kt; T/util/DatePickerConverterTest.kt` — tes hasil format dan konversi.

### 6. File yang diubah dan bagaimana

- `K/ui/components/PaymentChip.kt` — memakai hasil pemetaan pembayaran.
- `K/ui/preview/ComponentGallery.kt` — menambahkan varian kartu.
- `R/values/strings.xml` — label kekurangan pembayaran dan pola rentang lintas hari.

### 7. Testing steps

1. Periksa fee 0, dibayar 0, pembayaran sebagian, dan lunas; chip sesuai aturan.
2. Buat job 18 Oktober pukul 22.00 selama 4 jam; akhir tampil 02.00 (19 Okt).
3. Uji picker pada tanggal batas bulan/tahun; tanggal tidak bergeser satu hari.
4. Periksa nominal Rp 1.500.000 dan nama pasangan sangat panjang pada lebar 360 dp.

### 8. Branch & commit message

- Branch: `frontend/fe-06-job-card-formatters`
- Commit message: `feat(frontend): add job cards and localized display formatting`

---

## FE-07 — Implementasikan Beranda, tab, dan filter

### 1. Judul

Implementasikan Beranda, tab, dan filter.

### 2. Subbab detail

Acuan: FR-1.3–1.4; DESIGN §6.1. Prasyarat: FE-06 selesai dan sudah digabungkan.

- Hubungkan Beranda ke daftar demo reaktif. Mendatang mencakup job yang belum selesai, termasuk yang sedang berlangsung; Riwayat berisi yang sudah selesai.
- Urutkan Mendatang dari waktu mulai terdekat dan Riwayat dari waktu mulai terbaru; gunakan ID sebagai tie-breaker konsisten.
- Filter Belum lunas hanya memfilter tab aktif; simpan pilihan masing-masing tab sebagai keputusan frontend.
- Gunakan LazyColumn dengan key stabil, FAB tambah, padding bawah cukup, dan empty state berbeda untuk tab kosong versus hasil filter kosong.
- Evaluasi ulang kategori saat aplikasi aktif kembali dan saat batas waktu selesai terlewati melalui clock/pemicu presentasi yang dapat diuji.

### 3. Apa yang dilakukan dan supaya apa

Membantu pengguna segera melihat jadwal berikutnya tanpa tertukar dengan job yang sudah selesai. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Kerjakan FE-07 menggunakan data demo bersama, state Beranda di ViewModel, JobCard FE-06, LazyColumn, dua tab dan satu filter. Job berpindah ke Riwayat saat waktu selesai tercapai sebagai aturan batas yang dicatat. Pertahankan tab/filter/scroll saat kembali dari Detail. Hindari memuat seluruh item sebagai Column biasa.

### 5. File yang dibuat dan bagaimana

- `K/presentation/home/HomeUiState.kt; K/presentation/home/HomeViewModel.kt` — state, filter dan pemilahan.
- `T/presentation/HomeRulesTest.kt` — uji urutan, filter, dan batas waktu selesai.

### 6. File yang diubah dan bagaimana

- `K/ui/screens/home/HomeScreen.kt` — daftar, tab, filter, FAB, kosong/loading/error.
- `K/ui/navigation/KonteninNavHost.kt; K/AppContainer.kt` — ViewModel dan callback navigasi.
- `R/values/strings.xml` — empty state Beranda dan filter.

### 7. Testing steps

1. Dengan clock tetap, bandingkan job masa depan, berlangsung, tepat selesai, dan masa lalu; kategori benar.
2. Aktifkan filter di Mendatang lalu buka Riwayat; data tidak terfilter silang.
3. Ketuk kartu → kembali; tab, filter dan scroll pulih. Ketuk FAB; Form tambah terbuka.
4. Seed 1000 job; scroll tetap dapat digunakan dan item terakhir tidak tertutup FAB.

### 8. Branch & commit message

- Branch: `frontend/fe-07-home`
- Commit message: `feat(frontend): implement home job tabs and unpaid filter`

---

## FE-08 — Bangun Form Job dan input dasar

### 1. Judul

Bangun Form Job dan input dasar.

### 2. Subbab detail

Acuan: FR-1.1–1.2; FR-3.2; DESIGN §6.2. Prasyarat: FE-07 selesai dan sudah digabungkan.

- Susun seksi Pasangan, Waktu, Acara dan sumber, Lokasi, Pembayaran, Catatan dengan seluruh field PRD.
- Gunakan tanggal dan jam melalui picker, durasi awal 4 jam, nilai dibayar awal 0, tanggal awal belum dipilih. Tanggal masa lalu tetap bisa dipilih.
- Simpan input mentah di state agar nilai kosong, koma, dan input sementara tidak hilang saat mengetik. Field rupiah memakai pemisah ribuan dan cursor tetap wajar.
- Tampilkan penghitung Alamat/Catatan saat sisa karakter kurang dari 20% batas; catatan tetap tempat kontak PIC.
- Simpan selalu terjangkau di bawah dengan keyboard terbuka. Draft bertahan saat recomposition dan recreation melalui SavedStateHandle atau mekanisme setara.

### 3. Apa yang dilakukan dan supaya apa

Membuat pengisian job nyaman dan lengkap sebelum validasi serta penyimpanan simulasi dihubungkan. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan FE-08 sebagai Form Job terkontrol oleh ViewModel dengan JobDraft. Bangun semua field sesuai urutan DESIGN, picker tanggal/jam, format rupiah saat mengetik, durasi desimal, dan sticky Simpan. Gunakan fungsi konversi picker FE-06. Belum menambahkan backend atau menyimpan draft ke disk.

### 5. File yang dibuat dan bagaimana

- `K/presentation/jobform/JobFormUiState.kt; K/presentation/jobform/JobFormViewModel.kt` — draft dan event input.
- `K/ui/screens/jobform/JobFormSections.kt; K/ui/components/DateTimeFields.kt; K/ui/components/DurationField.kt` — seksi dan input khusus.

### 6. File yang diubah dan bagaimana

- `K/ui/screens/jobform/JobFormScreen.kt` — komposisi field dan action bar.
- `K/ui/components/MoneyField.kt` — format saat mengetik tanpa merusak cursor.
- `K/AppContainer.kt; K/ui/navigation/KonteninNavHost.kt` — factory dan mode form.
- `R/values/strings.xml` — seluruh label, helper, satuan, dan penghitung.

### 7. Testing steps

1. Isi semua field lalu buka/tutup keyboard; Simpan dan field aktif dapat dijangkau.
2. Masukkan 1,5 dan 1.5 pada durasi; input mentah tetap dapat diedit.
3. Ketik/hapus angka di tengah nominal; cursor tidak meloncat secara mengganggu.
4. Recreate Activity; draft tidak kembali kosong. Pilih tanggal masa lalu; tidak diblokir.

### 8. Branch & commit message

- Branch: `frontend/fe-08-job-form`
- Commit message: `feat(frontend): build job form fields and draft state`

---

## FE-09 — Tambahkan validasi form dan tombol Lunas

### 1. Judul

Tambahkan validasi form dan tombol Lunas.

### 2. Subbab detail

Acuan: FR-1.2; FR-3.2; OQ-8. Prasyarat: FE-08 selesai dan sudah digabungkan.

- Validasi kedua nama, acara, sumber: wajib 1–50 karakter setelah trim. Tanggal/jam wajib; jam harus valid.
- Durasi 0,5–24 jam kelipatan 0,5; alamat maksimal 300, catatan maksimal 1000; shareloc opsional tetapi bila diisi harus diawali http:// atau https://.
- Fee wajib integer nonnegatif; dibayar kosong berarti 0 dan wajib berada pada 0..fee. Tangani overflow Long dengan pesan ramah.
- Tombol Lunas mengisi dibayar = fee; nonaktif saat fee kosong, tidak valid atau 0. Fee turun di bawah dibayar menyebabkan error di kolom dibayar.
- Validasi field yang sudah diisi saat kehilangan fokus; validasi seluruh form saat Simpan dan gulir/fokus ke error pertama. Setelah error muncul, perbarui saat input diperbaiki.

### 3. Apa yang dilakukan dan supaya apa

Mencegah data salah masuk ke alur simpan dan memberikan arahan perbaikan yang jelas. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Kerjakan FE-09 sebagai validator murni dengan error bertipe yang dipetakan ke strings.xml. Uji seluruh batas PRD, parsing durasi koma/titik, bilangan besar, dan hubungan fee-dibayar. Hubungkan error ke field serta tombol Lunas tanpa menambahkan validasi jaringan.

### 5. File yang dibuat dan bagaimana

- `K/presentation/jobform/JobFormValidator.kt; K/presentation/jobform/FormValidationError.kt` — validasi dan jenis error.
- `T/presentation/JobFormValidatorTest.kt` — uji batas dan parsing.

### 6. File yang diubah dan bagaimana

- `K/presentation/jobform/JobFormViewModel.kt; K/presentation/jobform/JobFormUiState.kt` — validasi, status Lunas, fokus error.
- `K/ui/screens/jobform/JobFormScreen.kt; K/ui/screens/jobform/JobFormSections.kt` — pesan inline dan gulir ke error.
- `R/values/strings.xml` — pesan wajib, panjang, durasi, tautan dan pembayaran.

### 7. Testing steps

1. Simpan form kosong; tidak memanggil gateway dan fokus menuju error pertama.
2. Uji nama 50/51 karakter, alamat 300/301, catatan 1000/1001; batas valid diterima.
3. Uji durasi 0,5; 24; 0; 24,5; 1,25; hasil mengikuti rentang dan langkah.
4. Uji fee kosong, 0, negatif, overflow; Lunas hanya aktif pada fee valid > 0.
5. Set fee 1.000.000 dan dibayar 1.100.000; error dibayar. Turunkan fee setelah Lunas; error tetap muncul.

### 8. Branch & commit message

- Branch: `frontend/fe-09-form-validation`
- Commit message: `feat(frontend): validate job inputs and paid amount`

---

## FE-10 — Tambahkan autocomplete acara dan sumber job

### 1. Judul

Tambahkan autocomplete acara dan sumber job.

### 2. Subbab detail

Acuan: FR-2; EC-23. Prasyarat: FE-09 selesai dan sudah digabungkan.

- Tampilkan maksimal lima saran saat field fokus atau mengetik, berdasarkan job demo yang ada dan diurutkan frekuensi tertinggi.
- Pencarian tidak membedakan kapital; sebagai keputusan frontend gunakan pencocokan substring. Frekuensi seri diurutkan alfabet agar deterministik.
- Field tetap teks bebas dan dapat diedit setelah memilih saran. Tanpa saran, dropdown disembunyikan.
- Saat simpan, trim dan rapikan spasi ganda; nilai yang sama tanpa membedakan kapital memakai ejaan lama. Bila fixture memiliki beberapa ejaan, pilih ejaan yang tercatat paling awal sebagai aturan deterministik.
- Jangan membuat master data acara/vendor atau layar pengelolaannya.

### 3. Apa yang dilakukan dan supaya apa

Mempercepat pengisian dan mengurangi sumber job yang terpecah akibat perbedaan ejaan. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan FE-10 dengan autocomplete yang bersumber dari JobGateway demo. Pisahkan pencarian saran dan normalisasi menjadi fungsi murni. Terapkan batas lima, urutan frekuensi, kapital tidak sensitif, dan normalisasi sebelum simpan. Buat tes untuk spasi, huruf kapital dan frekuensi seri.

### 5. File yang dibuat dan bagaimana

- `K/ui/components/AutocompleteField.kt` — field dan dropdown aksesibel.
- `K/presentation/jobform/AutocompleteRules.kt` — pencarian, pengurutan dan normalisasi.
- `T/presentation/AutocompleteRulesTest.kt` — kasus saran dan normalisasi.

### 6. File yang diubah dan bagaimana

- `K/ui/screens/jobform/JobFormSections.kt` — mengganti field acara/sumber dengan autocomplete.
- `K/presentation/jobform/JobFormViewModel.kt; K/presentation/jobform/JobFormUiState.kt` — query/saran dan normalisasi draft.
- `R/values/strings.xml` — label semantik saran.
- `docs/frontend-decisions.md` — aturan pencocokan dan tie-breaker.

### 7. Testing steps

1. Seed lebih dari lima acara; fokus field hanya menampilkan lima teratas.
2. Ketik resepsi dan pilih Resepsi; lalu edit menjadi teks baru, tetap diperbolehkan.
3. Simpan sumber dengan spasi berlebih dan kapital berbeda; ejaan lama dipakai.
4. Hapus satu job demo; saran berikutnya mengikuti data terbaru, tanpa daftar master tersembunyi.

### 8. Branch & commit message

- Branch: `frontend/fe-10-autocomplete`
- Commit message: `feat(frontend): add event and source autocomplete`

---

## FE-11 — Hubungkan simpan demo, ubah, duplikat, dan keluar form

### 1. Judul

Hubungkan simpan demo, ubah, duplikat, dan keluar form.

### 2. Subbab detail

Acuan: FR-1.1, 1.7; DESIGN D-6, D-13. Prasyarat: FE-10 selesai dan sudah digabungkan.

- Hubungkan submit valid ke gateway demo; tahan ketuk ganda, tampilkan loading dan pertahankan draft jika gagal.
- Mode ubah memuat Job berdasarkan ID dan mempertahankan ID/createdAt. Mode tambah mendapat ID baru dari gateway.
- Duplikat menyalin nama, jam mulai, durasi, alamat, shareloc, sumber, fee, catatan; tanggal dan acara kosong, dibayar 0. Job asli tidak berubah.
- Judul duplikat: Tambah Job (Disalin dari Andi & Sari), memakai nama dinamis dan layout yang dapat membungkus.
- Jika draft berubah lalu menutup lewat toolbar atau system back, tampilkan Buang perubahan?; memilih tetap mengedit mempertahankan draft. Bandingkan dengan snapshot awal mode terkait.
- Sesudah simpan berhasil kembali ke layar asal dengan data terbaru. Form tambah dari Beranda kembali ke Beranda; ubah/duplikat dari Detail kembali ke Detail asal sebagai keputusan navigasi sementara.

### 3. Apa yang dilakukan dan supaya apa

Menyelesaikan alur CRUD dari sisi frontend dan melindungi input yang belum disimpan. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Kerjakan FE-11 menggunakan gateway in-memory bersama. Implementasikan tambah/ubah/duplikat persis sesuai field PRD, busy state, retry, dan dialog Buang perubahan untuk back gesture maupun toolbar. Jangan menambahkan dialog bentrok pada task ini; sediakan titik pemeriksaan untuk FE-12. Tidak boleh membuat penyimpanan database.

### 5. File yang dibuat dan bagaimana

- `K/presentation/jobform/JobDraftFactory.kt` — pemetaan mode tambah/ubah/duplikat.
- `K/ui/screens/jobform/DiscardChangesDialog.kt` — konfirmasi meninggalkan form.
- `T/presentation/JobDraftFactoryTest.kt` — field yang disalin/reset dan identitas.

### 6. File yang diubah dan bagaimana

- `K/presentation/jobform/JobFormViewModel.kt; K/presentation/jobform/JobFormUiState.kt` — submit, pemuatan, dirty state dan efek navigasi.
- `K/demo/DemoJobGateway.kt` — mutasi in-memory dan kegagalan deterministik.
- `K/ui/screens/jobform/JobFormScreen.kt; K/ui/navigation/KonteninNavHost.kt` — mode, back handler, hasil simpan.
- `R/values/strings.xml; docs/frontend-decisions.md` — copy dan keputusan navigasi.

### 7. Testing steps

1. Tambah job valid; Beranda menampilkan data baru hanya sekali meski Simpan diketuk berulang.
2. Ubah nama/fee; ID tetap dan layar asal menampilkan perubahan.
3. Duplikat; tanggal/acara kosong dan dibayar 0. Simpan setelah melengkapi; jumlah job bertambah satu dan sumber tidak berubah.
4. Ubah draft lalu back; batal membuang mempertahankan isian, konfirmasi membuang kembali ke asal.
5. Simulasikan simpan gagal; draft tetap ada dan retry berhasil tanpa duplikasi.

### 8. Branch & commit message

- Branch: `frontend/fe-11-job-form-actions`
- Commit message: `feat(frontend): connect demo save edit and duplicate flows`

---

## FE-12 — Tambahkan dialog bentrok sebelum simpan

### 1. Judul

Tambahkan dialog bentrok sebelum simpan.

### 2. Subbab detail

Acuan: FR-1.8; EC-4–6, EC-25; DESIGN D-5, D-7. Prasyarat: FE-11 selesai dan sudah digabungkan.

- Panggil pemeriksaan bentrok melalui kontrak sebelum mutasi. Implementasi demo menggunakan interval menit absolut dari tanggal/jam/durasi.
- Kandidat tanggal minus satu hingga plus satu hari; overlap jika mulaiA < selesaiB dan mulaiB < selesaiA. Abaikan ID sendiri saat ubah.
- Dialog menampilkan seluruh bentrok dengan nama, tanggal, acara dan rentang jam; daftar dapat digulir.
- Kembali sebagai tombol utama; Tetap Simpan outline. Kembali mempertahankan draft tanpa simpan; Tetap Simpan menyimpan satu kali tanpa mengulang dialog tanpa akhir.
- Cegah draft berubah saat hasil konfirmasi sedang diproses; bila berubah setelah Kembali, lakukan pemeriksaan baru pada submit berikutnya.

### 3. Apa yang dilakukan dan supaya apa

Memperingatkan jadwal tumpang tindih sambil tetap memperbolehkan pengguna memilih menyimpan. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan FE-12: kontrak pemeriksaan bentrok dihubungkan ke dialog dan implementasi demo memiliki aturan overlap PRD. Tambahkan uji interval berurutan, lintas hari, durasi 24 jam, edit diri sendiri dan beberapa konflik. Ini simulasi frontend; backend nanti wajib menjadi sumber pemeriksaan final.

### 5. File yang dibuat dan bagaimana

- `K/demo/DemoConflictRules.kt` — perhitungan bentrok untuk demo.
- `K/ui/screens/jobform/ConflictDialog.kt` — daftar bentrok dan dua aksi.
- `T/demo/DemoConflictRulesTest.kt` — tabel kasus overlap.

### 6. File yang diubah dan bagaimana

- `K/demo/DemoJobGateway.kt` — hasil bentrok melalui kontrak.
- `K/presentation/jobform/JobFormViewModel.kt; K/presentation/jobform/JobFormUiState.kt` — langkah cek/konfirmasi/simpan.
- `K/ui/screens/jobform/JobFormScreen.kt` — dialog.
- `R/values/strings.xml` — judul, pengantar, Kembali dan Tetap Simpan.

### 7. Testing steps

1. Job 10.00–14.00 bertemu 14.00–16.00; tidak ada dialog. Mulai 13.59; dialog muncul.
2. Job 22.00–02.00 bertemu job hari berikut pukul 01.00; terdeteksi dan tanggal terlihat.
3. Ubah job tanpa konflik lain; tidak bentrok dengan dirinya sendiri.
4. Kembali tidak menyimpan; Tetap Simpan menambah/mengubah tepat satu job.
5. Buat beberapa konflik dan nama panjang; semua dapat dibaca melalui scroll.

### 8. Branch & commit message

- Branch: `frontend/fe-12-conflict-dialog`
- Commit message: `feat(frontend): show non-blocking schedule conflict warning`

---

## FE-13 — Implementasikan Detail Job, Maps, dan hapus

### 1. Judul

Implementasikan Detail Job, Maps, dan hapus.

### 2. Subbab detail

Acuan: FR-1.5–1.6, 1.9; EC-12, EC-21, EC-24; DESIGN D-3, D-11. Prasyarat: FE-12 selesai dan sudah digabungkan.

- Tampilkan semua data pengguna: nama lengkap, acara, sumber, tanggal/rentang, durasi, alamat/shareloc, pembayaran, catatan; identitas teknis tidak perlu ditampilkan.
- Pasang bottom action bar Ubah, Duplikat, Hapus. Hapus wajib konfirmasi dan tidak memiliki undo.
- Buka Maps hanya muncul jika shareloc tidak kosong dan dapat dipakai menurut adapter. Bila percobaan membuka gagal, tampilkan pesan PRD dan sembunyikan tombol untuk nilai link tersebut sesuai D-11; tampilkan kembali setelah link diperbaiki atau evaluasi ulang berhasil.
- Pada fase frontend, pembukaan Maps adalah event ke adapter demo; tidak benar-benar menjalankan ACTION_VIEW.
- Job tidak ditemukan menampilkan pesan lalu kembali ke Beranda. Setelah hapus sukses, Beranda/Rekap ikut berubah; jika gagal, Detail tetap terbuka.

### 3. Apa yang dilakukan dan supaya apa

Memberikan satu tempat untuk melihat dan mengelola job tanpa bergantung pada penyimpanan atau integrasi Maps nyata. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Kerjakan FE-13 dengan state detail reaktif berdasarkan ID, tampilan seluruh field, navigasi ubah/duplikat, konfirmasi hapus, dan ExternalActionGateway demo. Ikuti resolusi D-11: link bermasalah menyembunyikan tombol; kegagalan percobaan tetap diberi pesan. Jangan mengimplementasikan pembatalan alarm nyata.

### 5. File yang dibuat dan bagaimana

- `K/presentation/jobdetail/JobDetailUiState.kt; K/presentation/jobdetail/JobDetailViewModel.kt` — state detail, hapus dan Maps.
- `K/ui/screens/jobdetail/DeleteJobDialog.kt; K/ui/screens/jobdetail/JobDetailSections.kt` — konfirmasi dan seksi detail.

### 6. File yang diubah dan bagaimana

- `K/ui/screens/jobdetail/JobDetailScreen.kt; K/ui/navigation/KonteninNavHost.kt; K/AppContainer.kt` — UI dan navigasi.
- `K/demo/DemoJobGateway.kt; K/demo/DemoExternalActionGateway.kt` — hasil hapus dan pembukaan link simulasi.
- `R/values/strings.xml` — job hilang, konfirmasi hapus dan error Maps.

### 7. Testing steps

1. Buka nama/catatan panjang; isi lengkap dapat dibaca, action bar tetap terjangkau.
2. Shareloc kosong/rusak: tombol tersembunyi. Link valid: tap merekam intent demo. Kegagalan pembuka: pesan muncul lalu tombol disembunyikan.
3. Batal hapus tidak mengubah data; konfirmasi hapus menghilangkan job dari Beranda dan Rekap.
4. Simulasikan hapus gagal; job tidak hilang. Buka ID yang tidak ada; pesan lalu Beranda.
5. Pastikan copy demo tidak menyatakan alarm sungguhan telah dibatalkan.

### 8. Branch & commit message

- Branch: `frontend/fe-13-job-detail`
- Commit message: `feat(frontend): implement job detail and delete flow`

---

## FE-14 — Implementasikan perubahan pembayaran di Detail

### 1. Judul

Implementasikan perubahan pembayaran di Detail.

### 2. Subbab detail

Acuan: FR-3; EC-7–8. Prasyarat: FE-13 selesai dan sudah digabungkan.

- Tampilkan fee, total dibayar, sisa, dan chip dari nilai terbaru; tidak ada daftar transaksi.
- Tandai Lunas hanya muncul ketika belum lunas dan mengirim total dibayar sama dengan fee.
- Ubah Pembayaran membuka dialog satu kolom total Sudah dibayar; angka menggantikan total sebelumnya, bukan menambah transaksi.
- Validasi 0..fee, error inline, loading, kegagalan, dan anti-ketuk ganda. Batal tidak mengubah data.
- Perubahan pembayaran menyebar ke Beranda, filter, Rekap, Piutang; tidak mengubah jadwal atau state pengingat.

### 3. Apa yang dilakukan dan supaya apa

Memudahkan pencatatan pelunasan dengan angka yang konsisten di semua layar. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan FE-14 dengan dialog pembayaran dan tombol Tandai Lunas. Gunakan validator pembayaran bersama, bukan aturan berbeda dari Form. Mutasi hanya total dibayar di gateway demo, lalu semua observer diperbarui. Pastikan pembayaran tidak memanggil penjadwalan ulang pengingat.

### 5. File yang dibuat dan bagaimana

- `K/ui/screens/jobdetail/PaymentDialog.kt` — input total dan error.
- `K/presentation/PaymentValidator.kt` — validasi pembayaran bersama.
- `T/presentation/PaymentValidatorTest.kt` — batas pembayaran.

### 6. File yang diubah dan bagaimana

- `K/presentation/jobdetail/JobDetailViewModel.kt; K/presentation/jobdetail/JobDetailUiState.kt` — state dialog dan update pembayaran.
- `K/ui/screens/jobdetail/JobDetailSections.kt; K/ui/screens/jobdetail/JobDetailScreen.kt` — tombol dan dialog.
- `K/presentation/jobform/JobFormValidator.kt` — menggunakan validator bersama.
- `K/demo/DemoJobGateway.kt; R/values/strings.xml` — mutasi demo dan label total dibayar.

### 7. Testing steps

1. Fee Rp 2.000.000, dibayar Rp 500.000; ubah total ke Rp 800.000, sisa menjadi Rp 1.200.000.
2. Masukkan nilai di atas fee; dialog tetap terbuka dengan error, data lama tidak berubah.
3. Tandai Lunas; chip dan tombol tersebut hilang, filter belum lunas tidak lagi memuat job.
4. Fee 0 tidak menampilkan Tandai Lunas. Batalkan dialog; total tetap.
5. Simulasikan gagal update; angka lama tetap dan retry dapat dilakukan tanpa aksi ganda.

### 8. Branch & commit message

- Branch: `frontend/fe-14-payments`
- Commit message: `feat(frontend): implement payment update and mark-paid actions`

---

## FE-15 — Implementasikan Rekap, Per Sumber, dan Piutang

### 1. Judul

Implementasikan Rekap, Per Sumber, dan Piutang.

### 2. Subbab detail

Acuan: FR-5; OQ-5, OQ-9; DESIGN D-2. Prasyarat: FE-14 selesai dan sudah digabungkan.

- Buat pilihan Hari Ini, Minggu Ini, Bulan Ini; minggu mulai Senin, zona Asia/Jakarta. Periode memakai tanggal mulai acara, bukan tanggal pembayaran.
- Urutan seksi: ringkasan, Per Sumber Job, Daftar Job, Piutang (semua periode).
- Ringkasan berisi jumlah job, total fee, dibayar dan sisa. Per sumber berisi jumlah job, total fee dan sisa; nominal penuh tanpa singkatan jt.
- Piutang hanya job yang waktu selesainya telah tercapai dan belum lunas, urut tanggal acara terlama; tidak mengikuti pilihan periode. Fee nol bukan piutang.
- Gunakan satu daftar lazy utama dengan seksi, hindari lazy vertikal bersarang tanpa batas tinggi. Kosong: total Rp 0 dan copy PRD; sumber disembunyikan jika periode kosong.
- Agregasi demo berada di fungsi terpisah agar tidak tersebar di composable dan mudah diganti pada integrasi.

### 3. Apa yang dilakukan dan supaya apa

Memberikan ringkasan penghasilan dan fee yang perlu ditagih berdasarkan data demo yang sama. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Kerjakan FE-15 menggunakan gateway bersama dan AppClock. Implementasikan agregasi presentasi yang dapat diuji, periode Senin–Minggu, tanggal acara, pengelompokan sumber, serta piutang lintas periode setelah waktu selesai. Gunakan JobCard padat, nominal penuh, lazy list, dan navigasi Detail.

### 5. File yang dibuat dan bagaimana

- `K/presentation/recap/RecapUiState.kt; K/presentation/recap/RecapViewModel.kt; K/presentation/recap/RecapCalculator.kt` — periode dan agregasi.
- `K/ui/screens/recap/RecapSections.kt` — ringkasan, per sumber, daftar, piutang.
- `T/presentation/RecapCalculatorTest.kt` — akurasi total, batas periode dan piutang.

### 6. File yang diubah dan bagaimana

- `K/ui/screens/recap/RecapScreen.kt; K/ui/navigation/KonteninNavHost.kt; K/AppContainer.kt` — integrasi layar.
- `R/values/strings.xml` — label periode, nominal, dan kosong.

### 7. Testing steps

1. Pada clock 29 Oktober 2026 12.00 WIB, seed A tanggal 29 pukul 08.00 durasi 2 jam fee 1.000.000 dibayar 400.000; B tanggal 29 pukul 15.00 fee 2.000.000 lunas; C tanggal 20 fee 500.000 belum dibayar.
2. Hari Ini menghasilkan 2 job, fee 3.000.000, dibayar 2.400.000, sisa 600.000. Piutang menghasilkan C lalu A, total 1.100.000.
3. Pindah periode; piutang tetap sama, ringkasan berubah. Uji minggu lintas bulan/tahun dan pergantian bulan.
4. Tandai A lunas di Detail; rekap dan piutang langsung berubah.
5. Periode kosong menunjukkan Rp 0 dan copy yang tepat, tidak ada bagian sumber kosong yang mengganggu.

### 8. Branch & commit message

- Branch: `frontend/fe-15-recap`
- Commit message: `feat(frontend): add recap source totals and receivables`

---

## FE-16 — Implementasikan Pengaturan dan simulasi pengingat

### 1. Judul

Implementasikan Pengaturan dan simulasi pengingat.

### 2. Subbab detail

Acuan: FR-4, FR-6; OQ-6; DESIGN D-4. Prasyarat: FE-15 selesai dan sudah digabungkan.

- Tampilkan status izin notifikasi dan alarm tepat waktu berupa teks + ikon, banner hanya di Pengaturan, tombol Buka Pengaturan, versi build aktual.
- Sediakan dialog petunjuk baterai/autostart dengan teks umum dan aksi ke pengaturan melalui adapter demo; jangan mengarang menu khusus versi Poco yang belum diketahui.
- Uji Pengingat tersedia dalam UI semua build; untuk frontend hasilnya simulasi, tanpa notifikasi OS. Bedakan status dijadwalkan, izin ditolak, dan gagal.
- Saat simpan pertama, tampilkan penjelasan notifikasi dan simulasikan Lanjut/Nanti/ditolak. Job tetap tersimpan jika izin ditolak; state ini hanya bertahan selama sesi demo.
- Gunakan hasil ReminderGateway untuk snackbar pengingat dilewati: Beberapa pengingat dilewati karena acara terlalu dekat, durasi dasar 4 detik; boleh diperpanjang untuk aksesibilitas.
- Sediakan event membuka Detail dari ID job seolah notifikasi diketuk; uji ID valid dan dihapus. AlarmManager, receiver, reboot, dan permission launcher asli belum dibuat.

### 3. Apa yang dilakukan dan supaya apa

Menyelesaikan pengalaman izin dan pengingat tanpa mencampurkan pekerjaan layanan Android ke fase frontend. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Implementasikan FE-16 melalui ReminderGateway dan ExternalActionGateway demo. Bangun Pengaturan, dialog baterai, penjelasan izin saat simpan pertama, banner, Uji Pengingat dan event navigasi notifikasi simulasi. Jangan menjadwalkan alarm nyata. Tandai hasil demo dengan jelas dan catat bahwa reliabilitas alarm belum diuji.

### 5. File yang dibuat dan bagaimana

- `K/presentation/settings/SettingsUiState.kt; K/presentation/settings/SettingsViewModel.kt` — state izin dan pengingat.
- `K/ui/screens/settings/BatteryHelpDialog.kt; K/ui/screens/settings/NotificationRationaleDialog.kt` — dua dialog.
- `K/ui/debug/DemoControls.kt` — pemilih skenario debug dan pemicu tap notifikasi simulasi.

### 6. File yang diubah dan bagaimana

- `K/ui/screens/settings/SettingsScreen.kt; K/AppContainer.kt` — render status dan factory.
- `K/demo/DemoReminderGateway.kt; K/demo/DemoExternalActionGateway.kt` — respons simulasi.
- `K/presentation/jobform/JobFormViewModel.kt; K/ui/KonteninApp.kt; K/ui/navigation/KonteninNavHost.kt` — efek pascasimpan dan event ID notifikasi.
- `R/values/strings.xml` — izin, petunjuk, versi, copy pengingat dan label simulasi.

### 7. Testing steps

1. Simulasikan kedua izin aktif/nonaktif; banner serta tombol mengikuti status, Beranda tidak mendapat banner izin.
2. Simpan pertama lalu tolak izin; job tetap ada dan Pengaturan menjelaskan status.
3. Simulasikan pengingat dilewati; snackbar tampil sekitar 4 detik pada aksesibilitas normal.
4. Uji Pengingat demo sukses/gagal; UI tidak menyatakan notifikasi OS benar-benar terkirim.
5. Kirim event ID valid; Detail terbuka. ID dihapus; pesan lalu Beranda.
6. Buka dialog baterai; teks dan aksi terlihat, tidak berisi instruksi vendor yang belum diverifikasi.

### 8. Branch & commit message

- Branch: `frontend/fe-16-settings-reminders`
- Commit message: `feat(frontend): add settings and reminder simulation flows`

---

## FE-17 — Implementasikan UI ekspor dan impor simulasi

### 1. Judul

Implementasikan UI ekspor dan impor simulasi.

### 2. Subbab detail

Acuan: FR-7; EC-16–18; DESIGN §6.5, §7.2. Prasyarat: FE-16 selesai dan sudah digabungkan.

- Bangun alur Ekspor Data: memilih tujuan simulasi, memproses, berhasil, gagal dengan Coba lagi, atau dibatalkan. Tampilkan nama saran kontenin-backup-YYYYMMDD.json.
- Bangun alur Impor Data: memilih fixture cadangan, memvalidasi via gateway demo, menampilkan jumlah job, konfirmasi Ganti Data/Batal, memproses lalu hasil.
- Satu job tidak valid atau schema lebih baru membuat seluruh impor ditolak sebelum konfirmasi; data demo lama tidak berubah.
- Konfirmasi berbunyi Semua data saat ini akan diganti dengan isi file (N job). Terapkan penggantian atomik hanya pada koleksi demo saat sukses; gagal tidak meninggalkan data parsial.
- Nonaktifkan kedua aksi saat proses; dialog Memproses muncul bila > 500 ms. Bedakan batal dari sukses; pembatalan ekspor mengikuti pesan gagal dengan opsi coba lagi sesuai EC-18.
- Tidak membuat file JSON nyata, SAF picker, serializer, transaksi Room, atau reschedule alarm. Semua keberhasilan diberi penanda simulasi.

### 3. Apa yang dilakukan dan supaya apa

Membuat seluruh pengalaman cadangan dapat direview tanpa menunggu implementasi penyimpanan yang berisiko mengganti data. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Kerjakan FE-17 sebagai UI cadangan dengan DemoBackupGateway. Sediakan fixture valid, rusak, schema lebih baru, satu job invalid, gagal proses, dan batal. Validasi simulasi terjadi sebelum konfirmasi; penggantian koleksi demo hanya setelah persetujuan. Jangan membuka picker sistem atau mengklaim file backup sungguhan telah dibuat.

### 5. File yang dibuat dan bagaimana

- `K/presentation/settings/BackupUiState.kt; K/presentation/settings/BackupController.kt` — state machine ekspor/impor.
- `K/ui/screens/settings/ImportConfirmationDialog.kt; K/ui/screens/settings/BackupProgressDialog.kt` — konfirmasi dan proses.
- `K/demo/DemoBackupFixtures.kt` — hasil validasi/isi demo untuk setiap skenario.

### 6. File yang diubah dan bagaimana

- `K/demo/DemoBackupGateway.kt; K/demo/DemoJobGateway.kt; K/AppContainer.kt` — penggantian koleksi demo bersama.
- `K/ui/screens/settings/SettingsScreen.kt; K/presentation/settings/SettingsViewModel.kt` — aksi dan hasil.
- `K/ui/debug/DemoControls.kt; R/values/strings.xml` — skenario dan copy.

### 7. Testing steps

1. Pilih fixture 3 job valid; konfirmasi menunjukkan 3. Batal menjaga seluruh data lama.
2. Setujui Ganti Data; koleksi demo menjadi tepat 3, Beranda/Rekap mengikuti.
3. Fixture rusak/schema baru/satu job invalid ditolak; tidak ada konfirmasi penggantian atau perubahan data.
4. Simulasikan gagal proses; koleksi lama utuh. Ketuk berulang saat busy; tidak ada proses kedua.
5. Ekspor gagal/dibatalkan menawarkan coba lagi; sukses diberi label simulasi dan tidak mengklaim file tersimpan di perangkat.

### 8. Branch & commit message

- Branch: `frontend/fe-17-backup-ui`
- Commit message: `feat(frontend): add simulated export and import workflows`

---

## FE-18 — Rapikan state, aksesibilitas, dan layout seluruh layar

### 1. Judul

Rapikan state, aksesibilitas, dan layout seluruh layar.

### 2. Subbab detail

Acuan: PRD §12; OQ-19; DESIGN §7–8. Prasyarat: FE-17 selesai dan sudah digabungkan.

- Audit loading, kosong, error, retry dan disabled semua layar; tidak ada spinner tanpa akhir atau exception mentah.
- Loading daftar > 300 ms memakai tiga skeleton statis; operasi cadangan > 500 ms memakai indikator sesuai task sebelumnya. Retry mutasi tidak mengulang operasi yang sudah sukses.
- Periksa TalkBack: urutan baca, label tombol ikon, status pembayaran/izin, pesan error, fokus dialog, dan kembalinya fokus setelah dialog ditutup.
- Pastikan font 200%, layar 360 dp, mode gelap, nominal besar, judul duplikat panjang, dan keyboard tidak merusak layout. Tanggal/jam boleh membungkus agar terbaca.
- Pastikan semua copy berada di strings.xml, tanpa warna sebagai satu-satunya indikator. Tidak menambahkan animasi dekoratif, network error, atau pull-to-refresh.
- Review demo dengan 1000 job; gunakan lazy composition dan key stabil. Tidak menambahkan paging database pada fase ini.

### 3. Apa yang dilakukan dan supaya apa

Menghasilkan frontend yang utuh dan dapat digunakan, termasuk pada perangkat kecil serta kebutuhan aksesibilitas dasar. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Audit dan perbaiki FE-18 pada seluruh halaman yang sudah selesai. Gunakan matriks state, TalkBack, font 200%, light/dark, keyboard dan 1000 job. Perbaiki penyebab layout/state, bukan hanya screenshot tunggal. Catat hasil uji aktual, bedakan belum diuji dari lulus, dan jangan memperluas fitur.

### 5. File yang dibuat dan bagaimana

- `docs/frontend-qa-checklist.md` — matriks layar × state × light/dark/font/TalkBack, hasil, perangkat dan temuan.
- `K/ui/preview/ScreenPreviewData.kt` — provider preview state ekstrem bila belum tersedia.

### 6. File yang diubah dan bagaimana

- `K/ui/components/LoadingState.kt; K/ui/components/ConfirmationDialog.kt; K/ui/components/KonteninTextField.kt` — skeleton, fokus, error aksesibel.
- `K/ui/screens/home/HomeScreen.kt; K/ui/screens/recap/RecapScreen.kt; K/ui/screens/settings/SettingsScreen.kt; K/ui/screens/jobform/JobFormScreen.kt; K/ui/screens/jobdetail/JobDetailScreen.kt` — perbaikan layout/state yang teridentifikasi.
- `K/ui/screens/jobform/JobFormSections.kt; K/ui/screens/jobdetail/JobDetailSections.kt; K/ui/screens/recap/RecapSections.kt` — wrapping dan semantik.
- `R/values/strings.xml` — kelengkapan copy dan deskripsi aksesibilitas.

### 7. Testing steps

1. Jalankan semua layar pada light/dark dan font 100%/200%; tidak ada aksi utama yang hilang atau teks penting tertutup.
2. Gunakan TalkBack untuk tambah job, memperbaiki error, membuka detail dan membatalkan hapus tanpa bergantung pada visual.
3. Aktifkan loading/error/kosong melalui kontrol demo; setiap state dapat keluar atau retry dengan benar.
4. Scroll daftar 1000 job, masuk Detail lalu kembali; tidak crash dan posisi pulih.
5. Catat model perangkat/API aktual; Poco X3 GT hanya ditandai diuji jika benar-benar digunakan.

### 8. Branch & commit message

- Branch: `frontend/fe-18-ui-polish`
- Commit message: `fix(frontend): polish states accessibility and compact layouts`

---

## FE-19 — Verifikasi alur frontend dari awal sampai akhir

### 1. Judul

Verifikasi alur frontend dari awal sampai akhir.

### 2. Subbab detail

Acuan: Seluruh alur frontend FR-1–7. Prasyarat: FE-18 selesai dan sudah digabungkan.

- Lakukan smoke test terhubung: tambah → cek bentrok → simpan → ubah → duplikat → pembayaran → rekap → hapus → impor simulasi.
- Pertahankan unit test hanya untuk logika murni yang bermakna; gunakan Compose UI test untuk alur kritis dan checklist manual untuk review visual.
- Reset fixture/clock sebelum tiap skenario agar hasil tidak dipengaruhi urutan pengujian. Gunakan hasil tertunda untuk menguji ketuk ganda dan error.
- Jalankan assembleDebug, testDebugUnitTest, lintDebug dan connectedDebugAndroidTest bila perangkat tersedia. Jika tidak tersedia, catat pengujian perangkat sebagai belum dilakukan.
- Pastikan kontrol demo hanya ada di debug; aplikasi fase frontend tetap memiliki keterangan data simulasi yang jelas. Jangan menamai build ini siap produksi.

### 3. Apa yang dilakukan dan supaya apa

Membuktikan seluruh halaman bekerja sebagai satu aplikasi, bukan sekadar layar terpisah. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Kerjakan FE-19 dengan tes alur kritis frontend terhadap gateway demo, clock tetap, dan reset fixture per test. Tambahkan coverage UI untuk navigasi, validasi, simpan/konflik, pembayaran dan impor batal/gagal. Jalankan pemeriksaan yang tersedia dan catat output nyata, bukan klaim lulus tanpa eksekusi. Jangan menguji alarm atau database yang belum dibuat.

### 5. File yang dibuat dan bagaimana

- `A/FrontendJourneyTest.kt` — alur tambah, konflik, ubah, pembayaran, duplikat, hapus.
- `A/BackupUiFlowTest.kt` — konfirmasi, batal, invalid, gagal dan sukses impor demo.
- `A/NavigationStateTest.kt` — tab, kembali, draft dan job tidak ditemukan.
- `docs/frontend-test-report.md` — perintah, hasil aktual, perangkat, bukti dan issue tersisa.

### 6. File yang diubah dan bagaimana

- `app/build.gradle.kts` — dependensi/runner UI test bila belum dikonfigurasi.
- `K/ui/debug/DemoControls.kt; K/AppContainer.kt` — reset skenario dan pembatasan kontrol debug.
- `docs/frontend-qa-checklist.md` — hasil regresi dan status temuan.
- `File UI/presentation yang gagal uji` — hanya perbaikan terukur, sebutkan path aktual pada laporan dan commit.

### 7. Testing steps

1. Run :app:testDebugUnitTest; seluruh aturan yang diuji lulus.
2. Run :app:assembleDebug dan :app:lintDebug; perbaiki error yang relevan dan dokumentasikan warning yang tersisa.
3. Run :app:connectedDebugAndroidTest pada perangkat tersedia; alur kritis lulus. Jika terhalang lingkungan, tulis blocker secara spesifik.
4. Lakukan smoke test manual dan cocokkan angka Rekap dengan fixture FE-15.
5. Periksa tidak ada perubahan data parsial setelah gagal simpan/impor, serta callback tidak dieksekusi ganda.

### 8. Branch & commit message

- Branch: `frontend/fe-19-frontend-verification`
- Commit message: `test(frontend): verify complete demo user journeys`

---

## FE-20 — Finalisasi dokumentasi dan serah terima ke fase backend

### 1. Judul

Finalisasi dokumentasi dan serah terima ke fase backend.

### 2. Subbab detail

Acuan: Batas frontend/backend; PRD §13 disusun ulang sesuai instruksi pengguna. Prasyarat: FE-19 selesai dan sudah digabungkan.

- Dokumentasikan seluruh layar, route, ViewModel, kontrak, fixture, cara reset, dan daftar skenario demo.
- Petakan setiap gateway ke integrasi tahap berikutnya tanpa mengimplementasikannya: JobGateway ke penyimpanan lokal; ReminderGateway ke izin/alarm; BackupGateway ke SAF/JSON/transaksi; ExternalActionGateway ke Intent Android.
- Jelaskan state yang tetap menjadi tanggung jawab UI dan hasil/error yang harus diberikan backend. Catat fungsi aturan demo yang harus dipakai ulang atau dibandingkan dengan aturan final agar tidak menyimpang.
- Catat perbedaan frontend selesai versus produk selesai: data persisten, reliabilitas alarm setelah reboot, round-trip backup asli dan rollback belum terbukti.
- Tutup semua isu yang memblokir UI, sertakan daftar uji perangkat yang belum dilakukan, dan tetapkan hasil review frontend sebelum pekerjaan backend dimulai.

### 3. Apa yang dilakukan dan supaya apa

Menjadikan frontend landasan yang jelas bagi integrasi berikutnya tanpa perlu menebak kontrak atau mengulang desain. Hasil task diterima jika perilaku pada subbab detail terpenuhi dan testing steps di bawah menunjukkan hasil yang diharapkan.

### 4. Prompt AI

> Finalisasikan FE-20 dengan dokumentasi serah terima berdasarkan kode aktual dan hasil FE-19. Jangan menulis implementasi backend. Petakan operasi kontrak, state UI, respons sukses/gagal dan pekerjaan integrasi yang tertunda. Nyatakan frontend siap integrasi hanya jika kriteria selesai frontend terpenuhi; jangan menyatakan MVP selesai.

### 5. File yang dibuat dan bagaimana

- `docs/frontend-handoff.md` — panduan integrasi, daftar layar, event, error, simulasi dan batas pengujian.
- `docs/frontend-acceptance.md` — checklist selesai frontend, hasil review, blocker, dan keputusan mulai fase berikutnya.

### 6. File yang diubah dan bagaimana

- `README.md` — cara build demo dan tautan dokumentasi.
- `docs/frontend-contracts.md` — selaraskan dengan kontrak aktual.
- `docs/frontend-decisions.md` — finalisasi keputusan/resolusi sumber.
- `docs/frontend-test-report.md; docs/frontend-qa-checklist.md` — status akhir dan isu tersisa.

### 7. Testing steps

1. Minta reviewer mengikuti README dari checkout bersih; aplikasi demo bisa dibangun/dijalankan.
2. Cocokkan signature kontrak dengan dokumen; setiap operasi memiliki hasil sukses, gagal, dan pembatalan bila relevan.
3. Pastikan seluruh FE-01–FE-19 selesai, atau blocker ditulis jelas tanpa menandainya lulus.
4. Periksa tidak ada Room/DAO/AlarmManager/receiver/SAF/serializer produksi yang terselip dalam scope frontend.
5. Verifikasi daftar pengujian backend yang tertunda tercantum dan tidak dianggap telah lulus.

### 8. Branch & commit message

- Branch: `frontend/fe-20-frontend-handoff`
- Commit message: `docs(frontend): finalize acceptance and backend handoff`

---

## 5. Gerbang selesai frontend

Frontend boleh diserahterimakan ke fase backend setelah checklist berikut terpenuhi:

- [ ] Kelima layar utama/turunan selesai: Beranda, Form Job, Detail Job, Rekap, Pengaturan.
- [ ] Tambah, ubah, duplikat, hapus, autocomplete, bentrok, pembayaran dan rekap bekerja dengan satu sumber data demo bersama.
- [ ] Ekspor/impor dan pengingat memiliki alur simulasi lengkap, jelas ditandai sebagai demo.
- [ ] Empty, loading, error, retry, dialog, ketuk ganda dan back navigation telah diperiksa.
- [ ] Light/dark, font 200%, TalkBack baseline, keyboard dan 1000 job lolos pemeriksaan yang disepakati; hasil nyata tercatat.
- [ ] Build, lint, unit test dan uji alur pada perangkat/emulator selesai; blocker yang mencegah penerimaan sudah ditutup.
- [ ] Tidak ada fitur fase 2 atau layanan backend nyata yang disisipkan.
- [ ] Kontrak, keputusan, laporan QA dan handoff sesuai kode aktual.
- [ ] Hasil review frontend tercatat sebagai diterima sebelum memulai implementasi backend.

**Belum boleh dianggap selesai pada tahap ini:** penyimpanan setelah aplikasi ditutup, migrasi Room, alarm tepat waktu/layar mati/reboot/update aplikasi, permission flow OS asli, Maps nyata, ekspor–hapus–impor file asli, rollback transaksi, dan pemulihan alarm setelah impor gagal. Pengujian tersebut menunggu fase backend dan integrasi.

Scope awal tetap mencakup seluruh task. Jika kelak pemilik meminta pengurangan, urutan PRD OQ-4 adalah Piutang → Per Sumber Job → filter Belum lunas → Duplikat. **Ekspor/Impor tidak dipotong.** Jangan melakukan pemotongan otomatis.
