package net.eltiburon.minimax.ui.explorar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.eltiburon.minimax.model.EstadoGrupo
import net.eltiburon.minimax.model.GrupoResumen
import net.eltiburon.minimax.ui.theme.*

// ── Categorías disponibles ────────────────────────────────────────────────────

private val CATEGORIAS = listOf(
    "Todos", "Alimentos & Bebidas", "Electrónica",
    "Decoración", "Cafetería", "Textil", "Gadgets"
)

// ── Pantalla principal ────────────────────────────────────────────────────────

@Composable
fun ExplorarGruposScreen(
    onGrupoClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: ExplorarGruposViewModel = viewModel()
) {
    val gruposFiltrados by viewModel.gruposFiltrados.collectAsState()
    val filtroCategoria by viewModel.filtroCategoria.collectAsState()
    val filtroEstado by viewModel.filtroEstado.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()

    Scaffold(
        containerColor = MiniMaxBackground,
        bottomBar = {
            ExplorarBottomBar(onHomeClick = onHomeClick, onPerfilClick = onPerfilClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            ExplorarHeader()

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 12.dp, end = 12.dp,
                    top = 12.dp, bottom = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SearchBarRow(
                        query = textoBusqueda,
                        onQueryChange = viewModel::onBusquedaChange
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CategoriaChipsRow(
                        selected = filtroCategoria,
                        onSelect = viewModel::onCategoriaChange
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EstadoChipsRow(
                        selected = filtroEstado,
                        onSelect = viewModel::onEstadoChange
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "${gruposFiltrados.size} grupos disponibles",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                if (gruposFiltrados.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyState()
                    }
                } else {
                    items(gruposFiltrados, key = { it.id }) { grupo ->
                        GrupoResumenCard(
                            grupo = grupo,
                            onClick = { onGrupoClick(grupo.id) }
                        )
                    }
                }
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun ExplorarHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiniMaxPrimary)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Explorar Grupos",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    tint = Color.White
                )
            }
        }
    }
}

// ── Barra de búsqueda ─────────────────────────────────────────────────────────

@Composable
private fun SearchBarRow(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
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
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MiniMaxPrimary)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Filtros",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ── Chips de categoría ────────────────────────────────────────────────────────

@Composable
private fun CategoriaChipsRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CATEGORIAS.forEach { cat ->
            val isSelected = cat == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MiniMaxPrimary else Color.White)
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = cat,
                    color = if (isSelected) Color.White else MiniMaxTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Chips de estado ───────────────────────────────────────────────────────────

@Composable
private fun EstadoChipsRow(selected: EstadoGrupo?, onSelect: (EstadoGrupo?) -> Unit) {
    val opciones = listOf(
        Triple("Todos los estados", null as EstadoGrupo?, MiniMaxAccent),
        Triple("Formándose", EstadoGrupo.FORMANDOSE, MiniMaxTeal),
        Triple("Casi Lleno", EstadoGrupo.CASI_LLENO, MiniMaxOrange),
        Triple("Urgente", EstadoGrupo.URGENTE, MiniMaxBadgeRed)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { (label, estado, activeColor) ->
            val isSelected = selected == estado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) activeColor else Color.White)
                    .clickable { onSelect(if (selected == estado) null else estado) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else MiniMaxTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Card de grupo ─────────────────────────────────────────────────────────────

@Composable
private fun GrupoResumenCard(grupo: GrupoResumen, onClick: () -> Unit) {
    val (bgColor, iconColor, cardIcon) = categoriaVisuals(grupo.categoria)
    val (estadoColor, estadoLabel) = estadoVisuals(grupo.estado)

    val progressColor = when (grupo.estado) {
        EstadoGrupo.URGENTE -> MiniMaxBadgeRed
        EstadoGrupo.CASI_LLENO -> MiniMaxOrange
        EstadoGrupo.FORMANDOSE -> MiniMaxTeal
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Imagen con badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(bgColor)
            ) {
                Icon(
                    imageVector = cardIcon,
                    contentDescription = null,
                    tint = iconColor.copy(alpha = 0.45f),
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.Center)
                )
                // Badge categoría (esquina superior izquierda)
                CardBadge(
                    text = abreviarCategoria(grupo.categoria),
                    bgColor = MiniMaxAccent,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )
                // Badge estado (esquina superior derecha)
                CardBadge(
                    text = estadoLabel,
                    bgColor = estadoColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                )
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = grupo.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MiniMaxTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
                Text(
                    text = grupo.proveedor,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                // Precio grupal destacado
                Text(
                    text = "$${grupo.precioGrupal.toLong()}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MiniMaxPrimary
                )
                // Precio original tachado + badge de descuento
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$${grupo.precioOriginal.toLong()}",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        style = TextStyle(textDecoration = TextDecoration.LineThrough)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MiniMaxTeal.copy(alpha = 0.12f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "-${grupo.descuentoPorcentaje}%",
                            fontSize = 9.sp,
                            color = MiniMaxTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Barra de progreso
                LinearProgressIndicator(
                    progress = { grupo.progresoActual / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = progressColor,
                    trackColor = MiniMaxProgressBg
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${grupo.progresoActual}%",
                        fontSize = 10.sp,
                        color = progressColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = grupo.tiempoRestante,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Botón reservar
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MiniMaxAccent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Reservar Cupo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ── Componentes auxiliares ────────────────────────────────────────────────────

@Composable
private fun CardBadge(text: String, bgColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MiniMaxPrimary.copy(alpha = 0.25f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No encontramos grupos",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MiniMaxTextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "para tu búsqueda",
            fontSize = 14.sp,
            color = Color.Gray
        )
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
                label = { Text(label, fontSize = 10.sp) },
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
