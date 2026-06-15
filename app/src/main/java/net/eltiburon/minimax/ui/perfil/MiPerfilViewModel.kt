package net.eltiburon.minimax.ui.perfil

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.eltiburon.minimax.model.UsuarioComprador

class MiPerfilViewModel : ViewModel() {

    // Cada estado se expone como StateFlow de solo lectura (.asStateFlow()): la UI
    // observa, pero solo puede modificarlo a través de los eventos (onXxxChange / guardarCambios).
    private val _usuario = MutableStateFlow(
        UsuarioComprador(
            nombre = "Lucas Gonzalez",
            email = "lucas.gonzalez@minimax.com",
            telefono = "+54 9 11 2345-6789",
            negocio = "Distribuidora Los Andes",
            direccion = "Av. Corrientes 1234, CABA",
            totalAhorrado = 12450.0,
            gruposCompletados = 8,
            pedidosRealizados = 23,
            avatarRes = 0
        )
    )
    val usuario: StateFlow<UsuarioComprador> = _usuario.asStateFlow()

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
        _modoEdicion.value = false
    }
}
