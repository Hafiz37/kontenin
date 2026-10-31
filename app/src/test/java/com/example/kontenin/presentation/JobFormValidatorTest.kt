package com.example.kontenin.presentation

import com.example.kontenin.presentation.jobform.FormValidationError
import com.example.kontenin.presentation.jobform.JobFormField
import com.example.kontenin.presentation.jobform.JobFormValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test murni untuk JobFormValidator (FE-09).
 *
 * Menguji seluruh batas PRD, parsing durasi koma/titik, bilangan besar,
 * overflow, hubungan fee-dibayar, dan status tombol Lunas.
 */
class JobFormValidatorTest {

    // ── 1. Uji batas Nama Pengantin 1 & 2 (50/51 karakter) ──────────────────

    @Test
    fun `nama pengantin wajib diisi dan trim whitespace`() {
        assertEquals(FormValidationError.NamaPengantin1Required, JobFormValidator.validateNamaPengantin1(""))
        assertEquals(FormValidationError.NamaPengantin1Required, JobFormValidator.validateNamaPengantin1("   "))
        assertEquals(FormValidationError.NamaPengantin2Required, JobFormValidator.validateNamaPengantin2(""))
        assertEquals(FormValidationError.NamaPengantin2Required, JobFormValidator.validateNamaPengantin2("   "))

        assertNull(JobFormValidator.validateNamaPengantin1("Andi"))
        assertNull(JobFormValidator.validateNamaPengantin2("Sari"))
    }

    @Test
    fun `nama pengantin batas 50 karakter valid dan 51 karakter ditolak`() {
        val nama50 = "A".repeat(50)
        val nama51 = "A".repeat(51)

        assertNull(JobFormValidator.validateNamaPengantin1(nama50))
        assertNull(JobFormValidator.validateNamaPengantin2(nama50))

        assertEquals(FormValidationError.NamaPengantin1TooLong, JobFormValidator.validateNamaPengantin1(nama51))
        assertEquals(FormValidationError.NamaPengantin2TooLong, JobFormValidator.validateNamaPengantin2(nama51))

        // Uji trim: 50 karakter dengan spasi di ujung tetap valid
        val nama50WithSpaces = "   $nama50   "
        assertNull(JobFormValidator.validateNamaPengantin1(nama50WithSpaces))
    }

    // ── 2. Uji Tanggal dan Jam Mulai ────────────────────────────────────────

    @Test
    fun `tanggal wajib diisi dan tanggal masa lalu diperbolehkan`() {
        assertEquals(FormValidationError.TanggalRequired, JobFormValidator.validateTanggal(null))

        // Tanggal masa lalu (misalnya -100 hari atau 0)
        assertNull(JobFormValidator.validateTanggal(-100L))
        assertNull(JobFormValidator.validateTanggal(0L))
        assertNull(JobFormValidator.validateTanggal(20000L))
    }

    @Test
    fun `jam mulai wajib diisi dan berada pada 0 sampai 1439`() {
        assertEquals(FormValidationError.JamMulaiRequired, JobFormValidator.validateJamMulai(null))
        assertEquals(FormValidationError.JamMulaiRequired, JobFormValidator.validateJamMulai(-1))
        assertEquals(FormValidationError.JamMulaiRequired, JobFormValidator.validateJamMulai(1440))

        assertNull(JobFormValidator.validateJamMulai(0))    // 00:00
        assertNull(JobFormValidator.validateJamMulai(480))  // 08:00
        assertNull(JobFormValidator.validateJamMulai(1439)) // 23:59
    }

    // ── 3. Uji Durasi (0,5; 24; 0; 24,5; 1,25; koma dan titik) ─────────────

    @Test
    fun `durasi 0,5 dan 24 valid dengan koma maupun titik`() {
        // 0.5 dan 0,5
        assertEquals(0.5, JobFormValidator.parseDurationHours("0.5")!!, 1e-6)
        assertEquals(0.5, JobFormValidator.parseDurationHours("0,5")!!, 1e-6)
        assertEquals(30, JobFormValidator.parseDurationMinutes("0,5"))
        assertNull(JobFormValidator.validateDurasi("0.5"))
        assertNull(JobFormValidator.validateDurasi("0,5"))

        // 24 dan 24,0
        assertEquals(24.0, JobFormValidator.parseDurationHours("24")!!, 1e-6)
        assertEquals(24.0, JobFormValidator.parseDurationHours("24,0")!!, 1e-6)
        assertEquals(1440, JobFormValidator.parseDurationMinutes("24"))
        assertNull(JobFormValidator.validateDurasi("24"))
        assertNull(JobFormValidator.validateDurasi("24,0"))

        // Nilai umum (4 jam)
        assertEquals(4.0, JobFormValidator.parseDurationHours("4")!!, 1e-6)
        assertEquals(240, JobFormValidator.parseDurationMinutes("4"))
        assertNull(JobFormValidator.validateDurasi("4"))
    }

