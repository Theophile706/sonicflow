package com.example.sonicflow.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sonicflow.data.database.entities.RecentlyPlayedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedDao {
    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT :limit")
    fun getRecentlyPlayed(limit: Int = 20): Flow<List<RecentlyPlayedEntity>>

    @Query("SELECT trackId FROM recently_played ORDER BY playedAt DESC LIMIT :limit")
    fun getRecentlyPlayedTrackIds(limit: Int = 20): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecentlyPlayed(recentlyPlayed: RecentlyPlayedEntity)

    @Query("DELETE FROM recently_played WHERE trackId = :trackId")
    suspend fun removeRecentlyPlayed(trackId: Long)

    @Query("DELETE FROM recently_played")
    suspend fun clearRecentlyPlayed()
}
