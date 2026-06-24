package net.eltiburon.minimax.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.eltiburon.minimax.model.EstadoGrupo

/** Espejo de [net.eltiburon.minimax.model.Oportunidad] para persistencia con Room. */
@Entity(tableName = "oportunidades")
data class OportunidadEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val proveedor: String,
    val proveedorDescripcion: String,
    val categoria: String,
    val descripcion: String,
    val imagenRes: Int,
    val imagenUri: String?,
    val precioUnitario: Double,
    val precioMayorista: Double,
    val descuentoPorcentaje: Int,
    val progresoActual: Int,
    val unidadesFaltantes: Int,
    val cantidadMaxima: Int,
    val minutosRestantes: Int,
    val tiempoRestanteTexto: String,
    val miembrosActivos: Int,
    val stockDisponible: Int,
    val lote: String,
    val prioridad: String,
    val origen: String,
    val acidez: String,
    val crecimientoPorcentaje: Int,
    val estado: EstadoGrupo
)
