package net.eltiburon.minimax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import net.eltiburon.minimax.data.UsuarioRepository
import net.eltiburon.minimax.ui.common.ShellBottomBar
import net.eltiburon.minimax.ui.common.ShellDrawerContent
import net.eltiburon.minimax.ui.common.ShellTopBar
import net.eltiburon.minimax.ui.common.compradorTabs
import net.eltiburon.minimax.ui.common.inicialesDe
import net.eltiburon.minimax.ui.common.proveedorTabs
import net.eltiburon.minimax.ui.common.rutaBase
import net.eltiburon.minimax.ui.common.rutasConBarras
import net.eltiburon.minimax.ui.common.subtituloDe
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
import net.eltiburon.minimax.ui.proveedor.CatalogoProveedorScreen
import net.eltiburon.minimax.ui.proveedor.DashboardProveedorScreen
import net.eltiburon.minimax.ui.proveedor.NuevaOportunidadScreen
import net.eltiburon.minimax.ui.proveedor.VentasProveedorScreen
import net.eltiburon.minimax.ui.seleccionrol.SeleccionRolScreen
import net.eltiburon.minimax.ui.theme.MiniMaxTheme

/**
 * Rutas del grafo de navegación. Centralizar los nombres acá evita "magic strings" repartidos
 * por la app y facilita construir las rutas con argumentos (helpers como [grupoDetalle]).
 */
object Rutas {
    const val ONBOARDING = "onboarding"
    const val SELECCION_ROL = "seleccion_rol"
    const val LOGIN = "login/{rol}"
    const val REGISTRO = "registro/{rol}"
    const val HOME = "home"
    const val DASHBOARD_PROVEEDOR = "dashboard_proveedor"
    const val EXPLORAR_GRUPOS = "explorar_grupos"
    const val MI_PERFIL = "mi_perfil"
    const val MIS_COMPRAS = "mis_compras"
    const val NOTIFICACIONES = "notificaciones"
    const val INVENTARIO = "inventario"
    const val ANALITICA = "analitica"
    const val VENTAS_PROVEEDOR = "ventas_proveedor"
    const val CATALOGO_PROVEEDOR = "catalogo_proveedor"

    // Rutas con argumentos.
    const val NUEVA_OPORTUNIDAD = "nueva_oportunidad?oportunidadId={oportunidadId}"
    const val GRUPO_DETALLE = "grupo_detalle/{grupoId}"
    const val ELEGIR_CANTIDAD = "elegir_cantidad/{grupoId}"
    const val CONFIRMAR_PARTICIPACION = "confirmar_participacion/{grupoId}/{cantidad}"
    const val CONFIRMACION_PARTICIPACION = "confirmacion_participacion/{grupoId}/{cantidad}"

    // Roles posibles que se eligen antes de autenticarse y se pasan como argumento a login/registro.
    const val ROL_COMPRADOR = "comprador"
    const val ROL_PROVEEDOR = "proveedor"

    // login/registro reciben el rol elegido en SeleccionRol para saber a dónde ir tras autenticarse.
    fun login(rol: String) = "login/$rol"
    fun registro(rol: String) = "registro/$rol"

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

    // Ruta actual (sin argumentos) para decidir qué barras mostrar y qué pestaña resaltar.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = rutaBase(backStackEntry?.destination?.route)

    // El rol del usuario logueado define qué bottom bar / menú lateral corresponde.
    val usuario by UsuarioRepository.usuarioActual.collectAsState()
    val rol = usuario?.rol ?: Rutas.ROL_COMPRADOR
    val esProveedor = rol == Rutas.ROL_PROVEEDOR
    val tabs = if (esProveedor) proveedorTabs else compradorTabs
    val rolHome = if (esProveedor) Rutas.DASHBOARD_PROVEEDOR else Rutas.HOME
    val nombreUsuario = usuario?.nombre ?: "MiniMax"

