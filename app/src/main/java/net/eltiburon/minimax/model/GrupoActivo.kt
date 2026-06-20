package net.eltiburon.minimax.model

data class GrupoActivo(
    val id: String,
    val nombreProducto: String,
    val proveedor: String,
    val lote: String,
    val progreso: Float,         // 0f..1f
    val unidadesFaltantes: Int,
    val horasRestantes: Int,
    val prioridad: String = "PRIORIDAD ALTA"
)
