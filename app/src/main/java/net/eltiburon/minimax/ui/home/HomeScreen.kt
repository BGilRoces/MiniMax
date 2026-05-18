package net.eltiburon.minimax.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// ── Pantalla principal ───────────────────────────────────────────────────────

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val gruposActivos by viewModel.gruposActivos.collectAsState()
    val allRecomendados by viewModel.gruposRecomendados.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val gruposFiltrados = remember(searchQuery, allRecomendados) {
        if (searchQuery.isBlank()) allRecomendados
        else allRecomendados.filter {
            it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.proveedor.contains(searchQuery, ignoreCase = true)
        }
    }

    var selectedTab by remember { mutableStateOf(NavTab.DASHBOARD) }

    Scaffold(
        containerColor = MiniMaxBackground,
        bottomBar = {
            MiniMaxBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
        ) {
            item { HomeHeader() }
            item { SavingsBanner() }
            item { GruposActivosSection(gruposActivos) }
            item {
                GruposRecomendadosSection(
                    grupos = gruposFiltrados,
                    searchQuery = searchQuery,
                    onSearchChange = viewModel::onSearchQueryChange
                )
            }
        }
    }
}

// ── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiniMaxPrimary)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text("JD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

// ── Sección Grupos Activos ───────────────────────────────────────────────────

@Composable
private fun GruposActivosSection(grupos: List<GrupoActivo>) {
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
                GrupoActivoCard(grupo)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun GrupoActivoCard(grupo: GrupoActivo) {
    Card(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Imagen / placeholder del producto
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
                // Badge de prioridad
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

                // Barra de progreso
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

                // Tiempo restante
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
    onSearchChange: (String) -> Unit
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
            GrupoRecomendadoItem(grupo)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GrupoRecomendadoItem(grupo: GrupoRecomendado) {
    Card(
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
            // Ícono del producto
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
                label = { Text(tab.label, fontSize = 10.sp) },
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
