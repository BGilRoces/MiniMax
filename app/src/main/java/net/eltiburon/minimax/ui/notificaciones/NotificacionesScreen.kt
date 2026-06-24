package net.eltiburon.minimax.ui.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
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
import net.eltiburon.minimax.data.Notificacion
import net.eltiburon.minimax.ui.common.MiniMaxTopBar
import net.eltiburon.minimax.ui.theme.*

@Composable
fun NotificacionesScreen(
    viewModel: NotificacionesViewModel = viewModel()
) {
    val notificaciones by viewModel.notificaciones.collectAsState()
    val noLeidas = notificaciones.count { !it.leida }

    // La top bar y la bottom bar las dibuja el Scaffold persistente del NavHost; acá solo el contenido.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MiniMaxBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (noLeidas > 0) "Tenés $noLeidas sin leer" else "Estás al día",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MiniMaxTextPrimary
                    )
                    if (noLeidas > 0) {
                        TextButton(onClick = viewModel::marcarTodasLeidas) {
                            Text(
                                text = "Marcar todas leídas",
                                color = MiniMaxAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (notificaciones.isEmpty()) {
                item { EmptyState() }
            } else {
                items(notificaciones, key = { it.id }) { notif ->
                    NotificacionCard(notif = notif, onClick = { viewModel.marcarLeida(notif.id) })
                }
            }
        }
}

@Composable
private fun NotificacionCard(notif: Notificacion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.leida) Color.White else MiniMaxAccent.copy(alpha = 0.06f)
        ),
        // Misma elevación para leídas y no leídas: las no abiertas ya no tienen ese borde/sombra
        // gris más marcado; el estado sin leer se distingue por el fondo y el punto rojo.
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(notif.tipo.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notif.tipo.icon,
                    contentDescription = null,
                    tint = notif.tipo.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notif.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MiniMaxTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notif.leida) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MiniMaxBadgeRed)
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = notif.descripcion,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = notif.tiempo,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.NotificationsNone,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No tenés notificaciones",
            color = Color.Gray,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotificacionesScreenPreview() {
    MiniMaxTheme {
        NotificacionesScreen()
    }
}
