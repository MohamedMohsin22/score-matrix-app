package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremierLeagueColorScheme = darkColorScheme(
    primary = PlMint,
    onPrimary = PlPlumBackground,
    primaryContainer = PlPlumCardElevated,
    onPrimaryContainer = PlMint,
    secondary = PlCyan,
    onSecondary = PlPlumBackground,
    secondaryContainer = PlPlumCard,
    onSecondaryContainer = PlCyan,
    tertiary = PlPink,
    onTertiary = Color.White,
    background = PlPlumBackground,
    onBackground = PlTextPrimary,
    surface = PlPlumSurface,
    onSurface = PlTextPrimary,
    surfaceVariant = PlPlumCard,
    onSurfaceVariant = PlTextSecondary,
    outline = PlPlumBorder,
    outlineVariant = PlPlumCardElevated,
    error = PlError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Enforce Premier League Signature palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PremierLeagueColorScheme,
        typography = Typography,
        content = content
    )
}
