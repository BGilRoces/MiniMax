package net.eltiburon.minimax.model

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String? = null
)
