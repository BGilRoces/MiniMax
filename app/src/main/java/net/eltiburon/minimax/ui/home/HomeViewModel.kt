package net.eltiburon.minimax.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.eltiburon.minimax.model.EstadoGrupo
import net.eltiburon.minimax.model.GrupoActivo
import net.eltiburon.minimax.model.GrupoRecomendado

class HomeViewModel : ViewModel() {

    private val _gruposActivos = MutableStateFlow(gruposActivosMock())
    val gruposActivos: StateFlow<List<GrupoActivo>> = _gruposActivos.asStateFlow()

    private val _todosRecomendados = MutableStateFlow(gruposRecomendadosMock())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val gruposRecomendados: StateFlow<List<GrupoRecomendado>> = _todosRecomendados

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun gruposActivosMock() = listOf(
        GrupoActivo(
            id = 1,
            nombreProducto = "Aceite de Girasol 5L",
            proveedor = "Oleico SA",
            lote = "Lote #2847",
            progreso = 0.80f,
            unidadesFaltantes = 24,
            horasRestantes = 4
        ),
        GrupoActivo(
            id = 2,
            nombreProducto = "Arroz Largo Fino 5kg",
            proveedor = "Arrocera del Sur",
            lote = "Lote #1293",
            progreso = 0.55f,
            unidadesFaltantes = 45,
            horasRestantes = 12
        ),
        GrupoActivo(
            id = 3,
            nombreProducto = "Papel Higiénico x48",
            proveedor = "HigieniMax SRL",
            lote = "Lote #5521",
            progreso = 0.92f,
            unidadesFaltantes = 8,
            horasRestantes = 2,
            prioridad = "URGENTE"
        ),
        GrupoActivo(
            id = 4,
            nombreProducto = "Detergente Industrial 5L",
            proveedor = "CleanPro SA",
            lote = "Lote #3310",
            progreso = 0.35f,
            unidadesFaltantes = 78,
            horasRestantes = 24
        )
    )

    private fun gruposRecomendadosMock() = listOf(
        GrupoRecomendado(1, "Yerba Mate Premium 1kg", "Yerbatería Norte", 22, EstadoGrupo.FORMANDOSE),
        GrupoRecomendado(2, "Fideos Mostachol x20", "Pasta del Campo", 18, EstadoGrupo.CASI_LLENO),
        GrupoRecomendado(3, "Azúcar Común 50kg", "Ingenio Central", 30, EstadoGrupo.FORMANDOSE),
        GrupoRecomendado(4, "Sal Gruesa 25kg", "Salinera Patagónica", 15, EstadoGrupo.CASI_LLENO),
        GrupoRecomendado(5, "Tomates en Lata x24", "Conservas La Granja", 25, EstadoGrupo.FORMANDOSE)
    )
}
