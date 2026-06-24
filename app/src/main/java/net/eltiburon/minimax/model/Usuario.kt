package net.eltiburon.minimax.model

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String? = null,
    /** URI de la foto de perfil tomada con la cámara (file://). null = se muestra el avatar con iniciales. */
    val fotoUri: String? = null
)
