package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkSlateColorScheme = darkColorScheme(
  primary = Emerald400,
  onPrimary = Slate950,
  primaryContainer = EmeraldBg,
  onPrimaryContainer = Emerald400,
  secondary = Indigo400,
  onSecondary = Slate950,
  secondaryContainer = IndigoBg,
  onSecondaryContainer = Indigo400,
  tertiary = Amber400,
  onTertiary = Slate950,
  tertiaryContainer = AmberBg,
  onTertiaryContainer = Amber400,
  background = Slate950,
  onBackground = Slate100,
  surface = Slate900,
  onSurface = Slate100,
  surfaceVariant = Slate850,
  onSurfaceVariant = Slate300,
  outline = Slate800,
  outlineVariant = Slate700,
  error = Rose400,
  onError = Slate950,
  errorContainer = RoseBg,
  onErrorContainer = Rose400
)

@Composable
fun AlignOSTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkSlateColorScheme,
    typography = Typography,
    content = content
  )
}
