package com.example.kontenin.model

data class JobDraft(
    val namaPengantin1: String,
    val namaPengantin2: String,
    val tanggal: Long?,
    val jamMulaiMenit: Int,
    val durasiMenit: Int,
    val acara: String,
    val alamat: String,
    val shareloc: String,
    val sumberJob: String,
    val fee: Long?,
    val dibayar: Long?,
    val catatan: String
)
