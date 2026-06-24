package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.util.formatearPrecio

/** Pedido cuyo grupo ya alcanzó el mínimo y está pendiente de validación por el proveedor. */
data class PedidoPendiente(
    val id: Int,
    val nombre: String,
    val compradores: Int,
    val monto: String,
    val cierraEn: String
)

/** Producto del catálogo del proveedor, derivado de [OportunidadRepository]. */
data class ProductoCatalogo(
    val id: String,
    val nombre: String,
    val unidades: Int,
    val precio: String
)

/**
 * ViewModel del dashboard del proveedor.
 *
 * El catálogo ya no es un mock propio y desconectado: se deriva de OportunidadRepository,
 * la misma fuente de datos que alimenta Home/Explorar/Detalle. Así, una oportunidad
 * publicada desde "Nueva oportunidad" aparece automáticamente acá.
 */
class DashboardProveedorViewModel : ViewModel() {

    private val _pedidosPendientes = MutableStateFlow(mockPedidos())
    val pedidosPendientes: StateFlow<List<PedidoPendiente>> = _pedidosPendientes.asStateFlow()

    val catalogo: StateFlow<List<ProductoCatalogo>> = OportunidadRepository.obtenerTodas()
        .map { todas ->
            todas.map { oportunidad ->
                ProductoCatalogo(
                    id = oportunidad.id,
                    nombre = oportunidad.nombre,
                    unidades = oportunidad.stockDisponible,
                    precio = formatearPrecio(oportunidad.precioMayorista)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Borra la oportunidad del repo; el catálogo se actualiza solo porque viene del Flow. */
    fun eliminarOportunidad(id: String) {
        viewModelScope.launch { OportunidadRepository.eliminar(id) }
    }

    /**
     * Valida un lote: lo saca de la lista de pendientes. Por ahora es solo en memoria (mock);
     * al integrar backend, acá iría la llamada que confirma el lote contra el servidor.
     */
    fun validarPedido(id: Int) {
        _pedidosPendientes.value = _pedidosPendientes.value.filterNot { it.id == id }
    }

    private fun mockPedidos() = listOf(
        PedidoPendiente(1, "Componentes Electrónicos X-200", 12, "$4,200", "4h"),
        PedidoPendiente(2, "Sillas Ergonómicas Serie-A", 45, "$12,850", "4h")
    )
}
