package net.eltiburon.minimax.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import net.eltiburon.minimax.model.EstadoGrupo
import net.eltiburon.minimax.model.GrupoActivo
import net.eltiburon.minimax.model.GrupoRecomendado
import net.eltiburon.minimax.ui.theme.*

// ── Navegación inferior ──────────────────────────────────────────────────────

private enum class NavTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Filled.Home),
    PEDIDOS("Pedidos", Icons.Filled.ShoppingBag),
    GRUPOS("Grupos", Icons.Filled.Group),
    INVENTARIO("Inventario", Icons.Filled.Inventory2),
    PERFIL("Perfil", Icons.Filled.Person)
}

// ── Items del Drawer ─────────────────────────────────────────────────────────

private enum class DrawerNavItem(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Filled.Home),
    MIS_PEDIDOS("Mis Pedidos", Icons.Filled.ShoppingBag),
    GRUPOS_ACTIVOS("Grupos Activos", Icons.Filled.Group),
    INVENTARIO("Inventario", Icons.Filled.Inventory2),
    ANALITICA("Analítica", Icons.Filled.BarChart),
    MI_PERFIL("Mi Perfil", Icons.Filled.Person),
    CONFIGURACION("Configuración", Icons.Filled.Settings)
}

// ── Pantalla principal ───────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onGrupoClick: (String) -> Unit = {},
    onGruposClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val gruposActivos by viewModel.gruposActivos.collectAsState()
    // La lista ya viene filtrada desde el ViewModel; la UI solo la muestra.
    val gruposFiltrados by viewModel.gruposRecomendados.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // rememberSaveable: la pestaña seleccionada sobrevive a la rotación de pantalla.
    var selectedTab by rememberSaveable { mutableStateOf(NavTab.DASHBOARD) }
    var activeDrawerItem by rememberSaveable { mutableStateOf(DrawerNavItem.DASHBOARD) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MiniMaxDrawerContent(
                activeItem = activeDrawerItem,
                onItemClick = { item ->
                    scope.launch { drawerState.close() }
                    activeDrawerItem = item
                    when (item) {
                        DrawerNavItem.MI_PERFIL -> onPerfilClick()
                        DrawerNavItem.DASHBOARD -> { /* ya estamos aquí */ }
                        else -> { /* pantallas no implementadas aún */ }
                    }
                },
                onCerrarSesion = {
                    scope.launch { drawerState.close() }
                    onCerrarSesion()
                }
            )
        }
    ) {
        Scaffold(
            containerColor = MiniMaxBackground,
            bottomBar = {
                MiniMaxBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        when (tab) {
                            NavTab.PERFIL -> onPerfilClick()
                            NavTab.GRUPOS -> onGruposClick()
                            else -> selectedTab = tab
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
            ) {
                item { HomeHeader(onMenuClick = { scope.launch { drawerState.open() } }) }
                item { SavingsBanner() }
                item { GruposActivosSection(gruposActivos, onGrupoClick) }
                item {
                    GruposRecomendadosSection(
                        grupos = gruposFiltrados,
                        searchQuery = searchQuery,
                        onSearchChange = viewModel::onSearchQueryChange,
                        onGrupoClick = onGrupoClick
                    )
                }
            }
        }
    }
}

// ── Drawer ───────────────────────────────────────────────────────────────────

