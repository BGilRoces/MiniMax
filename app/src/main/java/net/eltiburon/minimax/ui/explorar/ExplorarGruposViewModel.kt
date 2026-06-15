package net.eltiburon.minimax.ui.explorar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.model.EstadoGrupo
import net.eltiburon.minimax.model.GrupoResumen

class ExplorarGruposViewModel : ViewModel() {

    // Lista completa (mock). Privada: la UI nunca la toca directamente.
    private val _todos = MutableStateFlow(mockGrupos())

    // Estado de los filtros. Cada uno es un MutableStateFlow privado y se expone
    // como StateFlow de solo lectura (.asStateFlow()) para respetar el flujo
    // unidireccional: la UI lee el estado, pero solo lo modifica vía los onXxxChange().
    private val _filtroCategoria = MutableStateFlow(CATEGORIA_TODAS)
    val filtroCategoria: StateFlow<String> = _filtroCategoria.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    // La lógica de negocio (el filtrado) vive en el ViewModel, no en la UI.
    // Combinamos la lista con los filtros: cada vez que cambia cualquiera, se recalcula.
    val gruposFiltrados: StateFlow<List<GrupoResumen>> = combine(
        _todos, _filtroCategoria, _textoBusqueda
    ) { todos, categoria, texto ->
        todos.filter { grupo ->
            (categoria == CATEGORIA_TODAS || grupo.categoria == categoria) &&
                (texto.isBlank() ||
                    grupo.nombre.contains(texto, ignoreCase = true) ||
                    grupo.proveedor.contains(texto, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, mockGrupos())

    fun onCategoriaChange(categoria: String) { _filtroCategoria.value = categoria }
    fun onBusquedaChange(texto: String) { _textoBusqueda.value = texto }

    private fun mockGrupos() = listOf(
        GrupoResumen(
            id = "101", nombre = "Auriculares Bluetooth Pro Max",
            proveedor = "TechSound", categoria = "Electrónica", imagenRes = 0,
            precioGrupal = 42000.0, precioOriginal = 54000.0, descuentoPorcentaje = 22,
            progresoActual = 78, tiempoRestante = "5 días", estado = EstadoGrupo.FORMANDOSE
        ),
        GrupoResumen(
            id = "102", nombre = "Café Gourmet Etiopía 1kg",
            proveedor = "Café del Origen", categoria = "Alimentos & Bebidas", imagenRes = 0,
            precioGrupal = 8500.0, precioOriginal = 12000.0, descuentoPorcentaje = 29,
            progresoActual = 91, tiempoRestante = "12 hs", estado = EstadoGrupo.CASI_LLENO
        ),
        GrupoResumen(
            id = "103", nombre = "Smart Watch Fitness Pro",
            proveedor = "WearTech", categoria = "Electrónica", imagenRes = 0,
            precioGrupal = 68000.0, precioOriginal = 89000.0, descuentoPorcentaje = 24,
            progresoActual = 95, tiempoRestante = "2 hs", estado = EstadoGrupo.URGENTE
        ),
        GrupoResumen(
            id = "104", nombre = "Set Almohadas Premium x2",
            proveedor = "DormWell", categoria = "Textil", imagenRes = 0,
            precioGrupal = 22000.0, precioOriginal = 34000.0, descuentoPorcentaje = 35,
            progresoActual = 33, tiempoRestante = "10 días", estado = EstadoGrupo.FORMANDOSE
        ),
        GrupoResumen(
            id = "105", nombre = "Cafetera Italiana Bialetti 6 Tz",
            proveedor = "Bialetti AR", categoria = "Cafetería", imagenRes = 0,
            precioGrupal = 18500.0, precioOriginal = 24000.0, descuentoPorcentaje = 23,
            progresoActual = 82, tiempoRestante = "18 hs", estado = EstadoGrupo.CASI_LLENO
        ),
        GrupoResumen(
            id = "106", nombre = "Cuadros Modernos Enmarcados x3",
            proveedor = "DecoHome", categoria = "Decoración", imagenRes = 0,
            precioGrupal = 31000.0, precioOriginal = 42000.0, descuentoPorcentaje = 26,
            progresoActual = 60, tiempoRestante = "6 días", estado = EstadoGrupo.FORMANDOSE
        ),
        GrupoResumen(
            id = "107", nombre = "Lámpara LED Escritorio USB-C",
            proveedor = "LumaTech", categoria = "Gadgets", imagenRes = 0,
            precioGrupal = 9800.0, precioOriginal = 14500.0, descuentoPorcentaje = 32,
            progresoActual = 88, tiempoRestante = "3 hs", estado = EstadoGrupo.URGENTE
        ),
        GrupoResumen(
            id = "108", nombre = "Yerba Mate Premium 1kg x5",
            proveedor = "El Mensú", categoria = "Alimentos & Bebidas", imagenRes = 0,
            precioGrupal = 12400.0, precioOriginal = 18000.0, descuentoPorcentaje = 31,
            progresoActual = 25, tiempoRestante = "12 días", estado = EstadoGrupo.FORMANDOSE
        ),
        GrupoResumen(
            id = "109", nombre = "Teclado Mecánico RGB TKL",
            proveedor = "KeyMaster", categoria = "Electrónica", imagenRes = 0,
            precioGrupal = 54000.0, precioOriginal = 72000.0, descuentoPorcentaje = 25,
            progresoActual = 75, tiempoRestante = "1 día", estado = EstadoGrupo.CASI_LLENO
        ),
        GrupoResumen(
            id = "110", nombre = "Kit Ollas Acero Inox x5",
            proveedor = "Chef Pro", categoria = "Alimentos & Bebidas", imagenRes = 0,
            precioGrupal = 48000.0, precioOriginal = 65000.0, descuentoPorcentaje = 26,
            progresoActual = 48, tiempoRestante = "7 días", estado = EstadoGrupo.FORMANDOSE
        ),
    )

    companion object {
        // Valor "centinela" que representa "sin filtro de categoría".
        const val CATEGORIA_TODAS = "Todos"
    }
}
