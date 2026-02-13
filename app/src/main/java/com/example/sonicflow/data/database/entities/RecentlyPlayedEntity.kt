package com.example.sonicflow.data.database.entities

import androidx.room.*

@Entity(
    tableName = "recently_played",
    indices = [Index(value = ["playedAt"], orders = [Index.Order.DESC])]
)
data class RecentlyPlayedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "trackId") val trackId: Long,
    @ColumnInfo(name = "playedAt") val playedAt: Long = System.currentTimeMillis()
)
