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
import net.eltiburon.minimax.ui.common.UriImage
import net.eltiburon.minimax.ui.theme.*
import net.eltiburon.minimax.util.formatearPrecio

// ── Entry point ──────────────────────────────────────────────────────────────

@Composable
fun GrupoDetalleScreen(
    grupoId: String,
    onBack: () -> Unit,
    onSumarseClick: () -> Unit = {},
    viewModel: GrupoDetalleViewModel = viewModel()
) {
    val grupo by viewModel.grupo.collectAsState()
    val estaUnido by viewModel.estaUnido.collectAsState()

    LaunchedEffect(grupoId) { viewModel.cargarGrupo(grupoId) }

    grupo?.let { g ->
        GrupoDetalleContent(
            grupo           = g,
            estaUnido       = estaUnido,
            onBack          = onBack,
            onToggleUnirse  = viewModel::alternarMembresia,
            onSumarseClick  = onSumarseClick
        )
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

// ── Layout principal ─────────────────────────────────────────────────────────

@Composable
private fun GrupoDetalleContent(
    grupo: GrupoDetalle,
    estaUnido: Boolean,
    onBack: () -> Unit,
    onToggleUnirse: () -> Unit,
    onSumarseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            CTAButton(estaUnido = estaUnido, onToggle = onToggleUnirse, onSumarseClick = onSumarseClick)
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
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onPrimary)
            }
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "Marketplace > Alimentos & Bebidas",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.60f),
                    fontSize = 11.sp
                )
                Text(
                    text = nombre,
                    color = MaterialTheme.colorScheme.onPrimary,
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
        val imagenUri = grupo.imagenUri
        if (imagenUri != null) {
            UriImage(uri = imagenUri, modifier = Modifier.fillMaxSize())
        } else {
            Image(
                painter = painterResource(id = grupo.imagenRes),
                contentDescription = grupo.nombre,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
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
            HeroBadge(
                text = "OFERTA GRUPAL",
                color = MaterialTheme.colorScheme.tertiary,
                textColor = MaterialTheme.colorScheme.onTertiary
            )
            // Badge decorativo de marca con fondo siempre oscuro: el texto blanco se
            // mantiene fijo a propósito (no depende del tema, a diferencia del de arriba).
            HeroBadge(text = "PREMIUM SELECT", color = Color(0xFF374151))
        }
    }
}

@Composable
private fun HeroBadge(text: String, color: Color, textColor: Color = Color.White) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Precios ──────────────────────────────────────────────────────────────────

@Composable
private fun PrecioBlock(grupo: GrupoDetalle) {
    val ahorro = ((grupo.precioUnitario - grupo.precioMayorista) / grupo.precioUnitario * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Precio unitario", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = formatearPrecio(grupo.precioUnitario),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    text = formatearPrecio(grupo.precioMayorista),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " / caja",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Text(
                text = "Precio mayorista grupal",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${grupo.progresoActual}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { grupo.progresoActual / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Faltan solo ${grupo.unidadesFaltantes} cajas para alcanzar el precio mayorista",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            iconTint = MaterialTheme.colorScheme.secondary,
            label = "Finaliza en",
            value = timer,
            bgColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
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
                Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun MembersChip(modifier: Modifier = Modifier, count: Int) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StackedAvatars()
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "+$count miembros", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "activos", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun StackedAvatars() {
    val avatarData = listOf(
        Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, "A"),
        Triple(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary, "B"),
        Triple(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary, "C")
    )
    Box(modifier = Modifier.size(width = 58.dp, height = 26.dp)) {
        avatarData.forEachIndexed { idx, (bgColor, onColor, initial) ->
            Box(
                modifier = Modifier
                    .absoluteOffset(x = (idx * 17).dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initial, color = onColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── CTA ──────────────────────────────────────────────────────────────────────

@Composable
private fun CTAButton(estaUnido: Boolean, onToggle: () -> Unit, onSumarseClick: () -> Unit) {
    Button(
        onClick = if (estaUnido) onToggle else onSumarseClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (estaUnido) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
            contentColor = if (estaUnido) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(
            imageVector = if (estaUnido) Icons.Filled.CheckCircle else Icons.Filled.Group,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (estaUnido) "¡Ya soy miembro!" else "Sumarme al grupo",
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
        Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Pago Seguro", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant))
        Spacer(modifier = Modifier.width(16.dp))
        Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Envío a Regiones", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Pulso Colectivo ──────────────────────────────────────────────────────────

@Composable
private fun PulsoColectivoCard(grupo: GrupoDetalle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Pulso Colectivo", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Este grupo creció un ${grupo.crecimientoPorcentaje}% en las últimas 2 horas",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
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
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = grupo.descripcion,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
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
            badgeColor = MaterialTheme.colorScheme.tertiary
        ),
        ProveedorCardData(
            titulo = "Garantía MiniMax",
            descripcion = "Si el grupo no alcanza el mínimo, tu pago es devuelto automáticamente sin demoras.",
            badge = "Compra Protegida",
            badgeColor = MaterialTheme.colorScheme.secondary
        )
    )

    Column {
        Text(
            text = "Información del Proveedor",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.primary,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            if (card.showCatalogLink) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ver catálogo completo →",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GrupoDetallePreview() {
    net.eltiburon.minimax.ui.theme.MiniMaxTheme {
        GrupoDetalleScreen(grupoId = "1", onBack = {})
    }
}
