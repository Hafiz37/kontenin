# Frontend Contracts

Dokumentasi kontrak gateway untuk Kontenin. Semua kontrak menggunakan hasil bertipe (sealed interface) dan tidak melempar exception langsung untuk operasi bisnis.

## JobGateway

**Tanggung jawab**: Mengelola operasi CRUD job dan pemeriksaan bentrok jadwal.

### observeAllJobs(): Flow<List<Job>>
- **Return**: Flow yang memancarkan daftar semua job, diurutkan descending berdasarkan tanggal
- **State ownership**: Gateway memiliki state, observer menerima snapshot
- **Thread**: Flow dapat dikumpulkan dari thread mana saja

### observeJob(id: String): Flow<Job?>
- **Parameter**: 
  - `id`: ID job yang akan diobservasi
- **Return**: Flow yang memancarkan job atau null jika tidak ditemukan
- **State ownership**: Gateway memiliki state

### saveJob(draft: JobDraft): JobResult
- **Parameter**: 
  - `draft`: Data job baru (tanpa ID)
- **Return**: 
  - `JobResult.Success(job)`: Job berhasil disimpan dengan ID baru
  - `JobResult.Error(message)`: Gagal menyimpan (validasi atau sistem)
- **Side effect**: Job baru ditambahkan ke storage, observer menerima update
- **Validation**: `dateEpochDay` dan `feeRupiah` tidak boleh null

### updateJob(id: String, draft: JobDraft): JobResult
- **Parameter**: 
  - `id`: ID job yang akan diubah
  - `draft`: Data job baru
- **Return**: 
  - `JobResult.Success(job)`: Job berhasil diubah
  - `JobResult.Error(message)`: Gagal (job tidak ada, validasi, atau sistem)
- **Side effect**: Job diupdate, `updatedAt` berubah, observer menerima update
- **Preservation**: `createdAt` dan `paidRupiah` (jika tidak diubah) tetap sama

### deleteJob(id: String): DeleteResult
- **Parameter**: 
  - `id`: ID job yang akan dihapus
- **Return**: 
  - `DeleteResult.Success`: Job berhasil dihapus
  - `DeleteResult.Error(message)`: Gagal (job tidak ada atau sistem)
- **Side effect**: Job dihapus dari storage, observer menerima update

### updatePayment(id: String, paidRupiah: Long): PaymentUpdateResult
- **Parameter**: 
  - `id`: ID job
  - `paidRupiah`: Jumlah rupiah yang sudah dibayar
- **Return**: 
  - `PaymentUpdateResult.Success(job)`: Pembayaran berhasil diupdate
  - `PaymentUpdateResult.Error(message)`: Gagal (job tidak ada atau sistem)
- **Side effect**: Field `paidRupiah` dan `updatedAt` berubah, observer menerima update

### checkConflict(dateEpochDay: Long, startTimeMinutes: Int, durationMinutes: Int, excludeId: String?): ConflictCheckResult
- **Parameter**: 
  - `dateEpochDay`: Tanggal job
  - `startTimeMinutes`: Waktu mulai (menit dari tengah malam)
  - `durationMinutes`: Durasi
  - `excludeId`: ID job yang dikecualikan (untuk update job)
- **Return**: 
  - `ConflictCheckResult.NoConflict`: Tidak ada bentrok
  - `ConflictCheckResult.HasConflict(conflictingJobs)`: Ada job yang bentrok
- **Logic**: Bentrok jika ada job lain di tanggal sama dengan waktu overlapping

---

## ReminderGateway

**Tanggung jawab**: Mengelola izin notifikasi dan pengingat.

### getPermissionStatus(): PermissionStatus
- **Return**: 
  - `PermissionStatus.Granted`: Izin sudah diberikan
  - `PermissionStatus.Denied`: Izin ditolak
  - `PermissionStatus.NotRequested`: Belum pernah diminta
- **Thread**: Suspend function, dapat dipanggil dari coroutine mana saja

### requestPermission(): RequestPermissionResult
- **Return**: 
  - `RequestPermissionResult.Granted`: User memberikan izin
  - `RequestPermissionResult.Denied`: User menolak
  - `RequestPermissionResult.Cancelled`: User membatalkan dialog
- **Side effect**: Dialog sistem ditampilkan, status izin berubah
- **Note**: Di demo, tidak ada UI dialog sistem

### testReminder(): TestReminderResult
- **Return**: 
  - `TestReminderResult.Success`: Notifikasi uji berhasil dikirim
  - `TestReminderResult.Error(message)`: Gagal (izin belum diberikan atau sistem)
- **Side effect**: Notifikasi muncul di status bar (jika izin granted)

---

## BackupGateway

**Tanggung jawab**: Ekspor dan impor data job dalam format JSON.

### exportToJson(): ExportResult
- **Return**: 
  - `ExportResult.Success(path, jobCount)`: File berhasil diekspor
  - `ExportResult.Error(message)`: Gagal (storage atau sistem)
- **Side effect**: File JSON dibuat di storage eksternal
- **Format**: JSON array dari semua job

### validateImport(filePath: String): ImportValidationResult
- **Parameter**: 
  - `filePath`: Path file JSON yang akan diimpor
- **Return**: 
  - `ImportValidationResult.Valid(jobCount, preview)`: File valid dengan preview
  - `ImportValidationResult.Invalid(message)`: File tidak valid (format salah)
- **Side effect**: Tidak ada (read-only validation)

### confirmImport(filePath: String): ImportResult
- **Parameter**: 
  - `filePath`: Path file JSON yang akan diimpor
- **Return**: 
  - `ImportResult.Success(importedCount)`: Job berhasil diimpor
  - `ImportResult.Error(message)`: Gagal (validasi atau sistem)
- **Side effect**: Job dari file ditambahkan ke storage, observer menerima update
- **Note**: Harus dipanggil setelah `validateImport` sukses

---

## ExternalActionGateway

**Tanggung jawab**: Membuka aplikasi eksternal (Maps, Settings).

### openMaps(address: String): OpenMapsResult
- **Parameter**: 
  - `address`: Alamat yang akan dibuka di Maps
- **Return**: 
  - `OpenMapsResult.Success`: Aplikasi Maps berhasil dibuka
  - `OpenMapsResult.Error(message)`: Gagal (aplikasi tidak ada atau sistem)
- **Side effect**: Intent diluncurkan, user keluar dari aplikasi

### openSystemSettings(): OpenSystemSettingsResult
- **Return**: 
  - `OpenSystemSettingsResult.Success`: Pengaturan sistem berhasil dibuka
  - `OpenSystemSettingsResult.Error(message)`: Gagal (sistem)
- **Side effect**: Intent diluncurkan, user masuk ke halaman pengaturan aplikasi

---

## State Ownership

- **Gateway**: Memegang single source of truth untuk semua data
- **ViewModel**: Subscribe ke Flow dari gateway, tidak menyimpan data duplikat
- **UI**: Menerima state dari ViewModel, tidak menyimpan data bisnis

## Error Handling

- Semua operasi menggunakan sealed result types
- Exception sistem (OutOfMemoryError, dll) tidak ditangkap
- Error bisnis (validasi, not found) dikembalikan sebagai result
- UI harus menangani semua kemungkinan result

## Threading

- Semua suspend function dapat dipanggil dari coroutine mana saja
- Gateway bertanggung jawab untuk thread safety internal
- Flow dapat dikumpulkan dari thread mana saja
