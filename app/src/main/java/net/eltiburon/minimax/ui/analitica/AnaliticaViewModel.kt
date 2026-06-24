package net.eltiburon.minimax.ui.analitica

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.eltiburon.minimax.data.EstadisticasRepository
import net.eltiburon.minimax.model.formatearPesos

/** Una métrica destacada (tarjeta superior). */
data class Metrica(
    val titulo: String,
    val valor: String,
    /** Texto del badge de contexto (ej. lo ahorrado este mes). Null = sin badge. */
    val variacion: String? = null,
    val positiva: Boolean = true
)

/** Punto del gráfico de barras de ahorro mensual. */
data class BarraMes(
    val mes: String,
    val monto: Double
)

/**
 * ViewModel de Analítica. Deriva todo de [EstadisticasRepository] (mismas participaciones reales
 * que alimentan "Mis Ahorros" en Home), así los números coinciden y se actualizan solos al
 * confirmar o cancelar una compra.
 */
class AnaliticaViewModel : ViewModel() {

    val metricas: StateFlow<List<Metrica>> = EstadisticasRepository.estadisticas()
        .map { stats ->
            val tasaExito = if (stats.pedidosRealizados > 0) {
                (stats.gruposCompletados * 100) / stats.pedidosRealizados
            } else 0
            listOf(
                Metrica(
                    titulo = "Ahorro total",
                    valor = formatearPesos(stats.totalAhorrado),
                    variacion = if (stats.ahorroEsteMes > 0)
                        "+${formatearPesos(stats.ahorroEsteMes)} este mes" else null,
                    positiva = true
                ),
                Metrica("Grupos completados", stats.gruposCompletados.toString()),
                Metrica("Pedidos realizados", stats.pedidosRealizados.toString()),
                Metrica(
                    titulo = "Tasa de éxito",
                    valor = "$tasaExito%",
                    variacion = "${stats.gruposCompletados}/${stats.pedidosRealizados} cerrados",
                    positiva = tasaExito >= 50
                )
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ahorroMensual: StateFlow<List<BarraMes>> = EstadisticasRepository.ahorroMensual()
        .map { meses -> meses.map { BarraMes(it.etiqueta, it.monto) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
