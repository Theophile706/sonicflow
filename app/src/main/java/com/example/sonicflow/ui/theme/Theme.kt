package com.example.sonicflow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = Color.White,
    primaryContainer = AccentPrimaryDark,
    onPrimaryContainer = TextPrimary,

    secondary = AccentSecondary,
    onSecondary = Color.White,
    secondaryContainer = AccentSecondaryDark,
    onSecondaryContainer = Color.White,

    tertiary = AccentSecondaryLight,
    onTertiary = TextBlack,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,

    surfaceVariant = DarkInput,
    onSurfaceVariant = TextSecondary,

    error = ErrorColor,
    onError = Color.White,

    outline = TextTertiary,
    outlineVariant = Color(0xFF3A3A3A),

    inverseSurface = TextPrimary,
    inverseOnSurface = DarkBackground,
    inversePrimary = AccentPrimaryLight
)

private val LightColorScheme = lightColorScheme(
    primary = AccentPrimary,
    onPrimary = Color.White,
    primaryContainer = AccentPrimaryLight,
    onPrimaryContainer = AccentPrimaryDark,

    secondary = AccentSecondary,
    onSecondary = Color.White,
    secondaryContainer = AccentSecondaryLight,
    onSecondaryContainer = AccentSecondaryDark,

    tertiary = AccentSecondaryDark,
    onTertiary = Color.White,

    background = LightBackground,
    onBackground = TextBlack,

    surface = LightSurface,
    onSurface = TextBlack,

    surfaceVariant = LightInput,
    onSurfaceVariant = TextGray,

    error = ErrorColor,
    onError = Color.White,

    outline = Color(0xFFD0D0D0),
    outlineVariant = Color(0xFFE5E5E5),

    inverseSurface = TextBlack,
    inverseOnSurface = LightBackground,
    inversePrimary = AccentPrimary
)

@Composable
fun SonicFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color est disponible sur Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
