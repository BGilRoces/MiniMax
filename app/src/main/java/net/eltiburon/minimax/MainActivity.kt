package net.eltiburon.minimax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.eltiburon.minimax.ui.OnboardingScreen
import net.eltiburon.minimax.ui.confirmacionparticipacion.ConfirmacionParticipacionScreen
import net.eltiburon.minimax.ui.confirmarparticipacion.ConfirmarParticipacionScreen
import net.eltiburon.minimax.ui.elegircantidad.ElegirCantidadScreen
import net.eltiburon.minimax.ui.explorar.ExplorarGruposScreen
import net.eltiburon.minimax.ui.grupodetalle.GrupoDetalleScreen
import net.eltiburon.minimax.ui.home.HomeScreen
import net.eltiburon.minimax.ui.perfil.MiPerfilScreen
import net.eltiburon.minimax.ui.proveedor.DashboardProveedorScreen
import net.eltiburon.minimax.ui.proveedor.NuevaOportunidadScreen
import net.eltiburon.minimax.ui.seleccionrol.SeleccionRolScreen
import net.eltiburon.minimax.ui.theme.MiniMaxTheme

/**
 * Rutas del grafo de navegación. Centralizar los nombres acá evita "magic strings" repartidos
 * por la app y facilita construir las rutas con argumentos (helpers como [grupoDetalle]).
 */
object Rutas {
    const val ONBOARDING = "onboarding"
    const val SELECCION_ROL = "seleccion_rol"
    const val HOME = "home"
    const val DASHBOARD_PROVEEDOR = "dashboard_proveedor"
    const val NUEVA_OPORTUNIDAD = "nueva_oportunidad"
    const val EXPLORAR_GRUPOS = "explorar_grupos"
    const val MI_PERFIL = "mi_perfil"

    // Rutas con argumentos.
    const val GRUPO_DETALLE = "grupo_detalle/{grupoId}"
    const val ELEGIR_CANTIDAD = "elegir_cantidad/{grupoId}"
    const val CONFIRMAR_PARTICIPACION = "confirmar_participacion/{grupoId}/{cantidad}"
    const val CONFIRMACION_PARTICIPACION = "confirmacion_participacion/{grupoId}/{cantidad}"

    fun grupoDetalle(grupoId: String) = "grupo_detalle/$grupoId"
    fun elegirCantidad(grupoId: String) = "elegir_cantidad/$grupoId"
    fun confirmarParticipacion(grupoId: String, cantidad: Int) =
        "confirmar_participacion/$grupoId/$cantidad"
    fun confirmacionParticipacion(grupoId: String, cantidad: Int) =
        "confirmacion_participacion/$grupoId/$cantidad"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniMaxTheme {
                MiniMaxNavHost()
            }
        }
    }
}

/**
 * Grafo de navegación de la app (NavController + NavHost).
 *
 * El NavHost solo se encarga de la navegación: cada pantalla recibe callbacks "tontos"
 * (onXxxClick) y es este grafo el que decide a qué ruta ir. Así las pantallas no conocen
 * el NavController y la lógica de navegación queda separada de la lógica de cada pantalla.
 */
@Composable
private fun MiniMaxNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.ONBOARDING) {

        composable(Rutas.ONBOARDING) {
            OnboardingScreen(
                onFinish = { navController.navigate(Rutas.SELECCION_ROL) }
            )
        }

        composable(Rutas.SELECCION_ROL) {
            SeleccionRolScreen(
                onCompradorClick = { navController.navigate(Rutas.HOME) },
                onProveedorClick = { navController.navigate(Rutas.DASHBOARD_PROVEEDOR) }
            )
        }

        composable(Rutas.HOME) {
            HomeScreen(
                onGrupoClick = { id -> navController.navigate(Rutas.grupoDetalle(id)) },
                onGruposClick = { navController.navigate(Rutas.EXPLORAR_GRUPOS) },
                onPerfilClick = { navController.navigate(Rutas.MI_PERFIL) },
                onCerrarSesion = {
                    // Cerrar sesión vuelve al onboarding y limpia todo el back stack.
                    navController.navigate(Rutas.ONBOARDING) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.DASHBOARD_PROVEEDOR) {
            DashboardProveedorScreen(
                onNuevaOportunidadClick = { navController.navigate(Rutas.NUEVA_OPORTUNIDAD) }
            )
        }

        composable(Rutas.NUEVA_OPORTUNIDAD) {
            NuevaOportunidadScreen(
                onBackClick = { navController.popBackStack() },
                onPublicadoOk = { navController.popBackStack() }
            )
        }

        composable(Rutas.EXPLORAR_GRUPOS) {
            ExplorarGruposScreen(
                onGrupoClick = { id -> navController.navigate(Rutas.grupoDetalle(id)) },
                onHomeClick = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Rutas.MI_PERFIL) }
            )
        }

        composable(
            route = Rutas.GRUPO_DETALLE,
            arguments = listOf(navArgument("grupoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val grupoId = backStackEntry.arguments?.getString("grupoId").orEmpty()
            GrupoDetalleScreen(
                grupoId = grupoId,
                onBack = { navController.popBackStack() },
                onSumarseClick = { navController.navigate(Rutas.elegirCantidad(grupoId)) }
            )
        }

        composable(
            route = Rutas.ELEGIR_CANTIDAD,
            arguments = listOf(navArgument("grupoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val grupoId = backStackEntry.arguments?.getString("grupoId").orEmpty()
            ElegirCantidadScreen(
                onBackClick = { navController.popBackStack() },
                onContinuarClick = { cantidad ->
                    navController.navigate(Rutas.confirmarParticipacion(grupoId, cantidad))
                }
            )
        }

        composable(
            route = Rutas.CONFIRMAR_PARTICIPACION,
            arguments = listOf(
                navArgument("grupoId") { type = NavType.StringType },
                navArgument("cantidad") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val grupoId = backStackEntry.arguments?.getString("grupoId").orEmpty()
            val cantidad = backStackEntry.arguments?.getInt("cantidad") ?: 1
            ConfirmarParticipacionScreen(
                cantidadSeleccionada = cantidad,
                onBackClick = { navController.popBackStack() },
                onConfirmarClick = {
                    navController.navigate(Rutas.confirmacionParticipacion(grupoId, cantidad))
                }
            )
        }

        composable(
            route = Rutas.CONFIRMACION_PARTICIPACION,
            arguments = listOf(
                navArgument("grupoId") { type = NavType.StringType },
                navArgument("cantidad") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cantidad = backStackEntry.arguments?.getInt("cantidad") ?: 1
            ConfirmacionParticipacionScreen(
                cantidadSeleccionada = cantidad,
                onBackClick = { navController.popBackStack() },
                onVerMisComprasClick = {
                    // Volver al Home limpiando el flujo de participación del back stack.
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = false }
                    }
                },
                onVolverInicioClick = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(Rutas.MI_PERFIL) {
            MiPerfilScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
