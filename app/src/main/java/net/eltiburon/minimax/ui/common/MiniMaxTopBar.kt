package net.eltiburon.minimax.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Encabezado reutilizable de la app: logo "M" + "MiniMax" + subtítulo opcional + botón volver.
 *
 * Antes este mismo bloque estaba copiado en varias pantallas (ElegirCantidad, Confirmar,
 * Confirmación, NuevaOportunidad). Se extrajo a un composable "tonto" (stateless) que recibe
 * el [subtitulo] y el callback [onBackClick] por parámetro (state hoisting + DRY).
 */
@Composable
fun MiniMaxTopBar(
    subtitulo: String? = null,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
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
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "MiniMax",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (subtitulo != null) 18.sp else 20.sp,
                    lineHeight = 20.sp
                )
                if (subtitulo != null) {
                    Text(
                        text = subtitulo,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f),
                        fontSize = 12.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
