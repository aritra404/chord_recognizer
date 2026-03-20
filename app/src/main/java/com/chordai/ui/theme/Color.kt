package com.chordai.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Background = Color(0xFF07070B)
val Surface = Color(0xFF12121A)
val CardBackground = Color(0xFF1A1A24)

// Vibe Purple Palette
val VibePurple = Color(0xFF9D59FF)
val VibePurpleDark = Color(0xFF6D28D9)
val VibeGlow = Color(0x4D9D59FF)

val PrimaryGradient = Brush.verticalGradient(
    colors = listOf(VibePurple, VibePurpleDark)
)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF8E8E93)

// Status Colors
val StatusDone = Color(0xFF10B981)
val StatusProcessing = Color(0xFF9D59FF)
val StatusPending = Color(0xFF3A3A4A)
