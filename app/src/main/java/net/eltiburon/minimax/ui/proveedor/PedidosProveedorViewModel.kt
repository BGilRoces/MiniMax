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
import net.eltiburon.minimax.ui.theme.MiniMaxBadgeRed
import net.eltiburon.minimax.ui.theme.MiniMaxOrange
import net.eltiburon.minimax.ui.theme.MiniMaxTeal

/** Estado de un pedido recibido por el proveedor. */
enum class EstadoPedidoProveedor(val label: String, val color: Color) {
    POR_VALIDAR("Por validar", MiniMaxOrange),
    EN_PREPARACION("En preparación", MiniMaxTeal),
    DESPACHADO("Despachado", MiniMaxTeal),
    CANCELADO("Cancelado", MiniMaxBadgeRed)
}

data class PedidoProveedor(
    val id: Int,
    val producto: String,
    val compradores: Int,
    val unidades: Int,
    val total: Int,
    val fecha: String,
    val estado: EstadoPedidoProveedor
)

/** ViewModel de la pantalla "Pedidos" del proveedor: lista mock + filtro por estado. */
class PedidosProveedorViewModel : ViewModel() {

    private val _todos = MutableStateFlow(mockPedidos())

    private val _filtroEstado = MutableStateFlow<EstadoPedidoProveedor?>(null)
    val filtroEstado: StateFlow<EstadoPedidoProveedor?> = _filtroEstado.asStateFlow()

    val pedidos: StateFlow<List<PedidoProveedor>> = combine(_todos, _filtroEstado) { todos, estado ->
        if (estado == null) todos else todos.filter { it.estado == estado }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, mockPedidos())

    fun onFiltroChange(estado: EstadoPedidoProveedor?) { _filtroEstado.value = estado }

    private fun mockPedidos() = listOf(
        PedidoProveedor(1, "Componentes Electrónicos X-200", 12, 120, 420000, "12 jun 2026", EstadoPedidoProveedor.POR_VALIDAR),
        PedidoProveedor(2, "Sillas Ergonómicas Serie-A", 45, 45, 1285000, "12 jun 2026", EstadoPedidoProveedor.POR_VALIDAR),
        PedidoProveedor(3, "Teclado Mecánico RGB", 30, 30, 255000, "10 jun 2026", EstadoPedidoProveedor.EN_PREPARACION),
        PedidoProveedor(4, "Monitor 4K 27\" Ultra", 18, 18, 711000, "6 jun 2026", EstadoPedidoProveedor.DESPACHADO),
        PedidoProveedor(5, "Hub USB-C Premium", 8, 8, 30400, "1 jun 2026", EstadoPedidoProveedor.CANCELADO)
    )
}
