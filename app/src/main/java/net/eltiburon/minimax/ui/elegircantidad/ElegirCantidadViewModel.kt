package net.eltiburon.minimax.ui.elegircantidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.model.ProductoParticipacion
import net.eltiburon.minimax.model.toProductoParticipacion

/**
 * ViewModel de la pantalla "Elegir cantidad".
 *
 * Acá vive el estado de la cantidad seleccionada y la lógica de negocio asociada
 * (límites min/max, cálculo de subtotal y ahorro). El producto ya no es un mock fijo:
 * [cargarGrupo] lo resuelve desde [OportunidadRepository] usando el grupoId que llega
 * por navegación, así la pantalla siempre muestra el producto real que el usuario eligió.
 */
class ElegirCantidadViewModel : ViewModel() {

    private val _producto = MutableStateFlow(ProductoParticipacion.demo())
    val producto: StateFlow<ProductoParticipacion> = _producto.asStateFlow()

    private val _cantidad = MutableStateFlow(1)
    val cantidad: StateFlow<Int> = _cantidad.asStateFlow()

    // Estados derivados: se recalculan automáticamente cuando cambia la cantidad o el producto.
    val subtotal: StateFlow<Int> = combine(_cantidad, _producto) { cantidad, producto ->
        cantidad * producto.precioMayorista
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _producto.value.precioMayorista)

    val ahorro: StateFlow<Int> = combine(_cantidad, _producto) { cantidad, producto ->
        cantidad * (producto.precioUnitario - producto.precioMayorista)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun cargarGrupo(grupoId: String) {
        _producto.value = OportunidadRepository.obtenerPorId(grupoId)?.toProductoParticipacion()
            ?: ProductoParticipacion.demo()
    }

    fun incrementar() {
        if (_cantidad.value < _producto.value.cantidadMaxima) _cantidad.value++
    }

    fun decrementar() {
        if (_cantidad.value > 1) _cantidad.value--
    }
}
