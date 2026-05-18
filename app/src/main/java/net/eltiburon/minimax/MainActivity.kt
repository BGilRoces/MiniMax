package net.eltiburon.minimax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import net.eltiburon.minimax.ui.OnboardingScreen
import net.eltiburon.minimax.ui.home.HomeScreen
import net.eltiburon.minimax.ui.theme.MiniMaxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniMaxTheme {
                var showHome by remember { mutableStateOf(false) }

                if (showHome) {
                    HomeScreen()
                } else {
                    OnboardingScreen(onFinish = { showHome = true })
                }
            }
        }
    }
}
