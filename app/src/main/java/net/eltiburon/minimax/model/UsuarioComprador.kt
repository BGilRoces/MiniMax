package net.eltiburon.minimax.model

data class UsuarioComprador(
    val nombre: String,
    val email: String,
    val telefono: String,
    val negocio: String,
    val direccion: String,
    val totalAhorrado: Double,
    val gruposCompletados: Int,
    val pedidosRealizados: Int,
    val avatarRes: Int
)
