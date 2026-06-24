package net.eltiburon.minimax.ui.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.R
import net.eltiburon.minimax.ui.onboarding.components.StepItem
import net.eltiburon.minimax.ui.theme.DarkPurple
import net.eltiburon.minimax.ui.theme.GrayishPurple
import net.eltiburon.minimax.ui.theme.MiniMaxOnPrimaryDark
import net.eltiburon.minimax.ui.theme.MiniMaxProgressBgDark
import net.eltiburon.minimax.ui.theme.MiniMaxSurfaceDark
import net.eltiburon.minimax.ui.theme.MiniMaxTextPrimaryDark
import net.eltiburon.minimax.ui.theme.MiniMaxTextSecondaryDark
import net.eltiburon.minimax.ui.theme.OnboardingPrimaryDark
import net.eltiburon.minimax.ui.theme.PrimaryPurple

@Composable
fun OnboardingPageTwo() {
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) MiniMaxSurfaceDark else Color.White
    val titleColor = if (isDark) MiniMaxTextPrimaryDark else DarkPurple
    val captionColor = if (isDark) MiniMaxTextSecondaryDark else GrayishPurple
    val accentColor = if (isDark) OnboardingPrimaryDark else PrimaryPurple
    val onAccentColor = if (isDark) MiniMaxOnPrimaryDark else Color.White
    val chipBgColor = if (isDark) MiniMaxProgressBgDark else Color(0xFFF1F1FF)
    val trackColor = if (isDark) MiniMaxProgressBgDark else Color(0xFFEEEEEE)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Image(
                        painter = painterResource(id = R.drawable.aceite),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Caja de Aceite\nde Oliva Premium",
                            color = titleColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = chipBgColor,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "$34.000 / caja",
                                color = accentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Surface(
                        color = accentColor,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Grupo activo",
                            color = onAccentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Progreso del grupo",
                        color = titleColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "80%",
                        color = titleColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { 0.8f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = accentColor,
                    trackColor = trackColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Faltan 4 cajas para alcanzar el mínimo",
                    color = captionColor,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Steps Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                StepItem(icon = Icons.AutoMirrored.Filled.Assignment, text = "Elegí una oportunidad")
                Spacer(modifier = Modifier.height(16.dp))
                StepItem(icon = Icons.Default.Inventory2, text = "Definí cantidad")
                Spacer(modifier = Modifier.height(16.dp))
                StepItem(icon = Icons.Default.BarChart, text = "Seguí el progreso")
            }
        }
    }
}
