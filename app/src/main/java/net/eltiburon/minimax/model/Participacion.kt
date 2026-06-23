package net.eltiburon.minimax.model

enum class EstadoCompra(val label: String) {
    ACTIVA("Activa"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada")
}

data class Participacion(
    val id: String,
    val oportunidadId: String,
    val usuarioEmail: String,
    val cantidad: Int,
    val fechaMillis: Long,
    val estado: EstadoCompra = EstadoCompra.ACTIVA
)
