# Frontend Architecture Decisions

Dokumentasi keputusan arsitektur untuk frontend Kontenin.

## Data Demo

**Keputusan**: Menggunakan implementasi in-memory untuk semua gateway selama fase pengembangan UI.

**Alasan**:
- Memungkinkan pengembangan UI tanpa bergantung pada backend
- Memfasilitasi testing berbagai skenario (sukses, error, pending, empty)
- Kontrak gateway menjadi titik integrasi yang jelas untuk implementasi nyata nanti

**Implementasi**:
- `DemoJobGateway`: MutableStateFlow<Map<String, Job>> sebagai storage
- Semua observer menerima data dari Flow yang sama
- Perubahan data langsung terlihat di semua layar yang subscribe

**Batasan**:
- Data tidak persisten antar proses restart
- Tidak ada validasi database constraint (unique, foreign key)
- Operasi tidak bisa gagal karena concurrency (optimistic locking)

## Reset Proses

**Keputusan**: Data kembali ke seed fixtures setiap kali aplikasi di-restart.

**Alasan**:
- Konsisten dengan sifat in-memory storage
- Memudahkan testing karena state awal selalu sama
- Tidak ada klaim data persisten untuk menghindari ekspektasi salah

**Implementasi**:
- `AppContainer` dibuat ulang di `MainActivity.onCreate`
- `DemoJobGateway.init` memanggil `resetToSeed()` dengan fixtures default
- Reset eksplisit hanya lewat alat debug (tidak ada UI)

**Nanti**:
- Implementasi nyata akan membaca dari database
- Tidak perlu reset karena data sudah persisten

## Batas Kontrak

**Keputusan**: Gateway contract tidak melempar exception untuk operasi bisnis, hanya sealed result types.

**Alasan**:
- Membuat semua kemungkinan hasil eksplisit di tipe sistem
- Memaksa UI untuk menangani semua kasus (success, error)
- Memisahkan error bisnis (validasi, not found) dari error sistem (OutOfMemoryError)

**Implementasi**:
- `JobResult.Success | Error`
- `DeleteResult.Success | Error`
- `PaymentUpdateResult.Success | Error`
- Dll.

**Pattern**:
```kotlin
when (val result = gateway.saveJob(draft)) {
    is JobResult.Success -> // handle success
    is JobResult.Error -> // handle error
}
```

## Model vs Draft

**Keputusan**: Memisahkan `Job` (data tersimpan) dari `JobDraft` (input form).

**Alasan**:
- `Job` selalu valid (semua field non-null kecuali notes/shareloc)
- `JobDraft` memungkinkan field kosong (dateEpochDay?, feeRupiah?) untuk representasi input
- Nilai turunan (sisa pembayaran, status lunas) tidak disimpan, dihitung on-demand

**Implementasi**:
- `Job`: semua ID dan timestamp dari sistem
- `JobDraft`: input user tanpa ID/timestamp
- Validasi di gateway saat konversi Draft → Job

## Dependency Injection

**Keputusan**: Manual DI dengan `AppContainer`, bukan framework (Hilt, Koin).

**Alasan**:
- Aplikasi kecil, kompleksitas DI framework tidak diperlukan
- `AppContainer` mudah dipahami dan di-debug
- Mudah diganti ke framework nanti jika diperlukan

**Implementasi**:
- `AppContainer` di `MainActivity` sebagai singleton aplikasi
- ViewModel factory manual menerima gateway dari container
- Semua layar menggunakan instance gateway yang sama

## Clock Injection

**Keputusan**: `AppClock` interface untuk sumber waktu, dapat diganti saat testing.

**Alasan**:
- Memungkinkan testing dengan waktu tetap
- Memastikan konsistensi timezone (WIB)
- Memudahkan simulasi skenario waktu tertentu

**Implementasi**:
- `SystemClock`: implementasi produksi menggunakan `Instant.now()`
- `FixedClock`: implementasi testing dengan waktu tetap
- Gateway menerima `AppClock` di constructor

## Demo Scenarios

**Keputusan**: Enum `DemoScenario` untuk mengontrol perilaku simulasi.

**Alasan**:
- Memudahkan testing berbagai kasus tanpa mengubah kode
- Dapat diganti lewat debug UI/menu
- Mensimulasikan kondisi yang sulit direplikasi (network error, slow response)

**Skenario**:
- `SUCCESS`: Operasi selalu berhasil
- `SAVE_ERROR`: Semua operasi simpan/ubah gagal
- `DELETE_ERROR`: Operasi hapus gagal
- `PENDING`: Operasi menunggu `releasePendingOperation()` dipanggil
- `EMPTY_DATA`: Seed kosong tanpa job
- `LARGE_DATASET`: Seed dengan 1000 job
- `JOB_NOT_FOUND`: Job yang dicari selalu tidak ada

## Fixtures

**Keputusan**: Fixtures deterministik dengan seed tetap, mencakup edge cases.

**Edge cases yang disediakan**:
- Job mendatang (dateEpochDay = today + N)
- Job sedang berlangsung (dateEpochDay = today, startTime dalam range sekarang)
- Job masa lalu (dateEpochDay = today - N)
- Job lintas tengah malam (startTimeMinutes > 1440 - durationMinutes)
- Fee nol (gratis)
- DP (paidRupiah < feeRupiah)
- Lunas (paidRupiah == feeRupiah)
- Nama panjang (> 30 karakter)
- 1000 job (performance testing)

**Implementasi**:
- `DemoFixtures.getDefaultSeed()`: 6 job dengan berbagai kondisi
- `DemoFixtures.getLargeDatasetSeed()`: 1000 job
- `DemoFixtures.getEmptySeed()`: list kosong

## Operasi Async

**Keputusan**: Semua operasi gateway adalah suspend function atau Flow.

**Alasan**:
- Konsisten dengan pattern asynchronous Kotlin Coroutines
- Mudah diganti ke implementasi nyata (Room, Retrofit) yang juga suspend
- Flow untuk observasi reaktif

**Pattern**:
- Read: `fun observeX(): Flow<X>` - reaktif, update otomatis
- Write: `suspend fun saveX(): Result` - satu kali eksekusi, return result

## Contract Segregation

**Keputusan**: Empat gateway terpisah berdasarkan domain responsibility.

**Pembagian**:
- `JobGateway`: CRUD job, conflict check
- `ReminderGateway`: Notifikasi dan izin
- `BackupGateway`: Ekspor/impor
- `ExternalActionGateway`: Buka aplikasi eksternal

**Alasan**:
- Interface Segregation Principle (SOLID)
- Implementasi nyata dapat menggunakan service berbeda (Room, NotificationManager, Intent)
- Testing lebih mudah dengan mock per-gateway
