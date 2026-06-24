package net.eltiburon.minimax.ui.proveedor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.ui.theme.*

/**
 * Pantalla unificada de gestión del proveedor. Antes "Pedidos recibidos" y "Grupos publicados"
 * eran dos ítems separados de la bottom bar que mostraban vistas muy parecidas; acá se combinan
 * en una sola sección con pestañas, liberando un lugar en la barra para el Catálogo.
 */
@Composable
fun VentasProveedorScreen() {
    var pestania by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiniMaxBackground)
    ) {
        TabRow(
            selectedTabIndex = pestania,
            containerColor = Color.White,
            contentColor = MiniMaxPrimary
        ) {
            VentasTab("Pedidos recibidos", pestania == 0) { pestania = 0 }
            VentasTab("Grupos publicados", pestania == 1) { pestania = 1 }
        }

        when (pestania) {
            0 -> PedidosProveedorScreen()
            else -> OportunidadesProveedorScreen()
        }
    }
}

@Composable
private fun VentasTab(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
    Tab(
        selected = seleccionada,
        onClick = onClick,
        text = {
            Text(
                text = texto,
                fontSize = 13.sp,
                fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium
            )
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VentasProveedorScreenPreview() {
    MiniMaxTheme {
        VentasProveedorScreen()
    }
}
