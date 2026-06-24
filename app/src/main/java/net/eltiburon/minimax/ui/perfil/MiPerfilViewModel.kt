package net.eltiburon.minimax.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.data.EstadisticasRepository
import net.eltiburon.minimax.data.UsuarioRepository
import net.eltiburon.minimax.model.EstadisticasComprador
import net.eltiburon.minimax.model.UsuarioComprador

class MiPerfilViewModel : ViewModel() {

    // Datos reales del usuario logueado: solo nombre y email vienen del registro. El resto de los
    // campos no se piden al registrarse, así que arrancan vacíos y se completan editando el perfil.
    private val sesion = UsuarioRepository.usuarioActual.value

    private val _usuario = MutableStateFlow(
        UsuarioComprador(
            nombre = sesion?.nombre.orEmpty(),
            email = sesion?.email.orEmpty(),
            telefono = "",
            negocio = "",
            direccion = "",
            totalAhorrado = 0.0,
            gruposCompletados = 0,
            pedidosRealizados = 0,
            avatarRes = 0
        )
    )
    val usuario: StateFlow<UsuarioComprador> = _usuario.asStateFlow()

    /** El proveedor no maneja ahorro ni pedidos: el perfil le muestra las métricas del dashboard. */
    val esProveedor: Boolean = sesion?.rol == "proveedor"

    /** Etiqueta de rol para el encabezado (Comprador / Proveedor). */
    val rolLabel: String = if (esProveedor) "Proveedor" else "Comprador"

    // Foto de perfil del usuario logueado. Se lee de la sesión (UsuarioRepository) para que el
    // mismo avatar se vea acá y en la top bar, y se actualice al instante al sacarse una foto.
    val fotoUri: StateFlow<String?> = UsuarioRepository.usuarioActual
        .map { it?.fotoUri }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), sesion?.fotoUri)

    // Estadísticas reales (total ahorrado, grupos completados, pedidos) derivadas de las compras
    // del usuario. Misma fuente que el banner de Home, así los números siempre coinciden y se
    // actualizan al confirmar/cancelar una compra.
    val estadisticas: StateFlow<EstadisticasComprador> = EstadisticasRepository.estadisticas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EstadisticasComprador())

    private val _modoEdicion = MutableStateFlow(false)
    val modoEdicion: StateFlow<Boolean> = _modoEdicion.asStateFlow()

    private val _nombreEdit = MutableStateFlow(_usuario.value.nombre)
    val nombreEdit: StateFlow<String> = _nombreEdit.asStateFlow()

    private val _emailEdit = MutableStateFlow(_usuario.value.email)
    val emailEdit: StateFlow<String> = _emailEdit.asStateFlow()

    private val _telefonoEdit = MutableStateFlow(_usuario.value.telefono)
    val telefonoEdit: StateFlow<String> = _telefonoEdit.asStateFlow()

    private val _negocioEdit = MutableStateFlow(_usuario.value.negocio)
    val negocioEdit: StateFlow<String> = _negocioEdit.asStateFlow()

    private val _direccionEdit = MutableStateFlow(_usuario.value.direccion)
    val direccionEdit: StateFlow<String> = _direccionEdit.asStateFlow()

    fun toggleEdicion() {
        if (_modoEdicion.value) {
            _nombreEdit.value = _usuario.value.nombre
            _emailEdit.value = _usuario.value.email
            _telefonoEdit.value = _usuario.value.telefono
            _negocioEdit.value = _usuario.value.negocio
            _direccionEdit.value = _usuario.value.direccion
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
        _usuario.value = _usuario.value.copy(
            nombre = _nombreEdit.value,
            email = _emailEdit.value,
            telefono = _telefonoEdit.value,
            negocio = _negocioEdit.value,
            direccion = _direccionEdit.value
        )
        // Nombre y email son datos de la cuenta: se persisten para que el resto de la app
        // (top bar, menú lateral) refleje el cambio.
        UsuarioRepository.actualizarPerfil(_nombreEdit.value, _emailEdit.value)
        _modoEdicion.value = false
    }
}
