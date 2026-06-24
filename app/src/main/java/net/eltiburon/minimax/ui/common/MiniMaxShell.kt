package net.eltiburon.minimax.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Capa de navegación persistente de MiniMax.
 *
 * Antes cada pantalla armaba su propio Scaffold: las "hub" (Home / Dashboard) traían bottom bar
 * y las "hoja" solo una flecha de volver. Resultado: al entrar a cualquier ítem de la bottom bar
 * o del menú lateral desaparecían ambas barras. Acá se centraliza todo: un único Scaffold a nivel
 * de NavHost dibuja la top bar + bottom bar correctas según el rol (comprador / proveedor) y la
 * ruta actual, y las pantallas pasan a renderizar solo su contenido.
 */

// ── Rutas (duplicadas como constantes locales para no acoplar este archivo a MainActivity) ──────
// Coinciden con net.eltiburon.minimax.Rutas.

object ShellRutas {
    const val ROL_COMPRADOR = "comprador"
    const val ROL_PROVEEDOR = "proveedor"

    const val HOME = "home"
    const val MIS_COMPRAS = "mis_compras"
    const val EXPLORAR_GRUPOS = "explorar_grupos"
    const val ANALITICA = "analitica"
    const val MI_PERFIL = "mi_perfil"
    const val NOTIFICACIONES = "notificaciones"

    const val DASHBOARD_PROVEEDOR = "dashboard_proveedor"
    const val VENTAS_PROVEEDOR = "ventas_proveedor"
    const val NUEVA_OPORTUNIDAD = "nueva_oportunidad"
    const val CATALOGO_PROVEEDOR = "catalogo_proveedor"
}

/** Una entrada de la bottom bar: a qué ruta lleva, su etiqueta y su ícono. */
data class ShellTab(val route: String, val label: String, val icon: ImageVector)

val compradorTabs = listOf(
    ShellTab(ShellRutas.HOME, "Dashboard", Icons.Filled.Home),
    ShellTab(ShellRutas.MIS_COMPRAS, "Pedidos", Icons.Filled.ShoppingBag),
    ShellTab(ShellRutas.EXPLORAR_GRUPOS, "Grupos", Icons.Filled.Group),
    ShellTab(ShellRutas.ANALITICA, "Analítica", Icons.Filled.BarChart),
    ShellTab(ShellRutas.MI_PERFIL, "Perfil", Icons.Filled.Person)
)

val proveedorTabs = listOf(
    ShellTab(ShellRutas.DASHBOARD_PROVEEDOR, "Dashboard", Icons.Filled.Home),
    ShellTab(ShellRutas.VENTAS_PROVEEDOR, "Ventas", Icons.Filled.Storefront),
    ShellTab(ShellRutas.NUEVA_OPORTUNIDAD, "Nueva orden", Icons.Filled.AddCircle),
    ShellTab(ShellRutas.CATALOGO_PROVEEDOR, "Catálogo", Icons.Filled.Inventory2),
    ShellTab(ShellRutas.MI_PERFIL, "Perfil", Icons.Filled.Person)
)

/** Ítem del menú lateral (drawer). */
data class ShellDrawerItem(val route: String, val label: String, val icon: ImageVector)

private val compradorDrawerItems = listOf(
    ShellDrawerItem(ShellRutas.HOME, "Dashboard", Icons.Filled.Home),
    ShellDrawerItem(ShellRutas.MIS_COMPRAS, "Mis Pedidos", Icons.Filled.ShoppingBag),
    ShellDrawerItem(ShellRutas.EXPLORAR_GRUPOS, "Grupos", Icons.Filled.Group),
    ShellDrawerItem(ShellRutas.ANALITICA, "Analítica", Icons.Filled.BarChart),
    ShellDrawerItem(ShellRutas.MI_PERFIL, "Mi Perfil", Icons.Filled.Person)
)

private val proveedorDrawerItems = listOf(
    ShellDrawerItem(ShellRutas.DASHBOARD_PROVEEDOR, "Dashboard", Icons.Filled.Home),
    ShellDrawerItem(ShellRutas.VENTAS_PROVEEDOR, "Ventas", Icons.Filled.Storefront),
    ShellDrawerItem(ShellRutas.NUEVA_OPORTUNIDAD, "Nueva orden", Icons.Filled.AddCircle),
    ShellDrawerItem(ShellRutas.CATALOGO_PROVEEDOR, "Catálogo", Icons.Filled.Inventory2),
    ShellDrawerItem(ShellRutas.MI_PERFIL, "Mi Perfil", Icons.Filled.Person)
)