    val mostrarBarras = rutaActual in rutasConBarras
    val esRaiz = tabs.any { rutaBase(it.route) == rutaActual }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Navegación de pestañas/menú: reemplaza el tope del back stack guardando estado, de modo que
    // alternar entre secciones no apile pantallas y "volver" regrese siempre al dashboard del rol.
    fun navegarASeccion(route: String) {
        navController.navigate(route) {
            popUpTo(rolHome) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun cerrarSesion() {
        UsuarioRepository.cerrarSesion()
        navController.navigate(Rutas.SELECCION_ROL) {
            popUpTo(0) { inclusive = true }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarBarras && drawerState.isOpen,
        drawerContent = {
            ShellDrawerContent(
                rol = rol,
                rutaActual = rutaActual,
                nombreUsuario = nombreUsuario,
                onItemClick = { route ->
                    scope.launch { drawerState.close() }
                    navegarASeccion(route)
                },
                onCerrarSesion = {
                    scope.launch { drawerState.close() }
                    cerrarSesion()
                }
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (mostrarBarras) {
                    ShellTopBar(
                        subtitulo = subtituloDe(rutaActual),
                        esRaiz = esRaiz,
                        iniciales = inicialesDe(nombreUsuario),
                        fotoUri = usuario?.fotoUri,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onBackClick = { navController.navigateUp() },
                        onNotificacionesClick = { navController.navigate(Rutas.NOTIFICACIONES) },
                        onPerfilClick = { navegarASeccion(Rutas.MI_PERFIL) }
                    )
                }
            },
            bottomBar = {
                if (mostrarBarras) {
                    ShellBottomBar(
                        tabs = tabs,
                        rutaActual = rutaActual,
                        onTabSelected = { route -> navegarASeccion(route) }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Rutas.ONBOARDING,
                modifier = Modifier.padding(innerPadding)
            ) {

        composable(Rutas.ONBOARDING) {
            OnboardingScreen(
                // Primero se elige el rol; recién después se inicia sesión o se registra.
                onFinish = { navController.navigate(Rutas.SELECCION_ROL) }
            )
        }

        composable(Rutas.SELECCION_ROL) {
            SeleccionRolScreen(
                onCompradorClick = { navController.navigate(Rutas.login(Rutas.ROL_COMPRADOR)) },
                onProveedorClick = { navController.navigate(Rutas.login(Rutas.ROL_PROVEEDOR)) }
            )
        }

        composable(
            route = Rutas.LOGIN,
            arguments = listOf(navArgument("rol") { type = NavType.StringType })
        ) { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol").orEmpty()
            LoginScreen(
                rol = rol,
                onLoginExitoso = {
                    // El rol se eligió antes de autenticarse; se aplica al usuario ya logueado.
                    UsuarioRepository.setRol(rol)
                    val destino = if (rol == Rutas.ROL_PROVEEDOR) Rutas.DASHBOARD_PROVEEDOR else Rutas.HOME
                    // Limpia todo el flujo previo (onboarding/rol/login) del back stack.
                    navController.navigate(destino) {
                        popUpTo(Rutas.ONBOARDING) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Rutas.registro(rol)) },
                // Volver a la selección de rol (queda directamente debajo en el back stack).
                onCambiarRol = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.REGISTRO,
            arguments = listOf(navArgument("rol") { type = NavType.StringType })
        ) { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol").orEmpty()
            RegistroScreen(
                rol = rol,
                // Tras registrarse, el usuario vuelve a Login (manteniendo el rol elegido).
                onRegistroExitoso = { navController.popBackStack() },
                onIrALogin = { navController.popBackStack() },
                // "Cambiar rol" salta hasta la selección de rol (saltea Login en el back stack).
                onCambiarRol = { navController.popBackStack(Rutas.SELECCION_ROL, inclusive = false) }
            )
        }

        composable(Rutas.HOME) {
            HomeScreen(
                onGrupoClick = { id -> navController.navigate(Rutas.grupoDetalle(id)) },
                onVerGruposClick = { navegarASeccion(Rutas.EXPLORAR_GRUPOS) }
            )
        }

        composable(Rutas.DASHBOARD_PROVEEDOR) {
            DashboardProveedorScreen(
                onNuevaOportunidadClick = { navController.navigate(Rutas.nuevaOportunidad()) },
                onOportunidadClick = { id -> navController.navigate(Rutas.grupoDetalle(id)) },
                onOportunidadEditClick = { id -> navController.navigate(Rutas.nuevaOportunidad(id)) },
                onCatalogoClick = { navegarASeccion(Rutas.CATALOGO_PROVEEDOR) }
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
                onPublicadoOk = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }

        composable(Rutas.EXPLORAR_GRUPOS) {
            ExplorarGruposScreen(
                onGrupoClick = { id -> navController.navigate(Rutas.grupoDetalle(id)) }
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
            MiPerfilScreen()
        }

        composable(Rutas.MIS_COMPRAS) {
            MisComprasScreen(
                onGrupoClick = { id -> navController.navigate(Rutas.grupoDetalle(id)) }
            )
        }

        composable(Rutas.NOTIFICACIONES) {
            NotificacionesScreen()
        }

        composable(Rutas.INVENTARIO) {
            InventarioScreen()
        }

        composable(Rutas.ANALITICA) {
            AnaliticaScreen()
        }

        composable(Rutas.VENTAS_PROVEEDOR) {
            VentasProveedorScreen()
        }

        composable(Rutas.CATALOGO_PROVEEDOR) {
            CatalogoProveedorScreen()
        }
            }
        }
    }
}
