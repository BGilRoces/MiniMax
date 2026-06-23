package net.eltiburon.minimax.ui.miscompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.data.ParticipacionRepository
import net.eltiburon.minimax.data.UsuarioRepository
import net.eltiburon.minimax.model.EstadoCompra
import net.eltiburon.minimax.model.Oportunidad

/** Compra del usuario, con los datos de la oportunidad ya resueltos para mostrar en la card. */
data class CompraUi(
    val participacionId: String,
    val oportunidad: Oportunidad,
    val cantidad: Int,
    val estado: EstadoCompra,
    val fechaMillis: Long
)

class MisComprasViewModel : ViewModel() {

    private val _tabSeleccionada = MutableStateFlow(EstadoCompra.ACTIVA)
    val tabSeleccionada: StateFlow<EstadoCompra> = _tabSeleccionada.asStateFlow()

    // Cruza las participaciones del usuario actual con el catálogo de oportunidades.
    // Si una oportunidad fue borrada del catálogo, la compra se omite (mapNotNull).
    private val comprasDelUsuario: StateFlow<List<CompraUi>> = combine(
        ParticipacionRepository.participaciones,
        UsuarioRepository.usuarioActual,
        OportunidadRepository.obtenerTodas()
    ) { participaciones, usuario, oportunidades ->
        val email = usuario?.email
        if (email == null) {
            emptyList()
        } else {
            participaciones
                .filter { it.usuarioEmail.equals(email, ignoreCase = true) }
                .mapNotNull { participacion ->
                    val oportunidad = oportunidades.find { it.id == participacion.oportunidadId }
                        ?: return@mapNotNull null
                    CompraUi(
                        participacionId = participacion.id,
                        oportunidad = oportunidad,
                        cantidad = participacion.cantidad,
                        estado = participacion.estado,
                        fechaMillis = participacion.fechaMillis
                    )
                }
                .sortedByDescending { it.fechaMillis }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val comprasFiltradas: StateFlow<List<CompraUi>> = combine(
        comprasDelUsuario, _tabSeleccionada
    ) { compras, tab -> compras.filter { it.estado == tab } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onTabChange(estado: EstadoCompra) {
        _tabSeleccionada.value = estado
    }

    fun cancelar(participacionId: String) {
        ParticipacionRepository.cancelar(participacionId)
    }
}
