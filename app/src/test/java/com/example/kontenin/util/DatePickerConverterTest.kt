package com.example.kontenin.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

/**
 * Unit tests untuk DatePickerConverter - FE-06.
 * 
 * Memastikan konversi UTC dari DatePicker tidak menggeser tanggal (EC-22).
 */
class DatePickerConverterTest {
    
    private val utcZone = ZoneId.of("UTC")
    
    @Test
    fun `convert UTC millis to LocalDate does not shift date`() {
        // Buat UTC millis dari LocalDate
        val originalDate = LocalDate.of(2026, 10, 31)
        val utcMillis = originalDate.atStartOfDay(utcZone).toInstant().toEpochMilli()
        
        val result = KonteninDatePickerConverter.convertPickerToLocalDate(utcMillis)
        
        assertEquals(originalDate, result)
    }
    
    @Test
    fun `convert on month boundary does not shift to previous month`() {
        // Awal bulan
        val originalDate = LocalDate.of(2026, 11, 1)
        val utcMillis = originalDate.atStartOfDay(utcZone).toInstant().toEpochMilli()
        
        val result = KonteninDatePickerConverter.convertPickerToLocalDate(utcMillis)
        
        assertEquals(originalDate, result)
    }
    
    @Test
    fun `convert on year boundary does not shift to previous year`() {
        // Awal tahun
        val originalDate = LocalDate.of(2027, 1, 1)
        val utcMillis = originalDate.atStartOfDay(utcZone).toInstant().toEpochMilli()
        
        val result = KonteninDatePickerConverter.convertPickerToLocalDate(utcMillis)
        
        assertEquals(originalDate, result)
    }
    
    @Test
    fun `convert end of month does not shift to next month`() {
        // Akhir bulan
        val originalDate = LocalDate.of(2026, 10, 31)
        val utcMillis = originalDate.atStartOfDay(utcZone).toInstant().toEpochMilli()
        
        val result = KonteninDatePickerConverter.convertPickerToLocalDate(utcMillis)
        
        assertEquals(originalDate, result)
    }
    
    @Test
    fun `round trip conversion preserves date`() {
        val originalDate = LocalDate.of(2026, 12, 25)
        val utcMillis = KonteninDatePickerConverter.convertLocalDateToPicker(originalDate)
        val resultDate = KonteninDatePickerConverter.convertPickerToLocalDate(utcMillis)
        
        assertEquals(originalDate, resultDate)
    }
    
    @Test
    fun `convert to epoch day for database storage`() {
        val date = LocalDate.of(2026, 10, 18)
        val epochDay = KonteninDatePickerConverter.convertToEpochDay(date)
        
        // Verify we can reconstruct the date
        val reconstructed = LocalDate.ofEpochDay(epochDay)
        assertEquals(date, reconstructed)
    }
}
