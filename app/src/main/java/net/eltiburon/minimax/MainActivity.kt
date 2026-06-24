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
import net.eltiburon.minimax.ui.auth.LoginScreen
import net.eltiburon.minimax.ui.auth.RegistroScreen
import net.eltiburon.minimax.ui.confirmacionparticipacion.ConfirmacionParticipacionScreen
import net.eltiburon.minimax.ui.confirmarparticipacion.ConfirmarParticipacionScreen
import net.eltiburon.minimax.ui.elegircantidad.ElegirCantidadScreen
import net.eltiburon.minimax.ui.explorar.ExplorarGruposScreen
import net.eltiburon.minimax.ui.grupodetalle.GrupoDetalleScreen
import net.eltiburon.minimax.ui.home.HomeScreen
import net.eltiburon.minimax.ui.miscompras.MisComprasScreen
import net.eltiburon.minimax.ui.perfil.MiPerfilScreen
import net.eltiburon.minimax.ui.analitica.AnaliticaScreen
import net.eltiburon.minimax.ui.inventario.InventarioScreen
import net.eltiburon.minimax.ui.notificaciones.NotificacionesScreen
import net.eltiburon.minimax.ui.pedidos.MisPedidosScreen
import net.eltiburon.minimax.ui.proveedor.CatalogoProveedorScreen
import net.eltiburon.minimax.ui.proveedor.DashboardProveedorScreen
import net.eltiburon.minimax.ui.proveedor.NuevaOportunidadScreen
import net.eltiburon.minimax.ui.proveedor.OportunidadesProveedorScreen
import net.eltiburon.minimax.ui.proveedor.PedidosProveedorScreen
import net.eltiburon.minimax.ui.seleccionrol.SeleccionRolScreen
import net.eltiburon.minimax.ui.theme.MiniMaxTheme

/**
 * Rutas del grafo de navegación. Centralizar los nombres acá evita "magic strings" repartidos
 * por la app y facilita construir las rutas con argumentos (helpers como [grupoDetalle]).
 */
object Rutas {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val SELECCION_ROL = "seleccion_rol"
    const val HOME = "home"
    const val DASHBOARD_PROVEEDOR = "dashboard_proveedor"
    const val EXPLORAR_GRUPOS = "explorar_grupos"
    const val MI_PERFIL = "mi_perfil"
    const val MIS_COMPRAS = "mis_compras"
    const val NOTIFICACIONES = "notificaciones"
    const val MIS_PEDIDOS = "mis_pedidos"
    const val INVENTARIO = "inventario"
    const val ANALITICA = "analitica"
    const val PEDIDOS_PROVEEDOR = "pedidos_proveedor"
    const val OPORTUNIDADES_PROVEEDOR = "oportunidades_proveedor"
    const val CATALOGO_PROVEEDOR = "catalogo_proveedor"

    // Rutas con argumentos.
    const val NUEVA_OPORTUNIDAD = "nueva_oportunidad?oportunidadId={oportunidadId}"
    const val GRUPO_DETALLE = "grupo_detalle/{grupoId}"
    const val ELEGIR_CANTIDAD = "elegir_cantidad/{grupoId}"
    const val CONFIRMAR_PARTICIPACION = "confirmar_participacion/{grupoId}/{cantidad}"
    const val CONFIRMACION_PARTICIPACION = "confirmacion_participacion/{grupoId}/{cantidad}"

