# Frontend Handoff: Panduan Integrasi Backend

**Tanggal**: 1 November 2026  
**Branch**: Belum ada branch khusus (mengikuti instruksi tanpa git operations)  
**Status**: ⚠️ **PARSIAL** - Fondasi selesai, beberapa task prasyarat masih dalam progress

---

## 1. Status Handoff

### 1.1 Komponen yang Telah Selesai

| Task | Status | Keterangan |
|------|--------|------------|
| FE-01 | ✅ Selesai | Struktur proyek, Kotlin, Compose, Material 3, DI manual |
| FE-02 | ✅ Selesai | Model, kontrak gateway, implementasi demo in-memory |
| FE-03 | ✅ Selesai | Tema, tipografi Roboto, palet warna light/dark |
| FE-04 | ✅ Selesai | Komponen UI bersama (button, field, chip, dialog) |
| FE-05 | ✅ Selesai | Navigasi bottom bar dan kerangka 5 layar |
| FE-06 | ✅ Selesai | Formatter waktu/rupiah, JobCard, converter date picker |
| FE-07 | ✅ Selesai | Beranda dengan tab Mendatang/Riwayat dan filter Belum Lunas |
| FE-08 | ✅ Selesai | Form Job dengan semua field dan SavedStateHandle |
| FE-09 | ✅ Selesai | Validasi form lengkap dan tombol Lunas |
| FE-10 | ✅ Selesai | Autocomplete acara dan sumber job |
| FE-11 | ⚠️ Parsial | Simpan/ubah/duplikat ada di ViewModel, belum sepenuhnya teruji |
| FE-12 | ✅ Selesai | Dialog bentrok dengan aturan overlap menit absolut |
| FE-13 | ⚠️ Placeholder | Detail Job ada struktur folder, belum implementasi penuh |
| FE-14 | ⚠️ Placeholder | Pembayaran ada PaymentValidator, UI Dialog belum lengkap |
| FE-15 | ⚠️ Placeholder | Rekap ada folder, perhitungan agregasi belum sepenuhnya UI |
| FE-16 | ⚠️ Placeholder | Pengaturan ada struktur, pengingat simulasi belum lengkap |
| FE-17 | ❌ Belum | UI ekspor/impor belum diimplementasikan |
| FE-18 | ❌ Belum | Polish aksesibilitas dan layout menyeluruh belum dijalankan |
| FE-19 | ❌ Gagal | 3 dari 6 test gagal (blocker fitur belum lengkap) |

### 1.2 Kesimpulan Status

**Frontend telah mencapai ~60% completion** dengan fondasi kuat:
- ✅ Arsitektur dan kontrak gateway stabil
- ✅ Tema dan komponen UI konsisten
- ✅ Form Job dengan validasi lengkap
- ✅ Beranda fungsional
- ⚠️ Detail, Rekap, Pengaturan perlu finalisasi UI
- ❌ Backup (ekspor/impor) dan pengingat masih simulasi

---

## 2. Daftar Layar yang Selesai

### 2.1 Beranda (FE-07) ✅

**Route**: `/home`  
**ViewModel**: `HomeViewModel`  
**File**:
- `ui/screens/home/HomeScreen.kt`
- `presentation/home/HomeViewModel.kt`
- `presentation/home/HomeUiState.kt`

**State**:
- Tab: Mendatang (job belum selesai) / Riwayat (job sudah selesai)
- Filter: Belum Lunas (berlaku per tab aktif)
- Kategori dihitung berdasarkan: `now >= (dateEpochDay * 1440 + startMinutes + duration)`

**Dependency**:
- `JobGateway.observeAllJobs()` → Flow reaktif

**Integrasi Backend**:
```kotlin
// Ganti DemoJobGateway dengan RoomJobGateway
class RoomJobGateway(private val dao: JobDao) : JobGateway {
    override fun observeAllJobs(): Flow<List<Job>> = 
        dao.getAllJobsFlow().map { entities -> entities.map { it.toJob() } }
}
```

