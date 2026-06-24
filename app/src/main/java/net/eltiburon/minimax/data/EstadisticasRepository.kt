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

    /** Abreviaturas de mes en español, indexadas por Calendar.MONTH (0 = enero). */
    private val MESES_ABREV = listOf(
        "Ene", "Feb", "Mar", "Abr", "May", "Jun",
        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    )

    /** Ahorro de una compra atribuido a un mes del calendario, para el gráfico de Analítica. */
    data class AhorroMes(val etiqueta: String, val monto: Double)

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

    /**
     * Ahorro del usuario agrupado por mes para los últimos [meses] meses (incluido el actual),
     * en orden cronológico. Mismo cruce participaciones × oportunidades que [estadisticas], así
     * el gráfico de Analítica usa los mismos números reales que "Mis Ahorros".
     */
    fun ahorroMensual(meses: Int = 6): Flow<List<AhorroMes>> = combine(
        ParticipacionRepository.participaciones,
        UsuarioRepository.usuarioActual,
        OportunidadRepository.obtenerTodas()
    ) { participaciones, usuario, oportunidades ->
        val email = usuario?.email ?: return@combine emptyList()

        val compras = participaciones
            .filter { it.usuarioEmail.equals(email, ignoreCase = true) }
            .filter { it.estado != EstadoCompra.CANCELADA }
            .mapNotNull { participacion ->
                val oportunidad = oportunidades.find { it.id == participacion.oportunidadId }
                    ?: return@mapNotNull null
                participacion to oportunidad
            }

        // Un bucket (año, mes) por cada uno de los últimos `meses` meses, del más viejo al actual.
        (meses - 1 downTo 0).map { atras ->
            val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -atras) }
            val anio = cal.get(Calendar.YEAR)
            val mes = cal.get(Calendar.MONTH)
            val monto = compras
                .filter { (participacion, _) ->
                    val fecha = Calendar.getInstance().apply { timeInMillis = participacion.fechaMillis }
                    fecha.get(Calendar.YEAR) == anio && fecha.get(Calendar.MONTH) == mes
                }
                .sumOf { (participacion, oportunidad) ->
                    ahorroDe(oportunidad.precioUnitario, oportunidad.precioMayorista, participacion.cantidad)
                }
            AhorroMes(MESES_ABREV[mes], monto)
        }
    }
}
