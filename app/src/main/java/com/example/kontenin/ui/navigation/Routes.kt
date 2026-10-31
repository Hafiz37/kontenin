package com.example.kontenin.ui.navigation

/**
 * Definisi seluruh route navigasi Kontenin (FE-05).
 *
 * Tiga destinasi utama (Beranda, Rekap, Pengaturan) menggunakan string route sederhana.
 * Route turunan (JobDetail, JobForm) menggunakan argument yang disisipkan langsung
 * ke dalam string route — jobId sebagai Long, mode sebagai String.
 *
 * Mode yang valid untuk JobForm: "add", "edit", "duplicate".
 */
sealed class Routes(val route: String) {

    // ── Destinasi utama (Bottom Navigation) ──────────────────────────────────

    /** Layar Beranda — daftar job mendatang dan riwayat. */
    data object Home : Routes("home")

    /** Layar Rekap — ringkasan pendapatan dan piutang. */
    data object Recap : Routes("recap")

    /** Layar Pengaturan — izin, pengingat, data, tentang. */
    data object Settings : Routes("settings")

    // ── Destinasi turunan ────────────────────────────────────────────────────

    /**
     * Layar Detail Job.
     *
     * @param jobId ID job yang akan ditampilkan.
     * Rute penuh: `"job_detail/{jobId}"`.
     */
    data object JobDetail : Routes("job_detail/{$ARG_JOB_ID}") {
        fun createRoute(jobId: Long) = "job_detail/$jobId"
    }

    /**
     * Layar Form Job — tambah, ubah, atau duplikat.
     *
     * @param mode  Salah satu dari [MODE_ADD], [MODE_EDIT], [MODE_DUPLICATE].
     * @param jobId ID job yang akan diubah/diduplikat; -1 untuk mode tambah.
     * Rute penuh: `"job_form/{mode}/{jobId}"`.
     */
    data object JobForm : Routes("job_form/{$ARG_MODE}/{$ARG_JOB_ID}") {
        fun createRoute(mode: String, jobId: Long = DEFAULT_JOB_ID) =
            "job_form/$mode/$jobId"
    }

    companion object {
        // ── Argument keys ─────────────────────────────────────────────────────
        const val ARG_JOB_ID = "jobId"
        const val ARG_MODE   = "mode"

        // ── Nilai default ─────────────────────────────────────────────────────
        /** Nilai jobId yang menandakan "tidak ada job terpilih" (mode tambah). */
        const val DEFAULT_JOB_ID = -1L

        // ── Konstanta mode Form ───────────────────────────────────────────────
        const val MODE_ADD       = "add"
        const val MODE_EDIT      = "edit"
        const val MODE_DUPLICATE = "duplicate"

        /** Kumpulan route destinasi utama (bottom navigation). */
        val topLevelRoutes = listOf(Home.route, Recap.route, Settings.route)

        /**
         * Mengembalikan true jika [currentRoute] merupakan layar yang
         * menyembunyikan bottom navigation (Form dan Detail).
         */
        fun shouldHideBottomBar(currentRoute: String?): Boolean {
            if (currentRoute == null) return false
            // Cocokkan awalan rute (sebelum /{arg})
            return currentRoute.startsWith("job_detail") ||
                    currentRoute.startsWith("job_form")
        }
    }
}
