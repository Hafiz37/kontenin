package com.example.kontenin.demo

import com.example.kontenin.contract.*
import com.example.kontenin.model.Job
import com.example.kontenin.model.JobDraft
import com.example.kontenin.util.AppClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class DemoJobGateway(
    private val clock: AppClock,
    initialScenario: DemoScenario = DemoScenario.SUCCESS
) : JobGateway {
    
    private val jobs = MutableStateFlow<Map<Long, Job>>(emptyMap())
    private var scenario = initialScenario
    private var pendingOperationReleased = false
    private var nextId = 1L
    
    init {
        resetToSeed()
    }
    
    fun resetToSeed() {
        val seed = when (scenario) {
            DemoScenario.EMPTY_DATA -> DemoFixtures.getEmptySeed()
            DemoScenario.LARGE_DATASET -> DemoFixtures.getLargeDatasetSeed()
            else -> DemoFixtures.getDefaultSeed()
        }
        jobs.value = seed.associateBy { it.id }
        nextId = (seed.maxOfOrNull { it.id } ?: 0L) + 1
        pendingOperationReleased = false
    }
    
    fun setScenario(newScenario: DemoScenario) {
        scenario = newScenario
        resetToSeed()
    }
    
    fun releasePendingOperation() {
        pendingOperationReleased = true
    }
    
    override fun observeAllJobs(): Flow<List<Job>> =
        jobs.map { it.values.sortedByDescending { job -> job.tanggal } }
    
    override fun observeJob(id: Long): Flow<Job?> =
        jobs.map { it[id] }
    
    override suspend fun saveJob(draft: JobDraft): JobResult {
        if (scenario == DemoScenario.SAVE_ERROR) {
            return JobResult.Error("Simulasi gagal simpan")
        }
        
        if (scenario == DemoScenario.PENDING) {
            while (!pendingOperationReleased) {
                delay(100)
            }
        }
        
        if (draft.tanggal == null || draft.fee == null) {
            return JobResult.Error("Tanggal dan fee harus diisi")
        }
        
        if (draft.jamMulaiMenit == null || draft.durasiMenit == null) {
            return JobResult.Error("Jam mulai dan durasi harus diisi")
        }
        
        val now = clock.nowEpochMillis()
        val newJob = Job(
            id = nextId++,
            namaPengantin1 = draft.namaPengantin1,
            namaPengantin2 = draft.namaPengantin2,
            tanggal = draft.tanggal,
            jamMulaiMenit = draft.jamMulaiMenit,
            durasiMenit = draft.durasiMenit,
            acara = draft.acara,
            alamat = draft.alamat,
            shareloc = draft.shareloc,
            sumberJob = draft.sumberJob,
            fee = draft.fee,
            dibayar = draft.dibayar ?: 0L,
            catatan = draft.catatan,
            createdAt = now,
            updatedAt = now
        )
        
        jobs.value = jobs.value + (newJob.id to newJob)
        return JobResult.Success(newJob)
    }
    
    override suspend fun updateJob(id: Long, draft: JobDraft): JobResult {
        if (scenario == DemoScenario.SAVE_ERROR) {
            return JobResult.Error("Simulasi gagal ubah")
        }
        
        if (scenario == DemoScenario.PENDING) {
            while (!pendingOperationReleased) {
                delay(100)
            }
        }
        
        if (scenario == DemoScenario.JOB_NOT_FOUND || !jobs.value.containsKey(id)) {
            return JobResult.Error("Job tidak ditemukan")
        }
        
        if (draft.tanggal == null || draft.fee == null) {
            return JobResult.Error("Tanggal dan fee harus diisi")
        }
        
        if (draft.jamMulaiMenit == null || draft.durasiMenit == null) {
            return JobResult.Error("Jam mulai dan durasi harus diisi")
        }
        
        val existing = jobs.value[id]!!
        val updated = Job(
            id = id,
            namaPengantin1 = draft.namaPengantin1,
            namaPengantin2 = draft.namaPengantin2,
            tanggal = draft.tanggal,
            jamMulaiMenit = draft.jamMulaiMenit,
            durasiMenit = draft.durasiMenit,
            acara = draft.acara,
            alamat = draft.alamat,
            shareloc = draft.shareloc,
            sumberJob = draft.sumberJob,
            fee = draft.fee,
            dibayar = draft.dibayar ?: existing.dibayar,
            catatan = draft.catatan,
            createdAt = existing.createdAt,
            updatedAt = clock.nowEpochMillis()
        )
        
        jobs.value = jobs.value + (id to updated)
        return JobResult.Success(updated)
    }
    
    override suspend fun deleteJob(id: Long): DeleteResult {
        if (scenario == DemoScenario.DELETE_ERROR) {
            return DeleteResult.Error("Simulasi gagal hapus")
        }
        
        if (scenario == DemoScenario.PENDING) {
            while (!pendingOperationReleased) {
                delay(100)
            }
        }
        
        if (scenario == DemoScenario.JOB_NOT_FOUND || !jobs.value.containsKey(id)) {
            return DeleteResult.Error("Job tidak ditemukan")
        }
        
        jobs.value = jobs.value - id
        return DeleteResult.Success
    }
    
    override suspend fun updatePayment(id: Long, dibayar: Long): PaymentUpdateResult {
        if (scenario == DemoScenario.SAVE_ERROR) {
            return PaymentUpdateResult.Error("Simulasi gagal update pembayaran")
        }
        
        val existing = jobs.value[id] 
            ?: return PaymentUpdateResult.Error("Job tidak ditemukan")
        
        val updated = existing.copy(
            dibayar = dibayar,
            updatedAt = clock.nowEpochMillis()
        )
        
        jobs.value = jobs.value + (id to updated)
        return PaymentUpdateResult.Success(updated)
    }
    
    override suspend fun checkConflict(
        tanggal: Long,
        jamMulaiMenit: Int,
        durasiMenit: Int,
        excludeId: Long?
    ): ConflictCheckResult {
        val endTimeMinutes = jamMulaiMenit + durasiMenit
        
        val conflicts = jobs.value.values.filter { job ->
            job.id != excludeId &&
            job.tanggal == tanggal &&
            !(job.jamMulaiMenit + job.durasiMenit <= jamMulaiMenit ||
              job.jamMulaiMenit >= endTimeMinutes)
        }
        
        return if (conflicts.isEmpty()) {
            ConflictCheckResult.NoConflict
        } else {
            ConflictCheckResult.HasConflict(conflicts)
        }
    }
}
