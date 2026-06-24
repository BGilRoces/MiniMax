package net.eltiburon.minimax.ui.proveedor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import net.eltiburon.minimax.ui.common.UriImage
import net.eltiburon.minimax.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla principal
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DashboardProveedorScreen(
    onNuevaOportunidadClick: () -> Unit = {},
    onOportunidadClick: (String) -> Unit = {},
    onOportunidadEditClick: (String) -> Unit = {},
    onCatalogoClick: () -> Unit = {},
    viewModel: DashboardProveedorViewModel = viewModel()
) {
    // Los datos (pedidos y catálogo) bajan desde el ViewModel.
    val pedidosPendientes by viewModel.pedidosPendientes.collectAsState()
    val catalogo by viewModel.catalogo.collectAsState()

    // Id pendiente de confirmación de borrado (null = no se está mostrando el diálogo).
    var idAEliminar by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Pedido en proceso de validación: si no es null, se muestra el diálogo de confirmación.
    var pedidoAValidar by remember { mutableStateOf<PedidoPendiente?>(null) }

    idAEliminar?.let { id ->
        AlertDialog(
            onDismissRequest = { idAEliminar = null },
            title = { Text("¿Eliminar esta oportunidad?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarOportunidad(id)
                        idAEliminar = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { idAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    // La top bar y la bottom bar las dibuja el Scaffold persistente del NavHost; acá solo el contenido
    // (más un SnackbarHost propio para los avisos de validación de lotes).
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item { ResumenProveedorBlock(onNuevaOportunidadClick) }
            item { MetricasGrid() }
            item {
                PedidosPendientesSection(
                    pedidos = pedidosPendientes,
                    onValidar = { pedidoAValidar = it }
                )
            }
            item {
                CatalogoSection(
                    catalogo = catalogo,
                    onProductoClick = onOportunidadClick,
                    onEditarClick = onOportunidadEditClick,
                    onEliminarClick = { id -> idAEliminar = id },
                    onVerTodo = onCatalogoClick
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    pedidoAValidar?.let { pedido ->
        AlertDialog(
            onDismissRequest = { pedidoAValidar = null },
            title = { Text("Validar lote") },
            text = { Text("¿Confirmás la validación del lote \"${pedido.nombre}\"? Los compradores serán notificados.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.validarPedido(pedido.id)
                    pedidoAValidar = null
                    scope.launch { snackbarHostState.showSnackbar("Lote validado correctamente") }
                }) {
                    Text("Validar", color = MiniMaxPrimary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pedidoAValidar = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Gestiona tus ofertas activas y monitorea el pulso de tus grupos de compra colaborativa.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f),
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = onNuevaOportunidadClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
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
    // Misma fuente que el perfil del proveedor: las métricas viven en metricasProveedor().
    val metricas = metricasProveedor()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        metricas.chunked(2).forEachIndexed { index, fila ->
            if (index > 0) Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                fila.forEach { metrica ->
                    MetricaCard(
                        modifier = Modifier.weight(1f),
                        titulo = metrica.titulo,
                        valor = metrica.valor,
                        icon = metrica.icon,
                        iconBgColor = metrica.color
                    )
                }
                // Completa la fila si quedó impar, para que la card no ocupe todo el ancho.
                if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sección: Pedidos Pendientes
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PedidosPendientesSection(
    pedidos: List<PedidoPendiente>,
    onValidar: (PedidoPendiente) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Pedidos Pendientes (Mínimo Alcanzado)",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (pedidos.isEmpty()) {
            Text(
                text = "No hay lotes pendientes de validación.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        pedidos.forEach { pedido ->
            PedidoPendienteCard(pedido, onValidar = { onValidar(pedido) })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PedidoPendienteCard(pedido: PedidoPendiente, onValidar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Mínimo ✓",
                        color = MaterialTheme.colorScheme.tertiary,
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
                onClick = onValidar,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = texto,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sección: Gestión de Catálogo
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CatalogoSection(
    catalogo: List<ProductoCatalogo>,
    onProductoClick: (String) -> Unit,
    onEditarClick: (String) -> Unit,
    onEliminarClick: (String) -> Unit,
    onVerTodo: () -> Unit
) {
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
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = onVerTodo) {
                Text(
                    text = "Ver todo",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        catalogo.forEach { producto ->
            ProductoCatalogoItem(
                producto = producto,
                onClick = { onProductoClick(producto.id) },
                onEditarClick = { onEditarClick(producto.id) },
                onEliminarClick = { onEliminarClick(producto.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProductoCatalogoItem(
    producto: ProductoCatalogo,
    onClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto (con fallback a ícono si no tiene)
            val uri = producto.imagenUri
            if (uri != null) {
                UriImage(
                    uri = uri,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nombre + unidades
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${producto.unidades} unidades",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Precio
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = producto.precio,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "por unidad",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Acciones: editar / eliminar
            IconButton(onClick = onEditarClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar oportunidad",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onEliminarClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar oportunidad",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
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
