package net.eltiburon.minimax.ui.elegircantidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.model.ProductoParticipacion

/**
 * ViewModel de la pantalla "Elegir cantidad".
 *
 * Acá vive el estado de la cantidad seleccionada y la lógica de negocio asociada
 * (límites min/max, cálculo de subtotal y ahorro). Antes esos cálculos estaban dentro
 * del composable, lo que mezclaba lógica de negocio con la UI.
 */
class ElegirCantidadViewModel : ViewModel() {

    private val _producto = MutableStateFlow(ProductoParticipacion.demo())
    val producto: StateFlow<ProductoParticipacion> = _producto.asStateFlow()

    private val _cantidad = MutableStateFlow(1)
    val cantidad: StateFlow<Int> = _cantidad.asStateFlow()

    // Estados derivados: se recalculan automáticamente cuando cambia la cantidad.
    val subtotal: StateFlow<Int> = _cantidad
        .map { it * _producto.value.precioMayorista }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _producto.value.precioMayorista)

    val ahorro: StateFlow<Int> = _cantidad
        .map { it * (_producto.value.precioUnitario - _producto.value.precioMayorista) }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _producto.value.precioUnitario - _producto.value.precioMayorista
        )

    fun incrementar() {
        if (_cantidad.value < _producto.value.cantidadMaxima) _cantidad.value++
    }

    fun decrementar() {
        if (_cantidad.value > 1) _cantidad.value--
    }
}
