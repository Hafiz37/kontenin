package com.example.kontenin.ui.screens.jobdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kontenin.R
import com.example.kontenin.ui.components.MoneyField
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme
import com.example.kontenin.util.KonteninRupiahFormatter

/**
 * Dialog satu kolom untuk mengubah total yang sudah dibayar (FE-14 / DESIGN §6.3, FR-3.3).
 *
 * Menggantikan total dibayar sebelumnya secara langsung, bukan menambah transaksi baru.
 */
@Composable
fun PaymentDialog(
    currentFee: Long,
    amountText: String,
    onAmountChange: (String) -> Unit,
    errorMessageResId: Int?,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing
    val typography = KonteninTheme.typography

    Dialog(
        onDismissRequest = {
            if (!isLoading) onDismissRequest()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = modifier
                .padding(horizontal = KonteninTheme.dimensions.screenMargin)
                .widthIn(max = 420.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(KonteninRadius.xl),
            color = colors.surface,
            tonalElevation = KonteninTheme.dimensions.dialogElevation,
            shadowElevation = KonteninTheme.dimensions.dialogElevation,
        ) {
            Column(
                modifier = Modifier
                    .padding(KonteninTheme.dimensions.dialogPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.space3),
            ) {
                // Judul Dialog
                Text(
                    text = stringResource(R.string.action_edit_payment),
                    style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurface,
                    modifier = Modifier.semantics { heading() },
                )

                // Keterangan Total Fee
                Text(
                    text = stringResource(R.string.payment_dialog_total_fee, KonteninRupiahFormatter.formatRupiah(currentFee)),
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(spacing.space1))

                // Kolom Input Satu Kolom: Sudah Dibayar
                val errorString = errorMessageResId?.let { stringResource(it) }
                MoneyField(
                    valueText = amountText,
                    onValueChange = onAmountChange,
                    label = stringResource(R.string.field_paid),
                    placeholder = "0",
                    errorMessage = errorString,
                    isError = errorString != null,
                    enabled = !isLoading,
                )

                Spacer(modifier = Modifier.height(spacing.space2))

                // Tombol Aksi Batal dan Simpan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(KonteninRadius.md),
                    ) {
                        Text(text = stringResource(R.string.action_cancel))
                    }

                    Spacer(modifier = Modifier.width(spacing.space2))

                    Button(
                        onClick = onConfirm,
                        enabled = !isLoading && errorMessageResId == null,
                        shape = RoundedCornerShape(KonteninRadius.md),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = colors.onPrimary,
                                strokeWidth = 2.dp,
                            )
                            Spacer(modifier = Modifier.width(spacing.space2))
                        }
                        Text(text = stringResource(R.string.action_save))
                    }
                }
            }
        }
    }
}
