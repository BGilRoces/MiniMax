package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.model.Oportunidad

/**
 * ViewModel de "Grupos publicados": antes era una lista mock con su propia data class
 * Oportunidad (duplicada y desconectada del resto de la app); ahora lee las oportunidades
 * reales desde OportunidadRepository (Room), usando directamente model.Oportunidad.
 */
class OportunidadesProveedorViewModel : ViewModel() {

    val oportunidades: StateFlow<List<Oportunidad>> = OportunidadRepository.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
