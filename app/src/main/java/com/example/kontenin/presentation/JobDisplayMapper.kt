package com.example.kontenin.presentation

import com.example.kontenin.model.Job
import com.example.kontenin.util.KonteninDateTimeFormatter
import com.example.kontenin.util.KonteninRupiahFormatter

/**
 * JobDisplayMapper untuk Kontenin - FE-06.
 * 
 * Memetakan Job model menjadi nilai presentasi turunan yang siap ditampilkan.
 * Nilai turunan TIDAK disimpan di database, hanya dihitung saat diperlukan.
 * 
 * Sesuai FR-1.5: jam selesai, status lunas, dan sisa dihitung (tidak disimpan).
 */

/**
 * Status pembayaran job sesuai FR-3.4 dan DESIGN §5.2.
 */
enum class PaymentStatus {
    /** Fee = 0 atau dibayar >= fee: tidak ada chip */
    PAID,
    
    /** Dibayar = 0 dan fee > 0: chip "Belum lunas" */
    UNPAID,
    
    /** 0 < dibayar < fee: chip "Kurang Rp X" */
    PARTIAL,
    
    /** Fee = 0: kasus khusus, tidak ada chip */
    NO_FEE
}

/**
 * Data class berisi nilai turunan untuk presentasi UI.
 * Dihasilkan dari Job model.
 */
data class JobDisplay(
    val id: Long,
    
    // Data asli
    val namaPengantin1: String,
    val namaPengantin2: String,
    val tanggal: Long,
    val jamMulaiMenit: Int,
    val durasiMenit: Int,
    val acara: String,
    val alamat: String,
    val shareloc: String,
    val sumberJob: String,
    val fee: Long,
    val dibayar: Long,
    val catatan: String,
    
    // Nilai turunan untuk UI
    val namaPasangan: String,              // "Andi & Sari"
    val tanggalFormatted: String,          // "Sab, 18 Okt 2026"
    val timeRangeFormatted: String,        // "10.00 - 14.00" atau "22.00 - 02.00 (19 Okt)"
    val dateAndTimeFormatted: String,      // "Sab, 18 Okt 2026 · 10.00 - 14.00"
    val feeFormatted: String,              // "Rp 1.500.000"
    val dibayarFormatted: String,          // "Rp 500.000"
    val sisaFormatted: String,             // "Rp 1.000.000"
    val sisaAmount: Long,                  // 1000000
    val paymentStatus: PaymentStatus,      // PAID, UNPAID, PARTIAL, NO_FEE
    val paymentChipText: String?,          // "Belum lunas" atau "Kurang Rp 1.000.000" atau null
    val isLunas: Boolean,                  // dibayar >= fee
    val hasShareloc: Boolean               // shareloc tidak kosong
)

/**
 * Extension function untuk mapping Job → JobDisplay.
 * Pure function tanpa efek samping.
 */
fun Job.toDisplay(): JobDisplay {
    val namaPasangan = "$namaPengantin1 & $namaPengantin2"
    val tanggalFormatted = KonteninDateTimeFormatter.formatDate(tanggal)
    val timeRangeFormatted = KonteninDateTimeFormatter.formatTimeRange(
        tanggal, jamMulaiMenit, durasiMenit
    )
    val dateAndTimeFormatted = KonteninDateTimeFormatter.formatDateAndTimeRange(
        tanggal, jamMulaiMenit, durasiMenit
    )
    val feeFormatted = KonteninRupiahFormatter.formatRupiah(fee)
    val dibayarFormatted = KonteninRupiahFormatter.formatRupiah(dibayar)
    val sisaAmount = fee - dibayar
    val sisaFormatted = KonteninRupiahFormatter.formatRupiah(sisaAmount)
    val isLunas = dibayar >= fee
    val hasShareloc = shareloc.isNotBlank()
    
    // Hitung status pembayaran sesuai FR-3.4
    val paymentStatus = when {
        fee <= 0L -> PaymentStatus.NO_FEE
        isLunas -> PaymentStatus.PAID
        dibayar <= 0L -> PaymentStatus.UNPAID
        else -> PaymentStatus.PARTIAL
    }
    
    // Text untuk chip (null jika tidak ada chip)
    val paymentChipText = when (paymentStatus) {
        PaymentStatus.UNPAID -> "Belum lunas"
        PaymentStatus.PARTIAL -> "Kurang ${KonteninRupiahFormatter.formatRupiah(sisaAmount)}"
        else -> null
    }
    
    return JobDisplay(
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
        namaPasangan = namaPasangan,
        tanggalFormatted = tanggalFormatted,
        timeRangeFormatted = timeRangeFormatted,
        dateAndTimeFormatted = dateAndTimeFormatted,
        feeFormatted = feeFormatted,
        dibayarFormatted = dibayarFormatted,
        sisaFormatted = sisaFormatted,
        sisaAmount = sisaAmount,
        paymentStatus = paymentStatus,
        paymentChipText = paymentChipText,
        isLunas = isLunas,
        hasShareloc = hasShareloc
    )
}
