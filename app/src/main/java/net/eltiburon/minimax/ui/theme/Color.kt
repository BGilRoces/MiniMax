package net.eltiburon.minimax.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val PrimaryPurple = Color(0xFF4A24B2)
val DarkPurple = Color(0xFF0F044A)
val GrayishPurple = Color(0xFF4B4A62)
val White = Color(0xFFFFFFFF)

// MiniMax palette (modo claro)
val MiniMaxPrimary = Color(0xFF3D1D8A)
val MiniMaxAccent = Color(0xFF6B3FA0)
val MiniMaxTeal = Color(0xFF0D9488)
val MiniMaxBackground = Color(0xFFF8F7FF)
val MiniMaxTextPrimary = Color(0xFF1A1A2E)
val MiniMaxBadgeRed = Color(0xFFEF4444)
val MiniMaxProgressBg = Color(0xFFE5E7EB)
val MiniMaxOrange = Color(0xFFF59E0B)

// MiniMax palette (modo oscuro) — mismos roles que la paleta clara, ajustados para fondo
// oscuro (tonos más claros para los "accent" y fondos/superficies oscuros para el resto).
// Usadas únicamente desde Theme.kt al construir el DarkColorScheme; las pantallas siguen
// leyendo MaterialTheme.colorScheme, nunca estas constantes directamente.
val MiniMaxPrimaryDark = Color(0xFFA78BFA)
val MiniMaxOnPrimaryDark = Color(0xFF1E1340)
val MiniMaxAccentDark = Color(0xFFD8B4F8)
val MiniMaxOnSecondaryDark = Color(0xFF2E1750)
val MiniMaxTealDark = Color(0xFF5EEAD4)
val MiniMaxOnTertiaryDark = Color(0xFF003733)
val MiniMaxBackgroundDark = Color(0xFF14121F)
val MiniMaxSurfaceDark = Color(0xFF1E1B2E)
val MiniMaxTextPrimaryDark = Color(0xFFEDEBF7)
val MiniMaxTextSecondaryDark = Color(0xFFB6B2C9)
val MiniMaxProgressBgDark = Color(0xFF332F47)
val MiniMaxBadgeRedDark = Color(0xFFFF8A80)
val MiniMaxOnErrorDark = Color(0xFF410002)
val MiniMaxOutlineDark = Color(0xFF4A4560)
val MiniMaxOutlineVariantDark = Color(0xFF3A3650)

// Variante oscura exclusiva de la paleta de Onboarding (PrimaryPurple/DarkPurple/
// GrayishPurple de abajo). El resto de los tonos oscuros de Onboarding reutilizan los
// MiniMax*Dark de arriba (no hace falta duplicarlos: en modo oscuro no hay que preservar
// ningún aspecto "actual", solo que se vea bien y consistente con el resto de la app).
val OnboardingPrimaryDark = Color(0xFFB7A1F2)
