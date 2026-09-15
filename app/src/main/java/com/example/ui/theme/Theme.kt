package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Tema principal de Pocket Engine.
 * Diseñado con una paleta moderna de estudio de desarrollo de videojuegos,
 * con alto contraste, bordes definidos y colores vibrantes para categorías de bloques.
 */
private val StudioDarkColorScheme = darkColorScheme(
    primary = StudioPrimary,
    onPrimary = Color.Black,
    primaryContainer = StudioPrimaryContainer,
    onPrimaryContainer = StudioTextPrimary,
    secondary = StudioSecondary,
    onSecondary = Color.White,
    secondaryContainer = StudioSecondaryContainer,
    onSecondaryContainer = StudioTextPrimary,
    tertiary = StudioTertiary,
    onTertiary = Color.Black,
    background = StudioDarkBg,
    onBackground = StudioTextPrimary,
    surface = StudioSurface,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioBorder,
)

private val StudioLightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF7B1FA2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3DAFF),
    onSecondaryContainer = Color(0xFF2E004E),
    tertiary = Color(0xFF2E7D32),
    onTertiary = Color.White,
    background = Color(0xFFF4F6F9),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE5E9F0),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFFC5CCD6),
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent studio styling for game engine
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) StudioDarkColorScheme else StudioDarkColorScheme // Engine defaults to dark studio UI

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
