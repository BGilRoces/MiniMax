package net.eltiburon.minimax.model

data class GrupoResumen(
    val id: String,
    val nombre: String,
    val proveedor: String,
    val categoria: String,
    val imagenRes: Int,
    val precioGrupal: Double,
    val precioOriginal: Double,
    val descuentoPorcentaje: Int,
    val progresoActual: Int,
    val tiempoRestante: String,
    val estado: EstadoGrupo
)
