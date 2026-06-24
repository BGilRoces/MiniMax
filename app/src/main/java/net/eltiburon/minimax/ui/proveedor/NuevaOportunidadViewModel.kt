package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.eltiburon.minimax.R
import net.eltiburon.minimax.data.OportunidadRepository
import net.eltiburon.minimax.model.EstadoGrupo
import net.eltiburon.minimax.model.Oportunidad
import java.time.LocalDate

private fun minutosHasta(fechaLimite: String): Int = try {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val fecha = java.time.LocalDate.parse(fechaLimite, formatter)
    val dias = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), fecha).toInt()
    (dias * 1440).coerceAtLeast(60)
} catch (e: Exception) {
    1440
}

private fun formatearTiempoRestante(minutos: Int): String {
    val dias = minutos / 1440
    return if (dias >= 1) "$dias ${if (dias == 1) "día" else "días"}" else "${minutos / 60} hs"
}

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

    /** Si llega un id existente, precarga el formulario con sus datos (editar). */
    fun cargarSiExiste(oportunidadId: String?) {
        if (oportunidadId == null) return
        viewModelScope.launch {
            OportunidadRepository.obtenerPorId(oportunidadId)?.let { cargarDesde(it) }
        }
    }

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

    /**
     * Crea o edita la oportunidad en el repo según [oportunidadId]. [onResultado] avisa a la
     * pantalla si terminó bien (para mostrar el snackbar y navegar) o si faltan campos.
     */
    fun publicar(oportunidadId: String?, onResultado: (exito: Boolean, mensaje: String) -> Unit) {
        if (!camposObligatoriosCompletos()) {
            onResultado(false, "Completá los campos obligatorios")
            return
        }
        viewModelScope.launch {
            // Al editar, conservamos los campos que el formulario no captura
            // (proveedor, lote, progreso, etc.) tal como estaban.
            val existente = oportunidadId?.let { OportunidadRepository.obtenerPorId(it) }

            val precioMayoristaValor = _precioMayorista.value.toDoubleOrNull() ?: 0.0
            val precioUnitarioValor = _precioReferencia.value.toDoubleOrNull()
                ?.takeIf { it > 0 } ?: precioMayoristaValor
            val descuentoValor = if (precioUnitarioValor > 0) {
                (((precioUnitarioValor - precioMayoristaValor) / precioUnitarioValor) * 100)
                    .toInt()
                    .coerceAtLeast(0)
            } else 0
            val cantidadMinimaValor = _cantidadMinima.value.toIntOrNull() ?: 0
            val stockValor = _stockDisponible.value.toIntOrNull() ?: cantidadMinimaValor
            val minutosRestantesValor = minutosHasta(_fechaLimite.value)

            val oportunidad = Oportunidad(
                id = existente?.id ?: OportunidadRepository.nuevoId(),
                nombre = _nombre.value,
                proveedor = existente?.proveedor ?: "Mi Negocio",
                proveedorDescripcion = existente?.proveedorDescripcion ?: "",
                categoria = _categoria.value,
                descripcion = _descripcion.value,
                imagenRes = existente?.imagenRes ?: R.drawable.aceite,
                imagenUri = _imagenUri.value,
                precioUnitario = precioUnitarioValor,
                precioMayorista = precioMayoristaValor,
                descuentoPorcentaje = descuentoValor,
                progresoActual = existente?.progresoActual ?: 0,
                unidadesFaltantes = cantidadMinimaValor,
                cantidadMaxima = cantidadMinimaValor.takeIf { it > 0 } ?: 20,
                minutosRestantes = minutosRestantesValor,
                tiempoRestanteTexto = formatearTiempoRestante(minutosRestantesValor),
                miembrosActivos = existente?.miembrosActivos ?: 0,
                stockDisponible = stockValor,
                lote = existente?.lote ?: "",
                prioridad = existente?.prioridad ?: "PRIORIDAD ALTA",
                origen = existente?.origen ?: "",
                acidez = existente?.acidez ?: "",
                crecimientoPorcentaje = existente?.crecimientoPorcentaje ?: 0,
                estado = existente?.estado ?: EstadoGrupo.FORMANDOSE
            )

            if (existente != null) {
                OportunidadRepository.editar(oportunidad)
            } else {
                OportunidadRepository.agregar(oportunidad)
            }

            limpiar()
            onResultado(
                true,
                if (existente != null) "Cambios guardados" else "Oportunidad publicada correctamente"
            )
        }
    }
}