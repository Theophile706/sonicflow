package com.example.sonicflow.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sonicflow.data.database.entities.PlayHistoryEntity
import com.example.sonicflow.data.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayHistoryDao {

    @Query("""
        SELECT DISTINCT t.* FROM tracks t
        INNER JOIN play_history ph ON t.id = ph.trackId
        ORDER BY ph.playedAt DESC
        LIMIT :limit
    """)
    fun getRecentlyPlayedTracks(limit: Int = 10): Flow<List<Track>>

    @Insert
    suspend fun insertPlayHistory(playHistory: PlayHistoryEntity)

    @Query("DELETE FROM play_history WHERE id NOT IN (SELECT id FROM play_history ORDER BY playedAt DESC LIMIT 50)")
    suspend fun cleanOldHistory()
}