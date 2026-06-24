package net.eltiburon.minimax.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ItemInventario(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int,
    val precioUnitario: Int
) {
    /** Hay que reponer cuando el stock cae al mínimo o por debajo. */
    val stockBajo: Boolean get() = stock <= stockMinimo
}

/**
 * ViewModel de Inventario: lista mock de stock + búsqueda por nombre/categoría, filtrada en el VM.
 */
class InventarioViewModel : ViewModel() {

    private val _todos = MutableStateFlow(mockItems())

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val items: StateFlow<List<ItemInventario>> = combine(_todos, _busqueda) { todos, texto ->
        if (texto.isBlank()) todos
        else todos.filter {
            it.nombre.contains(texto, ignoreCase = true) ||
                it.categoria.contains(texto, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, mockItems())

    fun onBusquedaChange(texto: String) { _busqueda.value = texto }

    private fun mockItems() = listOf(
        ItemInventario(1, "Aceite de Girasol 5L", "Alimentos & Bebidas", 120, 40, 8000),
        ItemInventario(2, "Arroz Largo Fino 5kg", "Alimentos & Bebidas", 28, 30, 4900),
        ItemInventario(3, "Papel Higiénico x48", "Limpieza", 75, 25, 9200),
        ItemInventario(4, "Detergente Industrial 5L", "Limpieza", 12, 20, 8900),
        ItemInventario(5, "Yerba Mate Premium 1kg", "Alimentos & Bebidas", 200, 50, 2480),
        ItemInventario(6, "Azúcar Común 50kg", "Alimentos & Bebidas", 18, 15, 32000),
        ItemInventario(7, "Sal Gruesa 25kg", "Alimentos & Bebidas", 60, 20, 15000)
    )
}
