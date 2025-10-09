package com.zero.flow.presentation.theme


import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============= COLORS =============

// Light Theme Colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Custom Brand Colors
val FlowPrimary = Color(0xFF667EEA)
val FlowSecondary = Color(0xFF764BA2)
val FlowTertiary = Color(0xFFf093fb)

val LightColorScheme = lightColorScheme(
    primary = FlowPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EAFF),
    onPrimaryContainer = Color(0xFF1A1B4B),

    secondary = FlowSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD8E4),
    onSecondaryContainer = Color(0xFF31111D),

    tertiary = FlowTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9E2),
    onTertiaryContainer = Color(0xFF31101D),

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),

    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFAFC6FF),
    onPrimary = Color(0xFF002E6C),
    primaryContainer = Color(0xFF004399),
    onPrimaryContainer = Color(0xFFD8E2FF),

    secondary = Color(0xFFBBC2DC),
    onSecondary = Color(0xFF253047),
    secondaryContainer = Color(0xFF3B475E),
    onSecondaryContainer = Color(0xFFD7DEF9),

    tertiary = Color(0xFFDDBCE0),
    onTertiary = Color(0xFF3F2844),
    tertiaryContainer = Color(0xFF563E5C),
    onTertiaryContainer = Color(0xFFF9D8FD),

    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),

    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),

    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F)
)