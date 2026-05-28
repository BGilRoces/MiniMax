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
import net.eltiburon.minimax.ui.proveedor.DashboardProveedorScreen
import net.eltiburon.minimax.ui.confirmarparticipacion.ConfirmarParticipacionScreen
import net.eltiburon.minimax.ui.elegircantidad.ElegirCantidadScreen
import net.eltiburon.minimax.ui.proveedor.NuevaOportunidadScreen
import net.eltiburon.minimax.ui.confirmacionparticipacion.ConfirmacionParticipacionScreen
import net.eltiburon.minimax.ui.seleccionrol.SeleccionRolScreen
import net.eltiburon.minimax.ui.theme.MiniMaxTheme

sealed class AppScreen {
    object Onboarding         : AppScreen()
    object SeleccionRol       : AppScreen()   // nueva: elegir comprador o proveedor
    object Home               : AppScreen()   // dashboard comprador
    object DashboardProveedor : AppScreen()   // dashboard proveedor
    object ExplorarGrupos     : AppScreen()
    object MiPerfil           : AppScreen()
    object NuevaOportunidad   : AppScreen()
    data class GrupoDetalle(val grupoId: String) : AppScreen()
    data class ElegirCantidad(val grupoId: String) : AppScreen()
    data class ConfirmarParticipacion(val grupoId: String, val cantidadSeleccionada: Int) : AppScreen()
    data class ConfirmacionParticipacion(val grupoId: String, val cantidadSeleccionada: Int) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniMaxTheme {
                var screen by remember { mutableStateOf<AppScreen>(AppScreen.Onboarding) }

                when (val current = screen) {

                    // ── Onboarding → selección de rol ───────────────────────
                    is AppScreen.Onboarding -> OnboardingScreen(
                        onFinish = { screen = AppScreen.SeleccionRol }
                    )

                    // ── Selección de rol ─────────────────────────────────────
                    is AppScreen.SeleccionRol -> SeleccionRolScreen(
                        onCompradorClick = { screen = AppScreen.Home },
                        onProveedorClick = { screen = AppScreen.DashboardProveedor }
                    )

                    // ── Dashboard Comprador ──────────────────────────────────
                    is AppScreen.Home -> HomeScreen(
                        onGrupoClick  = { id -> screen = AppScreen.GrupoDetalle(id) },
                        onGruposClick = { screen = AppScreen.ExplorarGrupos },
                        onPerfilClick = { screen = AppScreen.MiPerfil },
                        onCerrarSesion = { screen = AppScreen.Onboarding }
                    )

                    // ── Dashboard Proveedor ──────────────────────────────────
                    is AppScreen.DashboardProveedor -> DashboardProveedorScreen(
                        onNuevaOportunidadClick = { screen = AppScreen.NuevaOportunidad }
                    )

                    is AppScreen.NuevaOportunidad -> NuevaOportunidadScreen(
                        onBackClick    = { screen = AppScreen.DashboardProveedor },
                        onPublicadoOk  = { screen = AppScreen.DashboardProveedor }
                    )

                    // ── Explorar grupos ──────────────────────────────────────
                    is AppScreen.ExplorarGrupos -> ExplorarGruposScreen(
                        onGrupoClick  = { id -> screen = AppScreen.GrupoDetalle(id) },
                        onHomeClick   = { screen = AppScreen.Home },
                        onPerfilClick = { screen = AppScreen.MiPerfil }
                    )

                    // ── Detalle de grupo ─────────────────────────────────────
                    is AppScreen.GrupoDetalle -> GrupoDetalleScreen(
                        grupoId        = current.grupoId,
                        onBack         = { screen = AppScreen.Home },
                        onSumarseClick = { screen = AppScreen.ElegirCantidad(current.grupoId) }
                    )

                    is AppScreen.ElegirCantidad -> ElegirCantidadScreen(
                        onBackClick      = { screen = AppScreen.GrupoDetalle(current.grupoId) },
                        onContinuarClick = { cantidad ->
                            screen = AppScreen.ConfirmarParticipacion(current.grupoId, cantidad)
                        }
                    )

                    is AppScreen.ConfirmarParticipacion -> ConfirmarParticipacionScreen(
                        cantidadSeleccionada = current.cantidadSeleccionada,
                        onBackClick          = { screen = AppScreen.ElegirCantidad(current.grupoId) },
                        onConfirmarClick     = { screen = AppScreen.ConfirmacionParticipacion(current.grupoId, current.cantidadSeleccionada) }
                    )

                    is AppScreen.ConfirmacionParticipacion -> ConfirmacionParticipacionScreen(
                        cantidadSeleccionada  = current.cantidadSeleccionada,
                        onBackClick           = { screen = AppScreen.ConfirmarParticipacion(current.grupoId, current.cantidadSeleccionada) },
                        onVerMisComprasClick  = { screen = AppScreen.Home },
                        onVolverInicioClick   = { screen = AppScreen.Home }
                    )

                    // ── Mi perfil ────────────────────────────────────────────
                    is AppScreen.MiPerfil -> MiPerfilScreen(
                        onBack = { screen = AppScreen.Home }
                    )
                }
            }
        }
    }
}
