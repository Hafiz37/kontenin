package com.example.kontenin.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.model.Job
import com.example.kontenin.ui.components.ConfirmationDialog
import com.example.kontenin.ui.components.EmptyState
import com.example.kontenin.ui.components.JobCard
import com.example.kontenin.ui.components.JobCardCompact
import com.example.kontenin.ui.components.KonteninDestructiveButton
import com.example.kontenin.ui.components.KonteninLoadingIndicator
import com.example.kontenin.ui.components.KonteninOutlinedButton
import com.example.kontenin.ui.components.KonteninPrimaryButton
import com.example.kontenin.ui.components.KonteninTextField
import com.example.kontenin.ui.components.KonteninTextButton
import com.example.kontenin.ui.components.KonteninTonalButton
import com.example.kontenin.ui.components.MoneyField
import com.example.kontenin.ui.components.PaymentChip
import com.example.kontenin.ui.components.PermissionBanner
import com.example.kontenin.ui.components.SectionHeading
import com.example.kontenin.ui.components.SkeletonJobList
import com.example.kontenin.ui.theme.KonteninTheme
import java.time.LocalDate

/**
 * Layar Galeri Preview Komponen Bersama - FE-04.
 * Menampilkan seluruh varian tombol, kolom input teks/uang, chip, dialog, empty state, dan skeleton.
 * Mendukung pengujian nama panjang, nominal besar, dark mode, dan skala font 200%.
 */
