package net.eltiburon.minimax.ui.explorar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.model.EstadoGrupo
import net.eltiburon.minimax.model.GrupoResumen
import net.eltiburon.minimax.model.toGrupoResumen

/** Criterio de ordenamiento del listado de grupos. */
enum class OrdenGrupos(val label: String) {
    RELEVANCIA("Relevancia"),
    MAYOR_DESCUENTO("Mayor descuento"),
    MENOR_PRECIO("Menor precio"),
    MAS_AVANZADOS("Más avanzados")
}

class ExplorarGruposViewModel : ViewModel() {

    // Lista completa, derivada de la fuente de datos única. Privada: la UI nunca la toca directamente.
    private val _todos: StateFlow<List<GrupoResumen>> = OportunidadRepository.obtenerTodas()
        .map { todas -> todas.map { it.toGrupoResumen() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado de los filtros. Cada uno es un MutableStateFlow privado y se expone
    // como StateFlow de solo lectura (.asStateFlow()) para respetar el flujo
    // unidireccional: la UI lee el estado, pero solo lo modifica vía los onXxxChange().
    private val _filtroCategoria = MutableStateFlow(CATEGORIA_TODAS)
    val filtroCategoria: StateFlow<String> = _filtroCategoria.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    // Filtros avanzados (bottom sheet): estado del grupo y orden del listado.
    private val _filtroEstado = MutableStateFlow<EstadoGrupo?>(null)
    val filtroEstado: StateFlow<EstadoGrupo?> = _filtroEstado.asStateFlow()

    private val _orden = MutableStateFlow(OrdenGrupos.RELEVANCIA)
    val orden: StateFlow<OrdenGrupos> = _orden.asStateFlow()

    // La lógica de negocio (el filtrado + orden) vive en el ViewModel, no en la UI.
    // Combinamos la lista con los filtros: cada vez que cambia cualquiera, se recalcula.
    val gruposFiltrados: StateFlow<List<GrupoResumen>> = combine(
        _todos, _filtroCategoria, _textoBusqueda, _filtroEstado, _orden
    ) { todos, categoria, texto, estado, orden ->
        todos.filter { grupo ->
            (categoria == CATEGORIA_TODAS || grupo.categoria == categoria) &&
                (estado == null || grupo.estado == estado) &&
                (texto.isBlank() ||
                    grupo.nombre.contains(texto, ignoreCase = true) ||
                    grupo.proveedor.contains(texto, ignoreCase = true))
        }.let { lista ->
            when (orden) {
                OrdenGrupos.RELEVANCIA -> lista
                OrdenGrupos.MAYOR_DESCUENTO -> lista.sortedByDescending { it.descuentoPorcentaje }
                OrdenGrupos.MENOR_PRECIO -> lista.sortedBy { it.precioGrupal }
                OrdenGrupos.MAS_AVANZADOS -> lista.sortedByDescending { it.progresoActual }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onCategoriaChange(categoria: String) { _filtroCategoria.value = categoria }
    fun onBusquedaChange(texto: String) { _textoBusqueda.value = texto }
    fun onEstadoChange(estado: EstadoGrupo?) { _filtroEstado.value = estado }
    fun onOrdenChange(orden: OrdenGrupos) { _orden.value = orden }

    /** Restablece estado y orden (los filtros del bottom sheet). */
    fun limpiarFiltrosAvanzados() {
        _filtroEstado.value = null
        _orden.value = OrdenGrupos.RELEVANCIA
    }

    companion object {
        // Valor "centinela" que representa "sin filtro de categoría".
        const val CATEGORIA_TODAS = "Todos"
    }
}
