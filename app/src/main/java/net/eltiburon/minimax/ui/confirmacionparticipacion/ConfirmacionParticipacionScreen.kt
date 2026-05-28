package net.eltiburon.minimax.ui.confirmacionparticipacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.eltiburon.minimax.ui.theme.*

private val MiniMaxSuccess = Color(0xFF10B981)

private const val NOMBRE_PRODUCTO  = "Caja de Aceite de Oliva Premium x12"
private const val PROVEEDOR        = "Olivares del Valle"
private const val CATEGORIA        = "Alimentos & Bebidas"
private const val PRECIO_MAYORISTA = 34_000
private const val PRECIO_UNITARIO  = 42_500

private fun formatearPrecio(valor: Int): String =
    "$" + String.format(java.util.Locale.US, "%,d", valor).replace(",", ".")

@Composable
fun ConfirmacionParticipacionScreen(
    cantidadSeleccionada: Int = 1,
    onBackClick: () -> Unit = {},
    onVerMisComprasClick: () -> Unit = {},
    onVolverInicioClick: () -> Unit = {}
) {
    val subtotal = cantidadSeleccionada * PRECIO_MAYORISTA
    val ahorro   = cantidadSeleccionada * (PRECIO_UNITARIO - PRECIO_MAYORISTA)

    Scaffold(
        containerColor = MiniMaxBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            item { ConfirmacionHeader(onBackClick = onBackClick) }

            item {
                ExitoBlock(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                )
            }

            item {
                ResumenParticipacionCard(
                    cantidadSeleccionada = cantidadSeleccionada,
                    subtotal             = subtotal,
                    ahorro               = ahorro,
                    modifier             = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                ProximosPasosCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                BotonesNavegacion(
                    onVerMisCompras  = onVerMisComprasClick,
                    onVolverInicio   = onVolverInicioClick,
                    modifier         = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ConfirmacionHeader(onBackClick: () -> Unit) {
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
                    text = "Confirmación",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 12.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ExitoBlock(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(48.dp))
                .background(MiniMaxSuccess.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(MiniMaxSuccess.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MiniMaxSuccess,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "¡Te sumaste al grupo!",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MiniMaxTextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Tu participación fue registrada correctamente.",
            fontSize = 15.sp,
            color = MiniMaxTextPrimary.copy(alpha = 0.68f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Podés seguir el avance del grupo desde Mis compras.",
            fontSize = 13.sp,
            color = MiniMaxAccent,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.sp
        )
    }
}

@Composable
private fun ResumenParticipacionCard(
    cantidadSeleccionada: Int,
    subtotal: Int,
    ahorro: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Receipt,
                    contentDescription = null,
                    tint = MiniMaxAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Resumen de tu participación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MiniMaxTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ResumenFila(label = "Producto", valor = NOMBRE_PRODUCTO, multilinea = true)
            Spacer(modifier = Modifier.height(8.dp))
            ResumenFila(label = "Proveedor", valor = PROVEEDOR)
            Spacer(modifier = Modifier.height(8.dp))
            ResumenFila(label = "Categoría", valor = CATEGORIA)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFE5E7EB)
            )

            ResumenFila(
                label = "Cantidad seleccionada",
                valor = "$cantidadSeleccionada ${if (cantidadSeleccionada == 1) "caja" else "cajas"}"
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResumenFila(
                label = "Precio por caja",
                valor = formatearPrecio(PRECIO_MAYORISTA)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResumenFila(
                label = "Subtotal estimado",
                valor = formatearPrecio(subtotal),
                negrita = true
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MiniMaxSuccess.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Savings,
                        contentDescription = null,
                        tint = MiniMaxSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ahorro estimado",
                        fontSize = 13.sp,
                        color = MiniMaxSuccess,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "- ${formatearPrecio(ahorro)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiniMaxSuccess
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MiniMaxTeal.copy(alpha = 0.10f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Groups,
                    contentDescription = null,
                    tint = MiniMaxTeal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "Estado: ",
                    fontSize = 13.sp,
                    color = MiniMaxTextPrimary.copy(alpha = 0.65f)
                )
                Text(
                    text = "Grupo activo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MiniMaxTeal
                )
            }
        }
    }
}

@Composable
private fun ResumenFila(
    label: String,
    valor: String,
    negrita: Boolean = false,
    multilinea: Boolean = false
) {
    if (multilinea) {
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MiniMaxTextPrimary.copy(alpha = 0.55f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = if (negrita) FontWeight.Bold else FontWeight.SemiBold,
                color = MiniMaxTextPrimary,
                lineHeight = 20.sp
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = MiniMaxTextPrimary.copy(alpha = 0.55f)
            )
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal,
                color = MiniMaxTextPrimary
            )
        }
    }
}

@Composable
private fun ProximosPasosCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MiniMaxPrimary.copy(alpha = 0.04f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "¿Qué sigue?",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MiniMaxPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            PasoItem(
                icon = Icons.Filled.Notifications,
                iconColor = MiniMaxAccent,
                texto = "Seguiremos notificándote cuando el grupo avance."
            )
            Spacer(modifier = Modifier.height(10.dp))
            PasoItem(
                icon = Icons.Filled.Groups,
                iconColor = MiniMaxTeal,
                texto = "La compra se confirma cuando se alcanza el mínimo mayorista."
            )
            Spacer(modifier = Modifier.height(10.dp))
            PasoItem(
                icon = Icons.Filled.Info,
                iconColor = Color(0xFF6B7280),
                texto = "Esta es una demo universitaria, no se realizó ningún pago real."
            )
        }
    }
}

@Composable
private fun PasoItem(icon: ImageVector, iconColor: Color, texto: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            color = MiniMaxTextPrimary.copy(alpha = 0.70f),
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun BotonesNavegacion(
    onVerMisCompras: () -> Unit,
    onVolverInicio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onVerMisCompras,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MiniMaxAccent)
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingBag,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ver mis compras",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        OutlinedButton(
            onClick = onVolverInicio,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MiniMaxPrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MiniMaxPrimary)
        ) {
            Text(
                text = "Volver al inicio",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfirmacionParticipacionScreenPreview() {
    net.eltiburon.minimax.ui.theme.MiniMaxTheme {
        ConfirmacionParticipacionScreen(cantidadSeleccionada = 2)
    }
}
