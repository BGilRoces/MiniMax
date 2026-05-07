package net.eltiburon.minimax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import net.eltiburon.minimax.ui.OnboardingScreen
import net.eltiburon.minimax.ui.theme.MiniMaxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniMaxTheme {
                OnboardingScreen()
            }
        }
    }
}
