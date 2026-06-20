package net.eltiburon.minimax.model

enum class EstadoGrupo(val label: String) {
    FORMANDOSE("Formándose"),
    CASI_LLENO("Casi Lleno"),
    URGENTE("¡Urgente!")
}

data class GrupoRecomendado(
    val id: String,
    val nombre: String,
    val proveedor: String,
    val descuento: Int,          // porcentaje, ej: 22 → "-22%"
    val estado: EstadoGrupo
)
