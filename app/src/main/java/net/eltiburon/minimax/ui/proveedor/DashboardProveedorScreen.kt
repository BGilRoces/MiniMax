package net.eltiburon.minimax.ui.proveedor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.eltiburon.minimax.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Bottom-nav tabs del proveedor
// ─────────────────────────────────────────────────────────────────────────────

private enum class NavTabProveedor(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Filled.Home),
    PEDIDOS("Pedidos", Icons.Filled.ShoppingBag),
    NUEVA_ORDEN("Nueva orden", Icons.Filled.AddCircle),
    OPORTUNIDADES("Oportunidades", Icons.Filled.Storefront),
    PERFIL("Perfil", Icons.Filled.Person)
}

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla principal
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DashboardProveedorScreen(
    onNuevaOportunidadClick: () -> Unit = {},
    viewModel: DashboardProveedorViewModel = viewModel()
) {
    // Los datos (pedidos y catálogo) bajan desde el ViewModel.
    val pedidosPendientes by viewModel.pedidosPendientes.collectAsState()
    val catalogo by viewModel.catalogo.collectAsState()

    // rememberSaveable: la pestaña seleccionada sobrevive a la rotación.
    var selectedTab by rememberSaveable { mutableStateOf(NavTabProveedor.DASHBOARD) }

    Scaffold(
        containerColor = MiniMaxBackground,
        bottomBar = {
            ProveedorBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    when (tab) {
                        NavTabProveedor.NUEVA_ORDEN -> onNuevaOportunidadClick()
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
            item { ProveedorHeader() }
            item { ResumenProveedorBlock(onNuevaOportunidadClick) }
            item { MetricasGrid() }
            item { PedidosPendientesSection(pedidosPendientes) }
            item { CatalogoSection(catalogo) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProveedorHeader() {
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
                IconButton(onClick = { /* drawer futuro */ }) {
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
                    Text(
                        text = "M",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MiniMax",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            // Notificaciones + Avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* notificaciones futuro */ }) {
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
                    Text(
                        text = "JP",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bloque principal: título + subtítulo + botón CTA
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ResumenProveedorBlock(onNuevaOportunidadClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Resumen de Proveedor",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MiniMaxTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Gestiona tus ofertas activas y monitorea el pulso de tus grupos de compra colaborativa.",
            fontSize = 14.sp,
            color = MiniMaxTextPrimary.copy(alpha = 0.60f),
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = onNuevaOportunidadClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MiniMaxAccent)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nuevo Grupo de Compra",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Métricas: grilla 2×2
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MetricasGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricaCard(
                modifier = Modifier.weight(1f),
                titulo = "Volumen total de ventas",
                valor = "$142,500",
                icon = Icons.Filled.AttachMoney,
                iconBgColor = MiniMaxTeal
            )
            MetricaCard(
                modifier = Modifier.weight(1f),
                titulo = "Grupos activos",
                valor = "24",
                icon = Icons.Filled.Group,
                iconBgColor = MiniMaxPrimary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricaCard(
                modifier = Modifier.weight(1f),
                titulo = "Ahorro promedio",
                valor = "18%",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconBgColor = MiniMaxAccent
            )
            MetricaCard(
                modifier = Modifier.weight(1f),
                titulo = "Meta alcanzada",
                valor = "85%",
                icon = Icons.Filled.Flag,
                iconBgColor = MiniMaxOrange
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun MetricaCard(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    icon: ImageVector,
    iconBgColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBgColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = valor,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MiniMaxTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 15.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sección: Pedidos Pendientes
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PedidosPendientesSection(pedidos: List<PedidoPendiente>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Pedidos Pendientes (Mínimo Alcanzado)",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MiniMaxTextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        pedidos.forEach { pedido ->
            PedidoPendienteCard(pedido)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PedidoPendienteCard(pedido: PedidoPendiente) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Nombre + badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = pedido.nombre,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MiniMaxTextPrimary,
                    modifier = Modifier.weight(1f),
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MiniMaxTeal.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Mínimo ✓",
                        color = MiniMaxTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info chips en una fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoChip(icon = Icons.Filled.Group, texto = "${pedido.compradores} compradores")
                Spacer(modifier = Modifier.width(14.dp))
                InfoChip(icon = Icons.Filled.AttachMoney, texto = pedido.monto)
                Spacer(modifier = Modifier.weight(1f))
                InfoChip(icon = Icons.Filled.AccessTime, texto = "Cierra en ${pedido.cierraEn}")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botón acción
            Button(
                onClick = { /* validar lote futuro */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MiniMaxPrimary)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Validar Lote",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = texto,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sección: Gestión de Catálogo
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CatalogoSection(catalogo: List<ProductoCatalogo>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión de Catálogo",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MiniMaxTextPrimary
            )
            TextButton(onClick = { /* ver catálogo completo futuro */ }) {
                Text(
                    text = "Ver todo",
                    color = MiniMaxAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        catalogo.forEach { producto ->
            ProductoCatalogoItem(producto)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProductoCatalogoItem(producto: ProductoCatalogo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono producto
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MiniMaxPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Inventory2,
                    contentDescription = null,
                    tint = MiniMaxPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nombre + unidades
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MiniMaxTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${producto.unidades} unidades",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Precio
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = producto.precio,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MiniMaxTextPrimary
                )
                Text(
                    text = "por unidad",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Bar (proveedor)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProveedorBottomBar(
    selectedTab: NavTabProveedor,
    onTabSelected: (NavTabProveedor) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavTabProveedor.entries.forEach { tab ->
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

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DashboardProveedorScreenPreview() {
    MiniMaxTheme {
        DashboardProveedorScreen()
    }
}
