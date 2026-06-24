package net.eltiburon.minimax.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Roles semánticos de Material 3 construidos sobre la paleta de marca MiniMax (Color.kt).
// El valor "claro" de cada rol es exactamente la constante que ya usaban las pantallas
// (MiniMaxPrimary, Color.White, Color.Gray, etc.), así que activar este ColorScheme no
// cambia nada en modo claro; en modo oscuro cada rol resuelve a su variante *Dark.
private val LightColorScheme = lightColorScheme(
    primary = MiniMaxPrimary,
    onPrimary = Color.White,
    secondary = MiniMaxAccent,
    onSecondary = Color.White,
    tertiary = MiniMaxTeal,
    onTertiary = Color.White,
    background = MiniMaxBackground,
    onBackground = MiniMaxTextPrimary,
    surface = Color.White,
    onSurface = MiniMaxTextPrimary,
    surfaceVariant = MiniMaxProgressBg,
    onSurfaceVariant = Color.Gray,
    outline = Color(0xFFDDD8F0),
    outlineVariant = Color.LightGray,
    error = MiniMaxBadgeRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = MiniMaxPrimaryDark,
    onPrimary = MiniMaxOnPrimaryDark,
    secondary = MiniMaxAccentDark,
    onSecondary = MiniMaxOnSecondaryDark,
    tertiary = MiniMaxTealDark,
    onTertiary = MiniMaxOnTertiaryDark,
    background = MiniMaxBackgroundDark,
    onBackground = MiniMaxTextPrimaryDark,
    surface = MiniMaxSurfaceDark,
    onSurface = MiniMaxTextPrimaryDark,
    surfaceVariant = MiniMaxProgressBgDark,
    onSurfaceVariant = MiniMaxTextSecondaryDark,
    outline = MiniMaxOutlineDark,
    outlineVariant = MiniMaxOutlineVariantDark,
    error = MiniMaxBadgeRedDark,
    onError = MiniMaxOnErrorDark
)

@Composable
fun MiniMaxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Material You) está disponible desde Android 12+. Se mantiene el
    // parámetro y la rama para poder activarlo más adelante, pero por defecto queda en
    // false para que la app use siempre la identidad visual de marca MiniMax (clara u
    // oscura) en vez de los colores que Android derive del wallpaper del usuario.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Iconos de status bar/nav bar oscuros sobre fondo claro, claros sobre fondo oscuro.
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
