package net.eltiburon.minimax.ui.proveedor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import net.eltiburon.minimax.ui.theme.MiniMaxOrange

/** Una métrica del proveedor (volumen de ventas, grupos activos, etc.). */
data class MetricaProveedor(
    val titulo: String,
    val valor: String,
    val icon: ImageVector,
    val color: Color
)

/**
 * Métricas del proveedor que se muestran en el dashboard (Home) y, reutilizadas, en Mi Perfil.
 * Vivir en un único lugar evita que los números se dupliquen y queden desfasados entre pantallas.
 * Son @Composable porque los colores salen del [MaterialTheme] activo.
 */
@Composable
fun metricasProveedor(): List<MetricaProveedor> = listOf(
    MetricaProveedor(
        titulo = "Volumen total de ventas",
        valor = "$142,500",
        icon = Icons.Filled.AttachMoney,
        color = MaterialTheme.colorScheme.tertiary
    ),
    MetricaProveedor(
        titulo = "Grupos activos",
        valor = "24",
        icon = Icons.Filled.Group,
        color = MaterialTheme.colorScheme.primary
    ),
    MetricaProveedor(
        titulo = "Ganancias",
        valor = "32%",
        icon = Icons.AutoMirrored.Filled.TrendingUp,
        color = MaterialTheme.colorScheme.secondary
    ),
    MetricaProveedor(
        titulo = "Meta alcanzada",
        valor = "85%",
        icon = Icons.Filled.Flag,
        color = MiniMaxOrange
    )
)
