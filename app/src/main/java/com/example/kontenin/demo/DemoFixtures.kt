package com.example.kontenin.demo

import com.example.kontenin.model.Job
import com.example.kontenin.util.AppClock
import com.example.kontenin.util.SystemClock

object DemoFixtures {
    
    private fun createJob(
        id: Long,
        namaPengantin1: String,
        namaPengantin2: String,
        tanggal: Long,
        jamMulaiMenit: Int,
        durasiMenit: Int,
        acara: String,
        alamat: String,
        shareloc: String,
        sumberJob: String,
        fee: Long,
        dibayar: Long,
        catatan: String,
        createdAt: Long,
        updatedAt: Long
    ): Job = Job(
        id = id,
        namaPengantin1 = namaPengantin1,
        namaPengantin2 = namaPengantin2,
        tanggal = tanggal,
        jamMulaiMenit = jamMulaiMenit,
        durasiMenit = durasiMenit,
        acara = acara,
        alamat = alamat,
        shareloc = shareloc,
        sumberJob = sumberJob,
        fee = fee,
        dibayar = dibayar,
        catatan = catatan,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    fun getDefaultSeed(clock: AppClock = SystemClock()): List<Job> {
        val today = clock.nowLocalDate().toEpochDay()
        val baseTime = clock.nowEpochMillis()
        
        return listOf(
            createJob(
                id = 1L,
                namaPengantin1 = "Budi",
                namaPengantin2 = "Santoso",
                tanggal = today + 3,
                jamMulaiMenit = 600,
                durasiMenit = 120,
                acara = "Pernikahan",
                alamat = "Gedung Sasana Kriya, Jl. Raya Bogor",
                shareloc = "https://maps.app.goo.gl/sample1",
                sumberJob = "Referral",
                fee = 5000000,
                dibayar = 2000000,
                catatan = "DP 40%",
                createdAt = baseTime - 86400000,
                updatedAt = baseTime - 86400000
            ),
            createJob(
                id = 2L,
                namaPengantin1 = "Siti",
                namaPengantin2 = "Rahayu",
                tanggal = today,
                jamMulaiMenit = 1380,
                durasiMenit = 180,
                acara = "Ulang Tahun",
                alamat = "Resto Padang Merdeka",
                shareloc = "",
                sumberJob = "Instagram",
                fee = 3000000,
                dibayar = 3000000,
                catatan = "Lunas",
                createdAt = baseTime - 172800000,
                updatedAt = baseTime - 172800000
            ),
            createJob(
                id = 3L,
                namaPengantin1 = "Ahmad",
                namaPengantin2 = "Fauzi",
                tanggal = today - 5,
                jamMulaiMenit = 720,
                durasiMenit = 90,
                acara = "Seminar",
                alamat = "Hotel Grand Indonesia",
                shareloc = "https://maps.app.goo.gl/sample3",
                sumberJob = "WhatsApp",
                fee = 2500000,
                dibayar = 2500000,
                catatan = "",
                createdAt = baseTime - 604800000,
                updatedAt = baseTime - 604800000
            ),
            createJob(
                id = 4L,
                namaPengantin1 = "Dewi",
                namaPengantin2 = "Lestari Wijayanti Kusuma",
                tanggal = today + 1,
                jamMulaiMenit = 60,
                durasiMenit = 240,
                acara = "Pra-wedding",
                alamat = "Taman Mini Indonesia Indah",
                shareloc = "https://maps.app.goo.gl/sample4",
                sumberJob = "Website",
                fee = 7500000,
                dibayar = 0,
                catatan = "Belum DP, client minta reschedule",
                createdAt = baseTime - 259200000,
                updatedAt = baseTime - 86400000
            ),
            createJob(
                id = 5L,
                namaPengantin1 = "Rudi",
                namaPengantin2 = "Hartono",
                tanggal = today + 7,
                jamMulaiMenit = 540,
                durasiMenit = 60,
                acara = "Graduation",
                alamat = "Universitas Indonesia, Depok",
                shareloc = "",
                sumberJob = "Referral",
                fee = 0,
                dibayar = 0,
                catatan = "Gratis untuk teman",
                createdAt = baseTime - 432000000,
                updatedAt = baseTime - 432000000
            ),
            createJob(
                id = 6L,
                namaPengantin1 = "Linda",
                namaPengantin2 = "Kusuma",
                tanggal = today + 14,
                jamMulaiMenit = 1200,
                durasiMenit = 300,
                acara = "Corporate Event",
                alamat = "JCC Senayan, Jakarta",
                shareloc = "https://maps.app.goo.gl/sample6",
                sumberJob = "Email",
                fee = 15000000,
                dibayar = 5000000,
                catatan = "DP 33%, sisa pelunasan H-3",
                createdAt = baseTime - 518400000,
                updatedAt = baseTime - 172800000
            )
        )
    }
    
    fun getLargeDatasetSeed(clock: AppClock = SystemClock()): List<Job> {
        val today = clock.nowLocalDate().toEpochDay()
        val baseTime = clock.nowEpochMillis()
        
        return (1..1000).map { i ->
            createJob(
                id = 1000L + i,
                namaPengantin1 = "Client",
                namaPengantin2 = "$i",
                tanggal = today + (i % 100) - 50,
                jamMulaiMenit = (i % 24) * 60,
                durasiMenit = 60 + (i % 5) * 30,
                acara = "Event $i",
                alamat = "Address $i",
                shareloc = if (i % 3 == 0) "https://maps.app.goo.gl/sample$i" else "",
                sumberJob = listOf("Instagram", "WhatsApp", "Referral", "Website", "Email")[i % 5],
                fee = (i % 10 + 1) * 1000000L,
                dibayar = if (i % 3 == 0) ((i % 10 + 1) * 1000000L) else 0L,
                catatan = if (i % 5 == 0) "Notes for job $i" else "",
                createdAt = baseTime - (i * 3600000L),
                updatedAt = baseTime - (i * 3600000L)
            )
        }
    }
    
    fun getEmptySeed(): List<Job> = emptyList()
}
