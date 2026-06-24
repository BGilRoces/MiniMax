package net.eltiburon.minimax.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.eltiburon.minimax.data.NotificacionRepository
import net.eltiburon.minimax.data.UsuarioRepository

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private const val PASSWORD_MIN_LENGTH = 6

/**
 * ViewModel compartido por LoginScreen y RegistroScreen.
 *
 * Hoy valida y pega contra UsuarioRepository (en memoria), pero el patrón de uiState
 * (Idle -> Loading -> Success/Error) es el mismo que se va a reusar cuando login/registrar
 * pasen a ser llamadas reales (Room/Retrofit). El delay() simula esa latencia para que el
 * estado de Loading sea visible en la demo.
 */
class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _confirmarPassword = MutableStateFlow("")
    val confirmarPassword: StateFlow<String> = _confirmarPassword.asStateFlow()

    fun onEmailChange(v: String) { _email.value = v }
    fun onPasswordChange(v: String) { _password.value = v }
    fun onNombreChange(v: String) { _nombre.value = v }
    fun onConfirmarPasswordChange(v: String) { _confirmarPassword.value = v }

    fun login() {
        val emailValor = _email.value.trim()
        val passwordValor = _password.value

        val error = validarEmail(emailValor) ?: validarPassword(passwordValor)
        if (error != null) {
            _uiState.value = UiState.Error(error)
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(400)
            UsuarioRepository.login(emailValor, passwordValor).fold(
                onSuccess = { usuario ->
                    // Bandeja de notificaciones de la sesión: bienvenida + recomendación de grupo.
                    NotificacionRepository.inicializarSesion(usuario.nombre)
                    _uiState.value = UiState.Success
                },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "No se pudo iniciar sesión") }
            )
        }
    }

    fun registrar() {
        val nombreValor = _nombre.value.trim()
        val emailValor = _email.value.trim()
        val passwordValor = _password.value
        val confirmarValor = _confirmarPassword.value

        val error = when {
            nombreValor.isBlank() -> "Ingresá tu nombre"
            else -> validarEmail(emailValor)
                ?: validarPassword(passwordValor)
                ?: if (passwordValor != confirmarValor) "Las contraseñas no coinciden" else null
        }
        if (error != null) {
            _uiState.value = UiState.Error(error)
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(400)
            UsuarioRepository.registrar(nombreValor, emailValor, passwordValor).fold(
                onSuccess = { _uiState.value = UiState.Success },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "No se pudo registrar") }
            )
        }
    }

    /** Vuelve el uiState a Idle, para no repetir la navegación tras un Success ya consumido. */
    fun resetEstado() {
        _uiState.value = UiState.Idle
    }

    private fun validarEmail(email: String): String? =
        if (!EMAIL_REGEX.matches(email)) "Ingresá un email válido" else null

    private fun validarPassword(password: String): String? =
        if (password.length < PASSWORD_MIN_LENGTH) {
            "La contraseña debe tener al menos $PASSWORD_MIN_LENGTH caracteres"
        } else null
}