    // nueva_oportunidad sirve tanto para crear (sin id) como para editar (con id).
    fun nuevaOportunidad(oportunidadId: String? = null) =
        if (oportunidadId != null) "nueva_oportunidad?oportunidadId=$oportunidadId" else "nueva_oportunidad"
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
                onFinish = { navController.navigate(Rutas.LOGIN) }
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    // Login exitoso limpia Onboarding/Login del back stack.
                    navController.navigate(Rutas.SELECCION_ROL) {
                        popUpTo(Rutas.ONBOARDING) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = {
                    // Tras registrarse, el usuario inicia sesión explícitamente.
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onIrALogin = { navController.popBackStack() }
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
                onMisComprasClick = { navController.navigate(Rutas.MIS_COMPRAS) },
                onInventarioClick = { navController.navigate(Rutas.INVENTARIO) },
                onAnaliticaClick = { navController.navigate(Rutas.ANALITICA) },
                onNotificacionesClick = { navController.navigate(Rutas.NOTIFICACIONES) },
                onCerrarSesion = {
                    // Cerrar sesión vuelve a Login y limpia todo el back stack.
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.DASHBOARD_PROVEEDOR) {
            DashboardProveedorScreen(
                onNuevaOportunidadClick = { navController.navigate(Rutas.nuevaOportunidad()) },
                onOportunidadClick = { id -> navController.navigate(Rutas.grupoDetalle(id)) },
                onOportunidadEditClick = { id -> navController.navigate(Rutas.nuevaOportunidad(id)) },
                onPedidosClick = { navController.navigate(Rutas.PEDIDOS_PROVEEDOR) },
                onOportunidadesClick = { navController.navigate(Rutas.OPORTUNIDADES_PROVEEDOR) },
                onPerfilClick = { navController.navigate(Rutas.MI_PERFIL) },
                onNotificacionesClick = { navController.navigate(Rutas.NOTIFICACIONES) },
                onCatalogoClick = { navController.navigate(Rutas.CATALOGO_PROVEEDOR) },
                onCerrarSesion = {
                    // Mismo comportamiento que el comprador: vuelve a Login limpiando el back stack.
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rutas.NUEVA_OPORTUNIDAD,
            arguments = listOf(
                navArgument("oportunidadId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val oportunidadId = backStackEntry.arguments?.getString("oportunidadId")
            NuevaOportunidadScreen(
                oportunidadId = oportunidadId,
                onBackClick = { navController.popBackStack() },
                onPublicadoOk = { navController.popBackStack() }
            )
        }

        composable(Rutas.EXPLORAR_GRUPOS) {
            ExplorarGruposScreen(
                onGrupoClick = { id -> navController.navigate(Rutas.grupoDetalle(id)) },
                onHomeClick = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Rutas.MI_PERFIL) },
                onPedidosClick = { navController.navigate(Rutas.MIS_PEDIDOS) },
                onInventarioClick = { navController.navigate(Rutas.INVENTARIO) }
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
                grupoId = grupoId,
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
                grupoId = grupoId,
                cantidadSeleccionada = cantidad,
                onBackClick = { navController.popBackStack() },
                onConfirmarClick = {
                    // El registro de la participación ya lo hace ConfirmarParticipacionScreen
                    // (vía ResumenParticipacionViewModel.confirmar) antes de invocar este callback.
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
            val grupoId = backStackEntry.arguments?.getString("grupoId").orEmpty()
            val cantidad = backStackEntry.arguments?.getInt("cantidad") ?: 1
            ConfirmacionParticipacionScreen(
                grupoId = grupoId,
                cantidadSeleccionada = cantidad,
                onBackClick = { navController.popBackStack() },
                onVerMisComprasClick = {
                    // Va a Mis Compras limpiando el flujo de participación del back stack.
                    navController.navigate(Rutas.MIS_COMPRAS) {
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

        composable(Rutas.MIS_COMPRAS) {
            MisComprasScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Rutas.NOTIFICACIONES) {
            NotificacionesScreen(onBack = { navController.popBackStack() })
        }

        composable(Rutas.MIS_PEDIDOS) {
            MisPedidosScreen(onBack = { navController.popBackStack() })
        }

        composable(Rutas.INVENTARIO) {
            InventarioScreen(onBack = { navController.popBackStack() })
        }

        composable(Rutas.ANALITICA) {
            AnaliticaScreen(onBack = { navController.popBackStack() })
        }

        composable(Rutas.PEDIDOS_PROVEEDOR) {
            PedidosProveedorScreen(onBack = { navController.popBackStack() })
        }

        composable(Rutas.OPORTUNIDADES_PROVEEDOR) {
            OportunidadesProveedorScreen(onBack = { navController.popBackStack() })
        }

        composable(Rutas.CATALOGO_PROVEEDOR) {
            CatalogoProveedorScreen(onBack = { navController.popBackStack() })
        }
    }
}
