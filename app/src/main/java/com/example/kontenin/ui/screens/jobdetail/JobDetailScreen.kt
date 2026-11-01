package com.example.kontenin.ui.screens.jobdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.presentation.jobdetail.JobDetailViewModel
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Layar Detail Job (FE-13).
 *
 * Menampilkan detail job secara lengkap, peta via ExternalActionGateway simulasi,
 * dan aksi Ubah, Duplikat, serta Hapus dengan dialog konfirmasi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    viewModel: JobDetailViewModel,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (jobId: Long) -> Unit,
    onNavigateToDuplicate: (jobId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing
    val dim = KonteninTheme.dimensions

    val uiState by viewModel.uiState.collectAsState()

    // ── Notifikasi & Navigasi Reaktif ──────────────────────────────────────────
    LaunchedEffect(uiState.userMessage) {
        val message = uiState.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onUserMessageConsumed()
        }
    }

    LaunchedEffect(uiState.isJobNotFound) {
        if (uiState.isJobNotFound) {
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onNavigateBack()
        }
    }

    // ── Dialog Konfirmasi Hapus ────────────────────────────────────────────────
    if (uiState.showDeleteDialog) {
        DeleteJobDialog(
            onConfirm = viewModel::onDeleteConfirm,
            onDismissRequest = viewModel::onDeleteDismiss,
            isLoading = uiState.isDeleting,
        )
    }

    // ── Dialog Ubah Pembayaran (FE-14) ─────────────────────────────────────────
    if (uiState.showPaymentDialog && uiState.job != null) {
        PaymentDialog(
            currentFee = uiState.job!!.fee,
            amountText = uiState.paymentDraftText,
            onAmountChange = viewModel::onPaymentDraftChange,
            errorMessageResId = uiState.paymentDraftErrorResId,
            onConfirm = viewModel::onPaymentDialogConfirm,
            onDismissRequest = viewModel::onPaymentDialogDismiss,
            isLoading = uiState.isUpdatingPayment,
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.screen_job_detail),
                        style = KonteninTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    navigationIconContentColor = colors.onSurface,
                    titleContentColor = colors.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.job != null && uiState.jobDisplay != null) {
            val job = uiState.job!!
            val jobDisplay = uiState.jobDisplay!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                // ── Area scroll konten detail ─────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.space4),
                ) {
                    Spacer(modifier = Modifier.height(spacing.space4))

                    // Header Pasangan & Acara/Sumber
                    JobDetailHeader(job = job)

                    Spacer(modifier = Modifier.height(spacing.space4))

                    // Kartu Waktu & Durasi
                    JobDetailTimeCard(job = job, jobDisplay = jobDisplay)

                    Spacer(modifier = Modifier.height(spacing.space4))

                    // Lokasi & Tombol Maps
                    JobDetailLocationSection(
                        job = job,
                        isMapsButtonVisible = uiState.isMapsButtonVisible,
                        onOpenMaps = viewModel::onOpenMaps,
                    )

                    Spacer(modifier = Modifier.height(spacing.space4))

                    // Seksi Pembayaran (FE-14)
                    JobDetailPaymentSection(
                        jobDisplay = jobDisplay,
                        isMarkPaidVisible = uiState.isMarkPaidVisible,
                        onMarkPaid = viewModel::onMarkPaidClick,
                        onEditPayment = viewModel::onEditPaymentClick,
                        isUpdatingPayment = uiState.isUpdatingPayment,
                    )

                    Spacer(modifier = Modifier.height(spacing.space4))

                    // Seksi Catatan
                    JobDetailNotesSection(job = job)

                    Spacer(modifier = Modifier.height(spacing.space5))
                }

                // ── Bottom Action Bar (DESIGN §5.2, §8.4) ─────────────────────────
                HorizontalDivider()
                Surface(
                    color = colors.surface,
                    shadowElevation = dim.fabElevation,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .heightIn(min = dim.bottomActionBarHeight)
                            .padding(horizontal = spacing.space4, vertical = spacing.space2),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Ubah
                        ActionBarButton(
                            icon = Icons.Filled.Edit,
                            label = stringResource(R.string.action_bar_edit),
                            contentDescription = stringResource(R.string.action_bar_edit),
                            iconTint = colors.onSurface,
                            onClick = { onNavigateToEdit(job.id) },
                        )

                        // Duplikat (primary)
                        ActionBarButton(
                            icon = Icons.Filled.ContentCopy,
                            label = stringResource(R.string.action_bar_duplicate),
                            contentDescription = stringResource(R.string.action_bar_duplicate),
                            iconTint = colors.primary,
                            onClick = { onNavigateToDuplicate(job.id) },
                        )

                        // Hapus (error / destructive)
                        ActionBarButton(
                            icon = Icons.Filled.Delete,
                            label = stringResource(R.string.action_bar_delete),
                            contentDescription = stringResource(R.string.action_bar_delete),
                            iconTint = colors.error,
                            onClick = viewModel::onDeleteClick,
                        )
                    }
                }
            }
        }
    }
}

// ── Private helper ─────────────────────────────────────────────────────────────

@Composable
private fun ActionBarButton(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = KonteninTheme.spacing

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(KonteninRadius.md))
            .clickable(
                onClick = onClick,
                role = Role.Button,
                onClickLabel = contentDescription
            )
            .heightIn(min = KonteninTheme.dimensions.minTouchTarget)
            .padding(horizontal = spacing.space2, vertical = spacing.space1)
            .semantics(mergeDescendants = true) {},
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(spacing.space1))
        Text(
            text = label,
            style = KonteninTheme.typography.labelSmall,
            color = iconTint,
        )
    }
}
