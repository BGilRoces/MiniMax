package net.eltiburon.minimax.ui.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.eltiburon.minimax.R

@Composable
fun OnboardingPageThree() {
    Image(
        painter = painterResource(id = R.drawable.onboarding_2),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    )
}