    @Test
    fun `durasi di luar rentang atau bukan kelipatan 0,5 ditolak`() {
        // 0 -> di luar rentang (< 0.5)
        assertNull(JobFormValidator.parseDurationHours("0"))
        assertEquals(FormValidationError.DurasiInvalid, JobFormValidator.validateDurasi("0"))

        // 24,5 -> di luar rentang (> 24.0)
        assertNull(JobFormValidator.parseDurationHours("24.5"))
        assertNull(JobFormValidator.parseDurationHours("24,5"))
        assertEquals(FormValidationError.DurasiInvalid, JobFormValidator.validateDurasi("24.5"))
        assertEquals(FormValidationError.DurasiInvalid, JobFormValidator.validateDurasi("24,5"))

        // 1,25 -> bukan kelipatan 0.5
        assertNull(JobFormValidator.parseDurationHours("1.25"))
        assertNull(JobFormValidator.parseDurationHours("1,25"))
        assertEquals(FormValidationError.DurasiInvalid, JobFormValidator.validateDurasi("1.25"))
        assertEquals(FormValidationError.DurasiInvalid, JobFormValidator.validateDurasi("1,25"))

        // Input kosong / non-angka
        assertEquals(FormValidationError.DurasiInvalid, JobFormValidator.validateDurasi(""))
        assertEquals(FormValidationError.DurasiInvalid, JobFormValidator.validateDurasi("abc"))
    }

    // ── 4. Uji Acara dan Sumber Job (1–50 karakter) ─────────────────────────

    @Test
    fun `acara dan sumber job wajib dan batas 50 karakter`() {
        assertEquals(FormValidationError.AcaraRequired, JobFormValidator.validateAcara(""))
        assertEquals(FormValidationError.AcaraRequired, JobFormValidator.validateAcara("   "))
        assertEquals(FormValidationError.SumberJobRequired, JobFormValidator.validateSumberJob(""))
        assertEquals(FormValidationError.SumberJobRequired, JobFormValidator.validateSumberJob("   "))

        val text50 = "X".repeat(50)
        val text51 = "X".repeat(51)

        assertNull(JobFormValidator.validateAcara(text50))
        assertNull(JobFormValidator.validateSumberJob(text50))

        assertEquals(FormValidationError.AcaraTooLong, JobFormValidator.validateAcara(text51))
        assertEquals(FormValidationError.SumberJobTooLong, JobFormValidator.validateSumberJob(text51))
    }

    // ── 5. Uji Alamat (300/301 karakter) ────────────────────────────────────

    @Test
    fun `alamat batas 300 karakter valid dan 301 ditolak`() {
        assertNull(JobFormValidator.validateAlamat(""))

        val text300 = "B".repeat(300)
        val text301 = "B".repeat(301)

        assertNull(JobFormValidator.validateAlamat(text300))
        assertEquals(FormValidationError.AlamatTooLong, JobFormValidator.validateAlamat(text301))
    }

    // ── 6. Uji Shareloc (http:// atau https://) ──────────────────────────────

    @Test
    fun `shareloc opsional dan bila diisi harus diawali http atau https`() {
        assertNull(JobFormValidator.validateShareloc(""))
        assertNull(JobFormValidator.validateShareloc("   "))

        assertNull(JobFormValidator.validateShareloc("https://maps.google.com/?q=-7.7,110.3"))
        assertNull(JobFormValidator.validateShareloc("http://maps.google.com"))
        assertNull(JobFormValidator.validateShareloc("HTTPS://MAPS.APP.GOO.GL/XYZ"))

        assertEquals(FormValidationError.SharelocInvalidPrefix, JobFormValidator.validateShareloc("maps.google.com"))
        assertEquals(FormValidationError.SharelocInvalidPrefix, JobFormValidator.validateShareloc("ftp://maps.google.com"))
        assertEquals(FormValidationError.SharelocInvalidPrefix, JobFormValidator.validateShareloc("geo:-7.7,110.3"))
    }

    // ── 7. Uji Catatan (1000/1001 karakter) ──────────────────────────────────

    @Test
    fun `catatan batas 1000 karakter valid dan 1001 ditolak`() {
        assertNull(JobFormValidator.validateCatatan(""))

        val text1000 = "C".repeat(1000)
        val text1001 = "C".repeat(1001)

        assertNull(JobFormValidator.validateCatatan(text1000))
        assertEquals(FormValidationError.CatatanTooLong, JobFormValidator.validateCatatan(text1001))
    }

    // ── 8. Uji Fee (kosong, 0, negatif, overflow) & Status Tombol Lunas ───────

