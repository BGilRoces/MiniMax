package net.eltiburon.minimax.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.eltiburon.minimax.data.local.UsuarioDao
import net.eltiburon.minimax.data.local.UsuarioEntity
import net.eltiburon.minimax.model.Usuario
import java.util.UUID

/**
 * Fuente única de datos para usuarios, ahora persistida con Room (antes era un MutableStateFlow
 * en memoria, así que los registros se perdían al cerrar la app). Mismo patrón que
 * OportunidadRepository: object singleton conectado a un DAO real vía [init] desde MiniMaxApp.
 *
 * La sesión activa ([usuarioActual]) sigue viviendo en memoria: identifica al usuario logueado
 * durante la ejecución, pero las cuentas en sí quedan guardadas en SQLite.
 */
object UsuarioRepository {

    private lateinit var dao: UsuarioDao

    // Scope propio para persistir cambios de sesión (ej. el rol) sin bloquear a quien los dispara.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun init(dao: UsuarioDao) {
        this.dao = dao
    }

    suspend fun registrar(nombre: String, email: String, password: String): Result<Usuario> {
        if (dao.obtenerPorEmail(email) != null) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta con ese email"))
        }
        val nuevo = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            email = email,
            password = password
        )
        dao.insertar(nuevo.toEntity())
        return Result.success(nuevo)
    }

    suspend fun login(email: String, password: String): Result<Usuario> {
        val entidad = dao.obtenerPorEmail(email)
        if (entidad == null || entidad.password != password) {
            return Result.failure(IllegalArgumentException("Email o contraseña incorrectos"))
        }
        val usuario = entidad.toDomain()
        _usuarioActual.value = usuario
        return Result.success(usuario)
    }

    fun setRol(rol: String) {
        val actualizado = _usuarioActual.value?.copy(rol = rol) ?: return
        _usuarioActual.value = actualizado
        scope.launch { dao.actualizar(actualizado.toEntity()) }
    }

    /** Actualiza nombre/email del usuario logueado y lo persiste (editado desde Mi Perfil). */
    fun actualizarPerfil(nombre: String, email: String) {
        val actualizado = _usuarioActual.value?.copy(nombre = nombre, email = email) ?: return
        _usuarioActual.value = actualizado
        scope.launch { dao.actualizar(actualizado.toEntity()) }
    }

    /**
     * Actualiza la foto de perfil del usuario logueado y la persiste. Al vivir en [usuarioActual],
     * el cambio se refleja al instante tanto en Mi Perfil como en el avatar de la top bar.
     */
    fun actualizarFoto(fotoUri: String?) {
        val actualizado = _usuarioActual.value?.copy(fotoUri = fotoUri) ?: return
        _usuarioActual.value = actualizado
        scope.launch { dao.actualizar(actualizado.toEntity()) }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    /** Primera instalación: si la tabla está vacía, la precarga con el usuario de prueba. */
    suspend fun sembrarSiEstaVacia() {
        if (dao.contar() == 0) {
            dao.insertar(seed().toEntity())
        }
    }

    private fun Usuario.toEntity() = UsuarioEntity(
        id = id,
        nombre = nombre,
        email = email,
        password = password,
        rol = rol,
        fotoUri = fotoUri
    )

    private fun UsuarioEntity.toDomain() = Usuario(
        id = id,
        nombre = nombre,
        email = email,
        password = password,
        rol = rol,
        fotoUri = fotoUri
    )

    private fun seed() = Usuario(
        id = "test-user",
        nombre = "Usuario de Prueba",
        email = "test@minimax.com",
        password = "123456"
    )
}
