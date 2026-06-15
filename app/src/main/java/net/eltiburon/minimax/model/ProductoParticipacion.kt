package net.eltiburon.minimax.model

/**
 * Datos del producto del flujo "sumarse a un grupo" (Elegir cantidad → Confirmar → Confirmación).
 *
 * Antes estos valores estaban como constantes copiadas dentro de cada pantalla. Al modelarlos
 * como un único data class con una fuente de datos común ([demo]), se cumple DRY y la lógica
 * de negocio (precios, máximo, etc.) deja de vivir en la UI.
 */
data class ProductoParticipacion(
    val nombre: String,
    val proveedor: String,
    val categoria: String,
    val precioUnitario: Int,
    val precioMayorista: Int,
    val descuentoPorcentaje: Int,
    val progresoGrupo: Float,
    val cajasFaltantes: Int,
    val cantidadMaxima: Int
) {
    companion object {
        /** Producto mock usado por toda la demo de participación. */
        fun demo() = ProductoParticipacion(
            nombre = "Caja de Aceite de Oliva Premium x12",
            proveedor = "Olivares del Valle",
            categoria = "Alimentos & Bebidas",
            precioUnitario = 42_500,
            precioMayorista = 34_000,
            descuentoPorcentaje = 20,
            progresoGrupo = 0.80f,
            cajasFaltantes = 12,
            cantidadMaxima = 20
        )
    }
}
