package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.util.formatearPrecio

/**
 * ViewModel del catálogo completo del proveedor. Reutiliza [ProductoCatalogo] (definido en
 * [DashboardProveedorViewModel]) y, al igual que el dashboard, deriva la lista de la fuente
 * de datos única ([OportunidadRepository]) para que ambas vistas muestren lo mismo.
 * Agrega búsqueda por nombre filtrada en el VM.
 */
class CatalogoProveedorViewModel : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val catalogo: StateFlow<List<ProductoCatalogo>> =
        OportunidadRepository.obtenerTodas().combine(_busqueda) { todas, texto ->
            todas
                .map { oportunidad ->
                    ProductoCatalogo(
                        id = oportunidad.id,
                        nombre = oportunidad.nombre,
                        unidades = oportunidad.stockDisponible,
                        precio = formatearPrecio(oportunidad.precioMayorista),
                        imagenUri = oportunidad.imagenUri
                    )
                }
                .filter { texto.isBlank() || it.nombre.contains(texto, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onBusquedaChange(texto: String) { _busqueda.value = texto }
}
