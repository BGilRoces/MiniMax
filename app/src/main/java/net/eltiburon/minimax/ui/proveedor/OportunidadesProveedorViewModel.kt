package net.eltiburon.minimax.ui.proveedor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Oportunidad (grupo de compra) publicada por el proveedor. */
data class Oportunidad(
    val id: Int,
    val producto: String,
    val unidadesVendidas: Int,
    val unidadesMinimas: Int,
    val precioGrupal: Int,
    val cierraEn: String,
    val activa: Boolean,
    val imagenUrl: String
) {
    val progreso: Float get() = (unidadesVendidas.toFloat() / unidadesMinimas).coerceIn(0f, 1f)
    val minimoAlcanzado: Boolean get() = unidadesVendidas >= unidadesMinimas
}

/** ViewModel de "Oportunidades" del proveedor: lista mock de grupos publicados. */
class OportunidadesProveedorViewModel : ViewModel() {

    private val _oportunidades = MutableStateFlow(mockOportunidades())
    val oportunidades: StateFlow<List<Oportunidad>> = _oportunidades.asStateFlow()

    private fun mockOportunidades() = listOf(
        Oportunidad(1, "Componentes Electrónicos X-200", 120, 100, 3500, "4 h", activa = true, imagenUrl = imagen("electronics,components", 201)),
        Oportunidad(2, "Sillas Ergonómicas Serie-A", 45, 60, 28500, "2 días", activa = true, imagenUrl = imagen("office,chair", 202)),
        Oportunidad(3, "Teclado Mecánico RGB", 30, 50, 8500, "1 día", activa = true, imagenUrl = imagen("keyboard", 203)),
        Oportunidad(4, "Monitor 4K 27\" Ultra", 18, 40, 39500, "5 días", activa = true, imagenUrl = imagen("monitor,screen", 204)),
        Oportunidad(5, "Hub USB-C Premium", 8, 30, 3800, "Cerrada", activa = false, imagenUrl = imagen("usb,hub", 205))
    )

    /** Misma fuente de imágenes "de mentira" que el catálogo demo (ver MockImagenes). */
    private fun imagen(keywords: String, lock: Int) =
        "https://loremflickr.com/400/300/$keywords?lock=$lock"
}
