package com.example.kontenin.presentation

import com.example.kontenin.presentation.jobform.AutocompleteRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test murni untuk AutocompleteRules (FE-10).
 *
 * Menguji:
 * - Batas maksimal 5 saran
 * - Pengurutan frekuensi tertinggi & alfabet tie-breaker
 * - Pencocokan substring dan case-insensitive
 * - Normalisasi spasi dan ejaan kanonikal saat simpan
 */
class AutocompleteRulesTest {

    // ── 1. Uji batas maksimal 5 saran ───────────────────────────────────────

    @Test
    fun `data lebih dari 5 item hanya mengembalikan 5 teratas berdasarkan frekuensi`() {
        val existing = listOf(
            "Resepsi", "Resepsi", "Resepsi", "Resepsi", // 4
            "Akad", "Akad", "Akad",                     // 3
            "Lamaran", "Lamaran",                       // 2
            "Siraman",                                  // 1
            "Tedak Sinten",                             // 1
            "Pengajian",                                // 1
            "Ngunduh Mantu"                             // 1
        )

        val suggestions = AutocompleteRules.getSuggestions(query = "", existingValues = existing)

        assertEquals(5, suggestions.size)
        // 4 teratas pasti: Resepsi, Akad, Lamaran
        assertEquals("Resepsi", suggestions[0])
        assertEquals("Akad", suggestions[1])
        assertEquals("Lamaran", suggestions[2])
        // Yang berseri dengan count 1 diurutkan alfabet: Ngunduh Mantu, Pengajian, Siraman, Tedak Sinten
        assertEquals("Ngunduh Mantu", suggestions[3])
        assertEquals("Pengajian", suggestions[4])
    }

    // ── 2. Uji Substring & Case-Insensitive Matching ────────────────────────

    @Test
    fun `pencarian cocok dengan substring tanpa membedakan huruf besar kecil`() {
        val existing = listOf("Resepsi", "Akad", "Siraman", "Tedak Sinten")

        // Substring di tengah "sep" -> "Resepsi"
        val bySubstring = AutocompleteRules.getSuggestions("sep", existing)
        assertEquals(listOf("Resepsi"), bySubstring)

        // Case-insensitive "RESEPSI" -> "Resepsi"
        val byUpper = AutocompleteRules.getSuggestions("RESEPSI", existing)
        assertEquals(listOf("Resepsi"), byUpper)

        // "an" -> "Lamaran", "Siraman"
        val bySuffix = AutocompleteRules.getSuggestions("an", listOf("Siraman", "Lamaran", "Akad"))
        assertEquals(listOf("Lamaran", "Siraman"), bySuffix)

        // Tidak ada yang cocok -> empty list
        val noMatch = AutocompleteRules.getSuggestions("xyz", existing)
        assertTrue(noMatch.isEmpty())
    }

    // ── 3. Uji Ejaan Kanonikal (yang Tercatat Paling Awal) ───────────────────

    @Test
    fun `ejaan yang tercatat paling awal digunakan sebagai saran representatif`() {
        // "resepsi" huruf kecil tercatat pertama kali
        val existing = listOf("resepsi", "Resepsi", "RESEPSI", "Akad")

        val suggestions = AutocompleteRules.getSuggestions("", existing)
        // "resepsi" frekuensi 3 dengan ejaan pertama "resepsi"
        assertEquals("resepsi", suggestions[0])
    }

    // ── 4. Uji Pengurutan Seri (Alphabetical Tie-breaker) ───────────────────

    @Test
    fun `frekuensi seri diurutkan alfabet secara deterministik`() {
        val existing = listOf("Siraman", "Akad", "Lamaran")

        val suggestions = AutocompleteRules.getSuggestions("", existing)
        assertEquals(listOf("Akad", "Lamaran", "Siraman"), suggestions)
    }

    // ── 5. Uji Normalisasi Spasi ────────────────────────────────────────────

    @Test
    fun `normalizeSpacing merapikan spasi awal akhir dan spasi ganda`() {
        assertEquals("Resepsi Pernikahan", AutocompleteRules.normalizeSpacing("   Resepsi     Pernikahan   "))
        assertEquals("Vendor A", AutocompleteRules.normalizeSpacing("Vendor\t\nA"))
    }

    // ── 6. Uji Normalisasi Sebelum Simpan (normalizeForSave) ─────────────────

    @Test
    fun `simpan dengan spasi berlebih dan kapital berbeda memakai ejaan lama`() {
        val existing = listOf("Resepsi", "Vendor Pribadi")

        // "  resepsi  " -> "Resepsi"
        val normalizedAcara = AutocompleteRules.normalizeForSave("   resepsi   ", existing)
        assertEquals("Resepsi", normalizedAcara)

        // "  vEndOr   pRiBaDi  " -> "Vendor Pribadi"
        val normalizedSumber = AutocompleteRules.normalizeForSave("  vEndOr   pRiBaDi  ", existing)
        assertEquals("Vendor Pribadi", normalizedSumber)
    }

    @Test
    fun `simpan ejaan dengan beberapa varian di fixture memilih ejaan paling awal`() {
        val existing = listOf("vendor lama", "Vendor Lama")

        val result = AutocompleteRules.normalizeForSave("VENDOR LAMA", existing)
        assertEquals("vendor lama", result)
    }

    @Test
    fun `simpan nilai baru yang belum pernah ada tetap mempertahankan teks setelah rapikan spasi`() {
        val existing = listOf("Resepsi", "Akad")

        val result = AutocompleteRules.normalizeForSave("   Tedak     Sinten   ", existing)
        assertEquals("Tedak Sinten", result)
    }
}
