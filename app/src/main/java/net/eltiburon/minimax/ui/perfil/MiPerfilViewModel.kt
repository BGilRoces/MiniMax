package net.eltiburon.minimax.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.eltiburon.minimax.data.EstadisticasRepository
import net.eltiburon.minimax.data.UsuarioRepository
import net.eltiburon.minimax.model.EstadisticasComprador
import net.eltiburon.minimax.model.UsuarioComprador

class MiPerfilViewModel : ViewModel() {

    // Campos que el registro no captura: viven solo en el ViewModel (no hay backing field en
    // Usuario/UsuarioRepository), por eso no pueden derivarse de la sesión como nombre/email.
    private val _telefonoLocal = MutableStateFlow("")
    private val _negocioLocal = MutableStateFlow("")
    private val _direccionLocal = MutableStateFlow("")

    /**
     * Datos reales del usuario logueado. Antes se leían una sola vez de
     * UsuarioRepository.usuarioActual.value al construirse, así que un logout/login posterior
     * (ej. cambiar de proveedor a comprador) no se reflejaba: quedaba "pegado" al usuario con el
     * que se abrió el perfil por primera vez. Ahora observa el StateFlow de la sesión, así que
     * nombre/email siempre son los del usuario actual.
     */
    val usuario: StateFlow<UsuarioComprador> = combine(
        UsuarioRepository.usuarioActual, _telefonoLocal, _negocioLocal, _direccionLocal
    ) { sesion, telefono, negocio, direccion ->
        UsuarioComprador(
            nombre = sesion?.nombre.orEmpty(),
            email = sesion?.email.orEmpty(),
            telefono = telefono,
            negocio = negocio,
            direccion = direccion,
            totalAhorrado = 0.0,
            gruposCompletados = 0,
            pedidosRealizados = 0,
            avatarRes = 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UsuarioComprador(
        nombre = "", email = "", telefono = "", negocio = "", direccion = "",
        totalAhorrado = 0.0, gruposCompletados = 0, pedidosRealizados = 0, avatarRes = 0
    ))

    /** El proveedor no maneja ahorro ni pedidos: el perfil le muestra las métricas del dashboard.
     * Reactivo (antes era un val fijo calculado una sola vez al construirse el ViewModel). */
    val esProveedor: StateFlow<Boolean> = UsuarioRepository.usuarioActual
        .map { it?.rol == "proveedor" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /** Etiqueta de rol para el encabezado (Comprador / Proveedor), reactiva igual que [esProveedor]. */
    val rolLabel: StateFlow<String> = esProveedor
        .map { if (it) "Proveedor" else "Comprador" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Comprador")

    // Foto de perfil del usuario logueado. Se lee de la sesión (UsuarioRepository) para que el
    // mismo avatar se vea acá y en la top bar, y se actualice al instante al sacarse una foto.
    val fotoUri: StateFlow<String?> = UsuarioRepository.usuarioActual
        .map { it?.fotoUri }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Estadísticas reales (total ahorrado, grupos completados, pedidos) derivadas de las compras
    // del usuario. Misma fuente que el banner de Home, así los números siempre coinciden y se
    // actualizan al confirmar/cancelar una compra.
    val estadisticas: StateFlow<EstadisticasComprador> = EstadisticasRepository.estadisticas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EstadisticasComprador())

    private val _modoEdicion = MutableStateFlow(false)
    val modoEdicion: StateFlow<Boolean> = _modoEdicion.asStateFlow()

    private val _nombreEdit = MutableStateFlow("")
    val nombreEdit: StateFlow<String> = _nombreEdit.asStateFlow()

    private val _emailEdit = MutableStateFlow("")
    val emailEdit: StateFlow<String> = _emailEdit.asStateFlow()

    private val _telefonoEdit = MutableStateFlow("")
    val telefonoEdit: StateFlow<String> = _telefonoEdit.asStateFlow()

    private val _negocioEdit = MutableStateFlow("")
    val negocioEdit: StateFlow<String> = _negocioEdit.asStateFlow()

    private val _direccionEdit = MutableStateFlow("")
    val direccionEdit: StateFlow<String> = _direccionEdit.asStateFlow()

    init {
        // Teléfono/negocio/dirección no tienen backing field en Usuario: si cambia el usuario
        // logueado (logout + login con otra cuenta) hay que reiniciarlos, o el perfil nuevo
        // "heredaría" los datos locales que había escrito el usuario anterior.
        viewModelScope.launch {
            UsuarioRepository.usuarioActual
                .map { it?.id }
                .distinctUntilChanged()
                .collect {
                    _telefonoLocal.value = ""
                    _negocioLocal.value = ""
                    _direccionLocal.value = ""
                    _modoEdicion.value = false
                }
        }

        // Mientras no se está editando, los campos del formulario siguen al usuario real (así
        // un logout/login con otro usuario se refleja también en el formulario, no solo en el
        // encabezado). Al entrar en modo edición se "congelan" para que escribir no se pelee
        // con las emisiones de la sesión.
        viewModelScope.launch {
            usuario.collect { actual ->
                if (!_modoEdicion.value) {
                    _nombreEdit.value = actual.nombre
                    _emailEdit.value = actual.email
                    _telefonoEdit.value = actual.telefono
                    _negocioEdit.value = actual.negocio
                    _direccionEdit.value = actual.direccion
                }
            }
        }
    }

    fun toggleEdicion() {
        if (_modoEdicion.value) {
            // Cancelar: descarta los cambios sin guardar, volviendo a los valores actuales.
            val actual = usuario.value
            _nombreEdit.value = actual.nombre
            _emailEdit.value = actual.email
            _telefonoEdit.value = actual.telefono
            _negocioEdit.value = actual.negocio
            _direccionEdit.value = actual.direccion
        }
        _modoEdicion.value = !_modoEdicion.value
    }

    /** Guarda la foto tomada con la cámara; se persiste y se propaga a la top bar. */
    fun onFotoTomada(uri: String) {
        UsuarioRepository.actualizarFoto(uri)
    }

    fun onNombreChange(v: String) { _nombreEdit.value = v }
    fun onEmailChange(v: String) { _emailEdit.value = v }
    fun onTelefonoChange(v: String) { _telefonoEdit.value = v }
    fun onNegocioChange(v: String) { _negocioEdit.value = v }
    fun onDireccionChange(v: String) { _direccionEdit.value = v }

    fun guardarCambios() {
        // Nombre y email son datos de la cuenta: se persisten para que el resto de la app
        // (top bar, menú lateral) refleje el cambio. Teléfono/negocio/dirección no tienen
        // backing field en Usuario, así que quedan solo en este ViewModel.
        UsuarioRepository.actualizarPerfil(_nombreEdit.value, _emailEdit.value)
        _telefonoLocal.value = _telefonoEdit.value
        _negocioLocal.value = _negocioEdit.value
        _direccionLocal.value = _direccionEdit.value
        _modoEdicion.value = false
    }
}
