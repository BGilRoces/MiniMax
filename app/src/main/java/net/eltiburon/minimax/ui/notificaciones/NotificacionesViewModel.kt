package net.eltiburon.minimax.ui.notificaciones

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import net.eltiburon.minimax.data.Notificacion
import net.eltiburon.minimax.data.NotificacionRepository

/**
 * ViewModel de la pantalla de Notificaciones. La lista ya no es mock: la genera
 * [NotificacionRepository] a partir de eventos reales (login, recomendación, compras).
 */
class NotificacionesViewModel : ViewModel() {

    val notificaciones: StateFlow<List<Notificacion>> = NotificacionRepository.notificaciones

    fun marcarTodasLeidas() = NotificacionRepository.marcarTodasLeidas()

    fun marcarLeida(id: Int) = NotificacionRepository.marcarLeida(id)
}
