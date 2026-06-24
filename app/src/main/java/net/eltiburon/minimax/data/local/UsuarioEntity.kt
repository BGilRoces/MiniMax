package net.eltiburon.minimax.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Espejo de [net.eltiburon.minimax.model.Usuario] para persistencia con Room. */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String?,
    val fotoUri: String? = null
)
