package com.tertiaryinfotech.aiexams.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand palette ported from the iOS Theme.swift.
val BrandPrimary = Color(0xFF0D5CEB)   // RGB(0.05, 0.36, 0.92)
val BrandSecondary = Color(0xFF008C94) // RGB(0.00, 0.55, 0.58)
val BrandHighlight = Color(0xFFEDA321) // RGB(0.93, 0.64, 0.13)

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    secondary = BrandSecondary,
    tertiary = BrandHighlight,
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEAEDF2),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6F9DFF),
    secondary = Color(0xFF4DBFC6),
    tertiary = BrandHighlight,
    background = Color(0xFF101114),
    surface = Color(0xFF1C1D22),
)

@Composable
fun AIExamsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}
