package net.eltiburon.minimax.ui.proveedor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Inventory2
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
import net.eltiburon.minimax.model.Oportunidad
import net.eltiburon.minimax.ui.common.UriImage
import net.eltiburon.minimax.ui.theme.*
import net.eltiburon.minimax.util.formatearPrecio

@Composable
fun OportunidadesProveedorScreen(
    onGrupoClick: (String) -> Unit = {},
    viewModel: OportunidadesProveedorViewModel = viewModel()
) {
    val oportunidades by viewModel.oportunidades.collectAsState()

    // La top bar y la bottom bar las dibuja el Scaffold persistente del NavHost; acá solo el contenido.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MiniMaxBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Grupos publicados (${oportunidades.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MiniMaxTextPrimary
            )
        }
        items(oportunidades, key = { it.id }) { oportunidad ->
            OportunidadCard(oportunidad, onClick = { onGrupoClick(oportunidad.id) })
        }
    }
}

@Composable
private fun OportunidadCard(oportunidad: Oportunidad, onClick: () -> Unit) {
    val minimoAlcanzado = oportunidad.unidadesFaltantes <= 0

    Card(
        onClick = onClick,
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
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    val uri = oportunidad.imagenUri
                    if (uri != null) {
                        UriImage(
                            uri = uri,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MiniMaxPrimary.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = null,
                                tint = MiniMaxPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = oportunidad.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MiniMaxTextPrimary
                    )
                }
                val (badgeColor, badgeText) = if (minimoAlcanzado) {
                    MiniMaxTeal to "Mínimo ✓"
                } else {
                    MiniMaxOrange to "Activa"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = badgeText, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${oportunidad.cantidadMaxima - oportunidad.unidadesFaltantes} / ${oportunidad.cantidadMaxima} u.",
                    fontSize = 12.sp,
                    color = MiniMaxTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${oportunidad.progresoActual}%",
                    fontSize = 12.sp,
                    color = MiniMaxAccent,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { oportunidad.progresoActual / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (minimoAlcanzado) MiniMaxTeal else MiniMaxPrimary,
                trackColor = MiniMaxProgressBg
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Cierra en ${oportunidad.tiempoRestanteTexto}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "${formatearPrecio(oportunidad.precioMayorista)} / u.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MiniMaxTextPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OportunidadesProveedorScreenPreview() {
    MiniMaxTheme {
        OportunidadesProveedorScreen()
    }
}