---

### 2.2 Form Job (FE-08, FE-09, FE-10, FE-11, FE-12) ✅

**Route**: `/job/form?mode={add|edit|duplicate}&id={jobId?}`  
**ViewModel**: `JobFormViewModel`  
**File**:
- `ui/screens/jobform/JobFormScreen.kt`
- `ui/screens/jobform/JobFormSections.kt`
- `presentation/jobform/JobFormViewModel.kt`
- `presentation/jobform/JobFormValidator.kt`
- `presentation/jobform/FormValidationError.kt`
- `presentation/jobform/AutocompleteRules.kt`
- `presentation/jobform/JobFormUiState.kt`

**Fitur Lengkap**:
- ✅ Input semua field dengan SavedStateHandle (survive recreation)
- ✅ Validasi ber-tipe untuk 12 field sesuai PRD FR-1.2
- ✅ Error inline di bawah kolom, validasi saat blur dan saat Simpan
- ✅ Gulir/fokus otomatis ke error pertama
- ✅ Tombol Lunas aktif hanya jika fee valid > 0 (OQ-8)
- ✅ Autocomplete acara/sumber job (maks 5 saran, frekuensi tertinggi)
- ✅ Normalisasi spasi dan penyatuan ejaan saat simpan (EC-23)
- ✅ Dialog bentrok non-blocking dengan daftar job yang overlap
- ✅ Mode: Add / Edit / Duplicate dengan judul dinamis

**Dependency**:
- `JobGateway.saveJob(draft)` → insert baru
- `JobGateway.updateJob(id, draft)` → update existing
- `JobGateway.checkConflict(...)` → deteksi overlap
- `ReminderGateway.getNotificationPermissionStatus()` → izin notifikasi
- `ReminderGateway.requestNotificationPermission()` → minta izin
- `ReminderGateway.scheduleJobReminders(date, startMinutes)` → jadwalkan 4 alarm

**Integrasi Backend**:

1. **JobGateway.saveJob**:
```kotlin
override suspend fun saveJob(draft: JobDraft): JobResult {
    return try {
        val job = Job(
            id = 0L, // Room auto-generate
            namaPengantin1 = draft.namaPengantin1,
            namaPengantin2 = draft.namaPengantin2,
            tanggal = draft.tanggal ?: return JobResult.Error("Tanggal wajib"),
            jamMulaiMenit = draft.jamMulaiMenit ?: return JobResult.Error("Jam wajib"),
            durasiMenit = draft.durasiMenit ?: return JobResult.Error("Durasi wajib"),
            acara = draft.acara,
            alamat = draft.alamat,
            shareloc = draft.shareloc,
            sumberJob = draft.sumberJob,
            fee = draft.fee ?: return JobResult.Error("Fee wajib"),
            dibayar = draft.dibayar,
            catatan = draft.catatan,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val insertedId = dao.insert(job.toEntity())
        val saved = dao.getById(insertedId) ?: return JobResult.Error("Gagal membaca job tersimpan")
        JobResult.Success(saved.toJob())
    } catch (e: Exception) {
        JobResult.Error(e.message ?: "Gagal menyimpan")
    }
}
```

2. **JobGateway.checkConflict** (gunakan aturan demo):
```kotlin
override suspend fun checkConflict(
    tanggal: Long,
    jamMulaiMenit: Int,
    durasiMenit: Int,
    excludeId: Long?
): ConflictCheckResult {
    // Kandidat: tanggal -1 hingga +1 hari
    val candidates = dao.getJobsByDateRange(tanggal - 1, tanggal + 1)
    
    val startA = tanggal * 1440L + jamMulaiMenit
    val endA = startA + durasiMenit
    
    val conflicts = candidates
        .filter { it.id != excludeId }
        .filter { candidate ->
            val startB = candidate.tanggal * 1440L + candidate.jamMulaiMenit
            val endB = startB + candidate.durasiMenit
            // Overlap: startA < endB && startB < endA
            startA < endB && startB < endA
        }
    
    return if (conflicts.isEmpty()) {
        ConflictCheckResult.NoConflict
    } else {
        ConflictCheckResult.HasConflict(conflicts)
    }
}
```

