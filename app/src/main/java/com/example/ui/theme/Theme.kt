package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MulticlonxDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = PrimaryText,
    primaryContainer = DeepIndigo,
    onPrimaryContainer = PrimaryText,
    secondary = CyanAccent,
    onSecondary = MainBackground,
    secondaryContainer = ElevatedSurface,
    onSecondaryContainer = PrimaryText,
    tertiary = CyanAccent,
    onTertiary = MainBackground,
    background = MainBackground,
    onBackground = PrimaryText,
    surface = CardBackground,
    onSurface = PrimaryText,
    surfaceVariant = ElevatedSurface,
    onSurfaceVariant = SecondaryText,
    outline = BorderColor,
    error = ErrorRed,
    onError = PrimaryText
)

@Composable
fun MulticlonxTheme(
    content: @Composable () -> Unit
) {
    // Dark mode only, as strictly required by specification
    MaterialTheme(
        colorScheme = MulticlonxDarkColorScheme,
        typography = Typography,
        content = content
    )
}
