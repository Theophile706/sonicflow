package com.example.sonicflow.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.example.sonicflow.data.database.AppDatabase
import com.example.sonicflow.data.database.dao.TrackDao
import com.example.sonicflow.data.database.dao.FavoriteDao
import com.example.sonicflow.data.database.dao.PlaylistDao
import com.example.sonicflow.data.database.dao.RecentlyPlayedDao
import com.example.sonicflow.data.database.dao.PlayHistoryDao
import com.example.sonicflow.data.preferences.PlaybackStateManager
import com.example.sonicflow.data.preferences.AudioPreferences
import com.example.sonicflow.data.preferences.LanguagePreferences
import com.example.sonicflow.data.repository.MusicRepository
import com.example.sonicflow.service.GenreDetectionService
import com.example.sonicflow.service.EqualizerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sonicflow_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTrackDao(database: AppDatabase): TrackDao {
        return database.trackDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(database: AppDatabase): PlaylistDao {
        return database.playlistDao()
    }

    @Provides
    @Singleton
    fun provideRecentlyPlayedDao(database: AppDatabase): RecentlyPlayedDao {
        return database.recentlyPlayedDao()
    }

    @Provides
    @Singleton
    fun providePlayHistoryDao(database: AppDatabase): PlayHistoryDao {
        return database.playHistoryDao()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context,
        trackDao: TrackDao,
        favoriteDao: FavoriteDao,
        playlistDao: PlaylistDao,
        recentlyPlayedDao: RecentlyPlayedDao
    ): MusicRepository {
        return MusicRepository(
            context, trackDao, favoriteDao, playlistDao, recentlyPlayedDao
        )
    }

    @Provides
    @Singleton
    fun providePlaybackStateManager(
        @ApplicationContext context: Context
    ): PlaybackStateManager {
        return PlaybackStateManager(context)
    }

    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideGenreDetectionService(): GenreDetectionService {
        return GenreDetectionService()
    }

    @Provides
    @Singleton
    fun provideEqualizerService(player: ExoPlayer): EqualizerService {
        return EqualizerService(player).apply {
            initializeEqualizer()
        }
    }

    @Provides
    @Singleton
    fun provideAudioPreferences(
        @ApplicationContext context: Context
    ): AudioPreferences {
        return AudioPreferences(context)
    }

    @Provides
    @Singleton
    fun provideLanguagePreferences(
        @ApplicationContext context: Context
    ): LanguagePreferences {
        return LanguagePreferences(context)
    }
}