package com.example.sonicflow.presentation.playlists

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.sonicflow.data.database.entities.PlaylistEntity
import com.example.sonicflow.data.model.Track
import com.example.sonicflow.presentation.components.PlaylistCard
import com.example.sonicflow.presentation.home.FavoriteTrackCard
import com.example.sonicflow.presentation.home.RecentTrackCard
import com.example.sonicflow.presentation.home.SectionHeader
import com.example.sonicflow.presentation.library.LibraryViewModel
import com.example.sonicflow.presentation.player.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    onNavigateToPlayer: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    playerViewModel: PlayerViewModel = hiltViewModel(),
    libraryViewModel: LibraryViewModel = hiltViewModel()
) {
    val playlists by libraryViewModel.playlists.collectAsState()
    val selectedPlaylistId by libraryViewModel.selectedPlaylistId.collectAsState()
    val tracksInPlaylist by libraryViewModel.tracksInSelectedPlaylist.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    val favoriteTracks by libraryViewModel.favoriteTracks.collectAsState()
    val recentlyPlayedTracks by libraryViewModel.recentlyPlayedTracks.collectAsState()

    if (selectedPlaylistId != null) {
        var showTrackMenu by remember { mutableStateOf<Track?>(null) }

        Scaffold(
            containerColor = Color(0xFF000000),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            playlists.find { it.id == selectedPlaylistId }?.name ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { libraryViewModel.selectPlaylist(null) }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Retour",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            playlists.find { it.id == selectedPlaylistId }?.let {
                                libraryViewModel.deletePlaylist(it)
                            }
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Supprimer",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black
                    )
                )
            }
        ) { padding ->
            if (tracksInPlaylist.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Playlist vide",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(tracksInPlaylist) { index, track ->
                        PlaylistTrackItem(
                            track = track,
                            isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying,
                            onClick = {
                                playerViewModel.playTrack(track, index)
                                onNavigateToPlayer()
                            },
                            onRemove = {
                                selectedPlaylistId?.let { playlistId ->
                                    libraryViewModel.removeTrackFromPlaylist(playlistId, track.id)
                                }
                            },
                            onMenuClick = {
                                showTrackMenu = track
                            }
                        )
                    }
                }
            }
        }

        if (showTrackMenu != null) {
            AlertDialog(
                onDismissRequest = { showTrackMenu = null },
                title = { Text("Options") },
                text = {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    playerViewModel.addToQueue(showTrackMenu!!)
                                    showTrackMenu = null
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.QueueMusic,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Ajouter à la file d'attente", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlaylistId?.let { playlistId ->
                                        libraryViewModel.removeTrackFromPlaylist(playlistId, showTrackMenu!!.id)
                                    }
                                    showTrackMenu = null
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFFFF4444),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Retirer de la playlist", color = Color.White)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTrackMenu = null }) {
                        Text("Fermer")
                    }
                },
                containerColor = Color(0xFF1E1E1E)
            )
        }
    } else {
        // Liste des playlists
        Scaffold(
            containerColor = Color(0xFF000000),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Listes de lecture",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = { showCreateDialog = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Créer",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.Black),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Section Favoris
                if (favoriteTracks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Aimé(s)",
                            subtitle = "${favoriteTracks.size} morceaux",
                            icon = Icons.Default.Favorite,
                            iconTint = Color(0xFFFF4444)
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(favoriteTracks.take(10)) { track ->
                                FavoriteTrackCard(
                                    track = track,
                                    isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying,
                                    onClick = {
                                        playerViewModel.playTrack(track)
                                        onNavigateToPlayer()
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Section Lecture récente
                if (recentlyPlayedTracks.isNotEmpty()) {
                    item {
                        val uniqueRecentTracks = recentlyPlayedTracks
                            .distinctBy { it.id }  // Élimine les doublons par ID
                            .take(5)

                        SectionHeader(
                            title = "Lecture récente",
                            subtitle = "${uniqueRecentTracks.size} morceaux",
                            icon = Icons.Default.History,
                            iconTint = Color(0xFF06B6D4)
                        )
                    }

                    item {
                        val uniqueRecentTracks = recentlyPlayedTracks
                            .distinctBy { it.id }
                            .take(5)

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uniqueRecentTracks) { track ->
                                RecentTrackCard(
                                    track = track,
                                    isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying,
                                    onClick = {
                                        playerViewModel.playTrack(track)
                                        onNavigateToPlayer()
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Playlists section title
                item {
                    Text(
                        "Listes de lecture",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (playlists.isNotEmpty()) {
                    items(playlists.chunked(2)) { playlistPair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PlaylistCard(
                                name = playlistPair[0].name,
                                trackCount = 0,
                                onClick = {
                                    libraryViewModel.selectPlaylist(playlistPair[0].id)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            if (playlistPair.size > 1) {
                                PlaylistCard(
                                    name = playlistPair[1].name,
                                    trackCount = 0,
                                    onClick = {
                                        libraryViewModel.selectPlaylist(playlistPair[1].id)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.LibraryMusic,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Aucune playlist",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { showCreateDialog = true }) {
                                    Text("Créer une playlist", color = Color(0xFF7C3AED))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                libraryViewModel.createPlaylist(name)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun PlaylistListItem(
    playlist: PlaylistEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.LibraryMusic,
                contentDescription = null,
                tint = Color(0xFFFF6600),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (playlist.description.isNotEmpty()) {
                Text(
                    text = playlist.description,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Supprimer",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun PlaylistTrackItem(
    track: Track,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onMenuClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art avec meilleure gestion
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            if (!track.albumArtUri.isNullOrEmpty()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(
                            when {
                                track.albumArtUri.startsWith("content://") -> Uri.parse(track.albumArtUri)
                                track.albumArtUri.startsWith("file://") -> Uri.parse(track.albumArtUri)
                                track.albumArtUri.startsWith("/") -> Uri.parse("file://${track.albumArtUri}")
                                else -> track.albumArtUri
                            }
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = "Album Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Loading",
                            tint = Color(0xFFFF6600).copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    error = {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "No Album Art",
                            tint = Color(0xFFFF6600),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "No Album Art",
                    tint = Color(0xFFFF6600),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = if (isPlaying) Color(0xFFFF6600) else Color.White,
                fontSize = 16.sp,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = track.artist,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onMenuClick) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Options",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0) }

    val colorOptions = listOf(
        Color(0xFF7C3AED) to "Purple",
        Color(0xFF06B6D4) to "Cyan",
        Color(0xFFEF4444) to "Red",
        Color(0xFF10B981) to "Green",
        Color(0xFFFF6600) to "Amber",
        Color(0xFF3B82F6) to "Blue"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Créer une playlist",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Playlist Name
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Nom de la playlist") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF7C3AED),
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = Color(0xFF7C3AED),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Selection Label
                Text(
                    "Couleur",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Color Palette
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.forEachIndexed { index, (color, _) ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                                .clickable { selectedColor = index }
                                .border(
                                    width = if (selectedColor == index) 3.dp else 0.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (playlistName.isNotBlank()) onConfirm(playlistName) },
                enabled = playlistName.isNotBlank()
            ) {
                Text("Créer", color = Color(0xFF7C3AED))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1E1E1E),
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    )
}
