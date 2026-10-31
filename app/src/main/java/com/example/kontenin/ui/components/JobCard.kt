package com.example.kontenin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kontenin.model.Job
import com.example.kontenin.presentation.toDisplay
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * JobCard untuk Kontenin - FE-06.
 * 
 * Kartu job standar untuk Beranda sesuai DESIGN §6.1 dan FR-1.4:
 * - Tanggal/jam sebagai informasi dominan (20 sp, tabular)
 * - Nama pasangan (16 sp SemiBold, maksimal 2 baris + elipsis)
 * - Acara · Sumber (14 sp sekunder)
 * - Fee (16 sp SemiBold, tabular)
 * - Chip pembayaran (kuning warning)
 * 
 * Seluruh kartu dapat diketuk (≥ 48 dp) untuk membuka Detail.
 * Permukaan surface, garis outline-variant, radius-md, padding 16.
 */
@Composable
fun JobCard(
    job: Job,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val display = job.toDisplay()
    val customTypography = KonteninTheme.customTypography
    val dimensions = KonteninTheme.dimensions
    val cardShape = RoundedCornerShape(KonteninRadius.md)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = cardShape
            )
            .clickable(
                onClick = onClick,
                role = Role.Button,
                onClickLabel = "Buka detail ${display.namaPasangan}"
            )
            .padding(dimensions.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacing.space2)
    ) {
        // Tanggal dan rentang jam - informasi dominan
        Text(
            text = display.dateAndTimeFormatted,
            style = customTypography.dateTime,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Nama pasangan - maksimal 2 baris
        Text(
            text = display.namaPasangan,
            style = customTypography.cardTitle,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        // Acara · Sumber job (info sekunder)
        Text(
            text = "${job.acara} · ${job.sumberJob}",
            style = customTypography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Row untuk fee dan chip pembayaran
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fee dengan format rupiah penuh
            Text(
                text = display.feeFormatted,
                style = customTypography.nominal,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Chip pembayaran (hilang jika lunas atau fee 0)
            if (display.paymentChipText != null) {
                PaymentChip(
                    text = display.paymentChipText
                )
            }
        }
    }
}

/**
 * JobCardCompact untuk Rekap - FE-06.
 * 
 * Varian padat untuk daftar job di halaman Rekap:
 * - Layout lebih ringkas tanpa mengorbankan keterbacaan
 * - Tanggal dan jam tetap dominan
 * - Tanpa sumber job (sudah terkelompok per sumber)
 * - Padding lebih kecil untuk efisiensi ruang
 */
@Composable
fun JobCardCompact(
    job: Job,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showSource: Boolean = false
) {
    val display = job.toDisplay()
    val customTypography = KonteninTheme.customTypography
    val cardShape = RoundedCornerShape(KonteninRadius.md)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = cardShape
            )
            .clickable(
                onClick = onClick,
                role = Role.Button,
                onClickLabel = "Buka detail ${display.namaPasangan}"
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Tanggal dan jam
        Text(
            text = display.dateAndTimeFormatted,
            style = customTypography.dateTime.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Nama pasangan dan acara
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = display.namaPasangan,
                    style = customTypography.cardTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val secondaryText = if (showSource) {
                    "${job.acara} · ${job.sumberJob}"
                } else {
                    job.acara
                }
                
                Text(
                    text = secondaryText,
                    style = customTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Fee dan chip di sisi kanan
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = display.feeFormatted,
                    style = customTypography.nominal,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (display.paymentChipText != null) {
                    PaymentChip(text = display.paymentChipText)
                }
            }
        }
    }
}
