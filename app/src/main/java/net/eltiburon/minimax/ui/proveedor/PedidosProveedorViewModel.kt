package net.eltiburon.minimax.ui.proveedor

import androidx.compose.ui.graphics.Color
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
import net.eltiburon.minimax.model.EstadoCompra
import net.eltiburon.minimax.ui.theme.MiniMaxBadgeRed
import net.eltiburon.minimax.ui.theme.MiniMaxOrange
import net.eltiburon.minimax.ui.theme.MiniMaxTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Color de chip/badge por estado, solo para esta pantalla (el enum vive en el dominio, sin UI). */
val EstadoCompra.colorBadge: Color
    get() = when (this) {
        EstadoCompra.ACTIVA -> MiniMaxOrange
        EstadoCompra.COMPLETADA -> MiniMaxTeal
        EstadoCompra.CANCELADA -> MiniMaxBadgeRed
    }

/** Pedido real de un comprador (una Participacion), con nombre/proveedor ya resueltos. */
data class PedidoProveedor(
    val id: String,
    val oportunidadId: String,
    val producto: String,
    val proveedor: String,
    val cantidad: Int,
    val compradorEmail: String,
    val fecha: String,
    val estado: EstadoCompra,
    val subtotal: Double
)

private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

/**
 * ViewModel de "Pedidos recibidos": antes era una lista mock; ahora deriva los pedidos de las
 * participaciones reales (Room), cruzándolas con el catálogo para resolver producto/proveedor.
 * Por ahora muestra TODOS los pedidos, sin filtrar por proveedor dueño (ver nota en el repo).
 */
class PedidosProveedorViewModel : ViewModel() {

    private val _filtroEstado = MutableStateFlow<EstadoCompra?>(null)
    val filtroEstado: StateFlow<EstadoCompra?> = _filtroEstado.asStateFlow()

    private val todosLosPedidos: StateFlow<List<PedidoProveedor>> = combine(
        ParticipacionRepository.participaciones,
        OportunidadRepository.obtenerTodas()
    ) { participaciones, oportunidades ->
        participaciones
            .sortedByDescending { it.fechaMillis }
            .map { participacion ->
                val oportunidad = oportunidades.find { it.id == participacion.oportunidadId }
                PedidoProveedor(
                    id = participacion.id,
                    oportunidadId = participacion.oportunidadId,
                    producto = oportunidad?.nombre ?: "Producto eliminado",
                    proveedor = oportunidad?.proveedor ?: "",
                    cantidad = participacion.cantidad,
                    compradorEmail = participacion.usuarioEmail,
                    fecha = formatoFecha.format(Date(participacion.fechaMillis)),
                    estado = participacion.estado,
                    subtotal = (oportunidad?.precioMayorista ?: 0.0) * participacion.cantidad
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidos: StateFlow<List<PedidoProveedor>> = combine(
        todosLosPedidos, _filtroEstado
    ) { todos, estado ->
        if (estado == null) todos else todos.filter { it.estado == estado }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onFiltroChange(estado: EstadoCompra?) {
        _filtroEstado.value = estado
    }
}
