package net.eltiburon.minimax.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.eltiburon.minimax.ui.onboarding.components.OnboardingFooter
import net.eltiburon.minimax.ui.onboarding.components.OnboardingHeader
import net.eltiburon.minimax.ui.onboarding.pages.OnboardingPageOne
import net.eltiburon.minimax.ui.onboarding.pages.OnboardingPageTwo
import net.eltiburon.minimax.ui.onboarding.pages.OnboardingPageThree
import net.eltiburon.minimax.ui.theme.*

@Composable
fun OnboardingScreen(onFinish: () -> Unit = {}) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo e indicadores de página
        OnboardingHeader(currentPage = pagerState.currentPage + 1)

        // Contenido deslizable (Pager) para mejor UX y rendimiento
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            when (page) {
                0 -> OnboardingPageOne()
                1 -> OnboardingPageTwo()
                2 -> OnboardingPageThree()
            }
        }

        // Textos dinámicos y botón de navegación
        OnboardingFooter(
            currentPage = pagerState.currentPage + 1,
            onNextClick = {
                if (pagerState.currentPage < 2) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onFinish()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MiniMaxTheme {
        OnboardingScreen()
    }
}
