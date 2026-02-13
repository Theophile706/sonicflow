package com.example.sonicflow.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sonicflow.data.database.dao.TrackDao
import com.example.sonicflow.data.database.dao.FavoriteDao
import com.example.sonicflow.data.database.dao.PlayHistoryDao
import com.example.sonicflow.data.database.dao.PlaylistDao
import com.example.sonicflow.data.database.dao.RecentlyPlayedDao
import com.example.sonicflow.data.database.entities.*
import com.example.sonicflow.data.database.entities.PlayHistoryEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        FavoriteEntity::class,
        RecentlyPlayedEntity::class,
        PlayHistoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
    abstract fun playHistoryDao(): PlayHistoryDao
}
