package net.eltiburon.minimax.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.eltiburon.minimax.model.Usuario
import java.util.UUID

/**
 * Fuente única de datos en memoria para usuarios y sesión (sin Room/DataStore todavía).
 * Mismo patrón que OportunidadRepository: un object singleton con MutableStateFlow.
 */
object UsuarioRepository {

    private val _usuarios = MutableStateFlow(seed())

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun registrar(nombre: String, email: String, password: String): Result<Usuario> {
        if (_usuarios.value.any { it.email.equals(email, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta con ese email"))
        }
        val nuevo = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            email = email,
            password = password
        )
        _usuarios.value = _usuarios.value + nuevo
        return Result.success(nuevo)
    }

    fun login(email: String, password: String): Result<Usuario> {
        val usuario = _usuarios.value.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        } ?: return Result.failure(IllegalArgumentException("Email o contraseña incorrectos"))

        _usuarioActual.value = usuario
        return Result.success(usuario)
    }

    fun setRol(rol: String) {
        val actualizado = _usuarioActual.value?.copy(rol = rol) ?: return
        _usuarioActual.value = actualizado
        _usuarios.value = _usuarios.value.map { if (it.id == actualizado.id) actualizado else it }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    private fun seed() = listOf(
        Usuario(
            id = "test-user",
            nombre = "Usuario de Prueba",
            email = "test@minimax.com",
            password = "123456"
        )
    )
}
