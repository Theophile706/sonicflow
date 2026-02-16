package com.example.sonicflow.presentation.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.sonicflow.data.model.Track
import com.example.sonicflow.presentation.components.AlbumArtPlaceholder
import com.example.sonicflow.presentation.components.EqualizerSection
import com.example.sonicflow.presentation.components.AudioSettingsSection
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val playbackState by viewModel.playbackState.collectAsState()
    val currentTrack = playbackState.currentTrack
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val genreAnalysis by viewModel.currentGenreAnalysis.collectAsState()
    val equalizerBands by viewModel.equalizerBands.collectAsState()
    val equalizerEnabled by viewModel.equalizerEnabled.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val audioQuality by viewModel.audioQuality.collectAsState()
    val language by viewModel.language.collectAsState(initial = com.example.sonicflow.data.preferences.Language.FRENCH)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenWidth < 360.dp
    val isMediumScreen = screenWidth >= 360.dp && screenWidth < 400.dp
    var showTrackMenu by remember { mutableStateOf<Track?>(null) }
    var showPlayerMenu by remember { mutableStateOf(false) }
    var showEqualizerMenu by remember { mutableStateOf(false) }
    var showAudioSettings by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7C3AED),
                        Color(0xFF0F0F0F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Now Playing",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmallScreen) 18.sp else 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    // Bouton Paramètres audio
                    IconButton(onClick = { showAudioSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Audio Settings",
                            tint = Color.Black
                        )
                    }
                    // Bouton Favoris
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFFF4444) else Color.Black
                        )
                    }
                    Box {
                        IconButton(onClick = { showPlayerMenu = !showPlayerMenu }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = showPlayerMenu,
                            onDismissRequest = { showPlayerMenu = false },
                            modifier = Modifier.background(Color(0xFF252525))
                        ) {
                            currentTrack?.let { track ->
                                DropdownMenuItem(
                                    text = { Text("Play Next", color = Color.White) },
                                    onClick = {
                                        viewModel.playNext(track)
                                        showPlayerMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color(0xFF7C3AED)
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add to Queue", color = Color.White) },
                                    onClick = {
                                        viewModel.addToQueue(track)
                                        showPlayerMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.QueueMusic,
                                            contentDescription = null,
                                            tint = Color(0xFF06B6D4)
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add to Playlist", color = Color.White) },
                                    onClick = {
                                        showTrackMenu = track
                                        showPlayerMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.PlaylistAdd,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981)
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Share", color = Color.White) },
                                    onClick = {
                                        showPlayerMenu = false
                                        // Share functionality would go here
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Share,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B)
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("View Album", color = Color.White) },
                                    onClick = {
                                        showPlayerMenu = false
                                        // Navigate to album view
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Album,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444)
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete", color = Color.White) },
                                    onClick = {
                                        viewModel.deleteTrack(track)
                                        showPlayerMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444)
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            if (currentTrack == null) {
                // État vide
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            modifier = Modifier.size(if (isSmallScreen) 60.dp else 80.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No track playing",
                            fontSize = if (isSmallScreen) 18.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = if (isSmallScreen) 16.dp else 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(if (isSmallScreen) 10.dp else 20.dp))

                    // Album Art avec cercle (comme dans l'image)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(
                                when {
                                    isSmallScreen -> 240.dp
                                    isMediumScreen -> 280.dp
                                    else -> 320.dp
                                }
                            )
                    ) {
                        // Cercle de fond avec gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF7C3AED),
                                            Color(0xFF06B6D4)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )

                        // Image album
                        AsyncImage(
                            model = currentTrack.albumArtUri,
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(if (isSmallScreen) 15.dp else 20.dp))

                    // Titre de la chanson (UNE SEULE LIGNE)
                    Text(
                        text = currentTrack.title,
                        fontSize = when {
                            isSmallScreen -> 18.sp
                            isMediumScreen -> 20.sp
                            else -> 24.sp
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Artiste
                    Text(
                        text = currentTrack.artist,
                        fontSize = when {
                            isSmallScreen -> 14.sp
                            isMediumScreen -> 15.sp
                            else -> 16.sp
                        },
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // WAVEFORM VISUALIZATION - Moved up for better prominence
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .height(
                                when {
                                    isSmallScreen -> 85.dp
                                    isMediumScreen -> 100.dp
                                    else -> 120.dp
                                }
                            )
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        AnimatedWaveform(
                            isPlaying = playbackState.isPlaying,
                            trackId = currentTrack.id,
                            currentPosition = playbackState.currentPosition,
                            duration = playbackState.duration,
                            complexity = genreAnalysis?.complexity ?: 0.5f,
                            energy = genreAnalysis?.energy ?: 0.5f,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    // Progress Bar - SECTION COMPLÈTE CORRIGÉE
// Remplacer la section Progress Bar (environ lignes 298-389)

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val duration = playbackState.duration.coerceAtLeast(1L)
                        val position = playbackState.currentPosition.coerceIn(0L, duration)
                        val progress = position.toFloat() / duration.toFloat()
                        var isDragging by remember { mutableStateOf(false) }
                        //var dragPosition by remember { mutableStateOf(progress) }
                        var dragPosition by remember { mutableStateOf(0f) }

                        LaunchedEffect(progress, isDragging) {
                            if (!isDragging) {
                                dragPosition = progress
                            }
                        }
                        val currentProgress = if (isDragging) dragPosition else progress

                        // Custom Progress Bar - Draggable
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { isDragging = true },
                                        onDragEnd = {
                                            isDragging = false
                                            viewModel.seekTo((dragPosition * duration).toLong())
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            val newProgress = ((change.position.x) / size.width).coerceIn(0f, 1f)
                                            dragPosition = newProgress
                                        }
                                    )
                                }
                        ) {
                            // Background track
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .align(Alignment.Center)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.Gray.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(currentProgress)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF7C3AED),
                                                    Color(0xFF06B6D4)
                                                )
                                            )
                                        )
                                )
                            }

                            // Draggable thumb
                            Box(
                                modifier = Modifier
                                    .size(1.dp)
                                    .background(Color(0xFF7C3AED), CircleShape)
                                    .align(Alignment.CenterStart)
                                    .offset(x = (currentProgress * (maxWidth - 12.dp)))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = viewModel.formatDuration(
                                    if (isDragging) (dragPosition * duration).toLong() else position
                                ),
                                color = Color.White,
                                fontSize = if (isSmallScreen) 11.sp else 12.sp
                            )
                            Text(
                                text = viewModel.formatDuration(duration),
                                color = Color.White,
                                fontSize = if (isSmallScreen) 11.sp else 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Contrôles de lecture
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isSmallScreen) 8.dp else 12.dp)
                    ) {
                        // Première ligne: Shuffle, Previous, Play/Pause, Next, Repeat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Shuffle
                            IconButton(
                                onClick = { viewModel.toggleShuffle() },
                                modifier = Modifier
                                    .size(if (isSmallScreen) 44.dp else 52.dp)
                                    .weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = if (isShuffleEnabled) Color(0xFF06B6D4) else Color.Gray,
                                    modifier = Modifier.size(if (isSmallScreen) 24.dp else 28.dp)
                                )
                            }

                            // Previous
                            IconButton(
                                onClick = { viewModel.skipToPrevious() },
                                modifier = Modifier
                                    .size(if (isSmallScreen) 48.dp else 56.dp)
                                    .weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "Previous",
                                    tint = Color.White,
                                    modifier = Modifier.size(if (isSmallScreen) 32.dp else 40.dp)
                                )
                            }

                            // Play/Pause - Central button
                            IconButton(
                                onClick = { viewModel.togglePlayPause() },
                                modifier = Modifier
                                    .size(if (isSmallScreen) 72.dp else 70.dp)
                                    .weight(1.3f)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF7C3AED),
                                                Color(0xFF06B6D4)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) {
                                        Icons.Default.Pause
                                    } else {
                                        Icons.Default.PlayArrow
                                    },
                                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(if (isSmallScreen) 40.dp else 48.dp)
                                )
                            }

                            // Next
                            IconButton(
                                onClick = { viewModel.skipToNext() },
                                modifier = Modifier
                                    .size(if (isSmallScreen) 48.dp else 56.dp)
                                    .weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(if (isSmallScreen) 32.dp else 40.dp)
                                )
                            }

                            // Repeat
                            IconButton(
                                onClick = { viewModel.toggleRepeat() },
                                modifier = Modifier
                                    .size(if (isSmallScreen) 44.dp else 52.dp)
                                    .weight(1f)
                            ) {
                                Icon(
                                    imageVector = when (repeatMode) {
                                        com.example.sonicflow.data.model.RepeatMode.ONE -> Icons.Default.RepeatOne
                                        else -> Icons.Default.Repeat
                                    },
                                    contentDescription = "Repeat",
                                    tint = when (repeatMode) {
                                        com.example.sonicflow.data.model.RepeatMode.OFF -> Color.Gray
                                        else -> Color(0xFF06B6D4)
                                    },
                                    modifier = Modifier.size(if (isSmallScreen) 24.dp else 28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Equalizer Modal
        if (showEqualizerMenu) {
            EqualizerSection(
                bands = equalizerBands,
                isEnabled = equalizerEnabled,
                onBandLevelChange = { index, level ->
                    viewModel.setEqualizerBandLevel(index, level)
                },
                onToggleEqualizer = {
                    viewModel.toggleEqualizer()
                },
                onReset = {
                    viewModel.resetEqualizer()
                },
                onDismiss = {
                    showEqualizerMenu = false
                }
            )
        }

        // Audio Settings Modal
        if (showAudioSettings) {
            AudioSettingsSection(
                currentQuality = audioQuality,
                currentSpeed = playbackSpeed,
                currentLanguage = language,
                onQualityChange = { quality ->
                    viewModel.setAudioQuality(quality)
                },
                onSpeedChange = { speed ->
                    viewModel.setPlaybackSpeed(speed)
                },
                onLanguageChange = { lang ->
                    viewModel.setLanguage(lang)
                },
                onDismiss = {
                    showAudioSettings = false
                }
            )
        }
    }
}
@Composable
fun GenreInfoChip(
    label: String,
    value: String,
    isCompact: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = if (isCompact) 11.sp else 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = if (isCompact) 12.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackContextMenu(
    track: Track,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShareTrack: () -> Unit,
    onAddToPlaylist: () -> Unit,
    isFavorite: Boolean
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // En-tête avec info de la piste
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    if (track.albumArtUri != null) {
                        AsyncImage(
                            model = track.albumArtUri,
                            contentDescription = "${track.title} album art",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = android.R.drawable.ic_media_play)
                        )
                    } else {
                        AlbumArtPlaceholder(
                            title = track.album,
                            artist = track.artist,
                            size = 56.dp,
                            cornerRadius = 8.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        track.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        track.artist,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Divider(color = Color.White.copy(alpha = 0.1f))

            // Options du menu
            MenuOption(
                icon = Icons.Default.PlayArrow,
                text = "Lire ensuite",
                onClick = onPlayNext
            )

            MenuOption(
                icon = Icons.Default.QueueMusic,
                text = "Ajouter à la file d'attente",
                onClick = onAddToQueue
            )

            MenuOption(
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                text = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                iconTint = if (isFavorite) Color(0xFFFF4444) else Color.White,
                onClick = onToggleFavorite
            )

            MenuOption(
                icon = Icons.Default.PlaylistAdd,
                text = "Ajouter à une playlist",
                onClick = onAddToPlaylist
            )

            MenuOption(
                icon = Icons.Default.Share,
                text = "Partager",
                onClick = onShareTrack
            )

            MenuOption(
                icon = Icons.Default.Album,
                text = "Voir l'album",
                onClick = onDismiss
            )

            MenuOption(
                icon = Icons.Default.Person,
                text = "Voir l'artiste",
                onClick = onDismiss
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
fun AnimatedWaveform(
    isPlaying: Boolean,
    trackId: Long,
    currentPosition: Long = 0L,
    duration: Long = 1L,
    complexity: Float = 0.5f,
    energy: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    // BoxWithConstraints pour obtenir les dimensions exactes disponibles
    BoxWithConstraints(modifier = modifier) {
        val availableWidth = constraints.maxWidth.toFloat()
        val availableHeight = constraints.maxHeight.toFloat()

        // Adapter le nombre de barres en fonction de la largeur de l'écran
        val barCount = remember(availableWidth) {
            (availableWidth / 10f).toInt().coerceIn(30, 50)
        }

        // Générer un pattern unique pour chaque chanson basé sur son ID et caractéristiques du genre
        val waveformData = remember(trackId, barCount, complexity, energy) {
            generateWaveformPattern(trackId, barCount, complexity, energy)
        }

        val infiniteTransition = rememberInfiniteTransition(label = "waveform")

        // Calculer la position de lecture relative (0 à 1)
        val progressRatio = remember(currentPosition, duration) {
            if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f
        }

        val animatedValues = waveformData.indices.map { index ->
            infiniteTransition.animateFloat(
                initialValue = if (isPlaying) 0f else 0.3f,
                targetValue = if (isPlaying) 1f else 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = waveformData[index].speed,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_$index"
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val totalBars = waveformData.size
            // Calculer dynamiquement la largeur et l'espacement des barres
            val totalSpacing = size.width * 0.6f
            val totalBarWidth = size.width * 0.4f
            val barWidth = (totalBarWidth / totalBars).coerceAtLeast(2f)
            val spacing = (totalSpacing / (totalBars - 1)).coerceAtLeast(2f)

            waveformData.forEachIndexed { index, data ->
                val animatedValue = if (isPlaying) {
                    animatedValues[index].value
                } else {
                    0.3f
                }

                // Hauteur minimale et maximale adaptées à la hauteur disponible
                val minHeight = size.height * 0.15f
                val maxHeight = size.height * 0.95f

                val barHeight = (data.height * animatedValue * maxHeight).coerceIn(
                    minHeight,
                    maxHeight
                )

                val x = index * (barWidth + spacing) + barWidth / 2 + spacing / 2

                // Déterminer si cette barre est passée ou en cours
                val barProgressRatio = index.toFloat() / totalBars
                val isBarPlayed = barProgressRatio < progressRatio
                val isNearPlayhead = kotlin.math.abs(barProgressRatio - progressRatio) < (1f / totalBars)

                // Couleur basée sur la position de lecture
                val barColor = when {
                    isNearPlayhead -> Color(0xFFFF6B35)
                    isBarPlayed -> Color(0xFFFF6600).copy(alpha = 0.9f)
                    else -> Color(0xFFFF6600).copy(alpha = 0.4f)
                }

                drawLine(
                    color = barColor,
                    start = Offset(x, size.height / 2 - barHeight / 2),
                    end = Offset(x, size.height / 2 + barHeight / 2),
                    strokeWidth = barWidth.coerceAtLeast(2f),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

data class WaveformBar(
    val height: Float,
    val speed: Int
)

fun generateWaveformPattern(
    trackId: Long,
    barCount: Int = 40,
    complexity: Float = 0.5f,
    energy: Float = 0.5f
): List<WaveformBar> {
    // Utiliser l'ID de la chanson comme seed pour générer un pattern unique
    val seed = trackId.hashCode()
    val random = Random(seed)

    val bars = mutableListOf<WaveformBar>()

    for (i in 0 until barCount) {
        // Créer un pattern qui varie mais reste cohérent pour la chanson
        // Utiliser plusieurs fréquences sinusoïdales pour un pattern plus naturel
        val wave1 = sin(i * 0.25).toFloat()
        val wave2 = sin(i * 0.5).toFloat()
        val baseHeight = (wave1 + wave2) / 2f

        val randomVariation = random.nextFloat() * 0.5f + 0.3f
        var height = ((baseHeight + 1f) / 2f * randomVariation + 0.35f).coerceIn(0.35f, 1f)

        // Ajuster la hauteur en fonction de l'énergie et de la complexité du genre
        height = height * (0.5f + energy) // Plus l'énergie est haute, plus les barres sont hautes
        height = height.coerceIn(0.35f, 1f)

        // Vitesse d'animation variée basée sur la complexité
        // Chansons complexes = animation plus rapide/variée
        // Chansons simples = animation plus lente/uniforme
        val speedBase = (350 + (complexity * 500f)).toInt()
        val speedVariation = (150 * complexity).toInt()
        val speed = random.nextInt(speedBase - speedVariation, speedBase + speedVariation)
            .coerceIn(200, 1200)

        bars.add(WaveformBar(height, speed))
    }

    return bars
}
