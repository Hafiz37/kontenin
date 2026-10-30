package com.example.kontenin.contract

import com.example.kontenin.model.Job
import com.example.kontenin.model.JobDraft
import kotlinx.coroutines.flow.Flow

sealed interface JobResult {
    data class Success(val job: Job) : JobResult
    data class Error(val message: String) : JobResult
}

sealed interface DeleteResult {
    data object Success : DeleteResult
    data class Error(val message: String) : DeleteResult
}

sealed interface PaymentUpdateResult {
    data class Success(val job: Job) : PaymentUpdateResult
    data class Error(val message: String) : PaymentUpdateResult
}

sealed interface ConflictCheckResult {
    data object NoConflict : ConflictCheckResult
    data class HasConflict(val conflictingJobs: List<Job>) : ConflictCheckResult
}

interface JobGateway {
    fun observeAllJobs(): Flow<List<Job>>
    fun observeJob(id: Long): Flow<Job?>
    suspend fun saveJob(draft: JobDraft): JobResult
    suspend fun updateJob(id: Long, draft: JobDraft): JobResult
    suspend fun deleteJob(id: Long): DeleteResult
    suspend fun updatePayment(id: Long, dibayar: Long): PaymentUpdateResult
    suspend fun checkConflict(tanggal: Long, jamMulaiMenit: Int, durasiMenit: Int, excludeId: Long?): ConflictCheckResult
}
