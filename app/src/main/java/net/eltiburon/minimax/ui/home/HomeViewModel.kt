package net.eltiburon.minimax.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.model.GrupoActivo
import net.eltiburon.minimax.model.GrupoRecomendado
import net.eltiburon.minimax.model.toGrupoActivo
import net.eltiburon.minimax.model.toGrupoRecomendado

// IDs curados para las secciones de Home dentro del catálogo único de OportunidadRepository.
private val ACTIVOS_IDS = setOf("1", "2", "3", "4")
private val RECOMENDADOS_IDS = setOf("5", "6", "7", "8", "9")

class HomeViewModel : ViewModel() {

    val gruposActivos: StateFlow<List<GrupoActivo>> = OportunidadRepository.obtenerTodas()
        .map { todas -> todas.filter { it.id in ACTIVOS_IDS }.map { it.toGrupoActivo() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todosRecomendados: StateFlow<List<GrupoRecomendado>> = OportunidadRepository.obtenerTodas()
        .map { todas -> todas.filter { it.id in RECOMENDADOS_IDS }.map { it.toGrupoRecomendado() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // El filtrado por búsqueda es lógica de negocio, así que vive en el ViewModel (antes
    // estaba en el composable). Combinamos la lista completa con el texto buscado y la UI
    // recibe directamente la lista ya filtrada.
    val gruposRecomendados: StateFlow<List<GrupoRecomendado>> = combine(
        _todosRecomendados, _searchQuery
    ) { recomendados, query ->
        if (query.isBlank()) recomendados
        else recomendados.filter {
            it.nombre.contains(query, ignoreCase = true) ||
                it.proveedor.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
