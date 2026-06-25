package net.eltiburon.minimax.data

import android.content.Context
import android.content.SharedPreferences
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
 * Fuente única de datos para usuarios, persistida con Room (antes era un MutableStateFlow
 * en memoria, así que los registros se perdían al cerrar la app). Mismo patrón que
 * OportunidadRepository: object singleton conectado a un DAO real vía [init] desde MiniMaxApp.
 *
 * La sesión activa ([usuarioActual]) se persiste en SharedPreferences. Antes vivía solo en
 * memoria, así que cuando Android mataba y recreaba el proceso (background, presión de memoria,
 * "No conservar actividades") el singleton se reiniciaba a null mientras Navigation restauraba
 * el back stack: el usuario quedaba en la pantalla donde estaba pero sin sesión, lo que se veía
 * como un cierre de sesión intermitente. Ahora la restauramos al iniciar, antes de la primera
 * composición, así sobrevive a la muerte de proceso.
 */
object UsuarioRepository {

    private lateinit var dao: UsuarioDao
    private lateinit var prefs: SharedPreferences

    // Scope propio para persistir cambios de sesión (ej. el rol) sin bloquear a quien los dispara.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun init(dao: UsuarioDao, context: Context) {
        this.dao = dao
        prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        // Restaura la sesión guardada de forma síncrona para que ya esté disponible en la
        // primera composición y no haya un parpadeo de "sesión cerrada" tras recrear el proceso.
        _usuarioActual.value = leerSesion()
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
        actualizarSesion(usuario)
        return Result.success(usuario)
    }

    fun setRol(rol: String) {
        val actualizado = _usuarioActual.value?.copy(rol = rol) ?: return
        actualizarSesion(actualizado)
        scope.launch { dao.actualizar(actualizado.toEntity()) }
    }

    /** Actualiza nombre/email del usuario logueado y lo persiste (editado desde Mi Perfil). */
    fun actualizarPerfil(nombre: String, email: String) {
        val actualizado = _usuarioActual.value?.copy(nombre = nombre, email = email) ?: return
        actualizarSesion(actualizado)
        scope.launch { dao.actualizar(actualizado.toEntity()) }
    }

    /**
     * Actualiza la foto de perfil del usuario logueado y la persiste. Al vivir en [usuarioActual],
     * el cambio se refleja al instante tanto en Mi Perfil como en el avatar de la top bar.
     */
    fun actualizarFoto(fotoUri: String?) {
        val actualizado = _usuarioActual.value?.copy(fotoUri = fotoUri) ?: return
        actualizarSesion(actualizado)
        scope.launch { dao.actualizar(actualizado.toEntity()) }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        prefs.edit().clear().apply()
    }

    /**
     * Actualiza la sesión en memoria y la persiste. Guardamos los campos directamente en
     * SharedPreferences (no el id solo) para poder restaurar en [init] de forma síncrona, sin
     * tocar Room en el hilo principal. La cuenta completa, con password, sigue viviendo en SQLite.
     */
    private fun actualizarSesion(usuario: Usuario) {
        _usuarioActual.value = usuario
        prefs.edit()
            .putString(KEY_ID, usuario.id)
            .putString(KEY_NOMBRE, usuario.nombre)
            .putString(KEY_EMAIL, usuario.email)
            .putString(KEY_PASSWORD, usuario.password)
            .putString(KEY_ROL, usuario.rol)
            .putString(KEY_FOTO_URI, usuario.fotoUri)
            .apply()
    }

    /** Reconstruye la sesión persistida desde SharedPreferences (o null si no hay ninguna). */
    private fun leerSesion(): Usuario? {
        val id = prefs.getString(KEY_ID, null) ?: return null
        return Usuario(
            id = id,
            nombre = prefs.getString(KEY_NOMBRE, "").orEmpty(),
            email = prefs.getString(KEY_EMAIL, "").orEmpty(),
            password = prefs.getString(KEY_PASSWORD, "").orEmpty(),
            rol = prefs.getString(KEY_ROL, null),
            fotoUri = prefs.getString(KEY_FOTO_URI, null)
        )
    }

    /** Primera instalación: si la tabla está vacía, la precarga con el usuario de prueba. */
    suspend fun sembrarSiEstaVacia() {
        if (dao.contar() == 0) {
            dao.insertar(seed().toEntity())
        }
        // Asegura una cuenta de proveedor de prueba. Idempotente: solo la inserta si no existe,
        // así sobrevive a reinstalar la demo sin duplicarse en cada arranque.
        if (dao.obtenerPorEmail("test1@gmail.com") == null) {
            dao.insertar(seedProveedor().toEntity())
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

    private fun seedProveedor() = Usuario(
        id = "test-proveedor",
        nombre = "Proveedor de Prueba",
        email = "test1@gmail.com",
        password = "testeo",
        rol = "proveedor"
    )

    // Claves de la sesión persistida en SharedPreferences.
    private const val KEY_ID = "usuario_id"
    private const val KEY_NOMBRE = "usuario_nombre"
    private const val KEY_EMAIL = "usuario_email"
    private const val KEY_PASSWORD = "usuario_password"
    private const val KEY_ROL = "usuario_rol"
    private const val KEY_FOTO_URI = "usuario_foto_uri"
}