3. **ReminderGateway** (produksi):
```kotlin
class AlarmReminderGateway(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val dao: JobDao
) : ReminderGateway {
    
    override suspend fun scheduleJobReminders(
        dateEpochDay: Long,
        startMinutes: Int
    ): ScheduleRemindersResult {
        val now = Instant.now().atZone(ZoneId.of("Asia/Jakarta"))
        val acara = LocalDate.ofEpochDay(dateEpochDay)
            .atTime(startMinutes / 60, startMinutes % 60)
            .atZone(ZoneId.of("Asia/Jakarta"))
        
        var scheduled = 0
        var skipped = 0
        
        // H-7: 7 hari sebelum, pukul 08:00 WIB
        val h7 = acara.minusDays(7).withHour(8).withMinute(0)
        if (h7.isAfter(now)) {
            scheduleAlarm(jobId, 0, h7.toInstant().toEpochMilli())
            scheduled++
        } else skipped++
        
        // H-3, H-1, 3 jam: sama
        // ...
        
        return ScheduleRemindersResult(scheduled, skipped)
    }
    
    private fun scheduleAlarm(jobId: Long, index: Int, triggerMillis: Long) {
        val requestCode = (jobId * 4 + index).toInt()
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.kontenin.REMINDER"
            putExtra("jobId", jobId)
            putExtra("index", index)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && 
            alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }
}
```

---

### 2.3 Detail Job (FE-13, FE-14) ⚠️ Struktur Ada

**Route**: `/job/detail/{jobId}`  
**ViewModel**: `JobDetailViewModel`  
**Status**: Folder dan file placeholder ada, implementasi UI perlu finalisasi

**Yang Harus Diimplementasikan**:
- Tampilkan semua field job (nama, acara, tanggal/jam, lokasi, fee, dibayar, sisa, catatan)
- Tombol "Buka Maps" (shareloc tidak kosong)
- Tombol "Tandai Lunas" (jika belum lunas)
- Dialog "Ubah Pembayaran" (input total dibayar)
- Bottom action bar: Ubah / Duplikat / Hapus
- Dialog konfirmasi hapus

**Integrasi Backend**:
```kotlin
// JobGateway.deleteJob
override suspend fun deleteJob(id: Long): DeleteResult {
    return try {
        // 1. Batalkan 4 alarm
        reminderScheduler.cancel(id)
        
        // 2. Hapus dari database
        dao.deleteById(id)
        
        DeleteResult.Success
    } catch (e: Exception) {
        DeleteResult.Error(e.message ?: "Gagal menghapus")
    }
}

// ExternalActionGateway.openMaps
override suspend fun openMaps(sharelocUrl: String): OpenMapsResult {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharelocUrl))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            OpenMapsResult.Success
        } else {
            OpenMapsResult.Error("Tidak ada aplikasi untuk membuka link ini")
        }
    } catch (e: Exception) {
        OpenMapsResult.Error(e.message ?: "Gagal membuka Maps")
    }
}
```

---

### 2.4 Rekap (FE-15) ⚠️ Perhitungan Ada, UI Perlu Finalisasi

**Route**: `/recap`  
**ViewModel**: `RecapViewModel`  
**Status**: Folder ada, perhitungan agregasi perlu integrasi penuh ke UI

**Fitur yang Harus Ditampilkan**:
- Segmented button: Hari Ini / Minggu Ini / Bulan Ini
- Kartu ringkasan: jumlah job, total fee, total dibayar, total sisa
- Seksi "Per Sumber Job": breakdown per sumber (jumlah, fee, sisa)
- Daftar job periode (ketuk → buka Detail)
- Seksi "Piutang (semua periode)": job lewat & belum lunas, urut terlama

