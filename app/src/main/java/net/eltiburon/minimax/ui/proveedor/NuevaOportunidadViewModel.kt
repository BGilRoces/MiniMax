package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.eltiburon.minimax.model.Oportunidad
import java.time.LocalDate

class NuevaOportunidadViewModel : ViewModel() {

    // Cada campo se expone como StateFlow de solo lectura (.asStateFlow()) para respetar
    // el flujo unidireccional: la UI lee el valor y avisa los cambios con los onXxxChange().
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _categoria = MutableStateFlow("")
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion.asStateFlow()

    private val _precioMayorista = MutableStateFlow("")
    val precioMayorista: StateFlow<String> = _precioMayorista.asStateFlow()

    private val _precioReferencia = MutableStateFlow("")
    val precioReferencia: StateFlow<String> = _precioReferencia.asStateFlow()

    private val _cantidadMinima = MutableStateFlow("")
    val cantidadMinima: StateFlow<String> = _cantidadMinima.asStateFlow()

    private val _stockDisponible = MutableStateFlow("")
    val stockDisponible: StateFlow<String> = _stockDisponible.asStateFlow()

    private val _fechaLimite = MutableStateFlow("")
    val fechaLimite: StateFlow<String> = _fechaLimite.asStateFlow()

    // Ruta/Uri de la imagen del producto (almacenamiento interno de la app). Null si no hay imagen.
    private val _imagenUri = MutableStateFlow<String?>(null)
    val imagenUri: StateFlow<String?> = _imagenUri.asStateFlow()

    fun onNombreChange(v: String)          { _nombre.value = v }
    fun onCategoriaChange(v: String)       { _categoria.value = v }
    fun onDescripcionChange(v: String)     { _descripcion.value = v }
    fun onPrecioMayoristaChange(v: String) { _precioMayorista.value = v }
    fun onPrecioReferenciaChange(v: String){ _precioReferencia.value = v }
    fun onCantidadMinimaChange(v: String)  { _cantidadMinima.value = v }
    fun onStockDisponibleChange(v: String) { _stockDisponible.value = v }
    fun onFechaLimiteChange(v: String)     { _fechaLimite.value = v }
    fun onImagenChange(uri: String?)       { _imagenUri.value = uri }

    fun camposObligatoriosCompletos(): Boolean =
        _nombre.value.isNotBlank() &&
        _categoria.value.isNotBlank() &&
        _precioMayorista.value.isNotBlank() &&
        _cantidadMinima.value.isNotBlank() &&
        _fechaLimite.value.isNotBlank()

    /** Precarga el formulario con los datos de una oportunidad existente, para editarla. */
    fun cargarDesde(oportunidad: Oportunidad) {
        _nombre.value = oportunidad.nombre
        _categoria.value = oportunidad.categoria
        _descripcion.value = oportunidad.descripcion
        _precioMayorista.value = oportunidad.precioMayorista.toInt().toString()
        _precioReferencia.value = if (oportunidad.precioUnitario > oportunidad.precioMayorista) {
            oportunidad.precioUnitario.toInt().toString()
        } else ""
        _cantidadMinima.value = oportunidad.unidadesFaltantes.toString()
        _stockDisponible.value = oportunidad.stockDisponible.toString()
        // El modelo no guarda la fecha límite original, solo los minutos restantes:
        // reconstruimos una fecha aproximada para no dejar vacío un campo obligatorio.
        val fechaAprox = LocalDate.now().plusDays((oportunidad.minutosRestantes / 1440).toLong())
        _fechaLimite.value = "%02d/%02d/%04d".format(
            fechaAprox.dayOfMonth, fechaAprox.monthValue, fechaAprox.year
        )
        _imagenUri.value = oportunidad.imagenUri
    }

    fun limpiar() {
        _nombre.value = ""
        _categoria.value = ""
        _descripcion.value = ""
        _precioMayorista.value = ""
        _precioReferencia.value = ""
        _cantidadMinima.value = ""
        _stockDisponible.value = ""
        _fechaLimite.value = ""
        _imagenUri.value = null
    }
}