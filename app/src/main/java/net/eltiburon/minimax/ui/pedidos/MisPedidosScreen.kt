package net.eltiburon.minimax.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.eltiburon.minimax.ui.common.MiniMaxTopBar
import net.eltiburon.minimax.ui.theme.*
import net.eltiburon.minimax.util.formatearPrecio

@Composable
fun MisPedidosScreen(
    onBack: () -> Unit = {},
    viewModel: MisPedidosViewModel = viewModel()
) {
    val pedidos by viewModel.pedidos.collectAsState()
    val filtro by viewModel.filtroEstado.collectAsState()

    Scaffold(
        containerColor = MiniMaxBackground,
        topBar = { MiniMaxTopBar(subtitulo = "Mis Pedidos", onBackClick = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FiltroEstadoRow(filtro = filtro, onFiltroChange = viewModel::onFiltroChange)

            if (pedidos.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pedidos, key = { it.id }) { pedido ->
                        PedidoCard(pedido)
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltroEstadoRow(filtro: EstadoPedido?, onFiltroChange: (EstadoPedido?) -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FiltroChip(texto = "Todos", seleccionado = filtro == null, onClick = { onFiltroChange(null) })
        EstadoPedido.entries.forEach { estado ->
            FiltroChip(
                texto = estado.label,
                seleccionado = filtro == estado,
                onClick = { onFiltroChange(estado) }
            )
        }
    }
}

@Composable
private fun FiltroChip(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (seleccionado) MiniMaxPrimary else Color(0xFFF3F4F6))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = texto,
            color = if (seleccionado) Color.White else Color.Gray,
            fontSize = 13.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun PedidoCard(pedido: Pedido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MiniMaxPrimary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = null,
                        tint = MiniMaxPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pedido.producto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MiniMaxTextPrimary
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(text = pedido.proveedor, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                EstadoBadge(pedido.estado)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Inventory2,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = "${pedido.cantidad} u.", fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text(text = pedido.fecha, fontSize = 13.sp, color = Color.Gray)
                }
                Text(
                    text = formatearPrecio(pedido.total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MiniMaxTextPrimary
                )
            }
        }
    }
}

@Composable
private fun EstadoBadge(estado: EstadoPedido) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(estado.color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado.label,
            color = estado.color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.ShoppingBag,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No hay pedidos en este estado",
            color = Color.Gray,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MisPedidosScreenPreview() {
    MiniMaxTheme {
        MisPedidosScreen()
    }
}