    @Test
    fun `fee kosong, 0, negatif, dan overflow ditangani dengan benar`() {
        // Fee kosong
        assertEquals(FormValidationError.FeeRequired, JobFormValidator.validateFee(""))
        assertEquals(FormValidationError.FeeRequired, JobFormValidator.validateFee("   "))

        // Fee 0 adalah valid integer nonnegatif (EC-8)
        assertNull(JobFormValidator.validateFee("0"))

        // Fee valid positif
        assertNull(JobFormValidator.validateFee("1000000"))

        // Fee negatif
        assertEquals(FormValidationError.FeeRequired, JobFormValidator.validateFee("-500000"))

        // Fee overflow (melebihi Long.MAX_VALUE)
        val overflowFee = "999999999999999999999999999999999"
        assertEquals(FormValidationError.FeeOverflow, JobFormValidator.validateFee(overflowFee))
    }

    @Test
    fun `tombol Lunas hanya aktif pada fee valid dan lebih dari 0`() {
        // Kosong -> nonaktif
        assertFalse(JobFormValidator.isLunasEnabled(""))
        assertFalse(JobFormValidator.isLunasEnabled("   "))

        // 0 -> nonaktif (OQ-8)
        assertFalse(JobFormValidator.isLunasEnabled("0"))

        // Negatif -> nonaktif
        assertFalse(JobFormValidator.isLunasEnabled("-100000"))

        // Overflow -> nonaktif
        assertFalse(JobFormValidator.isLunasEnabled("999999999999999999999999999999999"))

        // Fee valid > 0 -> aktif
        assertTrue(JobFormValidator.isLunasEnabled("1000000"))
        assertTrue(JobFormValidator.isLunasEnabled("50000"))
    }

    // ── 9. Uji Hubungan Fee dan Dibayar ─────────────────────────────────────

    @Test
    fun `dibayar tidak boleh melebihi fee dan penurunan fee memunculkan error`() {
        val fee1Juta = "1000000"

        // Dibayar kosong -> valid (diartikan 0)
        assertNull(JobFormValidator.validateDibayar("", fee1Juta))

        // Dibayar 0 -> valid
        assertNull(JobFormValidator.validateDibayar("0", fee1Juta))

        // Dibayar sama dengan fee (Lunas) -> valid
        assertNull(JobFormValidator.validateDibayar("1000000", fee1Juta))

        // Dibayar 1.100.000 > fee 1.000.000 -> error dibayar
        assertEquals(
            FormValidationError.DibayarExceedsFee,
            JobFormValidator.validateDibayar("1100000", fee1Juta)
        )

        // Turunkan fee setelah Lunas (fee 800.000, dibayar 1.000.000) -> error dibayar
        val fee800Ribu = "800000"
        assertEquals(
            FormValidationError.DibayarExceedsFee,
            JobFormValidator.validateDibayar("1000000", fee800Ribu)
        )

        // Dibayar overflow
        val overflowPaid = "999999999999999999999999999999999"
        assertEquals(
            FormValidationError.DibayarOverflow,
            JobFormValidator.validateDibayar(overflowPaid, fee1Juta)
        )
    }

    // ── 10. Uji Validasi Menyeluruh Form & Urutan Error Pertama ──────────────

    @Test
    fun `form kosong menghasilkan error pada semua field wajib dan error pertama adalah nama pengantin 1`() {
        val errors = JobFormValidator.validateAll(
            namaPengantin1 = "",
            namaPengantin2 = "",
            tanggalEpochDay = null,
            jamMulaiMenit = null,
            durasiText = "4", // Default 4 valid
            acara = "",
            sumberJob = "",
            alamat = "",
            shareloc = "",
            feeText = "",
            dibayarText = "0",
            catatan = ""
        )

        assertFalse(errors.isEmpty())
        assertTrue(errors.containsKey(JobFormField.NAMA_PENGANTIN_1))
        assertTrue(errors.containsKey(JobFormField.NAMA_PENGANTIN_2))
        assertTrue(errors.containsKey(JobFormField.TANGGAL))
        assertTrue(errors.containsKey(JobFormField.JAM_MULAI))
        assertTrue(errors.containsKey(JobFormField.ACARA))
        assertTrue(errors.containsKey(JobFormField.SUMBER_JOB))
        assertTrue(errors.containsKey(JobFormField.FEE))

        // Error pertama berdasarkan urutan visual form
        val firstErrorField = JobFormField.entries.firstOrNull { errors.containsKey(it) }
        assertEquals(JobFormField.NAMA_PENGANTIN_1, firstErrorField)
    }

    @Test
    fun `form terisi lengkap dan valid menghasilkan error map kosong`() {
        val errors = JobFormValidator.validateAll(
            namaPengantin1 = "Andi",
            namaPengantin2 = "Sari",
            tanggalEpochDay = 20000L,
            jamMulaiMenit = 600, // 10:00
            durasiText = "4,5",
            acara = "Resepsi",
            sumberJob = "Instagram",
            alamat = "Gedung Harmoni",
            shareloc = "https://maps.app.goo.gl/xyz",
            feeText = "3500000",
            dibayarText = "1000000",
            catatan = "Rundown aman"
        )

        assertTrue(errors.isEmpty())
    }
}
