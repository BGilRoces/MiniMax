package net.eltiburon.minimax.ui.miscompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    // null = "Todos". Se filtra en el VM; la UI solo recibe la lista ya filtrada,
    // siguiendo el mismo patrón de chips de Explorar Grupos.
    private val _filtroEstado = MutableStateFlow<EstadoCompra?>(null)
    val filtroEstado: StateFlow<EstadoCompra?> = _filtroEstado.asStateFlow()

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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val comprasFiltradas: StateFlow<List<CompraUi>> = combine(
        comprasDelUsuario, _filtroEstado
    ) { compras, filtro ->
        if (filtro == null) compras else compras.filter { it.estado == filtro }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onFiltroChange(estado: EstadoCompra?) {
        _filtroEstado.value = estado
    }

    fun cancelar(participacionId: String) {
        viewModelScope.launch { ParticipacionRepository.cancelar(participacionId) }
    }
}
