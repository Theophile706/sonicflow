package com.example.sonicflow.data.model

import androidx.room.Ignore

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val albumArtUri: String?,
    val dateAdded: Long,
    @Ignore val waveformData: List<Float>? = null
) {
    constructor(
        id: Long,
        title: String,
        artist: String,
        album: String,
        duration: Long,
        path: String,
        albumArtUri: String?,
        dateAdded: Long
    ) : this(id, title, artist, album, duration, path, albumArtUri, dateAdded, null)
}