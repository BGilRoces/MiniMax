package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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