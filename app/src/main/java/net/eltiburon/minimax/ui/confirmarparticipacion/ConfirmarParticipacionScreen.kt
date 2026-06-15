package net.eltiburon.minimax.ui.confirmarparticipacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import net.eltiburon.minimax.model.ProductoParticipacion
import net.eltiburon.minimax.ui.common.MiniMaxTopBar
import net.eltiburon.minimax.ui.participacion.ResumenParticipacionViewModel
import net.eltiburon.minimax.ui.theme.*
import net.eltiburon.minimax.util.formatearPrecio

@Composable
fun ConfirmarParticipacionScreen(
    cantidadSeleccionada: Int = 1,
    onBackClick: () -> Unit = {},
    onConfirmarClick: () -> Unit = {},
    viewModel: ResumenParticipacionViewModel = viewModel()
) {
    // La cantidad llega por navegación; se la pasamos al ViewModel para que calcule el resumen.
    LaunchedEffect(cantidadSeleccionada) { viewModel.setCantidad(cantidadSeleccionada) }

    val producto by viewModel.producto.collectAsState()
    val cantidad by viewModel.cantidad.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val ahorro by viewModel.ahorro.collectAsState()

    Scaffold(
        containerColor = MiniMaxBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {

            item { MiniMaxTopBar(subtitulo = "Confirmar participación", onBackClick = onBackClick) }

            item {
                ResumenProductoCard(
                    producto = producto,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            item {
                ParticipacionCard(
                    cantidadSeleccionada = cantidad,
                    precioMayorista = producto.precioMayorista,
                    subtotal = subtotal,
                    ahorro = ahorro,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                EstadoGrupoCard(
                    producto = producto,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                CondicionesCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                MetodoPagoCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            item {
                BotonesConfirmacion(
                    onVolver = onBackClick,
                    onConfirmar = onConfirmarClick,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ResumenProductoCard(producto: ProductoParticipacion, modifier: Modifier = Modifier) {
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
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MiniMaxTeal.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Inventory2,
                    contentDescription = null,
                    tint = MiniMaxTeal,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeOfertaGrupal()
                    Text(
                        text = formatearPrecio(producto.precioMayorista),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MiniMaxTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MiniMaxTextPrimary,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                InfoMiniFila(icon = Icons.Filled.Store, texto = producto.proveedor)
                Spacer(modifier = Modifier.height(3.dp))
                InfoMiniFila(icon = Icons.Filled.Category, texto = producto.categoria)
            }
        }
    }
}

@Composable
private fun BadgeOfertaGrupal() {
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
}

@Composable
private fun InfoMiniFila(icon: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = texto, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun ParticipacionCard(
    cantidadSeleccionada: Int,
    precioMayorista: Int,
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
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = null,
                    tint = MiniMaxAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "Tu participación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MiniMaxTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            FilaParticipacion(
                label = "Cantidad seleccionada",
                valor = "$cantidadSeleccionada ${if (cantidadSeleccionada == 1) "caja" else "cajas"}",
                valorColor = MiniMaxTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FilaParticipacion(
                label = "Precio por caja",
                valor = formatearPrecio(precioMayorista),
                valorColor = MiniMaxTextPrimary
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFE5E7EB)
            )

            FilaParticipacion(
                label = "Subtotal estimado",
                valor = formatearPrecio(subtotal),
                valorColor = MiniMaxTextPrimary,
                negrita = true,
                fontSize = 16
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MiniMaxTeal.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MiniMaxTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ahorro estimado",
                        fontSize = 13.sp,
                        color = MiniMaxTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "- ${formatearPrecio(ahorro)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiniMaxTeal
                )
            }
        }
    }
}

@Composable
private fun FilaParticipacion(
    label: String,
    valor: String,
    valorColor: Color,
    negrita: Boolean = false,
    fontSize: Int = 14
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MiniMaxTextPrimary.copy(alpha = 0.60f)
        )
        Text(
            text = valor,
            fontSize = fontSize.sp,
            fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal,
            color = valorColor
        )
    }
}

@Composable
private fun EstadoGrupoCard(producto: ProductoParticipacion, modifier: Modifier = Modifier) {
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
                    text = "Estado del grupo",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MiniMaxTextPrimary
                )
                Text(
                    text = "${(producto.progresoGrupo * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MiniMaxTeal
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { producto.progresoGrupo },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MiniMaxTeal,
                trackColor = Color(0xFFE5E7EB)
            )
            Spacer(modifier = Modifier.height(14.dp))
            InfoGrupoFila(
                icon = Icons.Filled.Info,
                iconColor = MiniMaxOrange,
                texto = "Faltan sólo ${producto.cajasFaltantes} cajas para alcanzar el precio mayorista."
            )
            Spacer(modifier = Modifier.height(8.dp))
            InfoGrupoFila(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconColor = MiniMaxTeal,
                texto = "Tu participación ayuda a acercar el grupo al mínimo mayorista."
            )
        }
    }
}

@Composable
private fun InfoGrupoFila(icon: ImageVector, iconColor: Color, texto: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(16.dp)
                .offset(y = 1.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = texto,
            fontSize = 12.sp,
            color = MiniMaxTextPrimary.copy(alpha = 0.68f),
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun CondicionesCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MiniMaxPrimary.copy(alpha = 0.04f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Condiciones",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MiniMaxPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            CondicionItem(
                icon = Icons.Filled.Groups,
                iconColor = MiniMaxAccent,
                texto = "La compra se confirma cuando el grupo alcanza el mínimo requerido."
            )
            Spacer(modifier = Modifier.height(10.dp))
            CondicionItem(
                icon = Icons.Filled.Lock,
                iconColor = Color(0xFF6B7280),
                texto = "No se realizará ningún pago real en esta demo."
            )
            Spacer(modifier = Modifier.height(10.dp))
            CondicionItem(
                icon = Icons.Filled.Notifications,
                iconColor = MiniMaxAccent,
                texto = "Recibirás una notificación mock cuando el grupo avance."
            )
        }
    }
}

@Composable
private fun CondicionItem(icon: ImageVector, iconColor: Color, texto: String) {
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
private fun MetodoPagoCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Método de pago",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MiniMaxTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp))
                        .background(MiniMaxAccent)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                        .background(MiniMaxAccent.copy(alpha = 0.06f))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MiniMaxPrimary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CreditCard,
                            contentDescription = null,
                            tint = MiniMaxPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Seña pendiente / Demo",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MiniMaxTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pago simulado para MVP universitario",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = MiniMaxTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonesConfirmacion(
    onVolver: () -> Unit,
    onConfirmar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onConfirmar,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MiniMaxAccent)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Confirmar participación",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MiniMaxPrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MiniMaxPrimary)
        ) {
            Text(
                text = "Volver",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfirmarParticipacionScreenPreview() {
    MiniMaxTheme {
        ConfirmarParticipacionScreen(cantidadSeleccionada = 2)
    }
}
