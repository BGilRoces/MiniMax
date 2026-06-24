package net.eltiburon.minimax.ui.explorar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.model.GrupoResumen
import net.eltiburon.minimax.model.toGrupoResumen

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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onCategoriaChange(categoria: String) { _filtroCategoria.value = categoria }
    fun onBusquedaChange(texto: String) { _textoBusqueda.value = texto }

    companion object {
        // Valor "centinela" que representa "sin filtro de categoría".
        const val CATEGORIA_TODAS = "Todos"
    }
}
