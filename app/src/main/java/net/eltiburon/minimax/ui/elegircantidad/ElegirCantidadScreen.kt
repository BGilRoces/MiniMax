package net.eltiburon.minimax.ui.elegircantidad

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.ui.theme.*

private const val NOMBRE_PRODUCTO     = "Caja de Aceite de Oliva Premium x12"
private const val PROVEEDOR           = "Olivares del Valle"
private const val CATEGORIA           = "Alimentos & Bebidas"
private const val PRECIO_UNITARIO     = 42_500
private const val PRECIO_MAYORISTA    = 34_000
private const val DESCUENTO_PCTJE     = 20
private const val PROGRESO_GRUPO      = 0.80f
private const val CAJAS_FALTANTES     = 12
private const val CANTIDAD_MAXIMA     = 20

private fun formatearPrecio(valor: Int): String =
    "$" + String.format(java.util.Locale.US, "%,d", valor).replace(",", ".")

@Composable
fun ElegirCantidadScreen(
    onBackClick: () -> Unit = {},
    onContinuarClick: (Int) -> Unit = {}
) {
    var cantidad by rememberSaveable { mutableStateOf(1) }

    val subtotal = cantidad * PRECIO_MAYORISTA
    val ahorro   = cantidad * (PRECIO_UNITARIO - PRECIO_MAYORISTA)

    Scaffold(
        containerColor = MiniMaxBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {

            item { ElegirCantidadHeader(onBackClick = onBackClick) }

            item {
                ProductoResumenCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            item {
                BloquePrecio(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                ProgresoGrupoCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                SelectorCantidadCard(
                    cantidad = cantidad,
                    onMenos  = { if (cantidad > 1) cantidad-- },
                    onMas    = { if (cantidad < CANTIDAD_MAXIMA) cantidad++ },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                ResumenCompraCard(
                    cantidad = cantidad,
                    subtotal = subtotal,
                    ahorro   = ahorro,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                BotonesAccion(
                    onCancelar  = onBackClick,
                    onContinuar = { onContinuarClick(cantidad) },
                    modifier    = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ElegirCantidadHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiniMaxPrimary)
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
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
            Column {
                Text(
                    text = "MiniMax",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 20.sp
                )
                Text(
                    text = "Elegir cantidad",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 12.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ProductoResumenCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MiniMaxTeal.copy(alpha = 0.10f))
                    .border(1.dp, MiniMaxTeal.copy(alpha = 0.28f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Inventory2,
                    contentDescription = "Producto",
                    tint = MiniMaxTeal,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MiniMaxAccent.copy(alpha = 0.11f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "OFERTA GRUPAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MiniMaxAccent,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = NOMBRE_PRODUCTO,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MiniMaxTextPrimary,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                InfoMiniFila(icon = Icons.Filled.Store, texto = PROVEEDOR)
                Spacer(modifier = Modifier.height(3.dp))
                InfoMiniFila(icon = Icons.Filled.Category, texto = CATEGORIA)
            }
        }
    }
}

@Composable
private fun InfoMiniFila(icon: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = texto, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun BloquePrecio(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Precio unitario",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = formatearPrecio(PRECIO_UNITARIO),
                    fontSize = 17.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Precio grupal",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = formatearPrecio(PRECIO_MAYORISTA),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiniMaxTextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MiniMaxTeal)
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "-${DESCUENTO_PCTJE}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Ahorro",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.88f),
                        lineHeight = 13.sp
                    )
                    Text(
                        text = "Grupal",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.88f),
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgresoGrupoCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso del Grupo",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MiniMaxTextPrimary
                )
                Text(
                    text = "${(PROGRESO_GRUPO * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MiniMaxTeal
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { PROGRESO_GRUPO },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MiniMaxTeal,
                trackColor = Color(0xFFE5E7EB)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MiniMaxAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Faltan sólo $CAJAS_FALTANTES cajas para alcanzar el precio mayorista.",
                    fontSize = 12.sp,
                    color = MiniMaxTextPrimary.copy(alpha = 0.68f),
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun SelectorCantidadCard(
    cantidad: Int,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Cuántas cajas querés comprar?",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MiniMaxTextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val menosActivo = cantidad > 1
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (menosActivo) MiniMaxPrimary else Color(0xFFE5E7EB))
                        .clickable(enabled = menosActivo, onClick = onMenos),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Reducir cantidad",
                        tint = if (menosActivo) Color.White else Color(0xFFBDBDBD),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Text(
                    text = cantidad.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 44.sp,
                    color = MiniMaxTextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(min = 80.dp)
                        .padding(horizontal = 28.dp)
                )

                val masActivo = cantidad < CANTIDAD_MAXIMA
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (masActivo) MiniMaxTeal else Color(0xFFE5E7EB))
                        .clickable(enabled = masActivo, onClick = onMas),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Aumentar cantidad",
                        tint = if (masActivo) Color.White else Color(0xFFBDBDBD),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Máximo disponible para este grupo: $CANTIDAD_MAXIMA cajas",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ResumenCompraCard(
    cantidad: Int,
    subtotal: Int,
    ahorro: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiniMaxPrimary.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Resumen de compra",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MiniMaxTextPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            FilaResumen(
                label = "Cantidad seleccionada",
                valor = "$cantidad ${if (cantidad == 1) "caja" else "cajas"}",
                valorColor = MiniMaxTextPrimary
            )
            Spacer(modifier = Modifier.height(1.dp))
            HorizontalDivider(color = Color(0xFFE5E7EB).copy(alpha = 0.80f))
            Spacer(modifier = Modifier.height(8.dp))
            FilaResumen(
                label = "Precio por caja",
                valor = formatearPrecio(PRECIO_MAYORISTA),
                valorColor = MiniMaxTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FilaResumen(
                label = "Subtotal estimado",
                valor = formatearPrecio(subtotal),
                valorColor = MiniMaxTextPrimary,
                negrita = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            FilaResumen(
                label = "Ahorro estimado",
                valor = "- ${formatearPrecio(ahorro)}",
                valorColor = MiniMaxTeal,
                negrita = true
            )
        }
    }
}

@Composable
private fun FilaResumen(
    label: String,
    valor: String,
    valorColor: Color,
    negrita: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MiniMaxTextPrimary.copy(alpha = 0.62f)
        )
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal,
            color = valorColor
        )
    }
}

@Composable
private fun BotonesAccion(
    onCancelar: () -> Unit,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MiniMaxPrimary),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MiniMaxPrimary)
        ) {
            Text(
                text = "Cancelar",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

        Button(
            onClick = onContinuar,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MiniMaxAccent)
        ) {
            Text(
                text = "Continuar",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ElegirCantidadScreenPreview() {
    MiniMaxTheme {
        ElegirCantidadScreen()
    }
}
