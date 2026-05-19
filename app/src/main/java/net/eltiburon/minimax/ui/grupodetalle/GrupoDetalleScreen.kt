package net.eltiburon.minimax.ui.grupodetalle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.eltiburon.minimax.model.GrupoDetalle
import net.eltiburon.minimax.ui.theme.*

// ── Entry point ──────────────────────────────────────────────────────────────

@Composable
fun GrupoDetalleScreen(
    grupoId: String,
    onBack: () -> Unit,
    viewModel: GrupoDetalleViewModel = viewModel()
) {
    val grupo by viewModel.grupo.collectAsState()
    val meUni by viewModel.meUni.collectAsState()

    LaunchedEffect(grupoId) { viewModel.cargarGrupo(grupoId) }

    grupo?.let { g ->
        GrupoDetalleContent(grupo = g, meUni = meUni, onBack = onBack, onToggleUnirse = viewModel::toggleUnirse)
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MiniMaxPrimary)
    }
}

// ── Layout principal ─────────────────────────────────────────────────────────

@Composable
private fun GrupoDetalleContent(
    grupo: GrupoDetalle,
    meUni: Boolean,
    onBack: () -> Unit,
    onToggleUnirse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiniMaxBackground)
            .verticalScroll(rememberScrollState())
    ) {
        DetalleHeader(nombre = grupo.nombre, onBack = onBack)
        HeroImageSection(grupo = grupo)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            PrecioBlock(grupo = grupo)
            Spacer(modifier = Modifier.height(12.dp))
            ProgresoSection(grupo = grupo)
            Spacer(modifier = Modifier.height(12.dp))
            InfoChipsRow(grupo = grupo)
            Spacer(modifier = Modifier.height(20.dp))
            CTAButton(meUni = meUni, onToggle = onToggleUnirse)
            Spacer(modifier = Modifier.height(12.dp))
            TrustIconsRow()
            Spacer(modifier = Modifier.height(16.dp))
            PulsoColectivoCard(grupo = grupo)
            Spacer(modifier = Modifier.height(16.dp))
            ProductInfoSection(grupo = grupo)
            Spacer(modifier = Modifier.height(16.dp))
            AttributeChipsRow(grupo = grupo)
        }

        Spacer(modifier = Modifier.height(20.dp))
        ProveedorSection(grupo = grupo)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun DetalleHeader(nombre: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiniMaxPrimary)
            .statusBarsPadding()
            .padding(end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "Marketplace > Alimentos & Bebidas",
                    color = Color.White.copy(alpha = 0.60f),
                    fontSize = 11.sp
                )
                Text(
                    text = nombre,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ── Hero ─────────────────────────────────────────────────────────────────────

@Composable
private fun HeroImageSection(grupo: GrupoDetalle) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Image(
            painter = painterResource(id = grupo.imagenRes),
            contentDescription = grupo.nombre,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)),
                        startY = 120f
                    )
                )
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HeroBadge(text = "OFERTA GRUPAL", color = MiniMaxTeal)
            HeroBadge(text = "PREMIUM SELECT", color = Color(0xFF374151))
        }
    }
}

@Composable
private fun HeroBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Precios ──────────────────────────────────────────────────────────────────

