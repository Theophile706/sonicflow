package com.example.sonicflow.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin
import kotlin.math.cos
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────────────────────
//  WAVEFORM VISUALIZER — barres horizontales animées
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WaveformVisualizer(
    currentPosition: Long,
    duration: Long,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF00FF00),
    barWidth: Float = 6f,
    barSpacing: Float = 4f,
    numberOfBars: Int = 50
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_transition")

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    val barHeights = remember {
        FloatArray(numberOfBars) {
            Random.nextFloat() * 0.7f + 0.3f
        }
    }

    val animatedBarHeights = barHeights.mapIndexed { index, baseHeight ->
        val progressRatio = if (duration > 0) (currentPosition.toFloat() / duration) else 0f
        val distanceFromPlayhead = kotlin.math.abs(index.toFloat() / numberOfBars - progressRatio)

        if (isPlaying) {
            val sineWave = (sin((index * 0.3f + waveOffset * 0.05f)) + 1) / 2
            val cosWave = (cos((index * 0.2f + waveOffset * 0.03f)) + 1) / 2
            val distanceEffect = kotlin.math.max(0f, 1f - distanceFromPlayhead * 3f)
            (baseHeight * 0.4f + sineWave * 0.35f + cosWave * 0.15f + distanceEffect * 0.3f)
                .coerceIn(0.2f, 1f)
        } else {
            val pulse = (sin(waveOffset * 0.03f + index * 0.1f) + 1) / 3
            (baseHeight * 0.5f + pulse * 0.3f).coerceIn(0.2f, 0.6f)
        }
    }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val totalBarWidth = numberOfBars * (barWidth + barSpacing)
        val startX = (canvasWidth - totalBarWidth) / 2

        val progressRatio = if (duration > 0) (currentPosition.toFloat() / duration) else 0f

        for (i in 0 until numberOfBars) {
            val x = startX + i * (barWidth + barSpacing)
            val barHeight = animatedBarHeights[i] * canvasHeight * 0.85f
            val y = (canvasHeight - barHeight) / 2

            val barProgressRatio = i.toFloat() / numberOfBars

            val color = if (barProgressRatio < progressRatio) {
                barColor.copy(alpha = 1f)
            } else {
                barColor.copy(alpha = 0.3f)
            }

            drawLine(
                color = color,
                start = Offset(x + barWidth / 2, y),
                end = Offset(x + barWidth / 2, y + barHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )

            if (barProgressRatio < progressRatio && isPlaying) {
                drawCircle(
                    color = barColor.copy(alpha = 0.6f),
                    radius = barWidth * 0.8f,
                    center = Offset(x + barWidth / 2, y)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  SIMPLE WAVEFORM BAR — barre de progression avec shimmer
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SimpleWaveformBar(
    currentPosition: Long,
    duration: Long,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF00FF00)
) {
    val progressRatio = if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f

    val infiniteTransition = rememberInfiniteTransition(label = "progress_transition")

    val shimmerOffset by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_animation"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            color = barColor.copy(alpha = 0.15f),
            size = Size(canvasWidth, canvasHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(canvasHeight / 2)
        )

        val progressWidth = canvasWidth * progressRatio
        if (progressWidth > 0) {
            drawRoundRect(
                color = barColor.copy(alpha = 0.9f),
                size = Size(progressWidth, canvasHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(canvasHeight / 2)
            )
        }

        if (isPlaying && progressWidth > 0) {
            val shimmerX = progressWidth * ((shimmerOffset + 1f) / 2f)
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = canvasHeight * 1.5f,
                center = Offset(shimmerX, canvasHeight / 2)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  CIRCULAR WAVEFORM — rotation sur elle-même
//  FIX : key(isPlaying) force le redémarrage de l'infiniteTransition
//        quand isPlaying change, ce qui garantit que la nouvelle
//        durationMillis est bien prise en compte immédiatement.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CircularWaveform(
    currentPosition: Long,
    duration: Long,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF00FF00),
    numberOfBars: Int = 60
) {
    // key(isPlaying) : recrée l'infiniteTransition à chaque changement d'état
    // → la vitesse de rotation est toujours cohérente avec isPlaying
    key(isPlaying) {
        val infiniteTransition = rememberInfiniteTransition(label = "circular_transition")

        // Rotation continue — le cercle tourne sur lui-même
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = if (isPlaying) 8000 else 20000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        // Pulsation des barres
        val pulse by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = if (isPlaying) 1500 else 3000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulse"
        )

        // Effet de respiration sur le rayon
        val breathe by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathe"
        )

        Canvas(modifier = modifier) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val baseRadius = kotlin.math.min(centerX, centerY) * 0.7f

            // Le rayon "respire" uniquement quand en lecture
            val radius = baseRadius * if (isPlaying) breathe else 1f

            for (i in 0 until numberOfBars) {
                // Chaque barre intègre la rotation → le cercle tourne sur lui-même
                val angle = (i.toFloat() / numberOfBars * 360f + rotation)
                val radians = Math.toRadians(angle.toDouble())

                val barHeight = if (isPlaying) {
                    val pulseEffect = (sin(pulse * 0.05f + i * 0.2f) + 1) / 2
                    pulseEffect * radius * 0.35f + radius * 0.1f
                } else {
                    val staticPulse = (sin(pulse * 0.02f + i * 0.15f) + 1) / 2
                    staticPulse * radius * 0.2f + radius * 0.05f
                }

                val startX = centerX + (radius - barHeight) * cos(radians).toFloat()
                val startY = centerY + (radius - barHeight) * sin(radians).toFloat()
                val endX   = centerX + radius * cos(radians).toFloat()
                val endY   = centerY + radius * sin(radians).toFloat()

                val progressRatio    = if (duration > 0) (currentPosition.toFloat() / duration) else 0f
                val barProgressRatio = i.toFloat() / numberOfBars

                val color = if (barProgressRatio < progressRatio) {
                    barColor.copy(alpha = 1f)
                } else {
                    barColor.copy(alpha = 0.25f)
                }

                drawLine(
                    color = color,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (isPlaying) 5f else 3f,
                    cap = StrokeCap.Round
                )

                // Glow sur les barres actives
                if (barProgressRatio < progressRatio && isPlaying) {
                    drawCircle(
                        color = barColor.copy(alpha = 0.4f),
                        radius = 3f,
                        center = Offset(endX, endY)
                    )
                }
            }

            // Cercle central décoratif
            drawCircle(
                color = barColor.copy(alpha = 0.1f),
                radius = radius * 0.3f,
                center = Offset(centerX, centerY)
            )

            // Point lumineux central quand en lecture
            if (isPlaying) {
                drawCircle(
                    color = barColor.copy(alpha = 0.8f),
                    radius = radius * 0.05f * breathe,
                    center = Offset(centerX, centerY)
                )
            }
        }
    }
}