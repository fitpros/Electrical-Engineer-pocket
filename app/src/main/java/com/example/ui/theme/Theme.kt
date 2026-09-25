package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = ElectricalBlueLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0F3159),
    onPrimaryContainer = Color(0xFFCCE4FF),
    secondary = SlateGray,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFCBD5E1),
    tertiary = ElectricalBlueCyan,
    onTertiary = Color.Black,
    background = DeepNavyDark,
    onBackground = Color(0xFFF1F5F9),
    surface = DeepNavy,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = NavyCardDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = NavyCardBorderDark,
    error = StatusCriticalRedDark,
    onError = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = ElectricalBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0EDFF),
    onPrimaryContainer = Color(0xFF003875),
    secondary = SteelGray,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = ElectricalBlueLight,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = SurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = SurfaceLightSecondary,
    onSurfaceVariant = Color(0xFF475569),
    outline = OutlineLight,
    error = StatusCriticalRed,
    onError = Color.White
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

@Composable
fun ElectricalEngineerProTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
