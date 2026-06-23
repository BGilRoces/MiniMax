package net.eltiburon.minimax.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.eltiburon.minimax.model.EstadoCompra
import net.eltiburon.minimax.model.Participacion
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Fuente única de datos en memoria para las participaciones de los compradores
 * (mismo patrón que OportunidadRepository/UsuarioRepository: object + MutableStateFlow).
 */
object ParticipacionRepository {

    private val _participaciones = MutableStateFlow(seed())
    val participaciones: StateFlow<List<Participacion>> = _participaciones.asStateFlow()

    fun obtenerDelUsuario(email: String): List<Participacion> =
        _participaciones.value.filter { it.usuarioEmail.equals(email, ignoreCase = true) }

    fun agregar(participacion: Participacion) {
        _participaciones.value = _participaciones.value + participacion
    }

    fun cancelar(id: String) {
        _participaciones.value = _participaciones.value.map {
            if (it.id == id) it.copy(estado = EstadoCompra.CANCELADA) else it
        }
    }

    fun nuevoId(): String = UUID.randomUUID().toString()

    // Oportunidades "1" y "3" son del seed de Home; "102" es del seed de Explorar
    // (ver OportunidadRepository), así que se ven correctamente al cruzarlas.
    private fun seed(): List<Participacion> {
        val ahora = System.currentTimeMillis()
        fun haceDias(dias: Long) = ahora - TimeUnit.DAYS.toMillis(dias)

        return listOf(
            Participacion(
                id = "seed-1",
                oportunidadId = "1",
                usuarioEmail = "test@minimax.com",
                cantidad = 3,
                fechaMillis = haceDias(2),
                estado = EstadoCompra.ACTIVA
            ),
            Participacion(
                id = "seed-2",
                oportunidadId = "102",
                usuarioEmail = "test@minimax.com",
                cantidad = 1,
                fechaMillis = haceDias(10),
                estado = EstadoCompra.COMPLETADA
            ),
            Participacion(
                id = "seed-3",
                oportunidadId = "3",
                usuarioEmail = "test@minimax.com",
                cantidad = 2,
                fechaMillis = haceDias(5),
                estado = EstadoCompra.ACTIVA
            )
        )
    }
}
