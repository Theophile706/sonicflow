package com.example.sonicflow.service

import com.example.sonicflow.data.model.GenreAnalysis
import com.example.sonicflow.data.model.MusicGenre
import kotlin.math.abs
import kotlin.random.Random

class GenreDetectionService {
    
    fun detectGenre(
        title: String,
        artist: String,
        album: String,
        duration: Long,
        filePath: String
    ): GenreAnalysis {
        // Simulate ML model based on metadata and file characteristics
        val seed = (title + artist + album).hashCode().toLong()
        val random = Random(seed)
        
        // Analyze metadata for genre hints
        val genreScore = analyzeMetadata(title, artist, album)
        
        // Combine with pseudo-audio analysis (simulated BPM detection)
        val bpm = simulateTempoDetection(duration, random)
        val energy = simulateEnergyDetection(title, artist, bpm, random)
        val complexity = simulateComplexity(album, artist, random)
        
        // Determine primary genre
        val genre = selectGenre(genreScore, bpm, energy, complexity)
        val confidence = 0.65f + random.nextFloat() * 0.3f // 65-95% confidence
        
        return GenreAnalysis(
            primaryGenre = genre,
            confidence = confidence,
            tempo = bpm,
            energy = energy,
            complexity = complexity
        )
    }
    
    private fun analyzeMetadata(
        title: String,
        artist: String,
        album: String
    ): Map<MusicGenre, Float> {
        val metadata = (title + artist + album).lowercase()
        val scores = mutableMapOf<MusicGenre, Float>()
        
        // Initialize all genres with base score
        MusicGenre.values().forEach { genre ->
            scores[genre] = 0f
        }
        
        // Genre keyword analysis
        val genreKeywords = mapOf(
            MusicGenre.REGGAE to listOf("reggae", "bob marley", "ska", "rasta"),
            MusicGenre.TROPICAL to listOf("tropical", "caribbean", "calypso", "bossa", "samba"),
            MusicGenre.POP to listOf("pop", "pop music", "chart", "hit"),
            MusicGenre.ROCK to listOf("rock", "guitar", "band", "hard rock"),
            MusicGenre.HIP_HOP to listOf("hip hop", "rap", "beats", "hiphop"),
            MusicGenre.EDM to listOf("edm", "electronic", "techno", "house", "dubstep"),
            MusicGenre.JAZZ to listOf("jazz", "blues", "bebop", "quartet"),
            MusicGenre.CLASSICAL to listOf("classical", "symphony", "orchestra", "bach", "mozart"),
            MusicGenre.R_AND_B to listOf("r&b", "rnb", "soul", "r and b"),
            MusicGenre.INDIE to listOf("indie", "alternative", "underground")
        )
        
        genreKeywords.forEach { (genre, keywords) ->
            keywords.forEach { keyword ->
                if (metadata.contains(keyword)) {
                    scores[genre] = (scores[genre] ?: 0f) + 0.2f
                }
            }
        }
        
        return scores
    }
    
    private fun simulateTempoDetection(
        duration: Long,
        random: Random
    ): Int {
        // Simulate BPM detection based on duration patterns
        val baseTempo = 60 + (duration % 140).toInt() // 60-200 BPM range
        val variation = random.nextInt(-10, 10)
        return (baseTempo + variation).coerceIn(60, 200)
    }
    
    private fun simulateEnergyDetection(
        title: String,
        artist: String,
        bpm: Int,
        random: Random
    ): Float {
        var energy = 0.5f
        
        // Adjust based on BPM
        energy += (bpm - 120f) / 200f // Higher BPM = higher energy
        energy += random.nextFloat() * 0.2f - 0.1f // Add variation
        
        // Check for energy indicators in metadata
        val metadata = (title + artist).lowercase()
        if (metadata.contains(listOf("party", "dance", "club", "high", "energy", "fast"))) {
            energy += 0.15f
        } else if (metadata.contains(listOf("slow", "calm", "sleep", "relax", "meditation"))) {
            energy -= 0.15f
        }
        
        return energy.coerceIn(0f, 1f)
    }
    
    private fun simulateComplexity(
        album: String,
        artist: String,
        random: Random
    ): Float {
        var complexity = 0.5f
        
        // Check for complexity indicators
        val metadata = (album + artist).lowercase()
        if (metadata.contains(listOf("classical", "symphony", "orchestra", "album"))) {
            complexity += 0.3f
        }
        
        complexity += random.nextFloat() * 0.3f - 0.15f
        return complexity.coerceIn(0f, 1f)
    }
    
    private fun selectGenre(
        scores: Map<MusicGenre, Float>,
        bpm: Int,
        energy: Float,
        complexity: Float
    ): MusicGenre {
        // Find highest scored genre
        var topGenre = MusicGenre.UNKNOWN
        var topScore = 0f
        
        scores.forEach { (genre, score) ->
            if (score > topScore) {
                topScore = score
                topGenre = genre
            }
        }
        
        // Override based on BPM and energy if no clear genre found
        if (topScore < 0.3f) {
            topGenre = when {
                bpm > 150 && energy > 0.7f -> MusicGenre.EDM
                bpm < 100 && energy < 0.4f -> MusicGenre.CLASSICAL
                energy > 0.8f -> MusicGenre.ROCK
                energy < 0.3f -> MusicGenre.JAZZ
                bpm in 80..120 && complexity > 0.6f -> MusicGenre.R_AND_B
                else -> MusicGenre.POP
            }
        }
        
        return topGenre
    }
    
    private fun String.contains(keywords: List<String>): Boolean {
        return keywords.any { this.contains(it) }
    }
}