**Aturan Perhitungan** (sudah ada di `RecapCalculator.kt`):
```kotlin
// Minggu mulai Senin
val weekFields = WeekFields.of(DayOfWeek.MONDAY, 1)

// Piutang: job selesai DAN belum lunas
fun isPiutang(job: Job, now: ZonedDateTime): Boolean {
    val endAbsoluteMinutes = job.tanggal * 1440L + job.jamMulaiMenit + job.durasiMenit
    val nowAbsoluteMinutes = now.toLocalDate().toEpochDay() * 1440L + 
                              now.hour * 60 + now.minute
    return nowAbsoluteMinutes >= endAbsoluteMinutes && job.dibayar < job.fee
}
```

---

### 2.5 Pengaturan (FE-16) ⚠️ Struktur Ada

**Route**: `/settings`  
**ViewModel**: `SettingsViewModel`  
**Status**: Folder ada, simulasi pengingat belum sepenuhnya UI

**Yang Harus Diimplementasikan**:
- Status izin notifikasi: Aktif / Belum aktif (teks + ikon)
- Status alarm tepat waktu: Aktif / Belum aktif
- Banner peringatan jika izin belum aktif
- Tombol "Buka Pengaturan" → Intent ke Settings.ACTION_APPLICATION_DETAILS_SETTINGS
- Dialog "Petunjuk Baterai dan Autostart" dengan teks umum
- Tombol "Uji Pengingat" → jadwalkan alarm 10 detik ke depan
- Tombol "Ekspor Data" (FE-17)
- Tombol "Impor Data" (FE-17)
- Teks versi aplikasi (dari BuildConfig)

---

## 3. Pemetaan Kontrak → Implementasi Backend

### 3.1 JobGateway

| Method | Implementasi Backend | File Target |
|--------|---------------------|-------------|
| `observeAllJobs()` | Room DAO Flow query | `data/local/JobDao.kt` |
| `observeJob(id)` | Room DAO by ID Flow | `data/local/JobDao.kt` |
| `saveJob(draft)` | Room insert + auto ID | `data/repository/RoomJobGateway.kt` |
| `updateJob(id, draft)` | Room update preserving createdAt | `data/repository/RoomJobGateway.kt` |
| `deleteJob(id)` | Room delete + cancel 4 alarms | `data/repository/RoomJobGateway.kt` |
| `updatePayment(id, paid)` | Room update field `dibayar` | `data/repository/RoomJobGateway.kt` |
| `checkConflict(...)` | Query ±1 hari + hitung overlap | `data/repository/RoomJobGateway.kt` |

**Room Entity**:
```kotlin
@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val namaPengantin1: String,
    val namaPengantin2: String,
    val tanggal: Long, // LocalDate.toEpochDay()
    val jamMulaiMenit: Int, // 0..1439
    val durasiMenit: Int, // 30..1440
    val acara: String,
    val alamat: String,
    val shareloc: String,
    val sumberJob: String,
    val fee: Long,
    val dibayar: Long,
    val catatan: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY tanggal DESC")
    fun getAllJobsFlow(): Flow<List<JobEntity>>
    
    @Query("SELECT * FROM jobs WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<JobEntity?>
    
    @Query("SELECT * FROM jobs WHERE tanggal BETWEEN :start AND :end")
    suspend fun getJobsByDateRange(start: Long, end: Long): List<JobEntity>
    
    @Insert
    suspend fun insert(job: JobEntity): Long
    
    @Update
    suspend fun update(job: JobEntity)
    
    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
```

---

### 3.2 ReminderGateway

