package net.eltiburon.minimax.ui.explorar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import net.eltiburon.minimax.model.EstadoGrupo
import net.eltiburon.minimax.model.GrupoRecomendado
import net.eltiburon.minimax.ui.theme.*

@Composable
fun ExplorarGruposScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onGrupoClick: (String) -> Unit = {}
) {
    // Mock data
    val grupos = remember {
        listOf(
            GrupoRecomendado(1, "Pack 24 Latas Coca-Cola 354ml", "Distribuidora Bebidas AR", 25, EstadoGrupo.CASI_LLENO),
            GrupoRecomendado(2, "Harina 0000 1kg - Pack 10", "Molinos del Sur", 15, EstadoGrupo.FORMANDOSE),
            GrupoRecomendado(3, "Aceite Girasol 1.5L - Caja 6", "Aceitera General Deheza", 20, EstadoGrupo.URGENTE),
            GrupoRecomendado(4, "Leche Entera 1L - Pack 12", "Lácteos del Campo", 10, EstadoGrupo.FORMANDOSE),
            GrupoRecomendado(5, "Arroz Largo Fino 1kg - Pack 10", "Arroceras Unidas", 18, EstadoGrupo.CASI_LLENO)
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val categorias = listOf("Todo", "Alimentos & Bebidas", "Electrónica", "Decoración", "Cafetería", "Textil", "Gadgets")
    var selectedCategoria by remember { mutableStateOf("Todo") }

    Scaffold(
        containerColor = MiniMaxBackground,
        topBar = {
            ExplorarTopBar(onBackClick)
        },
        bottomBar = {
            ExplorarBottomBar(onHomeClick = onHomeClick, onPerfilClick = onPerfilClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filtros y Búsqueda
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("¿Qué estás buscando?", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MiniMaxAccent,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedContainerColor = Color(0xFFF9FAFB),
                        unfocusedContainerColor = Color(0xFFF9FAFB)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                ScrollableTabRow(
                    selectedTabIndex = categorias.indexOf(selectedCategoria),
                    edgePadding = 16.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = {}
                ) {
                    categorias.forEach { categoria ->
                        val isSelected = categoria == selectedCategoria
                        Tab(
                            selected = isSelected,
                            onClick = { selectedCategoria = categoria },
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) MiniMaxPrimary else Color(0xFFF3F4F6))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = categoria,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Resultados
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Resultados (${grupos.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MiniMaxTextPrimary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(grupos) { grupo ->
                    ExplorarGrupoCard(grupo, onGrupoClick)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExplorarTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Explorar Grupos", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filtros")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun ExplorarGrupoCard(grupo: GrupoRecomendado, onGrupoClick: (String) -> Unit) {
    Card(
        onClick = { onGrupoClick(grupo.id.toString()) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(MiniMaxPrimary.copy(alpha = 0.05f))
            ) {
                val (bgColor, iconColor, icon) = categoriaVisuals("Alimentos & Bebidas")
                
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Badge descuento
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MiniMaxTeal)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "-${grupo.descuento}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = grupo.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MiniMaxTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = grupo.proveedor,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Estado badge
                    val (estadoColor, estadoText) = estadoVisuals(grupo.estado)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(estadoColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = estadoText,
                            color = estadoColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botón
                    Button(
                        onClick = { onGrupoClick(grupo.id.toString()) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MiniMaxPrimary)
                    ) {
                        Text("Unirse", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── Bottom Navigation ─────────────────────────────────────────────────────────

@Composable
private fun ExplorarBottomBar(onHomeClick: () -> Unit, onPerfilClick: () -> Unit) {
    val items = listOf(
        Triple(Icons.Filled.Home, "Dashboard", false),
        Triple(Icons.Filled.ShoppingBag, "Pedidos", false),
        Triple(Icons.Filled.Group, "Grupos", true),
        Triple(Icons.Filled.Inventory2, "Inventario", false),
        Triple(Icons.Filled.Person, "Perfil", false)
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEachIndexed { index, (icon, label, selected) ->
            NavigationBarItem(
                selected = selected,
                onClick = {
                    when (index) {
                        0 -> onHomeClick()
                        4 -> onPerfilClick()
                        else -> {}
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
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

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun categoriaVisuals(categoria: String): Triple<Color, Color, ImageVector> = when (categoria) {
    "Alimentos & Bebidas" -> Triple(Color(0xFFF0FDF4), Color(0xFF16A34A), Icons.Filled.Restaurant)
    "Electrónica"         -> Triple(Color(0xFFEFF6FF), Color(0xFF2563EB), Icons.Filled.Devices)
    "Decoración"          -> Triple(Color(0xFFFFF7ED), Color(0xFFEA580C), Icons.Filled.Style)
    "Cafetería"           -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), Icons.Filled.LocalCafe)
    "Textil"              -> Triple(Color(0xFFF5F3FF), MiniMaxAccent,     Icons.Filled.Checkroom)
    "Gadgets"             -> Triple(Color(0xFFECFEFF), Color(0xFF0891B2), Icons.Filled.Memory)
    else                  -> Triple(MiniMaxPrimary.copy(alpha = 0.06f), MiniMaxPrimary, Icons.Filled.ShoppingBag)
}

private fun estadoVisuals(estado: EstadoGrupo): Pair<Color, String> = when (estado) {
    EstadoGrupo.FORMANDOSE -> MiniMaxTeal     to "FORMÁNDOSE"
    EstadoGrupo.CASI_LLENO -> MiniMaxOrange   to "CASI LLENO"
    EstadoGrupo.URGENTE    -> MiniMaxBadgeRed to "URGENTE"
}

private fun abreviarCategoria(categoria: String): String = when (categoria) {
    "Alimentos & Bebidas" -> "ALIMENTOS"
    else                  -> categoria.uppercase().take(9)
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExplorarGruposScreenPreview() {
    MiniMaxTheme {
        ExplorarGruposScreen()
    }
}
