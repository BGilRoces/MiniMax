package net.eltiburon.minimax.ui.onboarding.components

import androidx.compose.foundation.background
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
import net.eltiburon.minimax.ui.theme.PrimaryPurple

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
