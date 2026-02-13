package com.example.sonicflow.service

import android.media.audiofx.Equalizer
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EqualizerBand(
    val frequency: String, // "60Hz", "250Hz", "1kHz", "4kHz", "16kHz"
    val frequencyValue: Int,
    val level: Int = 0 // -15 to 15 dB
)

@OptIn(UnstableApi::class)
class EqualizerService(private val player: ExoPlayer) {
    
    private val _bands = MutableStateFlow<List<EqualizerBand>>(emptyList())
    val bands: StateFlow<List<EqualizerBand>> = _bands.asStateFlow()
    
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()
    
    private var equalizer: Equalizer? = null
    private val audioSessionId: Int
        get() = player.audioSessionId
    
    fun initializeEqualizer() {
        try {
            if (audioSessionId != 0) {
                equalizer = Equalizer(0, audioSessionId)
                equalizer?.enabled = false
                
                initializeBands()
            }
        } catch (e: Exception) {
            // Fallback if Equalizer is not available
            _bands.value = createDefaultBands()
        }
    }
    
    private fun initializeBands() {
        val eqBands = mutableListOf<EqualizerBand>()
        
        // Define 5 bands: 60Hz, 250Hz, 1kHz, 4kHz, 16kHz
        val frequencies = listOf(
            Pair(60, "60 Hz"),
            Pair(250, "250 Hz"),
            Pair(1000, "1 kHz"),
            Pair(4000, "4 kHz"),
            Pair(16000, "16 kHz")
        )
        
        frequencies.forEach { (freq, label) ->
            eqBands.add(
                EqualizerBand(
                    frequency = label,
                    frequencyValue = freq,
                    level = 0
                )
            )
        }
        
        _bands.value = eqBands
    }
    
    private fun createDefaultBands(): List<EqualizerBand> {
        return listOf(
            EqualizerBand("60 Hz", 60),
            EqualizerBand("250 Hz", 250),
            EqualizerBand("1 kHz", 1000),
            EqualizerBand("4 kHz", 4000),
            EqualizerBand("16 kHz", 16000)
        )
    }
    
    fun setBandLevel(bandIndex: Int, level: Int) {
        val clampedLevel = level.coerceIn(-15, 15)
        
        val currentBands = _bands.value.toMutableList()
        if (bandIndex in currentBands.indices) {
            currentBands[bandIndex] = currentBands[bandIndex].copy(level = clampedLevel)
            _bands.value = currentBands
            
            // Apply to native equalizer if available
            try {
                equalizer?.setBandLevel(bandIndex.toShort(), (clampedLevel * 100).toShort())
            } catch (e: Exception) {
                // Fallback
            }
        }
    }
    
    fun toggleEqualizer() {
        val newState = !_isEnabled.value
        _isEnabled.value = newState
        
        try {
            equalizer?.enabled = newState
        } catch (e: Exception) {
            // Fallback
        }
    }
    
    fun resetEqualizer() {
        _bands.value = _bands.value.map { it.copy(level = 0) }
        
        try {
            _bands.value.forEachIndexed { index, _ ->
                equalizer?.setBandLevel(index.toShort(), 0)
            }
        } catch (e: Exception) {
            // Fallback
        }
    }
    
    fun release() {
        try {
            equalizer?.release()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
