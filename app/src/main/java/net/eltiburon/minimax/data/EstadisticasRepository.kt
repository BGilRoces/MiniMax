package net.eltiburon.minimax.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import net.eltiburon.minimax.model.EstadisticasComprador
import net.eltiburon.minimax.model.EstadoCompra
import java.util.Calendar

/**
 * Deriva las [EstadisticasComprador] del usuario logueado cruzando sus participaciones con el
 * catálogo de oportunidades. Es la única fuente para los números de "Mis Ahorros" (Home) y la
 * tarjeta de estadísticas (Mi Perfil), de modo que ambos siempre coinciden y se actualizan solos
 * al confirmar/cancelar una compra (todo sale de flows de Room).
 */
object EstadisticasRepository {

    /** Ahorro de una compra: lo que se ahorra vs. el precio unitario, por la cantidad comprada. */
    private fun ahorroDe(precioUnitario: Double, precioMayorista: Double, cantidad: Int): Double =
        (precioUnitario - precioMayorista).coerceAtLeast(0.0) * cantidad

    private fun esDelMesActual(fechaMillis: Long): Boolean {
        val ahora = Calendar.getInstance()
        val fecha = Calendar.getInstance().apply { timeInMillis = fechaMillis }
        return ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
            ahora.get(Calendar.MONTH) == fecha.get(Calendar.MONTH)
    }

    fun estadisticas(): Flow<EstadisticasComprador> = combine(
        ParticipacionRepository.participaciones,
        UsuarioRepository.usuarioActual,
        OportunidadRepository.obtenerTodas()
    ) { participaciones, usuario, oportunidades ->
        val email = usuario?.email ?: return@combine EstadisticasComprador()

        // Compras del usuario que cuentan: las que no fueron canceladas (ACTIVA + COMPLETADA).
        val compras = participaciones
            .filter { it.usuarioEmail.equals(email, ignoreCase = true) }
            .filter { it.estado != EstadoCompra.CANCELADA }
            .mapNotNull { participacion ->
                val oportunidad = oportunidades.find { it.id == participacion.oportunidadId }
                    ?: return@mapNotNull null
                participacion to oportunidad
            }

        var totalAhorrado = 0.0
        var ahorroEsteMes = 0.0
        var gruposCompletados = 0

        compras.forEach { (participacion, oportunidad) ->
            val ahorro = ahorroDe(
                precioUnitario = oportunidad.precioUnitario,
                precioMayorista = oportunidad.precioMayorista,
                cantidad = participacion.cantidad
            )
            totalAhorrado += ahorro
            if (esDelMesActual(participacion.fechaMillis)) ahorroEsteMes += ahorro
            if (participacion.estado == EstadoCompra.COMPLETADA) gruposCompletados++
        }

        EstadisticasComprador(
            totalAhorrado = totalAhorrado,
            ahorroEsteMes = ahorroEsteMes,
            gruposCompletados = gruposCompletados,
            pedidosRealizados = compras.size
        )
    }
}
