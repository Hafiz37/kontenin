package com.example.kontenin.presentation

import com.example.kontenin.demo.DemoBackupFixtures
import com.example.kontenin.demo.DemoBackupGateway
import com.example.kontenin.demo.DemoJobGateway
import com.example.kontenin.demo.DemoScenario
import com.example.kontenin.presentation.settings.BackupController
import com.example.kontenin.util.FixedClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

/**
 * Unit Test untuk FE-17: UI Ekspor dan Impor Simulasi.
 *
 * Menguji seluruh testing steps FE-17:
 * 1. Pilih fixture 3 job valid; konfirmasi menunjukkan 3. Batal menjaga seluruh data lama.
 * 2. Setujui Ganti Data; koleksi demo menjadi tepat 3, Beranda/Rekap mengikuti.
 * 3. Fixture rusak / schema baru / satu job invalid ditolak; tidak ada konfirmasi penggantian atau perubahan data.
 * 4. Simulasikan gagal proses; koleksi lama utuh. Ketuk berulang saat busy; tidak ada proses kedua.
 * 5. Ekspor gagal/dibatalkan menawarkan coba lagi; sukses diberi label simulasi dan tidak mengklaim file tersimpan di perangkat.
 */
class BackupControllerTest {

    private val fixedInstant = Instant.parse("2026-11-01T08:00:00Z")
    private val clock = FixedClock(fixedInstant)
    private val testScope = CoroutineScope(Dispatchers.Unconfined)

    private lateinit var jobGateway: DemoJobGateway
    private lateinit var backupGateway: DemoBackupGateway
    private lateinit var backupController: BackupController

    @Before
    fun setUp() {
        jobGateway = DemoJobGateway(clock, DemoScenario.SUCCESS)
        backupGateway = DemoBackupGateway(jobGateway, DemoScenario.SUCCESS, clock)
        backupController = BackupController(
            backupGateway = backupGateway,
            clock = clock,
            coroutineScope = testScope,
            dispatcher = Dispatchers.Unconfined,
            progressDelayMs = 0L // Untuk pengujian instan
        )
    }

    // ── Testing Step 1: Valid 3 Jobs & Batal ────────────────────────────────

    @Test
    fun `step 1 - pilih fixture 3 job valid memunculkan konfirmasi 3 job dan batal menjaga data lama`() {
        val initialJobCount = jobGateway.getCurrentJobs().size
        assertTrue("Seed awal harus memiliki data", initialJobCount > 0)

        // Buka pemilih fixture
        backupController.onImportClick()
        assertTrue(backupController.uiState.value.showImportChooserDialog)

        // Pilih fixture 3 job valid
        backupController.onSelectImportFixture(DemoBackupFixtures.FIXTURE_VALID_3_JOBS)

        val state = backupController.uiState.value
        assertFalse("Dialog pemilih harus tertutup", state.showImportChooserDialog)
        assertTrue("Dialog konfirmasi harus terbuka", state.showImportConfirmDialog)
        assertEquals("Jumlah job pada konfirmasi harus 3", 3, state.pendingImportJobCount)
        assertEquals(DemoBackupFixtures.FIXTURE_VALID_3_JOBS, state.pendingImportFixtureId)

        // Batalkan konfirmasi penggantian data
        backupController.dismissImportConfirmDialog()

        val stateAfterCancel = backupController.uiState.value
        assertFalse("Dialog konfirmasi harus tertutup", stateAfterCancel.showImportConfirmDialog)
        assertNull(stateAfterCancel.pendingImportFixtureId)

        // Verifikasi koleksi demo lama tetap utuh
        assertEquals("Data demo lama tidak boleh berubah saat batal", initialJobCount, jobGateway.getCurrentJobs().size)
    }

    // ── Testing Step 2: Setujui Ganti Data (Atomic Replacement) ──────────────

    @Test
    fun `step 2 - setujui Ganti Data mengganti koleksi demo menjadi tepat 3 job`() = runBlocking {
        val initialJobCount = jobGateway.getCurrentJobs().size
        assertNotEquals(3, initialJobCount)

        // Pilih fixture
        backupController.onSelectImportFixture(DemoBackupFixtures.FIXTURE_VALID_3_JOBS)

        // Setujui Ganti Data
        backupController.confirmImport()

        // Verifikasi koleksi demo menjadi tepat 3 job
        val updatedJobs = jobGateway.getCurrentJobs()
        assertEquals(3, updatedJobs.size)

        // Verifikasi Flow observeAllJobs juga menghasilkan 3 job
        val flowJobs = jobGateway.observeAllJobs().first()
        assertEquals(3, flowJobs.size)

        // Verifikasi snackbar sukses
        val state = backupController.uiState.value
        assertNotNull(state.snackbarEvent)
        assertTrue(state.snackbarEvent!!.message.contains("Impor berhasil: 3 job"))
    }

    // ── Testing Step 3: Fixture Rusak, Skema Baru & Satu Job Invalid Ditolak ──

    @Test
    fun `step 3 - fixture rusak ditolak sebelum konfirmasi dan data lama utuh`() {
        val initialJobs = jobGateway.getCurrentJobs()

        backupController.onSelectImportFixture(DemoBackupFixtures.FIXTURE_CORRUPTED)

        val state = backupController.uiState.value
        assertFalse("Tidak boleh ada dialog konfirmasi", state.showImportConfirmDialog)
        assertNotNull("Harus ada pesan snackbar error", state.snackbarEvent)
        assertEquals("File cadangan tidak valid", state.snackbarEvent!!.message)

        // Data tidak berubah
        assertEquals(initialJobs, jobGateway.getCurrentJobs())
    }

