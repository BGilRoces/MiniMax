package net.eltiburon.minimax.ui.notificaciones

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.eltiburon.minimax.ui.theme.MiniMaxAccent
import net.eltiburon.minimax.ui.theme.MiniMaxBadgeRed
import net.eltiburon.minimax.ui.theme.MiniMaxOrange
import net.eltiburon.minimax.ui.theme.MiniMaxTeal

/** Tipo de notificación: define el ícono y el color del avatar de cada item. */
enum class TipoNotificacion(val icon: ImageVector, val color: Color) {
    GRUPO_COMPLETO(Icons.Filled.CheckCircle, MiniMaxTeal),
    POR_VENCER(Icons.Filled.AccessTime, MiniMaxOrange),
    NUEVO_GRUPO(Icons.Filled.GroupAdd, MiniMaxAccent),
    PAGO(Icons.Filled.Payments, MiniMaxBadgeRed),
    GENERAL(Icons.Filled.Info, MiniMaxAccent)
}

data class Notificacion(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val tiempo: String,
    val tipo: TipoNotificacion,
    val leida: Boolean = false
)

/**
 * ViewModel de la pantalla de Notificaciones. La lista es mock (en memoria); el "marcar como
 * leída" se resuelve sobre el StateFlow, sin persistencia (se reemplazará al integrar backend).
 */
class NotificacionesViewModel : ViewModel() {

    private val _notificaciones = MutableStateFlow(mockNotificaciones())
    val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    fun marcarTodasLeidas() {
        _notificaciones.value = _notificaciones.value.map { it.copy(leida = true) }
    }

    fun marcarLeida(id: Int) {
        _notificaciones.value = _notificaciones.value.map {
            if (it.id == id) it.copy(leida = true) else it
        }
    }

    private fun mockNotificaciones() = listOf(
        Notificacion(
            1, "¡Grupo completado!",
            "El grupo de Aceite de Girasol 5L alcanzó el mínimo. Tu pedido ya está confirmado.",
            "Hace 5 min", TipoNotificacion.GRUPO_COMPLETO
        ),
        Notificacion(
            2, "Tu grupo está por cerrar",
            "Papel Higiénico x48 cierra en 2 horas. ¡Sumá unidades antes de que termine!",
            "Hace 1 h", TipoNotificacion.POR_VENCER
        ),
        Notificacion(
            3, "Nuevo grupo recomendado",
            "Se abrió un grupo de Yerba Mate Premium 1kg con 22% de descuento.",
            "Hace 3 h", TipoNotificacion.NUEVO_GRUPO
        ),
        Notificacion(
            4, "Seña registrada",
            "Recibimos tu seña por el grupo de Arroz Largo Fino 5kg.",
            "Ayer", TipoNotificacion.PAGO, leida = true
        ),
        Notificacion(
            5, "Bienvenido a MiniMax",
            "Empezá a unirte a grupos de compra y ahorrá comprando en conjunto.",
            "Hace 2 días", TipoNotificacion.GENERAL, leida = true
        )
    )
}
