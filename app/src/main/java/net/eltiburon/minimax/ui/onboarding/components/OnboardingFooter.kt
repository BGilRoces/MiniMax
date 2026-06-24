package net.eltiburon.minimax.ui.onboarding.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.ui.theme.DarkPurple
import net.eltiburon.minimax.ui.theme.GrayishPurple
import net.eltiburon.minimax.ui.theme.MiniMaxOnPrimaryDark
import net.eltiburon.minimax.ui.theme.MiniMaxTextPrimaryDark
import net.eltiburon.minimax.ui.theme.MiniMaxTextSecondaryDark
import net.eltiburon.minimax.ui.theme.OnboardingPrimaryDark
import net.eltiburon.minimax.ui.theme.PrimaryPurple

@Composable
fun OnboardingFooter(currentPage: Int, onNextClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val titleColor = if (isDark) MiniMaxTextPrimaryDark else DarkPurple
    val subtitleColor = if (isDark) MiniMaxTextSecondaryDark else GrayishPurple
    val buttonColor = if (isDark) OnboardingPrimaryDark else PrimaryPurple
    val buttonContentColor = if (isDark) MiniMaxOnPrimaryDark else Color.White

    val title = when (currentPage) {
        1 -> "Comprá como\nlos grandes"
        2 -> "Sumate a\ncompras grupales"
        else -> "Pagá menos y\nganá más"
    }

    val subtitle = when (currentPage) {
        1 -> "Unite a otros compradores para\nalcanzar mínimos mayoristas."
        2 -> "Elegí una oportunidad, definí cantidad\ny seguí el proceso del grupo"
        else -> "Cuando el grupo alcanza\nel mínimo, la compra se confirma"
    }

    val buttonText = if (currentPage == 3) "Comenzar" else "Siguiente"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = titleColor,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = subtitle,
            color = subtitleColor,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = buttonText,
                    color = buttonContentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = buttonContentColor
                )
            }
        }
    }
}
