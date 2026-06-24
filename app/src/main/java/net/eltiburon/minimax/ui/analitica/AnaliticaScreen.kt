package net.eltiburon.minimax.ui.analitica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
fun AnaliticaScreen(
    onBack: () -> Unit = {},
    viewModel: AnaliticaViewModel = viewModel()
) {
    val metricas by viewModel.metricas.collectAsState()
    val ahorroMensual by viewModel.ahorroMensual.collectAsState()

    Scaffold(
        containerColor = MiniMaxBackground,
        topBar = { MiniMaxTopBar(subtitulo = "Analítica", onBackClick = onBack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Resumen general",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MiniMaxTextPrimary
                )
            }

            // Métricas en grilla de 2 columnas.
            items(metricas.chunked(2).count()) { rowIndex ->
                val fila = metricas.chunked(2)[rowIndex]
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    fila.forEach { metrica ->
                        MetricaCard(metrica, modifier = Modifier.weight(1f))
                    }
                    if (fila.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Ahorro mensual",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MiniMaxTextPrimary
                )
            }

            item { GraficoBarras(ahorroMensual) }
        }
    }
}

@Composable
private fun MetricaCard(metrica: Metrica, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = metrica.titulo, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(6.dp))
            Text(
                text = metrica.valor,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MiniMaxTextPrimary
            )
            Spacer(Modifier.height(6.dp))
            val color = if (metrica.positiva) MiniMaxTeal else MiniMaxBadgeRed
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (metrica.positiva)
                        Icons.AutoMirrored.Filled.TrendingUp
                    else
                        Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = metrica.variacion,
                    color = color,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun GraficoBarras(barras: List<BarraMes>) {
    val maximo = (barras.maxOfOrNull { it.monto } ?: 1).coerceAtLeast(1)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            barras.forEach { barra ->
                val fraccion = barra.monto.toFloat() / maximo
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = formatearPrecio(barra.monto),
                        fontSize = 9.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraccion.coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(MiniMaxPrimary)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(text = barra.mes, fontSize = 11.sp, color = MiniMaxTextPrimary)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AnaliticaScreenPreview() {
    MiniMaxTheme {
        AnaliticaScreen()
    }
}