/** Rutas que muestran las barras persistentes (todas las hoja/hub navegables por bottom bar o menú). */
val rutasConBarras: Set<String> = setOf(
    ShellRutas.HOME,
    ShellRutas.MIS_COMPRAS,
    ShellRutas.EXPLORAR_GRUPOS,
    ShellRutas.ANALITICA,
    ShellRutas.MI_PERFIL,
    ShellRutas.NOTIFICACIONES,
    ShellRutas.DASHBOARD_PROVEEDOR,
    ShellRutas.VENTAS_PROVEEDOR,
    ShellRutas.NUEVA_OPORTUNIDAD,
    ShellRutas.CATALOGO_PROVEEDOR
)

/** Normaliza una ruta de NavController quitando argumentos: "login/{rol}" → "login", "x?y=z" → "x". */
fun rutaBase(route: String?): String =
    route.orEmpty().substringBefore('?').substringBefore('/')

/** Subtítulo que muestra la top bar debajo de "MiniMax" para cada ruta. null = sin subtítulo. */
fun subtituloDe(rutaBase: String): String? = when (rutaBase) {
    ShellRutas.MIS_COMPRAS -> "Mis Pedidos"
    ShellRutas.EXPLORAR_GRUPOS -> "Explorar grupos"
    ShellRutas.ANALITICA -> "Analítica"
    ShellRutas.MI_PERFIL -> "Mi perfil"
    ShellRutas.NOTIFICACIONES -> "Notificaciones"
    ShellRutas.VENTAS_PROVEEDOR -> "Ventas"
    ShellRutas.NUEVA_OPORTUNIDAD -> "Nueva oportunidad"
    ShellRutas.CATALOGO_PROVEEDOR -> "Catálogo completo"
    else -> null
}

// ── Top bar persistente ─────────────────────────────────────────────────────────────────────────

/**
 * Encabezado de marca compartido. Muestra hamburguesa (en pantallas raíz, abre el menú) o flecha de
 * volver (en el resto), el logo + subtítulo, y accesos a notificaciones y avatar.
 */
@Composable
fun ShellTopBar(
    subtitulo: String?,
    esRaiz: Boolean,
    iniciales: String,
    fotoUri: String?,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
    onNotificacionesClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = if (esRaiz) onMenuClick else onBackClick) {
                    Icon(
                        imageVector = if (esRaiz) Icons.Filled.Menu else Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (esRaiz) "Abrir menú" else "Volver",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("M", color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "MiniMax",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (subtitulo != null) 18.sp else 20.sp,
                        lineHeight = 20.sp
                    )
                    if (subtitulo != null) {
                        Text(
                            text = subtitulo,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f),
                            fontSize = 12.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNotificacionesClick) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notificaciones",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable { onPerfilClick() },
                    contentAlignment = Alignment.Center
                ) {
                    // Mismo avatar que Mi Perfil: la foto si la hay, o las iniciales como placeholder.
                    if (fotoUri != null) {
                        UriImage(uri = fotoUri, modifier = Modifier.fillMaxSize())
                    } else {
                        Text(iniciales, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ── Bottom bar persistente ──────────────────────────────────────────────────────────────────────

@Composable
fun ShellBottomBar(
    tabs: List<ShellTab>,
    rutaActual: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = rutaActual == rutaBase(tab.route),
                onClick = { onTabSelected(tab.route) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// ── Menú lateral persistente (drawer) ───────────────────────────────────────────────────────────

@Composable
fun ShellDrawerContent(
    rol: String,
    rutaActual: String,
    nombreUsuario: String,
    onItemClick: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val items = if (rol == ShellRutas.ROL_PROVEEDOR) proveedorDrawerItems else compradorDrawerItems
    val rolLabel = if (rol == ShellRutas.ROL_PROVEEDOR) "Proveedor" else "Comprador"

    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxHeight()) {
            DrawerHeader(nombreUsuario = nombreUsuario, rolLabel = rolLabel)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                items.forEach { item ->
                    DrawerItemRow(
                        item = item,
                        isActive = rutaActual == rutaBase(item.route),
                        onClick = { onItemClick(item.route) }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCerrarSesion() }
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Cerrar sesión",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader(nombreUsuario: String, rolLabel: String) {
    val iniciales = inicialesDe(nombreUsuario)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = nombreUsuario,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = rolLabel,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DrawerItemRow(
    item: ShellDrawerItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) else Color.Transparent
    val contentColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = item.label,
            color = contentColor,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.45f),
            modifier = Modifier.size(18.dp)
        )
    }
}

/** Iniciales (hasta 2 letras) a partir de un nombre; "M" si está vacío. */
fun inicialesDe(nombre: String): String =
    nombre.trim().split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "M" }