| Method | Implementasi Backend | Dependency |
|--------|---------------------|-----------|
| `getNotificationPermissionStatus()` | Check POST_NOTIFICATIONS (API 33+) | ContextCompat |
| `requestNotificationPermission()` | ActivityResultLauncher | Activity/Fragment |
| `getExactAlarmPermissionStatus()` | alarmManager.canScheduleExactAlarms() | AlarmManager |
| `scheduleJobReminders(date, start)` | AlarmManager.setExactAndAllowWhileIdle | AlarmManager + PendingIntent |
| `cancelJobReminders(jobId)` | cancel 4 PendingIntent | AlarmManager |
| `testReminder()` | schedule 10s ke depan | AlarmManager |
| `rescheduleAll()` | query all future jobs + schedule | JobDao + AlarmManager |

**BroadcastReceiver**:
```kotlin
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val jobId = intent.getLongExtra("jobId", -1)
        val index = intent.getIntExtra("index", -1)
        if (jobId == -1L) return
        
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(context).jobDao()
                val job = dao.getById(jobId) ?: return@launch
                
                val title = when (index) {
                    0 -> "H-7: ${job.acara} ${job.namaPengantin1} & ${job.namaPengantin2}"
                    1 -> "H-3: ${job.acara} ${job.namaPengantin1} & ${job.namaPengantin2}"
                    2 -> "H-1: ${job.acara} ${job.namaPengantin1} & ${job.namaPengantin2}"
                    3 -> "3 jam lagi: ${job.acara} ${job.namaPengantin1} & ${job.namaPengantin2}"
                    else -> "Pengingat Job"
                }
                
                showNotification(context, jobId, title, formatJobDetails(job))
            } finally {
                pendingResult.finish()
            }
        }
    }
}
```

**Manifest**:
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<receiver android:name=".reminder.ReminderReceiver" android:exported="false">
    <intent-filter>
        <action android:name="com.example.kontenin.REMINDER" />
    </intent-filter>
</receiver>

<receiver android:name=".reminder.BootReceiver" android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
        <action android:name="android.intent.action.MY_PACKAGE_REPLACED" />
    </intent-filter>
</receiver>
```

---

### 3.3 BackupGateway

| Method | Implementasi Backend | Dependency |
|--------|---------------------|-----------|
| `exportToJson()` | SAF ACTION_CREATE_DOCUMENT + serialize | kotlinx.serialization |
| `validateImport(uri)` | Parse JSON + validate schema | kotlinx.serialization |
| `confirmImport(uri)` | Transaction: cancel alarms + delete + insert + reschedule | Room + ReminderGateway |

**Format JSON** (dari PRD §7.3):
```json
{
  "app": "kontenin",
  "schemaVersion": 1,
  "exportedAt": 1730462029312,
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
      "createdAt": 1730462000000,
      "updatedAt": 1730462000000
    }
  ]
}
```

**Implementasi**:
```kotlin
override suspend fun confirmImport(uri: Uri): ImportResult {
    return try {
        // 1. Batalkan semua alarm lama
        val oldJobs = dao.getAll()
        oldJobs.forEach { reminderScheduler.cancel(it.id) }
        
        // 2. Transaksi atomik: hapus semua + insert baru
        dao.runInTransaction {
            dao.deleteAll()
            
            val json = context.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader().readText()
            } ?: return@runInTransaction
            
            val backup = Json.decodeFromString<BackupFile>(json)
            backup.jobs.forEach { dao.insert(it.toEntity()) }
        }
        
        // 3. Jadwalkan ulang semua alarm
        reminderScheduler.rescheduleAll()
        
        ImportResult.Success(jobCount)
    } catch (e: Exception) {
        ImportResult.Error(e.message ?: "Gagal mengimpor")
    }
}
```

---

### 3.4 ExternalActionGateway

| Method | Implementasi Backend | Dependency |
|--------|---------------------|-----------|
| `openMaps(sharelocUrl)` | Intent ACTION_VIEW | Context |
| `openSystemSettings()` | Intent ACTION_APPLICATION_DETAILS_SETTINGS | Context |

---

## 4. State Ownership

| Layer | Tanggung Jawab | Tidak Boleh |
|-------|----------------|-------------|
| **Gateway** | Single source of truth data (Room database) | Menyimpan UI state |
| **ViewModel** | Subscribe Flow, transformasi ke UI state | Menyimpan duplikasi data bisnis |
| **UI** | Render state dari ViewModel | Mengakses Gateway langsung |

---

## 5. Error Handling

Semua kontrak menggunakan **sealed result types**:

```kotlin
sealed interface JobResult {
    data class Success(val job: Job) : JobResult
    data class Error(val message: String) : JobResult
}

