package net.eltiburon.minimax.ui.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.ui.theme.DarkPurple
import net.eltiburon.minimax.ui.theme.PrimaryPurple

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
