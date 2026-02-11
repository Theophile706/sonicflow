package com.example.sonicflow.data.model

enum class MusicGenre(val displayName: String, val color: Int) {
    POP("Pop", 0xFFE91E63.toInt()),
    REGGAE("Reggae", 0xFF4CAF50.toInt()),
    TROPICAL("Tropical", 0xFFFFC107.toInt()),
    ROCK("Rock", 0xFFF44336.toInt()),
    HIP_HOP("Hip-Hop", 0xFF9C27B0.toInt()),
    EDM("EDM", 0xFF00BCD4.toInt()),
    JAZZ("Jazz", 0xFF3F51B5.toInt()),
    CLASSICAL("Classical", 0xFF8BC34A.toInt()),
    R_AND_B("R&B", 0xFFFF6F00.toInt()),
    INDIE("Indie", 0xFFC2185B.toInt()),
    UNKNOWN("Unknown", 0xFF9E9E9E.toInt())
}

data class GenreAnalysis(
    val primaryGenre: MusicGenre,
    val confidence: Float,
    val tempo: Int, // BPM
    val energy: Float, // 0f - 1f
    val complexity: Float // 0f - 1f (affects waveform speed)
)
