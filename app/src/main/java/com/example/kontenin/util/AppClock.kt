package com.example.kontenin.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

interface AppClock {
    fun now(): Instant
    fun nowLocalDateTime(): LocalDateTime
    fun nowLocalDate(): LocalDate
    fun nowEpochMillis(): Long
}

class SystemClock : AppClock {
    private val wibZone = ZoneId.of("Asia/Jakarta")
    
    override fun now(): Instant = Instant.now()
    
    override fun nowLocalDateTime(): LocalDateTime = 
        LocalDateTime.now(wibZone)
    
    override fun nowLocalDate(): LocalDate = 
        LocalDate.now(wibZone)
    
    override fun nowEpochMillis(): Long = 
        System.currentTimeMillis()
}

class FixedClock(private val fixedInstant: Instant) : AppClock {
    private val wibZone = ZoneId.of("Asia/Jakarta")
    
    override fun now(): Instant = fixedInstant
    
    override fun nowLocalDateTime(): LocalDateTime = 
        LocalDateTime.ofInstant(fixedInstant, wibZone)
    
    override fun nowLocalDate(): LocalDate = 
        LocalDate.ofInstant(fixedInstant, wibZone)
    
    override fun nowEpochMillis(): Long = 
        fixedInstant.toEpochMilli()
}
