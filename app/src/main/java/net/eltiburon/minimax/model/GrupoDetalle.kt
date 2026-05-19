package net.eltiburon.minimax.model

data class GrupoDetalle(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val imagenRes: Int,
    val precioUnitario: Double,
    val precioMayorista: Double,
    val progresoActual: Int,
    val unidadesFaltantes: Int,
    val minutosRestantes: Int,
    val miembrosActivos: Int,
    val origen: String,
    val acidez: String,
    val stockDisponible: Int,
    val proveedorNombre: String,
    val proveedorDescripcion: String,
    val crecimientoPorcentaje: Int
)
