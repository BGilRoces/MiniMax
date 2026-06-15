package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NuevaOportunidadViewModel : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre

    private val _categoria = MutableStateFlow("")
    val categoria: StateFlow<String> = _categoria

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion

    private val _precioMayorista = MutableStateFlow("")
    val precioMayorista: StateFlow<String> = _precioMayorista

    private val _precioReferencia = MutableStateFlow("")
    val precioReferencia: StateFlow<String> = _precioReferencia

    private val _cantidadMinima = MutableStateFlow("")
    val cantidadMinima: StateFlow<String> = _cantidadMinima

    private val _stockDisponible = MutableStateFlow("")
    val stockDisponible: StateFlow<String> = _stockDisponible

    private val _fechaLimite = MutableStateFlow("")
    val fechaLimite: StateFlow<String> = _fechaLimite

    // Ruta/Uri de la imagen del producto (almacenamiento interno de la app). Null si no hay imagen.
    private val _imagenUri = MutableStateFlow<String?>(null)
    val imagenUri: StateFlow<String?> = _imagenUri

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