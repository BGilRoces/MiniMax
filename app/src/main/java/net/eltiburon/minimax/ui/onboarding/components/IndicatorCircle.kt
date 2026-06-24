package net.eltiburon.minimax.ui.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.ui.theme.MiniMaxOnPrimaryDark
import net.eltiburon.minimax.ui.theme.MiniMaxOutlineVariantDark
import net.eltiburon.minimax.ui.theme.OnboardingPrimaryDark
import net.eltiburon.minimax.ui.theme.PrimaryPurple

// La paleta de Onboarding (PrimaryPurple/DarkPurple/GrayishPurple) es propia, no la de
// marca MiniMax, así que no responde a MaterialTheme.colorScheme: en modo claro se ve
// exactamente igual que antes, y en modo oscuro resuelve a mano a su variante *Dark.
@Composable
fun IndicatorCircle(pageNumber: Int, isActive: Boolean) {
    val isDark = isSystemInDarkTheme()
    if (isActive) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(if (isDark) OnboardingPrimaryDark else PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pageNumber.toString(),
                color = if (isDark) MiniMaxOnPrimaryDark else Color.White,
                fontSize = 14.sp
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(if (isDark) MiniMaxOutlineVariantDark else Color(0xFFE0E0E0), CircleShape)
        )
    }
}
