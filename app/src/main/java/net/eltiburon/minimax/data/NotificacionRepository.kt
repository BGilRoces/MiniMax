package net.eltiburon.minimax.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import net.eltiburon.minimax.ui.theme.MiniMaxAccent
import net.eltiburon.minimax.ui.theme.MiniMaxBadgeRed
import net.eltiburon.minimax.ui.theme.MiniMaxOrange
import net.eltiburon.minimax.ui.theme.MiniMaxTeal
import java.util.concurrent.atomic.AtomicInteger

/** Tipo de notificación: define el ícono y el color del avatar de cada item. */
enum class TipoNotificacion(val icon: ImageVector, val color: Color) {
    GRUPO_COMPLETO(Icons.Filled.CheckCircle, MiniMaxTeal),
    POR_VENCER(Icons.Filled.AccessTime, MiniMaxOrange),
    NUEVO_GRUPO(Icons.Filled.GroupAdd, MiniMaxAccent),
    PAGO(Icons.Filled.Payments, MiniMaxBadgeRed),
    GENERAL(Icons.Filled.Info, MiniMaxAccent)
}

data class Notificacion(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val tiempo: String,
    val tipo: TipoNotificacion,
    val leida: Boolean = false
)

/**
 * Fuente única de las notificaciones del usuario. A diferencia de antes (lista hardcodeada en
 * el ViewModel), ahora se generan por eventos reales de la app:
 *  - al iniciar sesión se emite una de bienvenida y, a continuación, una recomendando un grupo
 *    real del catálogo ([inicializarSesion]);
 *  - cada vez que el comprador se compromete con una compra se agrega una ([emitirCompra]),
 *    aunque el grupo todavía no esté cerrado.
 *
 * Es un singleton en memoria (se reinicia al cerrar la app); se reemplazará al integrar backend.
 */
object NotificacionRepository {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    private val secuenciaId = AtomicInteger(0)

    fun marcarTodasLeidas() {
        _notificaciones.value = _notificaciones.value.map { it.copy(leida = true) }
    }

    fun marcarLeida(id: Int) {
        _notificaciones.value = _notificaciones.value.map {
            if (it.id == id) it.copy(leida = true) else it
        }
    }

    /**
     * Arranca la bandeja de la sesión: emite la bienvenida y, después de esa, una recomendación
     * de un grupo concreto del catálogo. Reinicia la lista para no acumular bienvenidas en cada
     * login. Se llama tras un login exitoso.
     */
    suspend fun inicializarSesion(nombreUsuario: String?) {
        secuenciaId.set(0)
        val bienvenida = Notificacion(
            id = secuenciaId.incrementAndGet(),
            titulo = "¡Bienvenido a MiniMax!",
            descripcion = if (nombreUsuario.isNullOrBlank()) {
                "Unite a grupos de compra y ahorrá comprando en conjunto."
            } else {
                "Hola $nombreUsuario, unite a grupos de compra y ahorrá comprando en conjunto."
            },
            tiempo = "Ahora",
            tipo = TipoNotificacion.GENERAL
        )

        // Recomendación dinámica: tomamos un grupo abierto del catálogo (el de mayor descuento).
        val recomendado = OportunidadRepository.obtenerTodas().first()
            .maxByOrNull { it.descuentoPorcentaje }
        val recomendacion = recomendado?.let {
            Notificacion(
                id = secuenciaId.incrementAndGet(),
                titulo = "Grupo recomendado para vos",
                descripcion = "Se abrió un grupo de ${it.nombre} con ${it.descuentoPorcentaje}% de descuento. ¡Sumate!",
                tiempo = "Ahora",
                tipo = TipoNotificacion.NUEVO_GRUPO
            )
        }

        // Más nuevas primero: la recomendación (emitida después) queda arriba de la bienvenida.
        _notificaciones.value = listOfNotNull(recomendacion, bienvenida)
    }

    /**
     * Notifica que el comprador se comprometió con una compra. Se dispara al confirmar la
     * participación, sin esperar a que el grupo se cierre.
     */
    fun emitirCompra(nombreProducto: String, cantidad: Int) {
        val unidades = if (cantidad == 1) "1 unidad" else "$cantidad unidades"
        val notificacion = Notificacion(
            id = secuenciaId.incrementAndGet(),
            titulo = "Compra confirmada",
            descripcion = "Te sumaste al grupo de $nombreProducto por $unidades. Te avisamos cuando se cierre.",
            tiempo = "Ahora",
            tipo = TipoNotificacion.PAGO
        )
        _notificaciones.value = listOf(notificacion) + _notificaciones.value
    }
}