sealed interface DeleteResult {
    data object Success : DeleteResult
    data class Error(val message: String) : DeleteResult
}

// dst untuk semua operasi
```

**JANGAN melempar exception untuk error bisnis** (validasi, not found).  
Exception sistem (OutOfMemoryError, SQLiteException) boleh propagate.

---

## 6. Aturan Perhitungan yang Harus Konsisten

### 6.1 Waktu Selesai (EC-4)

```kotlin
// Dari JobDisplayMapper.kt
fun getEndTime(job: Job): Pair<Int, Long> {
    val endMinutes = (job.jamMulaiMenit + job.durasiMenit) % 1440
    val endDate = if (endMinutes < job.jamMulaiMenit) {
        job.tanggal + 1 // lintas tengah malam
    } else {
        job.tanggal
    }
    return endMinutes to endDate
}

// Format rentang lintas hari (OQ-7):
// "22.00 - 02.00 (19 Okt)"
```

### 6.2 Piutang (FR-5.3, OQ-5)

```kotlin
// Job dianggap piutang jika waktu selesai sudah lewat DAN belum lunas
fun isPiutang(job: Job, clock: AppClock): Boolean {
    val now = clock.nowWIB()
    val endAbsoluteMinutes = job.tanggal * 1440L + job.jamMulaiMenit + job.durasiMenit
    val nowAbsoluteMinutes = now.toLocalDate().toEpochDay() * 1440L + 
                              now.hour * 60 + now.minute
    return nowAbsoluteMinutes >= endAbsoluteMinutes && job.dibayar < job.fee
}
```

### 6.3 Rekap Periode (FR-5.1)

```kotlin
// Minggu mulai Senin (OQ-9)
val weekFields = WeekFields.of(DayOfWeek.MONDAY, 1)

