package net.eltiburon.minimax.ui.analitica

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Una métrica destacada (tarjeta superior). */
data class Metrica(
    val titulo: String,
    val valor: String,
    val variacion: String,
    val positiva: Boolean
)

/** Punto del gráfico de barras de ahorro mensual. */
data class BarraMes(
    val mes: String,
    val monto: Int
)

/**
 * ViewModel de Analítica. Todo mock y en memoria: son datos de presentación que sirven
 * igual aunque después vengan de un backend real (la UI no cambia).
 */
class AnaliticaViewModel : ViewModel() {

    private val _metricas = MutableStateFlow(mockMetricas())
    val metricas: StateFlow<List<Metrica>> = _metricas.asStateFlow()

    private val _ahorroMensual = MutableStateFlow(mockAhorroMensual())
    val ahorroMensual: StateFlow<List<BarraMes>> = _ahorroMensual.asStateFlow()

    private fun mockMetricas() = listOf(
        Metrica("Ahorro total", "$45.200", "+12%", positiva = true),
        Metrica("Grupos completados", "23", "+5", positiva = true),
        Metrica("Pedidos realizados", "31", "+8%", positiva = true),
        Metrica("Tasa de éxito", "87%", "-2%", positiva = false)
    )

    private fun mockAhorroMensual() = listOf(
        BarraMes("Ene", 2200),
        BarraMes("Feb", 3100),
        BarraMes("Mar", 2800),
        BarraMes("Abr", 4200),
        BarraMes("May", 3600),
        BarraMes("Jun", 5400)
    )
}
