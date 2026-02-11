package com.example.sonicflow.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class Language(val code: String, val displayName: String) {
    FRENCH("fr", "Français"),
    ENGLISH("en", "English")
}

private const val LANGUAGE_PREFERENCES = "language_preferences"
private val LANGUAGE_KEY = stringPreferencesKey("language")

private val Context.languagePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = LANGUAGE_PREFERENCES
)

class LanguagePreferences(private val context: Context) {
    
    val language: Flow<Language> = context.languagePreferencesDataStore.data.map { preferences ->
        val lang = preferences[LANGUAGE_KEY] ?: Language.FRENCH.code
        if (lang == Language.ENGLISH.code) Language.ENGLISH else Language.FRENCH
    }
    
    suspend fun setLanguage(language: Language) {
        context.languagePreferencesDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[LANGUAGE_KEY] = language.code
            }
        }
    }
}

object Strings {
    // FR/EN translations
    private val translations = mapOf(
        // Menu & Navigation
        "home" to mapOf("fr" to "Accueil", "en" to "Home"),
        "now_playing" to mapOf("fr" to "En cours", "en" to "Now Playing"),
        "artists" to mapOf("fr" to "Artistes", "en" to "Artists"),
        "albums" to mapOf("fr" to "Albums", "en" to "Albums"),
        "playlists" to mapOf("fr" to "Playlists", "en" to "Playlists"),
        "favorites" to mapOf("fr" to "Favoris", "en" to "Favorites"),
        "settings" to mapOf("fr" to "Paramètres", "en" to "Settings"),
        
        // Player controls
        "play" to mapOf("fr" to "Lecture", "en" to "Play"),
        "pause" to mapOf("fr" to "Pause", "en" to "Pause"),
        "previous" to mapOf("fr" to "Précédent", "en" to "Previous"),
        "next" to mapOf("fr" to "Suivant", "en" to "Next"),
        "shuffle" to mapOf("fr" to "Aléatoire", "en" to "Shuffle"),
        "repeat" to mapOf("fr" to "Répétition", "en" to "Repeat"),
        "favorite" to mapOf("fr" to "Favoris", "en" to "Favorite"),
        "back" to mapOf("fr" to "Retour", "en" to "Back"),
        
        // Menu options
        "play_next" to mapOf("fr" to "Lire ensuite", "en" to "Play Next"),
        "add_to_queue" to mapOf("fr" to "Ajouter à la file", "en" to "Add to Queue"),
        "add_to_playlist" to mapOf("fr" to "Ajouter à playlist", "en" to "Add to Playlist"),
        "share" to mapOf("fr" to "Partager", "en" to "Share"),
        "view_album" to mapOf("fr" to "Voir l'album", "en" to "View Album"),
        "view_artist" to mapOf("fr" to "Voir l'artiste", "en" to "View Artist"),
        
        // Settings
        "audio_quality" to mapOf("fr" to "Qualité audio", "en" to "Audio Quality"),
        "playback_speed" to mapOf("fr" to "Vitesse de lecture", "en" to "Playback Speed"),
        "equalizer" to mapOf("fr" to "Égaliseur", "en" to "Equalizer"),
        "reset" to mapOf("fr" to "Réinitialiser", "en" to "Reset"),
        "language" to mapOf("fr" to "Langue", "en" to "Language"),
        "bass" to mapOf("fr" to "Basse", "en" to "Bass"),
        "treble" to mapOf("fr" to "Aigu", "en" to "Treble"),
        
        // Genre detection
        "genre" to mapOf("fr" to "Genre", "en" to "Genre"),
        "tempo" to mapOf("fr" to "Tempo", "en" to "Tempo"),
        "energy" to mapOf("fr" to "Énergie", "en" to "Energy"),
        "complexity" to mapOf("fr" to "Complexité", "en" to "Complexity"),
        "confidence" to mapOf("fr" to "Confiance", "en" to "Confidence"),
        
        // Messages
        "no_track_playing" to mapOf("fr" to "Aucune chanson en lecture", "en" to "No track playing"),
        "no_tracks_found" to mapOf("fr" to "Aucune chanson trouvée", "en" to "No tracks found"),
        "loading" to mapOf("fr" to "Chargement...", "en" to "Loading..."),
        
        // Units
        "bpm" to mapOf("fr" to "BPM", "en" to "BPM"),
        "hz" to mapOf("fr" to "Hz", "en" to "Hz"),
        "db" to mapOf("fr" to "dB", "en" to "dB")
    )
    
    fun getString(key: String, language: Language): String {
        return translations[key]?.get(language.code) ?: key
    }
    
    fun getString(key: String, languageCode: String): String {
        return translations[key]?.get(languageCode) ?: key
    }
}
