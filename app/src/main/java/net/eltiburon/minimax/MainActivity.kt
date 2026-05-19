package net.eltiburon.minimax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import net.eltiburon.minimax.ui.OnboardingScreen
import net.eltiburon.minimax.ui.explorar.ExplorarGruposScreen
import net.eltiburon.minimax.ui.grupodetalle.GrupoDetalleScreen
import net.eltiburon.minimax.ui.home.HomeScreen
import net.eltiburon.minimax.ui.perfil.MiPerfilScreen
import net.eltiburon.minimax.ui.theme.MiniMaxTheme

sealed class AppScreen {
    object Onboarding : AppScreen()
    object Home : AppScreen()
    object ExplorarGrupos : AppScreen()
    object MiPerfil : AppScreen()
    data class GrupoDetalle(val grupoId: String) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniMaxTheme {
                var screen by remember { mutableStateOf<AppScreen>(AppScreen.Onboarding) }

                when (val current = screen) {
                    is AppScreen.Onboarding -> OnboardingScreen(
                        onFinish = { screen = AppScreen.Home }
                    )
                    is AppScreen.Home -> HomeScreen(
                        onGrupoClick = { id -> screen = AppScreen.GrupoDetalle(id) },
                        onGruposClick = { screen = AppScreen.ExplorarGrupos },
                        onPerfilClick = { screen = AppScreen.MiPerfil },
                        onCerrarSesion = { screen = AppScreen.Onboarding }
                    )
                    is AppScreen.ExplorarGrupos -> ExplorarGruposScreen(
                        onGrupoClick = { id -> screen = AppScreen.GrupoDetalle(id) },
                        onHomeClick = { screen = AppScreen.Home },
                        onPerfilClick = { screen = AppScreen.MiPerfil }
                    )
                    is AppScreen.GrupoDetalle -> GrupoDetalleScreen(
                        grupoId = current.grupoId,
                        onBack = { screen = AppScreen.Home }
                    )
                    is AppScreen.MiPerfil -> MiPerfilScreen(
                        onBack = { screen = AppScreen.Home }
                    )
                }
            }
        }
    }
}
