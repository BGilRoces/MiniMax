package net.eltiburon.minimax.ui.pedidos

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

/** Estado de un pedido del comprador. Cada estado define su etiqueta y color de badge. */
enum class EstadoPedido(val label: String, val color: Color) {
    EN_CURSO("En curso", MiniMaxOrange),
    EN_CAMINO("En camino", MiniMaxTeal),
    COMPLETADO("Completado", MiniMaxTeal),
    CANCELADO("Cancelado", MiniMaxBadgeRed)
}

data class Pedido(
    val id: Int,
    val producto: String,
    val proveedor: String,
    val cantidad: Int,
    val total: Int,
    val fecha: String,
    val estado: EstadoPedido
)

/**
 * ViewModel de "Mis Pedidos". Lista mock + filtro por estado, siguiendo el mismo patrón de
 * filtrado en el VM que [net.eltiburon.minimax.ui.explorar.ExplorarGruposViewModel].
 */
class MisPedidosViewModel : ViewModel() {

    private val _todos = MutableStateFlow(mockPedidos())

    // null = "Todos". Se filtra en el VM; la UI solo recibe la lista ya filtrada.
    private val _filtroEstado = MutableStateFlow<EstadoPedido?>(null)
    val filtroEstado: StateFlow<EstadoPedido?> = _filtroEstado.asStateFlow()

    val pedidos: StateFlow<List<Pedido>> = combine(_todos, _filtroEstado) { todos, estado ->
        if (estado == null) todos else todos.filter { it.estado == estado }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, mockPedidos())

    fun onFiltroChange(estado: EstadoPedido?) { _filtroEstado.value = estado }

    private fun mockPedidos() = listOf(
        Pedido(1, "Papel Higiénico x48", "HigieniMax SRL", 2, 18400, "12 jun 2026", EstadoPedido.EN_CURSO),
        Pedido(2, "Aceite de Girasol 5L", "Oleico SA", 4, 32000, "10 jun 2026", EstadoPedido.EN_CAMINO),
        Pedido(3, "Arroz Largo Fino 5kg", "Arrocera del Sur", 3, 14700, "5 jun 2026", EstadoPedido.COMPLETADO),
        Pedido(4, "Yerba Mate Premium 1kg", "Yerbatería Norte", 5, 12400, "28 may 2026", EstadoPedido.COMPLETADO),
        Pedido(5, "Detergente Industrial 5L", "CleanPro SA", 1, 8900, "20 may 2026", EstadoPedido.CANCELADO)
    )
}
