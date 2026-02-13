package com.example.sonicflow.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sonicflow.data.preferences.AudioQuality
import com.example.sonicflow.data.preferences.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSettingsSection(
    currentQuality: AudioQuality,
    currentSpeed: Float,
    currentLanguage: Language = Language.FRENCH,
    onQualityChange: (AudioQuality) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onLanguageChange: (Language) -> Unit = {},
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paramètres audio",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Quality Section
            Text(
                text = "Qualité audio",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF252525),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                AudioQuality.values().forEach { quality ->
                    QualityOptionItem(
                        quality = quality,
                        isSelected = currentQuality == quality,
                        onClick = { onQualityChange(quality) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Playback Speed Section
            Text(
                text = "Vitesse de lecture",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                SpeedSliderControl(
                    currentSpeed = currentSpeed,
                    onSpeedChange = onSpeedChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick preset buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        SpeedPresetButton(
                            speed = speed,
                            isSelected = currentSpeed == speed,
                            onClick = { onSpeedChange(speed) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QualityOptionItem(
    quality: AudioQuality,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = quality.displayName,
            color = Color.White,
            fontSize = 14.sp
        )
        if (isSelected) {
            Surface(
                color = Color(0xFF7C3AED),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun SpeedSliderControl(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF252525),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Vitesse",
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = String.format("%.2fx", currentSpeed),
                color = Color(0xFF06B6D4),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = currentSpeed,
            onValueChange = onSpeedChange,
            valueRange = 0.5f..2.0f,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF7C3AED),
                activeTrackColor = Color(0xFF06B6D4),
                inactiveTrackColor = Color(0xFF333333)
            )
        )
    }
}

@Composable
fun SpeedPresetButton(
    speed: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF7C3AED) else Color(0xFF252525)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = String.format("%.2fx", speed),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
