package com.cancio.membrana.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MembranaViolet,
    onPrimary = DarkOnBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = MembranaVioletLight,
    secondary = MembranaCyan,
    onSecondary = DarkBackground,
    tertiary = MembranaCoral,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant
)

@Composable
fun MembranaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