    @Test
    fun `step 3 - fixture schema lebih baru ditolak sebelum konfirmasi dan data lama utuh`() {
        val initialJobs = jobGateway.getCurrentJobs()

        backupController.onSelectImportFixture(DemoBackupFixtures.FIXTURE_NEWER_SCHEMA)

        val state = backupController.uiState.value
        assertFalse("Tidak boleh ada dialog konfirmasi", state.showImportConfirmDialog)
        assertNotNull("Harus ada pesan snackbar error", state.snackbarEvent)
        assertEquals("File cadangan tidak valid", state.snackbarEvent!!.message)

        assertEquals(initialJobs, jobGateway.getCurrentJobs())
    }

    @Test
    fun `step 3 - fixture dengan satu job invalid ditolak sebelum konfirmasi dan data lama utuh`() {
        val initialJobs = jobGateway.getCurrentJobs()

        backupController.onSelectImportFixture(DemoBackupFixtures.FIXTURE_INVALID_JOB)

        val state = backupController.uiState.value
        assertFalse("Tidak boleh ada dialog konfirmasi", state.showImportConfirmDialog)
        assertNotNull("Harus ada pesan snackbar error", state.snackbarEvent)
        assertEquals("File cadangan tidak valid", state.snackbarEvent!!.message)

        assertEquals(initialJobs, jobGateway.getCurrentJobs())
    }

    // ── Testing Step 4: Gagal Proses & Proteksi Concurrency (Busy Guard) ──────

    @Test
    fun `step 4 - simulasi gagal proses menjaga koleksi lama utuh (rollback)`() {
        val initialJobs = jobGateway.getCurrentJobs()

        // Fixture gagal proses lolos validasi, namun gagal saat confirmImport
        backupController.onSelectImportFixture(DemoBackupFixtures.FIXTURE_PROCESS_ERROR)

        assertTrue("Validasi harus lolos untuk skenario gagal proses", backupController.uiState.value.showImportConfirmDialog)

        // Konfirmasi impor
        backupController.confirmImport()

        val state = backupController.uiState.value
        assertNotNull(state.snackbarEvent)
        assertTrue(state.snackbarEvent!!.message.contains("Gagal mengimpor data"))

        // Data lama harus tetap utuh (rollback tanpa perubahan parsial)
        assertEquals(initialJobs, jobGateway.getCurrentJobs())
    }

    @Test
    fun `step 4 - ketuk berulang saat busy tidak memicu proses kedua`() = runBlocking {
        // Buat controller dengan dispatcher default terpisah untuk mensimulasikan state isBusy
        var isBusyObserved = false
        val slowGateway = DemoBackupGateway(jobGateway, DemoScenario.SUCCESS, clock)
        slowGateway.simulatedDelayMs = 150L

        val slowController = BackupController(
            backupGateway = slowGateway,
            clock = clock,
            coroutineScope = CoroutineScope(Dispatchers.Default),
            dispatcher = Dispatchers.Default,
            progressDelayMs = 50L
        )

        slowController.onSelectImportFixture(DemoBackupFixtures.FIXTURE_VALID_3_JOBS)
        kotlinx.coroutines.delay(20)

        if (slowController.uiState.value.isBusy) {
            isBusyObserved = true
            // Coba ketuk lagi saat busy
            slowController.onExportClick()
            assertFalse("Aksi kedua harus diabaikan saat busy", slowController.uiState.value.showExportDialog)
        }

        kotlinx.coroutines.delay(200)
        assertFalse(slowController.uiState.value.isBusy)
        assertTrue("Busy state harus sempat aktif", isBusyObserved)
    }

    // ── Testing Step 5: Alur Ekspor Sukses, Gagal & Dibatalkan ───────────────

    @Test
    fun `step 5 - ekspor dibatalkan memunculkan snackbar gagal dengan opsi coba lagi (EC-18)`() {
        backupController.onExportClick()
        assertTrue(backupController.uiState.value.showExportDialog)

        backupController.onCancelExport()
        assertFalse(backupController.uiState.value.showExportDialog)

        val state = backupController.uiState.value
        assertNotNull(state.snackbarEvent)
        assertEquals("Gagal mengekspor data", state.snackbarEvent!!.message)
        assertEquals("Coba lagi", state.snackbarEvent!!.actionLabel)

        // Ketuk coba lagi
        state.snackbarEvent!!.onAction?.invoke()
        assertTrue("Coba lagi harus membuka kembali dialog ekspor", backupController.uiState.value.showExportDialog)
    }

    @Test
    fun `step 5 - ekspor gagal memunculkan snackbar gagal dengan opsi coba lagi (EC-18)`() {
        backupController.executeExport(simulateFail = true)

        val state = backupController.uiState.value
        assertNotNull(state.snackbarEvent)
        assertEquals("Gagal mengekspor data", state.snackbarEvent!!.message)
        assertEquals("Coba lagi", state.snackbarEvent!!.actionLabel)
    }

    @Test
    fun `step 5 - ekspor sukses diberi label simulasi dan nama saran YYYYMMDD`() {
        backupController.executeExport()

        val state = backupController.uiState.value
        assertNotNull(state.snackbarEvent)
        val msg = state.snackbarEvent!!.message

        // Cek nama saran kontenin-backup-YYYYMMDD.json (20261101 dari clock tetap)
        assertTrue("Pesan harus berisi nama saran kontenin-backup-20261101.json", msg.contains("kontenin-backup-20261101.json"))
        // Cek penanda simulasi demo
        assertTrue("Pesan harus menandai hasil sebagai simulasi demo", msg.contains("simulasi demo"))
        // Cek jumlah job
        val currentJobCount = jobGateway.getCurrentJobs().size
        assertTrue("Pesan harus memuat jumlah job", msg.contains("$currentJobCount job"))
    }
}