// Periode dihitung dari tanggal MULAI acara, bukan tanggal pembayaran
fun isInPeriod(job: Job, period: RecapPeriod, clock: AppClock): Boolean {
    val today = clock.nowWIB().toLocalDate()
    val jobDate = LocalDate.ofEpochDay(job.tanggal)
    
    return when (period) {
        RecapPeriod.TODAY -> jobDate == today
        RecapPeriod.THIS_WEEK -> {
            val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekEnd = weekStart.plusDays(6)
            jobDate in weekStart..weekEnd
        }
        RecapPeriod.THIS_MONTH -> {
            jobDate.year == today.year && jobDate.month == today.month
        }
    }
}
```

### 6.4 Bentrok (FR-1.8, EC-4, EC-5, EC-6)

```kotlin
// Dari DemoConflictRules.kt (HARUS dipakai ulang di backend)
fun checkOverlap(
    dateA: Long, startA: Int, durationA: Int,
    dateB: Long, startB: Int, durationB: Int
): Boolean {
    val absStartA = dateA * 1440L + startA
    val absEndA = absStartA + durationA
    val absStartB = dateB * 1440L + startB
    val absEndB = absStartB + durationB
    
    // Overlap: startA < endB && startB < endA
    // Berurutan (startB == endA): BUKAN bentrok
    return absStartA < absEndB && absStartB < absEndA
}
```

---

## 7. Batas Pengujian Frontend

### 7.1 Yang Sudah Diuji ✅

| Item | Hasil | File Test |
|------|-------|-----------|
| Reset fixture deterministik | ✅ Lulus | `DemoVerificationTest.kt` |
| Bentrok lintas hari (dua arah) | ✅ Lulus | `DemoVerificationTest.kt` |
| Bentrok berurutan (tidak bentrok) | ✅ Lulus | `DemoVerificationTest.kt` |
| Edit job dikecualikan dari bentrok diri | ✅ Lulus | `DemoVerificationTest.kt` |
| Gagal simpan tidak ubah koleksi | ✅ Lulus | `DemoVerificationTest.kt` |
| Format tanggal/rupiah/jam | ✅ Lulus | `DisplayFormatterTest.kt` |
| Konversi date picker UTC → LocalDate | ✅ Lulus | `DatePickerConverterTest.kt` |
| Validasi nama/durasi/fee/dibayar | ✅ Lulus | `JobFormValidatorTest.kt` |
| Kategori Mendatang/Riwayat | ✅ Lulus | `HomeRulesTest.kt` |

### 7.2 Yang Belum Diuji ❌

**PERINGATAN**: Item berikut **BELUM DIUJI** dan bukan tanggung jawab fase frontend:

- ❌ Reliabilitas alarm setelah reboot/layar mati
- ❌ Alarm berbunyi tepat waktu di perangkat fisik
- ❌ Round-trip backup file asli (ekspor → hapus app → install → impor)
- ❌ Rollback transaksi impor yang gagal di tengah proses
- ❌ Migrasi Room schema (version 1 → 2)
- ❌ Maps intent nyata ke Google Maps di perangkat
- ❌ Permission flow OS asli (REQUEST_POST_NOTIFICATIONS dialog)
- ❌ TalkBack lengkap pada seluruh layar
- ❌ Font 200% pada seluruh layar
- ❌ Performa scroll 1000 job di perangkat fisik
- ❌ Poco X3 GT (perangkat fisik belum digunakan untuk testing)

---

## 8. Skenario Demo yang Tersedia

Dari `DemoScenario.kt`:

```kotlin
enum class DemoScenario {
    SUCCESS,           // Semua operasi sukses
    SAVE_ERROR,        // Simpan/ubah selalu gagal
    DELETE_ERROR,      // Hapus selalu gagal
    PENDING,           // Operasi menunggu release manual
    EMPTY_DATA,        // Seed kosong (tidak ada job)
    LARGE_DATASET,     // Seed 1000 job untuk performance test
    JOB_NOT_FOUND,     // Job yang dicari selalu tidak ditemukan
}
```

**Cara menggunakan** (hanya untuk testing, tidak ada UI):
```kotlin
val container = AppContainer(clock = FixedClock(...))
(container.jobGateway as DemoJobGateway).setScenario(DemoScenario.SAVE_ERROR)
```

---

## 9. Cara Reset Demo

**PERINGATAN**: Data demo **TIDAK PERSISTEN**. Setiap kali aplikasi restart, data kembali ke seed default.

```kotlin
// Hanya untuk testing, tidak ada UI production
val gateway = container.jobGateway as DemoJobGateway
gateway.resetToSeed(DemoFixtures.getDefaultSeed(clock))
```

---

## 10. Pekerjaan Integrasi yang Tertunda

### 10.1 Prioritas Tinggi (Blocker Backend)

1. **Finalisasi UI Detail Job** (FE-13)
   - Implementasi seluruh seksi detail
   - Bottom action bar (Ubah/Duplikat/Hapus)
   - Dialog konfirmasi hapus

2. **Finalisasi UI Pembayaran di Detail** (FE-14)
   - Dialog "Ubah Pembayaran"
   - Tombol "Tandai Lunas"

3. **Finalisasi UI Rekap** (FE-15)
   - Kartu ringkasan
   - Seksi "Per Sumber Job"
   - Seksi "Piutang"

4. **Finalisasi UI Pengaturan** (FE-16)
   - Status izin dan banner
   - Tombol "Uji Pengingat"
   - Dialog petunjuk baterai

5. **Implementasi UI Ekspor/Impor** (FE-17)
   - Tombol Ekspor Data
   - Flow Impor: pilih file → validasi → konfirmasi → proses
   - Dialog konfirmasi: "Semua data akan diganti (N job)"

6. **Polish Aksesibilitas** (FE-18)
   - TalkBack untuk seluruh layar
   - Font 200% tanpa layout rusak
   - Keyboard navigation

7. **Verifikasi Alur Lengkap** (FE-19)
   - Test end-to-end: tambah → bentrok → simpan → ubah → duplikat → pembayaran → rekap → hapus → impor
   - Smoke test manual dengan fixture FE-15

### 10.2 Prioritas Sedang (Implementasi Backend)

8. **Room Database**
   - Entity, DAO, Database class
   - Migration strategy (version 1 → N)
   - RoomJobGateway implementation

9. **AlarmManager Integration**
   - ReminderScheduler
   - ReminderReceiver
   - BootReceiver
   - NotificationHelper

10. **Storage Access Framework**
    - Ekspor JSON via ACTION_CREATE_DOCUMENT
    - Impor JSON via ACTION_OPEN_DOCUMENT
    - Validasi dan transaksi atomik

11. **Permission Handling**
    - POST_NOTIFICATIONS launcher
    - SCHEDULE_EXACT_ALARM check
    - Settings navigation

### 10.3 Prioritas Rendah (Fase 2)

- Rekap bulan lain (bukan periode berjalan)
- Pengingat menagih fee
- Widget layar utama
- Kalender bulanan
- Ekspor PDF/CSV
- Riwayat transaksi pembayaran

---

## 11. Blocker yang Mencegah Integrasi Backend Sekarang

| Blocker | Dampak | Status |
|---------|--------|--------|
| FE-13–FE-17 belum UI lengkap | Tidak ada layar untuk menguji backend | ⚠️ Critical |
| 3 UI test gagal | Fitur yang diuji belum ada | ⚠️ Critical |
| Alur end-to-end tidak dapat dijalankan | Tidak bisa verifikasi integrasi | ⚠️ Critical |

---

## 12. Rekomendasi

### 12.1 JANGAN Mulai Backend Sebelum Frontend Selesai

**Alasan**:
1. Kontrak bisa berubah saat implementasi UI lengkap terungkap
2. Tidak ada UI untuk menguji integrasi backend
3. Risk mengerjakan ulang backend jika kontrak berubah
4. Test UI yang gagal menunjukkan blocker struktural

### 12.2 Prioritas Segera

1. ✅ **Selesaikan FE-13–FE-18** (Detail, Rekap, Pengaturan, Ekspor/Impor, Polish)
2. ✅ **Ulangi FE-19** hingga semua test lulus
3. ✅ **Finalisasi FE-20** (dokumen ini)
4. ✅ **Review frontend** dengan checklist `frontend-acceptance.md`
5. ⏸️ **Baru mulai backend** setelah frontend diterima

### 12.3 Setelah Backend Selesai

1. Integrasikan backend ke UI (ganti DemoGateway → RoomGateway dll)
2. Test manual di Poco X3 GT
3. Uji reliabilitas alarm (reboot, layar mati, doze mode)
4. Ekspor → hapus app → install → impor (round-trip nyata)
5. Dokumentasikan hasil uji perangkat fisik

---

## 13. Kontak dan Pertanyaan

Untuk pertanyaan tentang kontrak, keputusan desain, atau klarifikasi implementasi:

- **Kontrak Gateway**: `docs/frontend-contracts.md`
- **Keputusan Arsitektur**: `docs/frontend-decisions.md`
- **Laporan Test**: `docs/frontend-test-report.md`
- **QA Checklist**: `docs/frontend-qa-checklist.md`

---

**Dokumen ini akan diperbarui setelah FE-13–FE-19 selesai.**
