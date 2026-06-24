package net.eltiburon.minimax.ui.participacion

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
import net.eltiburon.minimax.model.ProductoParticipacion
import net.eltiburon.minimax.model.toProductoParticipacion

/**
 * ViewModel compartido por las pantallas "Confirmar participación" y "Confirmación".
 *
 * Las dos muestran el mismo resumen (producto + cantidad + subtotal + ahorro), por eso
 * reusamos un único ViewModel en vez de duplicar dos casi iguales (DRY). El producto se
 * resuelve desde [OportunidadRepository] vía [cargarGrupo] usando el grupoId que llega por
 * navegación; la cantidad llega por separado y se inyecta con [setCantidad].
 */
class ResumenParticipacionViewModel : ViewModel() {

    private val _producto = MutableStateFlow(ProductoParticipacion.demo())
    val producto: StateFlow<ProductoParticipacion> = _producto.asStateFlow()

    private val _cantidad = MutableStateFlow(1)
    val cantidad: StateFlow<Int> = _cantidad.asStateFlow()

    val subtotal: StateFlow<Int> = combine(_cantidad, _producto) { cantidad, producto ->
        cantidad * producto.precioMayorista
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _producto.value.precioMayorista)

    val ahorro: StateFlow<Int> = combine(_cantidad, _producto) { cantidad, producto ->
        cantidad * (producto.precioUnitario - producto.precioMayorista)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun cargarGrupo(grupoId: String) {
        viewModelScope.launch {
            _producto.value = OportunidadRepository.obtenerPorId(grupoId)?.toProductoParticipacion()
                ?: ProductoParticipacion.demo()
        }
    }

    fun setCantidad(cantidad: Int) {
        _cantidad.value = cantidad
    }

    /**
     * Registra la participación del usuario logueado e inserta + descuenta stock de la
     * oportunidad en una sola transacción (ParticipacionRepository.confirmarParticipacion).
     * [onListo] se invoca al terminar para que la pantalla navegue a la confirmación.
     */
    fun confirmar(grupoId: String, cantidad: Int, onListo: () -> Unit) {
        viewModelScope.launch {
            val email = UsuarioRepository.usuarioActual.value?.email
            if (email != null) {
                ParticipacionRepository.confirmarParticipacion(
                    oportunidadId = grupoId,
                    usuarioEmail = email,
                    cantidad = cantidad
                )
            }
            onListo()
        }
    }
}
