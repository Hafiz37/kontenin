package com.example.kontenin.model

data class JobDraft(
    val namaPengantin1: String = "",
    val namaPengantin2: String = "",
    val tanggal: Long? = null,
    val jamMulaiMenit: Int? = null,
    val durasiMenit: Int? = null,
    val acara: String = "",
    val alamat: String = "",
    val shareloc: String = "",
    val sumberJob: String = "",
    val fee: Long? = null,
    val dibayar: Long? = 0L,
    val catatan: String = ""
)
