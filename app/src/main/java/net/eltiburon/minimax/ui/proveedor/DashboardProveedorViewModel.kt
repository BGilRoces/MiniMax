package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Pedido cuyo grupo ya alcanzó el mínimo y está pendiente de validación por el proveedor. */
data class PedidoPendiente(
    val id: Int,
    val nombre: String,
    val compradores: Int,
    val monto: String,
    val cierraEn: String
)

/** Producto del catálogo del proveedor. */
data class ProductoCatalogo(
    val id: Int,
    val nombre: String,
    val unidades: Int,
    val precio: String
)

/**
 * ViewModel del dashboard del proveedor. Antes los datos mock estaban como propiedades
 * sueltas a nivel de archivo dentro de la pantalla; ahora son estado que el ViewModel
 * expone y la UI observa (MVVM).
 */
class DashboardProveedorViewModel : ViewModel() {

    private val _pedidosPendientes = MutableStateFlow(mockPedidos())
    val pedidosPendientes: StateFlow<List<PedidoPendiente>> = _pedidosPendientes.asStateFlow()

    private val _catalogo = MutableStateFlow(mockCatalogo())
    val catalogo: StateFlow<List<ProductoCatalogo>> = _catalogo.asStateFlow()

    private fun mockPedidos() = listOf(
        PedidoPendiente(1, "Componentes Electrónicos X-200", 12, "$4,200", "4h"),
        PedidoPendiente(2, "Sillas Ergonómicas Serie-A", 45, "$12,850", "4h")
    )

    private fun mockCatalogo() = listOf(
        ProductoCatalogo(1, "Teclado Mecánico RGB", 450, "$85.00"),
        ProductoCatalogo(2, "Monitor 4K 27\" Ultra", 120, "$395.00"),
        ProductoCatalogo(3, "Hub USB-C Premium", 12, "$38.00")
    )
}
