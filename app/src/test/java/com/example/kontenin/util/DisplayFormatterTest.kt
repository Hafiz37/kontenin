package com.example.kontenin.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests untuk DateTimeFormatter dan RupiahFormatter - FE-06.
 * 
 * Memastikan format sesuai DESIGN D-12 dan OQ-7.
 */
class DisplayFormatterTest {
    
    // ========== DateTimeFormatter Tests ==========
    
    @Test
    fun `format date with actual day name from calendar`() {
        // 18 Oktober 2026 adalah hari Minggu (bukan Sabtu!)
        val epochDay = LocalDate.of(2026, 10, 18).toEpochDay()
        val result = KonteninDateTimeFormatter.formatDate(epochDay)
        
        // Nama hari harus dari kalender aktual
        assertTrue("Result should contain day name: $result", 
            result.contains("Min") || result.contains("Mgu"))
        assertTrue("Result should contain date: $result", result.contains("18 Okt 2026"))
    }
    
    @Test
    fun `format time from minutes since midnight`() {
        assertEquals("10.00", KonteninDateTimeFormatter.formatTime(600))
        assertEquals("14.00", KonteninDateTimeFormatter.formatTime(840))
        assertEquals("22.00", KonteninDateTimeFormatter.formatTime(1320))
        assertEquals("00.00", KonteninDateTimeFormatter.formatTime(0))
        assertEquals("23.59", KonteninDateTimeFormatter.formatTime(1439))
    }
    
    @Test
    fun `time range on same day`() {
        // 18 Okt 2026, 10:00, durasi 4 jam (240 menit)
        val epochDay = LocalDate.of(2026, 10, 18).toEpochDay()
        val result = KonteninDateTimeFormatter.formatTimeRange(epochDay, 600, 240)
        
        assertEquals("10.00 - 14.00", result)
    }
    
    @Test
    fun `time range crosses midnight shows next day date OQ-7`() {
        // 18 Okt 2026, 22:00 (1320 menit), durasi 4 jam (240 menit)
        // Selesai 19 Okt 02:00
        val epochDay = LocalDate.of(2026, 10, 18).toEpochDay()
        val result = KonteninDateTimeFormatter.formatTimeRange(epochDay, 1320, 240)
        
        // Sesuai OQ-7: "22.00 - 02.00 (19 Okt)"
        assertTrue("Should show end time crossing midnight: $result", 
            result.contains("22.00 - 02.00"))
        assertTrue("Should show next day date: $result", 
            result.contains("19 Okt"))
    }
    
    @Test
    fun `time range with 24 hour duration crosses to next day`() {
        // 18 Okt 2026, 08:00, durasi 24 jam (1440 menit)
        val epochDay = LocalDate.of(2026, 10, 18).toEpochDay()
        val result = KonteninDateTimeFormatter.formatTimeRange(epochDay, 480, 1440)
        
        // 08:00 + 24 jam = 19 Okt 08:00
        assertTrue("Should cross to next day: $result", result.contains("19 Okt"))
    }
    
    @Test
    fun `format date and time range combined`() {
        val epochDay = LocalDate.of(2026, 10, 18).toEpochDay()
        val result = KonteninDateTimeFormatter.formatDateAndTimeRange(epochDay, 600, 240)
        
        assertTrue("Should contain date: $result", result.contains("2026"))
        assertTrue("Should contain separator: $result", result.contains("·"))
        assertTrue("Should contain time range: $result", result.contains("10.00 - 14.00"))
    }
    
    @Test
    fun `format date short without year`() {
        val epochDay = LocalDate.of(2026, 10, 19).toEpochDay()
        val result = KonteninDateTimeFormatter.formatDateShort(epochDay)
        
        assertEquals("19 Okt", result)
    }
    
    @Test
    fun `get day name in Indonesian`() {
        // 18 Oktober 2026 adalah Minggu
        val epochDay = LocalDate.of(2026, 10, 18).toEpochDay()
        val result = KonteninDateTimeFormatter.getDayName(epochDay)
        
        assertTrue("Day name should be Min or Mgu: $result", 
            result.contains("Min") || result.contains("Mgu"))
    }
    
    // ========== RupiahFormatter Tests ==========
    
    @Test
    fun `format rupiah with thousands separator`() {
        assertEquals("Rp 1.500.000", KonteninRupiahFormatter.formatRupiah(1_500_000))
        assertEquals("Rp 3.500.000", KonteninRupiahFormatter.formatRupiah(3_500_000))
    }
    
    @Test
    fun `format rupiah zero`() {
        assertEquals("Rp 0", KonteninRupiahFormatter.formatRupiah(0))
    }
    
    @Test
    fun `format rupiah large amount`() {
        assertEquals("Rp 10.000.000.000", KonteninRupiahFormatter.formatRupiah(10_000_000_000))
    }
    
    @Test
    fun `format rupiah with label`() {
        val result = KonteninRupiahFormatter.formatRupiahWithLabel(1_500_000, "Kurang")
        assertEquals("Kurang Rp 1.500.000", result)
    }
    
    @Test
    fun `format number only without Rp prefix`() {
        assertEquals("1.500.000", KonteninRupiahFormatter.formatNumberOnly(1_500_000))
    }
    
    @Test
    fun `parse rupiah string to long`() {
        assertEquals(1_500_000L, KonteninRupiahFormatter.parseRupiah("1.500.000"))
        assertEquals(1_500_000L, KonteninRupiahFormatter.parseRupiah("1500000"))
        assertEquals(1_500_000L, KonteninRupiahFormatter.parseRupiah("Rp 1.500.000"))
    }
    
    @Test
    fun `parse invalid rupiah returns null`() {
        assertEquals(null, KonteninRupiahFormatter.parseRupiah("abc"))
        assertEquals(null, KonteninRupiahFormatter.parseRupiah(""))
    }
    
    // ========== Integration Tests ==========
    
    @Test
    fun `payment status partial payment formatting`() {
        val fee = 2_000_000L
        val dibayar = 500_000L
        val sisa = fee - dibayar
        
        val result = "Kurang ${KonteninRupiahFormatter.formatRupiah(sisa)}"
        assertEquals("Kurang Rp 1.500.000", result)
    }
}
