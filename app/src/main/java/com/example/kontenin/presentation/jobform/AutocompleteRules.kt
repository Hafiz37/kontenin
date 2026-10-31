package com.example.kontenin.presentation.jobform

/**
 * Aturan murni pencarian saran autocomplete dan normalisasi teks (FE-10).
 *
 * Acuan: PRD FR-2; EC-23; Kontenin-Frontend-Tasks.md FE-10.
 * - Pencocokan substring tanpa membedakan kapital (case-insensitive).
 * - Pengurutan: frekuensi tertinggi (descending), seri diurutkan alfabet (ascending).
 * - Maksimal 5 saran.
 * - Saat simpan: trim dan rapikan spasi ganda (\s+ -> " ").
 * - Nilai yang sama case-insensitive memakai ejaan lama yang paling awal tercatat.
 */
object AutocompleteRules {

    private data class AggregateEntry(
        val key: String,             // lowercase normalized key
        val canonicalSpelling: String, // ejaan pertama yang ditemui
        var count: Int = 0,
        val firstIndex: Int          // indeks kemunculan pertama
    )

    /**
     * Membersihkan spasi di awal/akhir dan merapikan spasi ganda di tengah menjadi spasi tunggal.
     */
    fun normalizeSpacing(text: String): String {
        return text.trim().replace(Regex("\\s+"), " ")
    }

    /**
     * Mengambil daftar saran autocomplete berdasarkan query pengguna dan data yang ada:
     * - Tidak membedakan kapital (case-insensitive substring matching).
     * - Jika query kosong/blank, tampilkan saran berdasarkan frekuensi tertinggi.
     * - Frekuensi seri diurutkan secara alfabetis (deterministik).
     * - Maksimal [maxSuggestions] saran (default 5).
     */
    fun getSuggestions(
        query: String,
        existingValues: List<String>,
        maxSuggestions: Int = 5
    ): List<String> {
        if (existingValues.isEmpty()) return emptyList()

        // 1. Agregasi frekuensi dan ejaan kanonikal pertama
        val aggregateMap = linkedMapOf<String, AggregateEntry>()

        for ((index, raw) in existingValues.withIndex()) {
            val cleaned = normalizeSpacing(raw)
            if (cleaned.isEmpty()) continue
            val key = cleaned.lowercase()

            val existing = aggregateMap[key]
            if (existing == null) {
                aggregateMap[key] = AggregateEntry(
                    key = key,
                    canonicalSpelling = cleaned,
                    count = 1,
                    firstIndex = index
                )
            } else {
                existing.count++
            }
        }

        val allAggregates = aggregateMap.values.toList()

        // 2. Filter berdasarkan query (substring matching case-insensitive)
        val trimmedQuery = normalizeSpacing(query)
        val filtered = if (trimmedQuery.isEmpty()) {
            allAggregates
        } else {
            val lowerQuery = trimmedQuery.lowercase()
            allAggregates.filter {
                it.canonicalSpelling.contains(lowerQuery, ignoreCase = true)
            }
        }

        // 3. Urutkan: frekuensi descending, lalu alfabetis ascending
        val sorted = filtered.sortedWith(
            compareByDescending<AggregateEntry> { it.count }
                .thenBy { it.canonicalSpelling.lowercase() }
        )

        // 4. Ambil maksimal N saran
        return sorted.take(maxSuggestions).map { it.canonicalSpelling }
    }

    /**
     * Normalisasi nilai sebelum disimpan:
     * 1. Trim spasi di awal/akhir dan ganti spasi ganda menjadi spasi tunggal.
     * 2. Jika sama dengan isian lama tanpa memandang kapital, gunakan ejaan lama
     *    (bila ada beberapa ejaan, pilih ejaan yang tercatat paling awal).
     */
    fun normalizeForSave(
        input: String,
        existingValues: List<String>
    ): String {
        val cleaned = normalizeSpacing(input)
        if (cleaned.isEmpty()) return ""

        val lowerCleaned = cleaned.lowercase()

        // Cari kemunculan pertama pada existingValues yang cocok secara case-insensitive
        for (raw in existingValues) {
            val existingCleaned = normalizeSpacing(raw)
            if (existingCleaned.lowercase() == lowerCleaned) {
                return existingCleaned
            }
        }

        return cleaned
    }
}