@Composable
private fun PrecioBlock(grupo: GrupoDetalle) {
    val ahorro = ((grupo.precioUnitario - grupo.precioMayorista) / grupo.precioUnitario * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Column {
                    Text(text = "Precio unitario", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = formatPrecio(grupo.precioUnitario),
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF16A34A).copy(alpha = 0.10f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "-$ahorro% Ahorro Grupal",
                        color = Color(0xFF16A34A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = formatPrecio(grupo.precioMayorista),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MiniMaxPrimary
                )
                Text(
                    text = " / caja",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Text(
                text = "Precio mayorista grupal",
                fontSize = 12.sp,
                color = MiniMaxTeal,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Progreso ─────────────────────────────────────────────────────────────────

@Composable
private fun ProgresoSection(grupo: GrupoDetalle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "${grupo.progresoActual}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MiniMaxTeal
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { grupo.progresoActual / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MiniMaxTeal,
                trackColor = MiniMaxProgressBg
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Faltan solo ${grupo.unidadesFaltantes} cajas para alcanzar el precio mayorista",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

// ── Chips informativos ───────────────────────────────────────────────────────

@Composable
private fun InfoChipsRow(grupo: GrupoDetalle) {
    val horas = grupo.minutosRestantes / 60
    val mins = grupo.minutosRestantes % 60
    val timer = "${horas.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:10"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoChip(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.AccessTime,
            iconTint = MiniMaxAccent,
            label = "Finaliza en",
            value = timer,
            bgColor = MiniMaxAccent.copy(alpha = 0.08f)
        )
        MembersChip(
            modifier = Modifier.weight(1f),
            count = grupo.miembrosActivos
        )
    }
}

@Composable
private fun InfoChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    bgColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = label, fontSize = 10.sp, color = Color.Gray)
                Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MiniMaxTextPrimary)
            }
        }
    }
}

@Composable
private fun MembersChip(modifier: Modifier = Modifier, count: Int) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MiniMaxTeal.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StackedAvatars()
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "+$count miembros", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MiniMaxTextPrimary)
                Text(text = "activos", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun StackedAvatars() {
    val avatarData = listOf(MiniMaxPrimary to "A", MiniMaxAccent to "B", MiniMaxTeal to "C")
    Box(modifier = Modifier.size(width = 58.dp, height = 26.dp)) {
        avatarData.forEachIndexed { idx, (color, initial) ->
            Box(
                modifier = Modifier
                    .absoluteOffset(x = (idx * 17).dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initial, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── CTA ──────────────────────────────────────────────────────────────────────

@Composable
private fun CTAButton(meUni: Boolean, onToggle: () -> Unit) {
    Button(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (meUni) MiniMaxTeal else MiniMaxAccent,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(
            imageVector = if (meUni) Icons.Filled.CheckCircle else Icons.Filled.Group,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (meUni) "¡Ya soy miembro!" else "Sumarme al grupo",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun TrustIconsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Pago Seguro", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.LightGray))
        Spacer(modifier = Modifier.width(16.dp))
        Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Envío a Regiones", fontSize = 12.sp, color = Color.Gray)
    }
}

// ── Pulso Colectivo ──────────────────────────────────────────────────────────

@Composable
private fun PulsoColectivoCard(grupo: GrupoDetalle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MiniMaxPrimary.copy(alpha = 0.07f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MiniMaxPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = MiniMaxPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Pulso Colectivo", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MiniMaxPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Este grupo creció un ${grupo.crecimientoPorcentaje}% en las últimas 2 horas",
                    fontSize = 13.sp,
                    color = MiniMaxTextPrimary
                )
            }
        }
    }
}

// ── Información del producto ─────────────────────────────────────────────────

@Composable
private fun ProductInfoSection(grupo: GrupoDetalle) {
    Text(
        text = grupo.nombre,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = MiniMaxTextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = grupo.descripcion,
        fontSize = 14.sp,
        color = Color.Gray,
        lineHeight = 22.sp
    )
}

@Composable
private fun AttributeChipsRow(grupo: GrupoDetalle) {
    val chips = listOf(
        Icons.Filled.Place to grupo.origen,
        Icons.Filled.Science to grupo.acidez,
        Icons.Filled.Inventory2 to "Stock: ${grupo.stockDisponible} uds."
    )
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { (icon, label) ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MiniMaxPrimary.copy(alpha = 0.07f))
                    .border(1.dp, MiniMaxPrimary.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MiniMaxPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = label, fontSize = 13.sp, color = MiniMaxPrimary)
            }
        }
    }
}

// ── Sección Proveedores ──────────────────────────────────────────────────────

private data class ProveedorCardData(
    val titulo: String,
    val descripcion: String,
    val badge: String?,
    val badgeColor: Color,
    val showCatalogLink: Boolean = false
)

@Composable
private fun ProveedorSection(grupo: GrupoDetalle) {
    val cards = listOf(
        ProveedorCardData(
            titulo = grupo.proveedorNombre,
            descripcion = grupo.proveedorDescripcion,
            badge = null,
            badgeColor = Color.Transparent,
            showCatalogLink = true
        ),
        ProveedorCardData(
            titulo = "Logística Grupal",
            descripcion = "Envíos coordinados con centros de distribución regionales. Entregas en 48-72h hábiles.",
            badge = "Logística Eficiente",
            badgeColor = MiniMaxTeal
        ),
        ProveedorCardData(
            titulo = "Garantía MiniMax",
            descripcion = "Si el grupo no alcanza el mínimo, tu pago es devuelto automáticamente sin demoras.",
            badge = "Compra Protegida",
            badgeColor = MiniMaxAccent
        )
    )

    Column {
        Text(
            text = "Información del Proveedor",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MiniMaxTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            cards.forEach { card -> ProveedorCardItem(card = card) }
        }
    }
}

@Composable
private fun ProveedorCardItem(card: ProveedorCardData) {
    Card(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = card.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MiniMaxPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (card.badge != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(card.badgeColor.copy(alpha = 0.12f))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = card.badge,
                            color = card.badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = card.descripcion,
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 18.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            if (card.showCatalogLink) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ver catálogo completo →",
                    fontSize = 12.sp,
                    color = MiniMaxAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun formatPrecio(precio: Double): String {
    val str = precio.toLong().toString()
    return "$" + str.reversed().chunked(3).joinToString(".").reversed()
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GrupoDetallePreview() {
    net.eltiburon.minimax.ui.theme.MiniMaxTheme {
        GrupoDetalleScreen(grupoId = "1", onBack = {})
    }
}
