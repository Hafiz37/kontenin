package com.example.kontenin.demo

import com.example.kontenin.model.Job
import java.time.LocalDate

/**
 * Fixture Data Cadangan Demo (FE-17).
 *
 * Menyediakan berbagai skenario cadangan untuk memverifikasi alur ekspor dan impor simulasi:
 * 1. FIXTURE_VALID_3_JOBS: 3 job valid lengkap siap diimpor.
 * 2. FIXTURE_CORRUPTED: File rusak / bukan format JSON valid (EC-16).
 * 3. FIXTURE_NEWER_SCHEMA: File dengan schemaVersion lebih baru (v99) dari aplikasi (EC-16).
 * 4. FIXTURE_INVALID_JOB: File dengan satu job tidak valid (misal fee negatif) (EC-16).
 * 5. FIXTURE_PROCESS_ERROR: File valid saat validasi, namun gagal saat proses/transaksi (EC-17, rollback).
 */
data class BackupFixture(
    val id: String,
    val displayName: String,
    val fileName: String,
    val description: String,
    val isValid: Boolean,
    val errorMessage: String? = null,
    val willFailProcess: Boolean = false,
    val jobs: List<Job> = emptyList()
)

object DemoBackupFixtures {
    const val FIXTURE_VALID_3_JOBS = "valid_3_jobs"
    const val FIXTURE_CORRUPTED = "corrupted"
    const val FIXTURE_NEWER_SCHEMA = "newer_schema"
    const val FIXTURE_INVALID_JOB = "invalid_job"
    const val FIXTURE_PROCESS_ERROR = "process_error"

    // 3 job valid yang representatif (Testing step 1 & 2)
    val valid3Jobs: List<Job> = listOf(
        Job(
            id = 101L,
            namaPengantin1 = "Reza",
            namaPengantin2 = "Maya",
            tanggal = LocalDate.of(2026, 11, 5).toEpochDay(),
            jamMulaiMenit = 9 * 60, // 09:00
            durasiMenit = 4 * 60,   // 4 jam
            acara = "Akad & Resepsi",
            alamat = "Gedung Sasana Budaya, Jakarta",
            shareloc = "https://maps.google.com/?q=-6.200000,106.816666",
            sumberJob = "Vendor Berkah",
            fee = 3_000_000L,
            dibayar = 3_000_000L, // Lunas
            catatan = "Rundown lengkap sudah dikonfirmasi",
            createdAt = 1793500000000L,
            updatedAt = 1793500000000L
        ),
        Job(
            id = 102L,
            namaPengantin1 = "Dimas",
            namaPengantin2 = "Putri",
            tanggal = LocalDate.of(2026, 11, 12).toEpochDay(),
            jamMulaiMenit = 13 * 60, // 13:00
            durasiMenit = 3 * 60,   // 3 jam
            acara = "Resepsi",
            alamat = "Hotel Santika Premiere",
            shareloc = "https://maps.google.com/?q=-6.208800,106.845600",
            sumberJob = "Pribadi",
            fee = 2_500_000L,
            dibayar = 1_000_000L, // Kurang 1.500.000
            catatan = "DP 1 juta, pelunasan H-1",
            createdAt = 1793500000000L,
            updatedAt = 1793500000000L
        ),
        Job(
            id = 103L,
            namaPengantin1 = "Fajar",
            namaPengantin2 = "Bella",
            tanggal = LocalDate.of(2026, 11, 20).toEpochDay(),
            jamMulaiMenit = 10 * 60, // 10:00
            durasiMenit = 2 * 60,   // 2 jam
            acara = "Lamaran",
            alamat = "Kediaman Mempelai Wanita",
            shareloc = "",
            sumberJob = "Vendor Cahaya",
            fee = 1_500_000L,
            dibayar = 0L, // Belum dibayar
            catatan = "Dresscode hitam formal",
            createdAt = 1793500000000L,
            updatedAt = 1793500000000L
        )
    )

    val allFixtures: List<BackupFixture> = listOf(
        BackupFixture(
            id = FIXTURE_VALID_3_JOBS,
            displayName = "Cadangan Valid (3 Job)",
            fileName = "kontenin-backup-valid-3jobs.json",
            description = "File cadangan valid berisi 3 job lengkap",
            isValid = true,
            jobs = valid3Jobs
        ),
        BackupFixture(
            id = FIXTURE_CORRUPTED,
            displayName = "File Rusak / Format Salah",
            fileName = "kontenin-corrupted.json",
            description = "Bukan format JSON yang valid atau terpotong (EC-16)",
            isValid = false,
            errorMessage = "File cadangan tidak valid"
        ),
        BackupFixture(
            id = FIXTURE_NEWER_SCHEMA,
            displayName = "Skema Lebih Baru (v99)",
            fileName = "kontenin-v99-backup.json",
            description = "schemaVersion 99 lebih baru dari versi aplikasi (EC-16)",
            isValid = false,
            errorMessage = "File cadangan tidak valid"
        ),
        BackupFixture(
            id = FIXTURE_INVALID_JOB,
            displayName = "Satu Job Tidak Valid",
            fileName = "kontenin-invalid-job.json",
            description = "Satu job memiliki field wajib tidak valid (EC-16)",
            isValid = false,
            errorMessage = "File cadangan tidak valid"
        ),
        BackupFixture(
            id = FIXTURE_PROCESS_ERROR,
            displayName = "Gagal di Tengah Proses (Rollback)",
            fileName = "kontenin-fail-midway.json",
            description = "Validasi berhasil, namun transaksi gagal saat penyimpanan (EC-17)",
            isValid = true,
            willFailProcess = true,
            jobs = valid3Jobs
        )
    )

    fun getFixture(keyOrPath: String): BackupFixture? {
        return allFixtures.find {
            it.id.equals(keyOrPath, ignoreCase = true) ||
            it.fileName.equals(keyOrPath, ignoreCase = true) ||
            keyOrPath.endsWith(it.fileName, ignoreCase = true)
        }
    }
}
