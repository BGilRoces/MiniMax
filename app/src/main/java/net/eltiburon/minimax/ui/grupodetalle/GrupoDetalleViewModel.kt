package net.eltiburon.minimax.ui.grupodetalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.model.GrupoDetalle
import net.eltiburon.minimax.model.toGrupoDetalle

class GrupoDetalleViewModel : ViewModel() {

    private val _grupo = MutableStateFlow<GrupoDetalle?>(null)
    val grupo: StateFlow<GrupoDetalle?> = _grupo.asStateFlow()

    // Indica si el usuario ya se sumó al grupo (antes se llamaba "meUni", poco descriptivo).
    private val _estaUnido = MutableStateFlow(false)
    val estaUnido: StateFlow<Boolean> = _estaUnido.asStateFlow()

    fun cargarGrupo(grupoId: String) {
        viewModelScope.launch {
            _grupo.value = OportunidadRepository.obtenerPorId(grupoId)?.toGrupoDetalle()
        }
    }

    fun alternarMembresia() {
        _estaUnido.value = !_estaUnido.value
    }
}
