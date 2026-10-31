package com.example.kontenin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.kontenin.ui.theme.KonteninTheme
import kotlin.math.min

/**
 * VisualTransformation untuk format ribuan rupiah Indonesia dengan pemisah titik (DESIGN §5.2).
 * Contoh: "1500000" -> "1.500.000"
 */
class RupiahThousandsVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text.filter { it.isDigit() }
        if (raw.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formatted = formatThousandsString(raw)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, raw.length)
                if (raw.isEmpty()) return 0
                val totalDots = (raw.length - 1) / 3
                val remaining = raw.length - clamped
                val dotsRight = if (remaining > 0) (remaining - 1) / 3 else 0
                val dotsLeft = totalDots - dotsRight
                return (clamped + dotsLeft).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                val dotsBefore = formatted.take(clamped).count { it == '.' }
                return (clamped - dotsBefore).coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

/**
 * Format string angka murni menjadi format pemisah ribuan titik.
 * Contoh: "1234567" -> "1.234.567"
 */
fun formatThousandsString(digits: String): String {
    val clean = digits.filter { it.isDigit() }
    if (clean.isEmpty()) return ""
    val reversed = clean.reversed()
    val withDots = buildString {
        for (i in reversed.indices) {
            if (i > 0 && i % 3 == 0) {
                append('.')
            }
            append(reversed[i])
        }
    }
    return withDots.reversed()
}

/**
 * Format nominal Long menjadi format rupiah titik.
 */
fun formatRupiahAmount(amount: Long): String {
    return formatThousandsString(amount.toString())
}

/**
 * Mengubah angka menjadi kata-kata bahasa Indonesia untuk pembacaan TalkBack (DESIGN §8.5).
 * Contoh: 3500000 -> "tiga juta lima ratus ribu rupiah"
 */
fun rupiahToIndonesianWords(amount: Long): String {
    if (amount == 0L) return "nol rupiah"
    if (amount < 0L) return "minus ${rupiahToIndonesianWords(-amount)}"

    val units = arrayOf(
        "", "satu", "dua", "tiga", "empat", "lima",
        "enam", "tujuh", "delapan", "sembilan", "sepuluh", "sebelas"
    )

    fun convertBelowThousand(n: Long): String {
        return when {
            n < 12 -> units[n.toInt()]
            n < 20 -> "${units[(n - 10).toInt()]} belas"
            n < 100 -> {
                val tens = n / 10
                val rest = n % 10
                val base = "${units[tens.toInt()]} puluh"
                if (rest > 0) "$base ${units[rest.toInt()]}" else base
            }
            n < 200 -> {
                val rest = n - 100
                if (rest > 0) "seratus ${convertBelowThousand(rest)}" else "seratus"
            }
            else -> {
                val hundreds = n / 100
                val rest = n % 100
                val base = "${units[hundreds.toInt()]} ratus"
                if (rest > 0) "$base ${convertBelowThousand(rest)}" else base
            }
        }
    }

    val parts = mutableListOf<String>()
    var remaining = amount

    val billions = remaining / 1_000_000_000L
    if (billions > 0) {
        parts.add("${convertBelowThousand(billions)} miliar")
        remaining %= 1_000_000_000L
    }

    val millions = remaining / 1_000_000L
    if (millions > 0) {
        parts.add("${convertBelowThousand(millions)} juta")
        remaining %= 1_000_000L
    }

    val thousands = remaining / 1_000L
    if (thousands > 0) {
        if (thousands == 1L) {
            parts.add("seribu")
        } else {
            parts.add("${convertBelowThousand(thousands)} ribu")
        }
        remaining %= 1_000L
    }

    if (remaining > 0) {
        parts.add(convertBelowThousand(remaining))
    }

    return parts.joinToString(" ") + " rupiah"
}

/**
 * Kolom Input Uang (Rupiah) Kontenin - DESIGN §5.2.
 * Awalan "Rp", pemisah ribuan saat mengetik, keyboard numerik, tabular font.
 * Semantik TalkBack membaca nominal dalam kata-kata rupiah (DESIGN §8.5).
 */
@Composable
fun MoneyField(
    value: Long?,
    onValueChange: (Long?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
    helperText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val rawText = value?.toString() ?: ""
    val visualTransformation = remember { RupiahThousandsVisualTransformation() }
    val customTypography = KonteninTheme.customTypography
    val colors = KonteninTheme.colorScheme

    // Deskripsi semantik yang dibaca TalkBack secara manusiawi: "Fee: tiga juta lima ratus ribu rupiah"
    val spokenAmount = remember(value) {
        if (value == null) "" else rupiahToIndonesianWords(value)
    }

    KonteninTextField(
        value = rawText,
        onValueChange = { input ->
            val digitsOnly = input.filter { it.isDigit() }
            val parsedLong = digitsOnly.toLongOrNull()
            onValueChange(parsedLong)
        },
        label = label,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                if (spokenAmount.isNotEmpty()) {
                    contentDescription = "$label: $spokenAmount"
                }
                if (isError && !errorMessage.isNullOrBlank()) {
                    error(errorMessage)
                }
            },
        placeholder = placeholder,
        helperText = helperText,
        errorMessage = errorMessage,
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = true,
        leadingIcon = {
            Text(
                text = "Rp ",
                style = customTypography.nominal,
                color = if (enabled) colors.onSurface else colors.onSurface.copy(alpha = 0.38f)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation
    )
}

/**
 * Overload MoneyField menggunakan String murni untuk kemudahan binding state teks sementara jika diperlukan.
 */
@Composable
fun MoneyField(
    valueText: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "0",
    helperText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val digitsOnly = remember(valueText) { valueText.filter { it.isDigit() } }
    val visualTransformation = remember { RupiahThousandsVisualTransformation() }
    val customTypography = KonteninTheme.customTypography
    val colors = KonteninTheme.colorScheme

    val parsedLong = digitsOnly.toLongOrNull()
    val spokenAmount = remember(parsedLong) {
        if (parsedLong == null) "" else rupiahToIndonesianWords(parsedLong)
    }

    KonteninTextField(
        value = digitsOnly,
        onValueChange = { input ->
            val clean = input.filter { it.isDigit() }
            onValueChange(clean)
        },
        label = label,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                if (spokenAmount.isNotEmpty()) {
                    contentDescription = "$label: $spokenAmount"
                }
                if (isError && !errorMessage.isNullOrBlank()) {
                    error(errorMessage)
                }
            },
        placeholder = placeholder,
        helperText = helperText,
        errorMessage = errorMessage,
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = true,
        leadingIcon = {
            Text(
                text = "Rp ",
                style = customTypography.nominal,
                color = if (enabled) colors.onSurface else colors.onSurface.copy(alpha = 0.38f)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation
    )
}

