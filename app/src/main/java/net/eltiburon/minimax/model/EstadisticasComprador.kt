package net.eltiburon.minimax.model

/**
 * Estadísticas del comprador derivadas de sus participaciones (compras). No se persisten:
 * se recalculan en vivo a partir de [Participacion] + [Oportunidad] cada vez que el usuario
 * confirma o cancela una compra, así Home y Mi Perfil muestran siempre números reales.
 *
 * El ahorro cuenta tanto las compras COMPLETADA como las ACTIVA (grupo todavía formándose):
 * apenas el usuario confirma su compra ya ve reflejado lo que va a ahorrar, sin esperar a que
 * el grupo se cierre. Las CANCELADA quedan fuera.
 */
data class EstadisticasComprador(
    val totalAhorrado: Double = 0.0,
    val ahorroEsteMes: Double = 0.0,
    val gruposCompletados: Int = 0,
    val pedidosRealizados: Int = 0
)

/** Formatea un monto como pesos argentinos: "$12.450" (punto como separador de miles). */
fun formatearPesos(monto: Double): String =
    "$" + "%,.0f".format(monto).replace(",", ".")
