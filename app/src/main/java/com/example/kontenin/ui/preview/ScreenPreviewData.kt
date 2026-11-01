package com.example.kontenin.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.kontenin.demo.DemoFixtures
import com.example.kontenin.model.Job
import com.example.kontenin.presentation.toDisplay
import com.example.kontenin.ui.components.JobCard
import com.example.kontenin.ui.components.JobCardCompact
import com.example.kontenin.ui.components.SkeletonJobList
import com.example.kontenin.ui.theme.KonteninTheme
import java.time.LocalDate

/**
 * ScreenPreviewData (FE-18).
 *
 * Provider data preview dan skenario ekstrem untuk pengujian visual UI:
 * 1. Judul & nama pasangan sangat panjang (font scale 200%, judul duplikat).
 * 2. Nominal sangat besar (ratusan juta).
 * 3. Rentang waktu lintas tengah malam (22.00 - 02.00).
 * 4. Koleksi 1000 job untuk verifikasi performa LazyColumn.
 * 5. State skeleton loading statis 3 kartu (DESIGN §7.2).
 */
object ScreenPreviewData {

    private val todayEpoch = LocalDate.of(2026, 11, 1).toEpochDay()

    // ── 1. Skenario Ekstrem: Nama & Catatan Sangat Panjang, Lintas Hari ───────
    val extremeLongNameJob = Job(
        id = 9991L,
        namaPengantin1 = "Raden Mas Muhammad Dananjaya Kusuma Diningrat",
        namaPengantin2 = "Raden Ayu Sekar Arum Kinanthi Prameswari",
        tanggal = todayEpoch,
        jamMulaiMenit = 22 * 60, // 22:00
        durasiMenit = 4 * 60,    // 4 jam -> selesai 02:00 (+1 hari)
        acara = "Akad Nikah, Panggih Adat Jawa Gagrag Surakarta & Resepsi Akbar Malam",
        alamat = "Grand Ballroom Sasana Kriya, Kompleks Taman Mini Indonesia Indah, Pintu Utama TMII, Cipayung, Jakarta Timur, Daerah Khusus Ibukota Jakarta 13810",
        shareloc = "https://maps.google.com/?q=-6.3024,106.8951",
        sumberJob = "Vendor Wedding Organizer Samudera Berkah",
        fee = 175_000_000L,
        dibayar = 75_000_000L,
        catatan = "Rundown super detail: Pengantin pria tiba pukul 20.30, prosesi panggih dimulai tepat 22.00 setelah akad selesai. Dresscode seluruh tim dokumentasi jas beskap hitam bordir emas. Hubungi PIC Bapak Haryo (081234567890) untuk koordinasi parkir truk genset dan panggung pelaminan.",
        createdAt = 1793500000000L,
        updatedAt = 1793500000000L
    )

    // ── 2. Skenario Ekstrem: Nominal Sangat Besar (Ratusan Juta) ──────────────
    val extremeLargeNominalJob = Job(
        id = 9992L,
        namaPengantin1 = "Alexander",
        namaPengantin2 = "Catherine",
        tanggal = todayEpoch + 3,
        jamMulaiMenit = 11 * 60,
        durasiMenit = 8 * 60,
        acara = "Royal Wedding Gala",
        alamat = "The Ritz-Carlton Jakarta, Pacific Place",
        shareloc = "https://maps.google.com/?q=-6.2248,106.8094",
        sumberJob = "Platinum International",
        fee = 500_000_000L,
        dibayar = 500_000_000L, // Lunas nominal besar
        catatan = "Full package 3 hari liputan dokumentasi cinema 8K",
        createdAt = 1793500000000L,
        updatedAt = 1793500000000L
    )

    // ── 3. Skenario Khusus: Fee Nol (Gratis / Teman) ─────────────────────────
    val zeroFeeJob = Job(
        id = 9993L,
        namaPengantin1 = "Bambang",
        namaPengantin2 = "Dewi",
        tanggal = todayEpoch + 7,
        jamMulaiMenit = 8 * 60,
        durasiMenit = 3 * 60,
        acara = "Syukuran Pernikahan",
        alamat = "Masjid Agung Al-Azhar",
        shareloc = "",
        sumberJob = "Pribadi",
        fee = 0L,
        dibayar = 0L,
        catatan = "Job sukarela untuk sahabat kuliah",
        createdAt = 1793500000000L,
        updatedAt = 1793500000000L
    )

    // ── 4. Dataset 1000 Job untuk Performance & Memory Stress Test ───────────
    fun get1000Jobs(): List<Job> = DemoFixtures.getLargeDatasetSeed()
}

// ── Preview Ekstrem Font Scale 200% & Dark Mode ───────────────────────────────

@Preview(name = "Extreme Text - Font 200%", fontScale = 2.0f, showBackground = true)
@Composable
fun PreviewExtremeJobCardFont200() {
    KonteninTheme {
        JobCard(
            job = ScreenPreviewData.extremeLongNameJob,
            onClick = {}
        )
    }
}

@Preview(name = "Extreme Nominal - Dark Mode", showBackground = true)
@Composable
fun PreviewExtremeJobCardDarkMode() {
    KonteninTheme(darkTheme = true) {
        JobCard(
            job = ScreenPreviewData.extremeLargeNominalJob,
            onClick = {}
        )
    }
}

@Preview(name = "Compact Card - Font 200%", fontScale = 2.0f, showBackground = true)
@Composable
fun PreviewCompactCardFont200() {
    KonteninTheme {
        JobCardCompact(
            job = ScreenPreviewData.extremeLongNameJob,
            onClick = {},
            showSource = true
        )
    }
}

@Preview(name = "Skeleton Loading 3 Cards (DESIGN 7.2)", showBackground = true)
@Composable
fun PreviewSkeletonLoading3Cards() {
    KonteninTheme {
        SkeletonJobList(count = 3)
    }
}
