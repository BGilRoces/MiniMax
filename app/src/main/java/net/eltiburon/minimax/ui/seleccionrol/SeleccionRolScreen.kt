package net.eltiburon.minimax.ui.seleccionrol

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import net.eltiburon.minimax.data.UsuarioRepository
import net.eltiburon.minimax.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla de selección de rol
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SeleccionRolScreen(
    onCompradorClick: () -> Unit = {},
    onProveedorClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo / Badge ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Título ────────────────────────────────────────────────────
            Text(
                text = "¿Cómo usarás MiniMax?",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Elegí tu perfil para personalizar\ntu experiencia de compra colaborativa.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Card Comprador ────────────────────────────────────────────
            RolCard(
                icon = Icons.Filled.ShoppingCart,
                iconBg = MaterialTheme.colorScheme.tertiary,
                titulo = "Comprador",
                descripcion = "Explorá grupos de compra activos y sumarte para acceder a precios mayoristas.",
                badge = "Personal",
                badgeBg = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                badgeColor = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    UsuarioRepository.setRol("comprador")
                    onCompradorClick()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Card Proveedor ────────────────────────────────────────────
            RolCard(
                icon = Icons.Filled.Storefront,
                iconBg = MaterialTheme.colorScheme.secondary,
                titulo = "Proveedor",
                descripcion = "Publicá oportunidades de venta, gestioná grupos y validá pedidos mayoristas.",
                badge = "Negocio",
                badgeBg = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                badgeColor = MaterialTheme.colorScheme.secondary,
                onClick = {
                    UsuarioRepository.setRol("proveedor")
                    onProveedorClick()
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Podés cambiar esto más adelante desde tu perfil.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.40f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Card de rol
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RolCard(
    icon: ImageVector,
    iconBg: Color,
    titulo: String,
    descripcion: String,
    badge: String,
    badgeBg: Color,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = 1.5.dp,
                color = iconBg.copy(alpha = 0.25f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBg,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = badgeColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = iconBg.copy(alpha = 0.60f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SeleccionRolScreenPreview() {
    MiniMaxTheme {
        SeleccionRolScreen()
    }
}