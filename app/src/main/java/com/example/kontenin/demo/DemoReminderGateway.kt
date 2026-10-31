package com.example.kontenin.demo

import com.example.kontenin.contract.*
import com.example.kontenin.util.AppClock
import com.example.kontenin.util.SystemClock
import java.time.LocalDate
import java.time.ZoneId

/**
 * Implementasi demo/simulasi ReminderGateway untuk tahap frontend (FE-16).
 *
 * Mengelola state izin simulasi (NotRequested, Granted, Denied),
 * pengujian pengingat tanpa notifikasi OS sungguhan,
 * serta kalkulasi 4 pengingat otomatis (H-7, H-3, H-1, 3 jam)
 * untuk mendeteksi pengingat yang terlewat (FR-4.4, OQ-6).
 */
class DemoReminderGateway(
    private val clock: AppClock = SystemClock(),
    var scenario: DemoScenario = DemoScenario.SUCCESS
) : ReminderGateway {

    // Default status izin dalam sesi demo
    var notificationPermissionStatus: PermissionStatus = PermissionStatus.NotRequested
    var exactAlarmPermissionStatus: PermissionStatus = PermissionStatus.NotRequested

    var simulateTestReminderFailure: Boolean = false
    val cancelledJobs = mutableListOf<Long>()

    // Untuk kompatibilitas jika dipanggil dengan constructor 1 parameter DemoScenario
    constructor(scenario: DemoScenario) : this(SystemClock(), scenario)

    override suspend fun getNotificationPermissionStatus(): PermissionStatus {
        return notificationPermissionStatus
    }

    override suspend fun getExactAlarmPermissionStatus(): PermissionStatus {
        return exactAlarmPermissionStatus
    }

    override suspend fun requestNotificationPermission(): RequestPermissionResult {
        return if (scenario == DemoScenario.SAVE_ERROR) {
            notificationPermissionStatus = PermissionStatus.Denied
            RequestPermissionResult.Denied
        } else {
            notificationPermissionStatus = PermissionStatus.Granted
            RequestPermissionResult.Granted
        }
    }

    override suspend fun requestExactAlarmPermission(): RequestPermissionResult {
        exactAlarmPermissionStatus = PermissionStatus.Granted
        return RequestPermissionResult.Granted
    }

    override suspend fun testReminder(): TestReminderResult {
        if (simulateTestReminderFailure || scenario == DemoScenario.SAVE_ERROR) {
            return TestReminderResult.Failed("Simulasi kegagalan sistem pengingat")
        }
        return when (notificationPermissionStatus) {
            PermissionStatus.Granted -> TestReminderResult.Scheduled
            else -> TestReminderResult.PermissionDenied
        }
    }

    override suspend fun scheduleJobReminders(
        tanggalEpochDay: Long,
        jamMulaiMenit: Int
    ): ReminderScheduleResult {
        val now = clock.nowEpochMillis()
        val zoneId = ZoneId.systemDefault()

        val eventDate = LocalDate.ofEpochDay(tanggalEpochDay)
        val eventTimeMillis = eventDate
            .atTime(jamMulaiMenit / 60, jamMulaiMenit % 60)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        // 4 pengingat otomatis PRD FR-4:
        // 1. H-7 pukul 08.00
        val reminderH7 = eventDate.minusDays(7)
            .atTime(8, 0)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        // 2. H-3 pukul 08.00
        val reminderH3 = eventDate.minusDays(3)
            .atTime(8, 0)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        // 3. H-1 pukul 08.00
        val reminderH1 = eventDate.minusDays(1)
            .atTime(8, 0)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        // 4. 3 jam sebelum acara dimulai
        val reminder3Hours = eventTimeMillis - (3 * 3600 * 1000L)

        val reminderTimes = listOf(reminderH7, reminderH3, reminderH1, reminder3Hours)

        var scheduled = 0
        var skipped = 0

        for (time in reminderTimes) {
            if (time > now) {
                scheduled++
            } else {
                skipped++
            }
        }

        return ReminderScheduleResult(
            scheduledCount = scheduled,
            skippedCount = skipped
        )
    }

    override suspend fun cancelJobReminders(jobId: Long) {
        cancelledJobs.add(jobId)
    }

    // Helper untuk mengubah state izin secara eksplisit dalam testing / demo
    fun setNotificationPermission(status: PermissionStatus) {
        notificationPermissionStatus = status
    }

    fun setExactAlarmPermission(status: PermissionStatus) {
        exactAlarmPermissionStatus = status
    }
}
