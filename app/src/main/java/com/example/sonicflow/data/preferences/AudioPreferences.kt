package com.example.sonicflow.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AudioQuality(val displayName: String) {
    HIGH("High (320kbps)"),
    NORMAL("Normal (192kbps)"),
    LOW("Low (128kbps)")
}

private const val AUDIO_PREFERENCES = "audio_preferences"
private val QUALITY_KEY = stringPreferencesKey("audio_quality")
private val PLAYBACK_SPEED_KEY = floatPreferencesKey("playback_speed")
private val EQUALIZER_ENABLED = stringPreferencesKey("equalizer_enabled")

private val Context.audioPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = AUDIO_PREFERENCES
)

class AudioPreferences(private val context: Context) {
    
    val audioQuality: Flow<AudioQuality> = context.audioPreferencesDataStore.data.map { preferences ->
        val quality = preferences[QUALITY_KEY] ?: AudioQuality.NORMAL.name
        AudioQuality.valueOf(quality)
    }
    
    val playbackSpeed: Flow<Float> = context.audioPreferencesDataStore.data.map { preferences ->
        preferences[PLAYBACK_SPEED_KEY] ?: 1.0f
    }
    
    val equalizerEnabled: Flow<Boolean> = context.audioPreferencesDataStore.data.map { preferences ->
        val value = preferences[EQUALIZER_ENABLED] ?: "false"
        value == "true"
    }
    
    suspend fun setAudioQuality(quality: AudioQuality) {
        context.audioPreferencesDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[QUALITY_KEY] = quality.name
            }
        }
    }
    
    suspend fun setPlaybackSpeed(speed: Float) {
        val clampedSpeed = speed.coerceIn(0.5f, 2.0f)
        context.audioPreferencesDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[PLAYBACK_SPEED_KEY] = clampedSpeed
            }
        }
    }
    
    suspend fun setEqualizerEnabled(enabled: Boolean) {
        context.audioPreferencesDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[EQUALIZER_ENABLED] = enabled.toString()
            }
        }
    }
}
