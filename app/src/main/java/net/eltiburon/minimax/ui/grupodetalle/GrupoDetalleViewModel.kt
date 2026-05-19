package net.eltiburon.minimax.ui.grupodetalle

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.eltiburon.minimax.R
import net.eltiburon.minimax.model.GrupoDetalle

class GrupoDetalleViewModel : ViewModel() {

    private val _grupo = MutableStateFlow<GrupoDetalle?>(null)
    val grupo: StateFlow<GrupoDetalle?> = _grupo.asStateFlow()

    private val _meUni = MutableStateFlow(false)
    val meUni: StateFlow<Boolean> = _meUni.asStateFlow()

    fun cargarGrupo(grupoId: String) {
        _grupo.value = mockDetalle(grupoId)
    }

    fun toggleUnirse() {
        _meUni.value = !_meUni.value
    }

    private fun mockDetalle(id: String) = GrupoDetalle(
        id = id,
        nombre = "Caja de Aceite de Girasol x12",
        descripcion = "Aceite de girasol de primera calidad, elaborado con semillas seleccionadas del sur de la provincia de Buenos Aires. Ideal para uso gastronómico e industrial. Formato mayorista x12 unidades de 1.5L cada una. Sin conservantes ni aditivos artificiales.",
        imagenRes = R.drawable.aceite,
        precioUnitario = 42500.0,
        precioMayorista = 34000.0,
        progresoActual = 80,
        unidadesFaltantes = 12,
        minutosRestantes = 1365,
        miembrosActivos = 45,
        origen = "Buenos Aires",
        acidez = "Acidez < 0.1%",
        stockDisponible = 240,
        proveedorNombre = "Oleico SA",
        proveedorDescripcion = "Productora y distribuidora de aceites vegetales con más de 25 años de experiencia en el mercado mayorista argentino.",
        crecimientoPorcentaje = 35
    )
}
