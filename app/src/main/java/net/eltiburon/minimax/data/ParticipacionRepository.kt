package net.eltiburon.minimax.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.eltiburon.minimax.data.local.ParticipacionDao
import net.eltiburon.minimax.data.local.ParticipacionEntity
import net.eltiburon.minimax.model.EstadoCompra
import net.eltiburon.minimax.model.Participacion
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Fuente única de datos para las participaciones de los compradores, ahora persistida con
 * Room (mismo patrón que OportunidadRepository: object singleton conectado a un DAO vía [init]).
 */
object ParticipacionRepository {

    private lateinit var dao: ParticipacionDao

    fun init(dao: ParticipacionDao) {
        this.dao = dao
    }

    val participaciones: Flow<List<Participacion>>
        get() = dao.obtenerTodas().map { entidades -> entidades.map { it.toDomain() } }

    suspend fun cancelar(id: String) = dao.cancelar(id)

    fun nuevoId(): String = UUID.randomUUID().toString()

    /**
     * Inserta la participación y descuenta el stock de la oportunidad (floor en 0) en una sola
     * transacción de Room (ver ParticipacionDao.confirmarParticipacion). El progreso se
     * recalcula como el % de cantidadMaxima ya cubierto por las unidades restantes.
     */
    suspend fun confirmarParticipacion(oportunidadId: String, usuarioEmail: String, cantidad: Int) {
        val oportunidadActual = OportunidadRepository.obtenerPorId(oportunidadId) ?: return

        val unidadesFaltantesNuevas = (oportunidadActual.unidadesFaltantes - cantidad).coerceAtLeast(0)
        val progresoNuevo = if (oportunidadActual.cantidadMaxima > 0) {
            (((oportunidadActual.cantidadMaxima - unidadesFaltantesNuevas).toFloat() / oportunidadActual.cantidadMaxima) * 100)
                .toInt()
                .coerceIn(0, 100)
        } else {
            oportunidadActual.progresoActual
        }

        val participacion = Participacion(
            id = nuevoId(),
            oportunidadId = oportunidadId,
            usuarioEmail = usuarioEmail,
            cantidad = cantidad,
            fechaMillis = System.currentTimeMillis(),
            estado = EstadoCompra.ACTIVA
        )

        dao.confirmarParticipacion(
            participacion = participacion.toEntity(),
            oportunidadId = oportunidadId,
            unidadesFaltantesNuevas = unidadesFaltantesNuevas,
            progresoActualNuevo = progresoNuevo
        )
    }

    /** Primera instalación: si la tabla está vacía, la precarga con las compras demo. */
    suspend fun sembrarSiEstaVacia() {
        if (dao.contar() == 0) {
            dao.insertarTodas(seed().map { it.toEntity() })
        }
    }

    private fun Participacion.toEntity() = ParticipacionEntity(
        id = id,
        oportunidadId = oportunidadId,
        usuarioEmail = usuarioEmail,
        cantidad = cantidad,
        fechaMillis = fechaMillis,
        estado = estado
    )

    private fun ParticipacionEntity.toDomain() = Participacion(
        id = id,
        oportunidadId = oportunidadId,
        usuarioEmail = usuarioEmail,
        cantidad = cantidad,
        fechaMillis = fechaMillis,
        estado = estado
    )

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
