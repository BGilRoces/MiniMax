package net.eltiburon.minimax.ui.miscompras

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.eltiburon.minimax.model.EstadoCompra
import net.eltiburon.minimax.model.Oportunidad
import net.eltiburon.minimax.ui.common.MiniMaxTopBar
import net.eltiburon.minimax.ui.common.UriImage
import net.eltiburon.minimax.ui.theme.*

private val TABS = listOf(EstadoCompra.ACTIVA, EstadoCompra.COMPLETADA, EstadoCompra.CANCELADA)

@Composable
fun MisComprasScreen(
    onBackClick: () -> Unit = {},
    viewModel: MisComprasViewModel = viewModel()
) {
    val tabSeleccionada by viewModel.tabSeleccionada.collectAsState()
    val compras by viewModel.comprasFiltradas.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            MiniMaxTopBar(subtitulo = "Mis compras", onBackClick = onBackClick)

            TabRow(
                selectedTabIndex = TABS.indexOf(tabSeleccionada),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                TABS.forEach { estado ->
                    Tab(
                        selected = tabSeleccionada == estado,
                        onClick = { viewModel.onTabChange(estado) },
                        text = {
                            Text(
                                text = tituloTab(estado),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }

            if (compras.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxSize().weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(compras, key = { it.participacionId }) { compra ->
                        CompraCard(
                            compra = compra,
                            onCancelar = { viewModel.cancelar(compra.participacionId) }
                        )
                    }
                }
            }
        }
    }
}

private fun tituloTab(estado: EstadoCompra): String = when (estado) {
    EstadoCompra.ACTIVA -> "Activas"
    EstadoCompra.COMPLETADA -> "Completadas"
    EstadoCompra.CANCELADA -> "Canceladas"
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Inventory2,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No tenés compras en esta categoría",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CompraCard(compra: CompraUi, onCancelar: () -> Unit) {
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("¿Cancelar esta compra?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelar()
                        mostrarConfirmacion = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Cancelar compra") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) { Text("Volver") }
            }
        )
    }

    val oportunidad = compra.oportunidad

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                OportunidadThumbnail(
                    oportunidad = oportunidad,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = oportunidad.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = oportunidad.proveedor,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cantidad comprometida: ${compra.cantidad}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }

                EstadoBadge(estado = compra.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso del grupo",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${oportunidad.progresoActual}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { oportunidad.progresoActual / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (compra.estado == EstadoCompra.ACTIVA) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { mostrarConfirmacion = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun OportunidadThumbnail(oportunidad: Oportunidad, modifier: Modifier = Modifier) {
    val uri = oportunidad.imagenUri
    if (uri != null) {
        UriImage(uri = uri, modifier = modifier)
    } else {
        Image(
            painter = painterResource(id = oportunidad.imagenRes),
            contentDescription = oportunidad.nombre,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun EstadoBadge(estado: EstadoCompra) {
    val color = when (estado) {
        EstadoCompra.ACTIVA -> MaterialTheme.colorScheme.tertiary
        EstadoCompra.COMPLETADA -> MaterialTheme.colorScheme.secondary
        EstadoCompra.CANCELADA -> MaterialTheme.colorScheme.error
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado.label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MisComprasScreenPreview() {
    MiniMaxTheme {
        MisComprasScreen()
    }
}
