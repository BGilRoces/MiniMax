package net.eltiburon.minimax.ui.auth

/**
 * Estados genéricos de pantalla. Hoy lo usan Login/Registro contra un repo en memoria;
 * el patrón es el mismo que se va a reutilizar más adelante para llamadas a Room/Retrofit.
 */
sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data class Error(val mensaje: String) : UiState()
}