@Composable
fun ComponentGalleryScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val colors = KonteninTheme.colorScheme
    val dimensions = KonteninTheme.dimensions
    val spacing = KonteninTheme.spacing
    val customTypography = KonteninTheme.customTypography

    // State interaktif lokal untuk demonstrasi galeri
    var sampleText by remember { mutableStateOf("Andi & Sari") }
    var sampleLongAddress by remember {
        mutableStateOf("Gedung Graha Saba Pramana Lantai 2, Jl. Prof. DR. Drs Notonagoro No. 1, Karang Malang, Caturtunggal, Depok, Sleman, DI Yogyakarta")
    }
    var sampleErrorText by remember { mutableStateOf("") }
    var sampleNominal by remember { mutableStateOf<Long?>(3500000L) }
    var sampleLargeNominal by remember { mutableStateOf<Long?>(150000000L) }
    var sampleExcessiveNominal by remember { mutableStateOf<Long?>(4000000L) }

    var showNormalDialog by remember { mutableStateOf(false) }
    var showDestructiveDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = dimensions.maxContentWidth)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = dimensions.screenMargin, vertical = spacing.space4),
                verticalArrangement = Arrangement.spacedBy(spacing.space5)
            ) {
                // Header Galeri
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                    Text(
                        text = stringResource(R.string.gallery_title),
                        style = customTypography.screenTitle,
                        color = colors.onSurface
                    )
                    Text(
                        text = stringResource(R.string.gallery_subtitle),
                        style = customTypography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 1. BAGIAN TOMBOL & AKSI
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    SectionHeading(title = stringResource(R.string.gallery_section_buttons))

                    // Primary Button (Normal, Loading, Disabled)
                    Text("Tombol Utama (Primary, tinggi 56 dp):", style = customTypography.caption, color = colors.onSurfaceVariant)
                    KonteninPrimaryButton(
                        text = "Simpan Job (Normal)",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        KonteninPrimaryButton(
                            text = "Memuat…",
                            onClick = {},
                            loading = true,
                            modifier = Modifier.weight(1f)
                        )
                        KonteninPrimaryButton(
                            text = "Nonaktif",
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Tonal Button (Normal, Loading, Disabled)
                    Spacer(modifier = Modifier.height(spacing.space1))
                    Text("Tombol Tonal (min 48 dp):", style = customTypography.caption, color = colors.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        KonteninTonalButton(
                            text = stringResource(R.string.action_mark_paid),
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                        KonteninTonalButton(
                            text = stringResource(R.string.action_open_maps),
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        KonteninTonalButton(
                            text = "Memuat…",
                            onClick = {},
                            loading = true,
                            modifier = Modifier.weight(1f)
                        )
                        KonteninTonalButton(
                            text = "Nonaktif",
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Outline Button
                    Spacer(modifier = Modifier.height(spacing.space1))
                    Text("Tombol Outline (min 48 dp):", style = customTypography.caption, color = colors.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        KonteninOutlinedButton(
                            text = stringResource(R.string.action_duplicate),
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                        KonteninOutlinedButton(
                            text = stringResource(R.string.action_back),
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    }
                    KonteninOutlinedButton(
                        text = "Outline Nonaktif",
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Destructive Button (Filled untuk dialog, Outline untuk halaman)
                    Spacer(modifier = Modifier.height(spacing.space1))
                    Text("Tombol Destruktif (Isi di dialog, Outline di halaman):", style = customTypography.caption, color = colors.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        KonteninDestructiveButton(
                            text = "Hapus (Isi)",
                            onClick = {},
                            outlined = false,
                            modifier = Modifier.weight(1f)
                        )
                        KonteninDestructiveButton(
                            text = "Hapus (Outline)",
                            onClick = {},
                            outlined = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Text Button
                    Spacer(modifier = Modifier.height(spacing.space1))
                    Text("Tombol Teks:", style = customTypography.caption, color = colors.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        KonteninTextButton(
                            text = stringResource(R.string.action_cancel),
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        )
                        KonteninTextButton(
                            text = "Batal (Nonaktif)",
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Tombol Teks Panjang (Menguji adaptasi font 200%)
                    Spacer(modifier = Modifier.height(spacing.space1))
                    Text("Tombol Teks Panjang (Label Adaptif):", style = customTypography.caption, color = colors.onSurfaceVariant)
                    KonteninPrimaryButton(
                        text = "Simpan Perubahan Jadwal dan Notifikasi Pernikahan",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 2. BAGIAN KOLOM TEKS & UANG
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    SectionHeading(title = stringResource(R.string.gallery_section_fields))

                    // Text Field Normal
                    KonteninTextField(
                        value = sampleText,
                        onValueChange = { sampleText = it },
                        label = "Nama Pengantin 1",
                        placeholder = "Masukkan nama pengantin"
                    )

                    // Text Field Error (Validasi & TalkBack)
                    KonteninTextField(
                        value = sampleErrorText,
                        onValueChange = { sampleErrorText = it },
                        label = "Nama Pengantin 2",
                        placeholder = "Wajib diisi",
                        errorMessage = if (sampleErrorText.isEmpty()) "Nama pengantin 2 wajib diisi" else null
                    )

                    // Text Field Multiline dengan Counter Karakter (Alamat maks 300)
                    KonteninTextField(
                        value = sampleLongAddress,
                        onValueChange = { sampleLongAddress = it },
                        label = "Alamat Acara",
                        singleLine = false,
                        maxLines = 4,
                        maxLength = 300,
                        showCounterAlways = true,
                        helperText = "Gedung / rumah / detail lokasi"
                    )

                    // Text Field Disabled
                    KonteninTextField(
                        value = "Sumber: Instagram (Terkunci)",
                        onValueChange = {},
                        label = "Sumber Job",
                        enabled = false
                    )

                    // MoneyField Normal
                    MoneyField(
                        value = sampleNominal,
                        onValueChange = { sampleNominal = it },
                        label = "Fee Job",
                        helperText = "Nominal total kesepakatan"
                    )

                    // MoneyField Nominal Besar
                    MoneyField(
                        value = sampleLargeNominal,
                        onValueChange = { sampleLargeNominal = it },
                        label = "Fee Job Proyek Besar (Rp 150 Juta)",
                        helperText = "Menguji angka tabular dan pemisah ribuan"
                    )

                    // MoneyField Error (Dibayar > Fee)
                    MoneyField(
                        value = sampleExcessiveNominal,
                        onValueChange = { sampleExcessiveNominal = it },
                        label = "Sudah Dibayar (Error)",
                        errorMessage = "Tidak boleh lebih dari fee"
                    )
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 3. BAGIAN JOB CARDS (FE-06)
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    SectionHeading(title = "Kartu Job (FE-06)")

                    Text(
                        text = "JobCard Standar untuk Beranda (tanggal/jam dominan, max 2 baris nama):",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    // Sample job: 18 Oktober 2026, 10:00, durasi 4 jam
                    val sampleJob1 = Job(
                        id = 1L,
                        namaPengantin1 = "Andi",
                        namaPengantin2 = "Sari",
                        tanggal = LocalDate.of(2026, 10, 18).toEpochDay(),
                        jamMulaiMenit = 600, // 10:00
                        durasiMenit = 240, // 4 jam
                        acara = "Resepsi",
                        alamat = "Gedung X",
                        shareloc = "https://maps.app.goo.gl/...",
                        sumberJob = "Vendor A",
                        fee = 3_500_000L,
                        dibayar = 2_000_000L,
                        catatan = "",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    JobCard(
                        job = sampleJob1,
                        onClick = {}
                    )

                    // Job lintas tengah malam (OQ-7)
                    Text(
                        text = "Lintas Tengah Malam (OQ-7: 22.00 - 02.00 (19 Okt)):",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    val sampleJob2 = Job(
                        id = 2L,
                        namaPengantin1 = "Budi",
                        namaPengantin2 = "Rina",
                        tanggal = LocalDate.of(2026, 10, 18).toEpochDay(),
                        jamMulaiMenit = 1320, // 22:00
                        durasiMenit = 240, // 4 jam → selesai 02:00 (19 Okt)
                        acara = "Akad & Resepsi",
                        alamat = "Masjid Agung",
                        shareloc = "",
                        sumberJob = "Pribadi",
                        fee = 2_000_000L,
                        dibayar = 2_000_000L, // Lunas
                        catatan = "",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    JobCard(
                        job = sampleJob2,
                        onClick = {}
                    )

                    // Nama sangat panjang
                    Text(
                        text = "Nama Sangat Panjang (Max 2 Baris + Elipsis):",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    val sampleJob3 = Job(
                        id = 3L,
                        namaPengantin1 = "Muhammad Rizki Pratama Putra Mahardika",
                        namaPengantin2 = "Siti Nur Khadijah Azzahra Ramadhani",
                        tanggal = LocalDate.of(2026, 11, 1).toEpochDay(),
                        jamMulaiMenit = 480, // 08:00
                        durasiMenit = 300, // 5 jam
                        acara = "Tedak Sinten & Syukuran",
                        alamat = "",
                        shareloc = "",
                        sumberJob = "Vendor Mutiara Wedding",
                        fee = 5_000_000L,
                        dibayar = 0L, // Belum lunas
                        catatan = "",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    JobCard(
                        job = sampleJob3,
                        onClick = {}
                    )

                    // JobCardCompact untuk Rekap
                    Spacer(modifier = Modifier.height(spacing.space2))
                    Text(
                        text = "JobCardCompact untuk Rekap (Layout Padat):",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    JobCardCompact(
                        job = sampleJob1,
                        onClick = {},
                        showSource = true
                    )

                    JobCardCompact(
                        job = sampleJob3,
                        onClick = {},
                        showSource = false
                    )
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 4. BAGIAN CHIP & HEADING
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    SectionHeading(
                        title = stringResource(R.string.gallery_section_chips),
                        action = {
                            KonteninTextButton(text = "Aksi", onClick = {})
                        }
                    )

                    Text(
                        text = "Chip Pembayaran (Informatif, non-interaktif, tinggi 28 dp, bukan tombol):",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        PaymentChip(text = "Belum lunas")
                        PaymentChip(text = "Kurang Rp 1.500.000")
                    }

                    // Nominal besar pada chip
                    PaymentChip(text = "Kurang Rp 50.000.000 (Nominal Besar)")
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 5. BAGIAN BANNER IZIN
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    SectionHeading(title = stringResource(R.string.gallery_section_banners))

                    PermissionBanner(
                        title = stringResource(R.string.banner_permission_title),
                        description = stringResource(R.string.banner_permission_desc),
                        actionLabel = stringResource(R.string.banner_permission_action),
                        onActionClick = {}
                    )
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 6. BAGIAN DIALOG KONFIRMASI
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    SectionHeading(title = stringResource(R.string.gallery_section_dialogs))

                    Text(
                        text = "Uji Dialog Adaptif (Buka dialog untuk memeriksa tata letak vertikal pada label panjang / font 200%):",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        KonteninOutlinedButton(
                            text = "Buka Dialog Standar",
                            onClick = { showNormalDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                        KonteninDestructiveButton(
                            text = "Buka Dialog Hapus",
                            onClick = { showDestructiveDialog = true },
                            outlined = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (showNormalDialog) {
                        ConfirmationDialog(
                            title = stringResource(R.string.dialog_discard_title),
                            message = stringResource(R.string.dialog_discard_message),
                            confirmLabel = "Buang",
                            dismissLabel = stringResource(R.string.action_cancel),
                            onConfirm = { showNormalDialog = false },
                            onDismissRequest = { showNormalDialog = false }
                        )
                    }

                    if (showDestructiveDialog) {
                        ConfirmationDialog(
                            title = stringResource(R.string.dialog_delete_job_title),
                            message = stringResource(R.string.dialog_delete_job_message),
                            confirmLabel = stringResource(R.string.action_delete),
                            dismissLabel = stringResource(R.string.action_cancel),
                            isDestructive = true,
                            stackButtonsVertically = true,
                            onConfirm = { showDestructiveDialog = false },
                            onDismissRequest = { showDestructiveDialog = false }
                        )
                    }
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 7. BAGIAN EMPTY STATE & LOADING
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space4)) {
                    SectionHeading(title = stringResource(R.string.gallery_section_empty_loading))

                    Text(
                        text = "Empty State dengan Aksi Tonal (DESIGN §7.1):",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    EmptyState(
                        icon = Icons.Default.DateRange,
                        title = stringResource(R.string.empty_upcoming_title),
                        description = stringResource(R.string.empty_upcoming_desc),
                        actionLabel = stringResource(R.string.action_add_job),
                        onActionClick = {}
                    )

                    Spacer(modifier = Modifier.height(spacing.space2))

                    Text(
                        text = "Empty State Tanpa Aksi:",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    EmptyState(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.empty_history_title),
                        description = "Semua riwayat acara yang telah selesai akan diarsipkan di sini."
                    )

                    Spacer(modifier = Modifier.height(spacing.space2))

                    Text(
                        text = "Skeleton Statis (3 Kartu Statis tanpa Kedip - DESIGN §7.2):",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    SkeletonJobList(count = 3)

                    Spacer(modifier = Modifier.height(spacing.space2))

                    Text(
                        text = "Indikator Loading Berputar:",
                        style = customTypography.caption,
                        color = colors.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                    ) {
                        KonteninLoadingIndicator(size = 24)
                        Text(
                            text = stringResource(R.string.dialog_processing),
                            style = customTypography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.space6))
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ComponentGalleryPreviewLight() {
    KonteninTheme(darkTheme = false) {
        ComponentGalleryScreen()
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ComponentGalleryPreviewDark() {
    KonteninTheme(darkTheme = true) {
        ComponentGalleryScreen()
    }
}

@Preview(name = "Font Scale 200%", fontScale = 2.0f, showBackground = true)
@Composable
fun ComponentGalleryPreviewFontScale200() {
    KonteninTheme(darkTheme = false) {
        ComponentGalleryScreen()
    }
}

