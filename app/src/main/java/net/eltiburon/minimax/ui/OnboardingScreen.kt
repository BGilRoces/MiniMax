package net.eltiburon.minimax.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.R
import net.eltiburon.minimax.ui.theme.*

@Composable
fun OnboardingScreen() {
    var currentPage by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo and Page Indicator
        OnboardingHeader(currentPage = currentPage)

        // Content Area
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (currentPage) {
                1 -> OnboardingContentOne()
                2 -> OnboardingContentTwo()
                3 -> OnboardingContentThree()
            }
        }

        // Bottom Text and Button
        OnboardingFooter(
            currentPage = currentPage,
            onNextClick = {
                if (currentPage < 3) {
                    currentPage++
                } else {
                    // TODO: Handle completion or next flow
                }
            }
        )
    }
}

@Composable
fun OnboardingHeader(currentPage: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "M",
                color = PrimaryPurple,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MiniMax",
                color = DarkPurple,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Page Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IndicatorCircle(pageNumber = 1, isActive = currentPage == 1)
            Spacer(modifier = Modifier.width(8.dp))
            IndicatorCircle(pageNumber = 2, isActive = currentPage == 2)
            Spacer(modifier = Modifier.width(8.dp))
            IndicatorCircle(pageNumber = 3, isActive = currentPage == 3)
        }
    }
}

@Composable
fun IndicatorCircle(pageNumber: Int, isActive: Boolean) {
    if (isActive) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = pageNumber.toString(), color = Color.White, fontSize = 14.sp)
        }
    } else {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(0xFFE0E0E0), CircleShape)
        )
    }
}

@Composable
fun OnboardingContentOne() {
    Image(
        painter = painterResource(id = R.drawable.onboarding_1),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    )
}

@Composable
fun OnboardingContentTwo() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            color = DarkPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFF1F1FF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "$34.000 / caja",
                                color = PrimaryPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Surface(
                        color = Color(0xFF8E76F7),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Grupo activo",
                            color = Color.White,
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
                        color = DarkPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "80%",
                        color = DarkPurple,
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
                    color = PrimaryPurple,
                    trackColor = Color(0xFFEEEEEE)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Faltan 4 cajas para alcanzar el mínimo",
                    color = GrayishPurple,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Steps Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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

@Composable
fun OnboardingContentThree() {
    Image(
        painter = painterResource(id = R.drawable.onboarding_2),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    )
}

@Composable
fun StepItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            color = PrimaryPurple,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = DarkPurple,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun OnboardingFooter(currentPage: Int, onNextClick: () -> Unit) {
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
            color = DarkPurple,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = subtitle,
            color = GrayishPurple,
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
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MiniMaxTheme {
        OnboardingScreen()
    }
}
