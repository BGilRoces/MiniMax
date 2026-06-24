package net.eltiburon.minimax.ui.onboarding.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.ui.theme.DarkPurple
import net.eltiburon.minimax.ui.theme.MiniMaxOnPrimaryDark
import net.eltiburon.minimax.ui.theme.MiniMaxTextPrimaryDark
import net.eltiburon.minimax.ui.theme.OnboardingPrimaryDark
import net.eltiburon.minimax.ui.theme.PrimaryPurple

@Composable
fun StepItem(icon: ImageVector, text: String) {
    val isDark = isSystemInDarkTheme()
    val circleColor = if (isDark) OnboardingPrimaryDark else PrimaryPurple
    val iconColor = if (isDark) MiniMaxOnPrimaryDark else Color.White
    val textColor = if (isDark) MiniMaxTextPrimaryDark else DarkPurple

    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            color = circleColor,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
