# Frontend Contracts

Dokumentasi kontrak gateway untuk Kontenin. Semua kontrak menggunakan hasil bertipe (sealed interface) dan tidak melempar exception langsung untuk operasi bisnis.

## JobGateway

**Tanggung jawab**: Mengelola operasi CRUD job dan pemeriksaan bentrok jadwal.

### `observeAllJobs(): Flow<List<Job>>`
- **Return**: Flow yang memancarkan daftar semua job, diurutkan descending berdasarkan tanggal
- **State ownership**: Gateway memiliki state, observer menerima snapshot
- **Thread**: Flow dapat dikumpulkan dari thread coroutine mana saja

### `observeJob(id: Long): Flow<Job?>`
- **Parameter**: 
  - `id`: ID job (Long) yang akan diobservasi
- **Return**: Flow yang memancarkan job atau null jika tidak ditemukan
- **State ownership**: Gateway memiliki state

### `suspend fun saveJob(draft: JobDraft): JobResult`
- **Parameter**: 
  - `draft`: Data job baru (`JobDraft`)
- **Return**: 
  - `JobResult.Success(job)`: Job berhasil disimpan dengan ID baru
  - `JobResult.Error(message)`: Gagal menyimpan
- **Side effect**: Job baru ditambahkan ke storage, observer menerima update

### `suspend fun updateJob(id: Long, draft: JobDraft): JobResult`
- **Parameter**: 
  - `id`: ID job (Long) yang akan diubah
  - `draft`: Data draft job yang diperbarui
- **Return**: 
  - `JobResult.Success(job)`: Job berhasil diubah
  - `JobResult.Error(message)`: Gagal
- **Side effect**: Job diupdate, `updatedAt` berubah, observer menerima update
- **Preservation**: `createdAt` tetap dipertahankan

### `suspend fun deleteJob(id: Long): DeleteResult`
- **Parameter**: 
  - `id`: ID job (Long) yang akan dihapus
- **Return**: 
  - `DeleteResult.Success`: Job berhasil dihapus
  - `DeleteResult.Error(message)`: Gagal
- **Side effect**: Job dihapus dari storage, observer menerima update

### `suspend fun updatePayment(id: Long, dibayar: Long): PaymentUpdateResult`
- **Parameter**: 
  - `id`: ID job (Long)
  - `dibayar`: Jumlah nominal rupiah yang sudah dibayar (Long)
- **Return**: 
  - `PaymentUpdateResult.Success(job)`: Pembayaran berhasil diupdate
  - `PaymentUpdateResult.Error(message)`: Gagal
- **Side effect**: Field `dibayar` dan `updatedAt` berubah, observer menerima update

### `suspend fun checkConflict(tanggal: Long, jamMulaiMenit: Int, durasiMenit: Int, excludeId: Long?): ConflictCheckResult`
- **Parameter**: 
  - `tanggal`: Epoch day
  - `jamMulaiMenit`: Waktu mulai acara dalam menit dari tengah malam (0..1439)
  - `durasiMenit`: Durasi dalam menit
  - `excludeId`: ID job yang dikecualikan (misal saat edit)
- **Return**: 
  - `ConflictCheckResult.NoConflict`: Jadwal aman, tidak ada bentrok
  - `ConflictCheckResult.HasConflict(conflictingJobs)`: Terdapat daftar job yang bentrok
- **Logic**: Jadwal beririsan jika `max(mulai1, mulai2) < min(selesai1, selesai2)` pada hari yang sama atau rentang jam yang beririsan.

---

## ReminderGateway

**Tanggung jawab**: Mengelola status izin notifikasi, alarm tepat waktu, serta penjadwalan & pembatalan pengingat acara.

### `suspend fun getNotificationPermissionStatus(): PermissionStatus`
- **Return**: `Granted`, `Denied`, atau `NotRequested`

### `suspend fun getExactAlarmPermissionStatus(): PermissionStatus`
- **Return**: `Granted`, `Denied`, atau `NotRequested`

### `suspend fun requestNotificationPermission(): RequestPermissionResult`
- **Return**: `Granted`, `Denied`, atau `Cancelled`

### `suspend fun requestExactAlarmPermission(): RequestPermissionResult`
- **Return**: `Granted`, `Denied`, atau `Cancelled`

### `suspend fun testReminder(): TestReminderResult`
- **Return**: 
  - `TestReminderResult.Scheduled`: Simulasi pengingat berhasil dijadwalkan
  - `TestReminderResult.PermissionDenied`: Izin belum aktif
  - `TestReminderResult.Failed(message)`: Gagal menjadwalkan

### `suspend fun scheduleJobReminders(tanggalEpochDay: Long, jamMulaiMenit: Int): ReminderScheduleResult`
- **Return**: `ReminderScheduleResult(scheduledCount, skippedCount)`
- **Aturan**: Menjadwalkan pengingat H-7, H-3, H-1, dan 3 jam sebelum acara. Jadwal yang sudah lewat waktu saat ini di-skip.

### `suspend fun cancelJobReminders(jobId: Long)`
- **Tanggung jawab**: Membatalkan semua alarm/pengingat yang terdaftar untuk `jobId`.

---

## BackupGateway

**Tanggung jawab**: Ekspor dan impor data cadangan job (format JSON).

### `suspend fun exportToJson(destinationPath: String? = null): ExportResult`
- **Return**: 
  - `ExportResult.Success(path, jobCount)`: File cadangan berhasil dibuat
  - `ExportResult.Error(message)`: Gagal menyimpan berkas
  - `ExportResult.Cancelled`: Operasi dibatalkan oleh pengguna

### `suspend fun validateImport(filePath: String): ImportValidationResult`
- **Parameter**: 
  - `filePath`: Jalur berkas / URI berkas cadangan JSON
- **Return**: 
  - `ImportValidationResult.Valid(jobCount, preview)`: Berkas valid dengan preview metadata ringkas
  - `ImportValidationResult.Invalid(message)`: Format rusak, skema tidak cocok, atau data korup

### `suspend fun confirmImport(filePath: String): ImportResult`
- **Parameter**: 
  - `filePath`: Berkas yang telah divalidasi sebelumnya
- **Return**: 
  - `ImportResult.Success(importedCount)`: Data berhasil diganti secara atomik
  - `ImportResult.Error(message)`: Terjadi kegagalan saat proses penggantian (rollback)

---

## ExternalActionGateway

**Tanggung jawab**: Integrasi dengan Intent eksternal (Google Maps & Pengaturan Aplikasi OS).

### `suspend fun openMaps(address: String): OpenMapsResult`
- **Return**: `OpenMapsResult.Success` atau `OpenMapsResult.Error(message)`

### `suspend fun openSystemSettings(): OpenSystemSettingsResult`
- **Return**: `OpenSystemSettingsResult.Success` atau `OpenSystemSettingsResult.Error(message)`

---

## State Ownership & Error Handling

- **Gateway**: Single source of truth. Menyimpan koleksi dan memancarkan via Kotlin StateFlow.
- **ViewModel**: Menerjemahkan domain model ke UI state terformat.
- **UI**: Deklaratif Jetpack Compose murni, merender UI State & meneruskan event balik ke ViewModel.
- **Error Handling**: Semua kegagalan berupa sealed result types tanpa runtime exception unhandled.