@Composable
private fun MiniMaxDrawerContent(
    activeItem: DrawerNavItem,
    onItemClick: (DrawerNavItem) -> Unit,
    onCerrarSesion: () -> Unit
) {
    ModalDrawerSheet(drawerContainerColor = Color.White) {
        Column(modifier = Modifier.fillMaxHeight()) {
            DrawerHeader()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                val mainItems = listOf(
                    DrawerNavItem.DASHBOARD,
                    DrawerNavItem.MIS_PEDIDOS,
                    DrawerNavItem.GRUPOS_ACTIVOS,
                    DrawerNavItem.INVENTARIO,
                    DrawerNavItem.ANALITICA
                )
                val secondaryItems = listOf(
                    DrawerNavItem.MI_PERFIL,
                    DrawerNavItem.CONFIGURACION
                )

                mainItems.forEach { item ->
                    DrawerItemRow(
                        item = item,
                        isActive = item == activeItem,
                        onClick = { onItemClick(item) }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color(0xFFEEEEEE)
                )

                secondaryItems.forEach { item ->
                    DrawerItemRow(
                        item = item,
                        isActive = item == activeItem,
                        onClick = { onItemClick(item) }
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))
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
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Cerrar sesión",
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiniMaxPrimary)
            .padding(24.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MiniMaxAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LG",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Lucas Gonzalez",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Comprador",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DrawerItemRow(
    item: DrawerNavItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) MiniMaxPrimary.copy(alpha = 0.10f) else Color.Transparent
    val contentColor = if (isActive) MiniMaxPrimary else MiniMaxTextPrimary

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

// ── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(onMenuClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiniMaxPrimary)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hamburguesa + Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Abrir menú",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MiniMaxAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MiniMax",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            // Acciones
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MiniMaxAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

// ── Banner de ahorros ────────────────────────────────────────────────────────

@Composable
private fun SavingsBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MiniMaxTeal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mis Ahorros",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$12.450",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Este mes ahorraste $3.200 más",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

// ── Sección Grupos Activos ───────────────────────────────────────────────────

@Composable
private fun GruposActivosSection(grupos: List<GrupoActivo>, onGrupoClick: (String) -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Grupos Activos",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MiniMaxTextPrimary
            )
            TextButton(onClick = {}) {
                Text("Ver todos", color = MiniMaxAccent, fontSize = 14.sp)
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(grupos, key = { it.id }) { grupo ->
                GrupoActivoCard(grupo, onGrupoClick)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun GrupoActivoCard(grupo: GrupoActivo, onGrupoClick: (String) -> Unit) {
    Card(
        onClick = { onGrupoClick(grupo.id.toString()) },
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MiniMaxPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = MiniMaxPrimary.copy(alpha = 0.35f),
                    modifier = Modifier.size(52.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MiniMaxBadgeRed)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = grupo.prioridad,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = grupo.nombreProducto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MiniMaxTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${grupo.proveedor} · ${grupo.lote}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { grupo.progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MiniMaxTeal,
                    trackColor = MiniMaxProgressBg
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(grupo.progreso * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = MiniMaxTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Faltan ${grupo.unidadesFaltantes} uds.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = MiniMaxAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cierra en ${grupo.horasRestantes}h",
                        fontSize = 12.sp,
                        color = MiniMaxAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ── Sección Grupos Recomendados ──────────────────────────────────────────────

@Composable
private fun GruposRecomendadosSection(
    grupos: List<GrupoRecomendado>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onGrupoClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Grupos Recomendados",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MiniMaxTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Buscar grupos...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MiniMaxAccent,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        grupos.forEach { grupo ->
            GrupoRecomendadoItem(grupo, onGrupoClick)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GrupoRecomendadoItem(grupo: GrupoRecomendado, onGrupoClick: (String) -> Unit) {
    Card(
        onClick = { onGrupoClick(grupo.id.toString()) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MiniMaxPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Storefront,
                    contentDescription = null,
                    tint = MiniMaxPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = grupo.nombre,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MiniMaxTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = grupo.proveedor, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-${grupo.descuento}%",
                    color = MiniMaxTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                val (labelColor, bgColor) = when (grupo.estado) {
                    EstadoGrupo.CASI_LLENO -> MiniMaxBadgeRed to MiniMaxBadgeRed.copy(alpha = 0.10f)
                    EstadoGrupo.FORMANDOSE -> MiniMaxAccent to MiniMaxAccent.copy(alpha = 0.10f)
                    EstadoGrupo.URGENTE   -> Color(0xFFF59E0B) to Color(0xFFF59E0B).copy(alpha = 0.10f)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgColor)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = grupo.estado.label,
                        color = labelColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ── Bottom Navigation ────────────────────────────────────────────────────────

@Composable
private fun MiniMaxBottomBar(selectedTab: NavTab, onTabSelected: (NavTab) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MiniMaxPrimary,
                    selectedTextColor = MiniMaxPrimary,
                    indicatorColor = MiniMaxPrimary.copy(alpha = 0.10f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MiniMaxTheme {
        HomeScreen()
    }
}
