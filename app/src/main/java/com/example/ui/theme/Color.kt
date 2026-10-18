package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Multiclonx Strict Dark Mode Color Palette
val MainBackground = Color(0xFF080B16)
val SecondaryBackground = Color(0xFF101426)
val CardBackground = Color(0xFF151B30)
val ElevatedSurface = Color(0xFF1C2340)

val PrimaryPurple = Color(0xFF7047FF)
val DeepIndigo = Color(0xFF392080)
val CyanAccent = Color(0xFF38D9F5)

val PrimaryText = Color(0xFFF8F9FF)
val SecondaryText = Color(0xFFA5AEC8)
val BorderColor = Color(0xFF29314F)

val SuccessGreen = Color(0xFF35D49A)
val WarningAmber = Color(0xFFFFBF69)
val ErrorRed = Color(0xFFFF627C)

// Navigation colors
val NavActiveColor = Color(0xFF8D70FF)
val NavInactiveColor = Color(0xFF8D96B2)

// Premium gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(PrimaryPurple, DeepIndigo)
)

val GlowPurpleGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF8A5CFF), Color(0xFF512BD4))
)

val AccentCyanGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF38D9F5), Color(0xFF7047FF))
)

val CardGlowBorder = Brush.linearGradient(
    colors = listOf(Color(0x667047FF), Color(0x2238D9F5), Color(0x3329314F))
)
