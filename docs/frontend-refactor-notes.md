# Frontend Refactor Notes

## Refactor FE-02: Penyesuaian dengan Spesifikasi PRD §7.1

Tanggal: 30 Oktober 2026

### Perubahan yang Dilakukan

Refactor dilakukan untuk menyesuaikan model UI dengan spesifikasi PRD-Kontenin-v0.1.md §7.1 dan brief-kontenin.md §7.1.

#### 1. Model Job - Perubahan Nama Field

Dari konvensi Inggris ke Bahasa Indonesia sesuai PRD:

| Lama (Inggris) | Baru (Indonesia) | Alasan |
|----------------|------------------|--------|
| `firstName` | `namaPengantin1` | Sesuai PRD §7.1 |
| `lastName` | `namaPengantin2` | Sesuai PRD §7.1 |
| `dateEpochDay` | `tanggal` | Sesuai PRD §7.1 |
| `startTimeMinutes` | `jamMulaiMenit` | Sesuai PRD §7.1 |
| `durationMinutes` | `durasiMenit` | Sesuai PRD §7.1 |
| `event` | `acara` | Sesuai PRD §7.1 |
| `address` | `alamat` | Sesuai PRD §7.1 |
| `source` | `sumberJob` | Sesuai PRD §7.1 |
| `feeRupiah` | `fee` | Sesuai PRD §7.1 (field `fee` di tabel) |
| `paidRupiah` | `dibayar` | Sesuai PRD §7.1 (field `dibayar` di tabel) |
| `notes` | `catatan` | Sesuai PRD §7.1 |

Field tidak berubah: `shareloc`, `createdAt`, `updatedAt`

#### 2. Tipe ID - Long dengan Auto-increment

| Lama | Baru | Alasan |
|------|------|--------|
| `String` (UUID manual) | `Long` (auto-increment simulasi) | Sesuai PRD §7.1: "id: Long, Primary key, auto generate" |

Implementasi demo:
- `DemoJobGateway` menggunakan `nextId` counter untuk simulasi auto-increment
- Seed awal: 1L-6L untuk default, 1001L-2000L untuk large dataset
- `nextId` diupdate setiap `saveJob()` berhasil

#### 3. Perubahan di JobDraft

Semua field draft mengikuti nama field Job yang baru, dengan `tanggal?` dan `fee?` tetap nullable untuk representasi input kosong.

#### 4. Perubahan di JobGateway Contract

Parameter dan return type disesuaikan:
- `observeJob(id: Long)` - dulu `String`
- `updateJob(id: Long, ...)` - dulu `String`
- `deleteJob(id: Long)` - dulu `String`
- `updatePayment(id: Long, dibayar: Long)` - dulu `(String, paidRupiah)`
- `checkConflict(tanggal, jamMulaiMenit, durasiMenit, excludeId: Long?)` - dulu semua Inggris

#### 5. Perubahan di DemoJobGateway

- Storage: `Map<Long, Job>` - dulu `Map<String, Job>`
- ID generation: `nextId++` - dulu `UUID.randomUUID().toString()`
- Reset seed: `nextId` dihitung dari max ID seed + 1

#### 6. Perubahan di DemoFixtures

- Semua parameter `createJob()` memakai nama Indonesia
- ID menjadi `Long`: 1L, 2L, 3L, dst
- Large dataset: 1001L sampai 2000L (1000 job)

### Alasan Refactor

1. **Konsistensi dengan Spesifikasi Database**
   - PRD §7.1 mendefinisikan skema tabel Room dengan nama Indonesia
   - File backup JSON (PRD §7.3) akan memakai nama field yang sama
   - Migrasi ke backend Room tidak perlu mapping ulang

2. **Auto-increment ID**
   - Room auto-generate menghasilkan `Long`, bukan `String`
   - UUID hanya relevan untuk sistem distributed; aplikasi ini lokal offline
   - ID `Long` lebih efisien untuk indexing dan query

3. **Persiapan Integrasi Backend**
   - Nama field model UI sama dengan kolom database
   - Tidak perlu DTO/mapper terpisah untuk layer data
   - Mengurangi kompleksitas saat implementasi Room entity

### Dampak

- ✅ Model UI sekarang 100% sesuai PRD §7.1 dan Brief §7.1
- ✅ Siap untuk integrasi Room tanpa perubahan skema
- ✅ File backup JSON akan konsisten dengan spesifikasi
- ✅ Build Kotlin tetap berhasil setelah refactor

### File yang Diubah

1. `app/src/main/java/com/example/kontenin/model/Job.kt`
2. `app/src/main/java/com/example/kontenin/model/JobDraft.kt`
3. `app/src/main/java/com/example/kontenin/contract/JobGateway.kt`
4. `app/src/main/java/com/example/kontenin/demo/DemoJobGateway.kt`
5. `app/src/main/java/com/example/kontenin/demo/DemoFixtures.kt`

### Testing Verifikasi

Setelah refactor, testing steps FE-02 tetap valid:
1. ✅ Observer daftar/detail menerima nilai yang sama
2. ✅ Data kembali ke seed saat restart proses
3. ✅ Skenario gagal/tertunda bekerja deterministik
4. ✅ Fixture lengkap (mendatang, masa lalu, lintas malam, nama panjang, 1000 job)

### Catatan untuk Task Berikutnya

- FE-03+ dapat menggunakan nama field baru ini
- Formatter dan presenter harus memakai `job.namaPengantin1`, `job.tanggal`, dst
- Tidak perlu mapping/converter tambahan antara UI dan data layer
