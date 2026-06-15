package net.eltiburon.minimax.ui.participacion

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
 * ViewModel compartido por las pantallas "Confirmar participación" y "Confirmación".
 *
 * Las dos muestran el mismo resumen (producto + cantidad + subtotal + ahorro), por eso
 * reusamos un único ViewModel en vez de duplicar dos casi iguales (DRY). La cantidad llega
 * desde la navegación y se inyecta con [setCantidad] (mismo patrón que cargarGrupo()).
 */
class ResumenParticipacionViewModel : ViewModel() {

    private val _producto = MutableStateFlow(ProductoParticipacion.demo())
    val producto: StateFlow<ProductoParticipacion> = _producto.asStateFlow()

    private val _cantidad = MutableStateFlow(1)
    val cantidad: StateFlow<Int> = _cantidad.asStateFlow()

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

    fun setCantidad(cantidad: Int) {
        _cantidad.value = cantidad
    }
}
