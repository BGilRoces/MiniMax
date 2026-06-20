package net.eltiburon.minimax.model

/**
 * Modelo de dominio único para una oportunidad de compra grupal.
 *
 * Antes cada pantalla tenía su propio recorte de estos datos (GrupoActivo, GrupoRecomendado,
 * GrupoResumen, GrupoDetalle, ProductoParticipacion), cada uno con mocks propios y a veces
 * con el mismo campo bajo nombres distintos (proveedor/proveedorNombre, precioGrupal/
 * precioMayorista). [Oportunidad] unifica esos campos y [OportunidadRepository] es ahora la
 * única fuente de verdad; cada pantalla sigue viendo su formato de card de siempre a través
 * de las funciones toGrupoXxx()/toProductoParticipacion() de abajo.
 */
data class Oportunidad(
    val id: String,
    val nombre: String,
    val proveedor: String,
    val proveedorDescripcion: String = "",
    val categoria: String,
    val descripcion: String = "",
    val imagenRes: Int,
    val imagenUri: String? = null,
    val precioUnitario: Double,
    val precioMayorista: Double,
    val descuentoPorcentaje: Int,
    val progresoActual: Int,           // 0..100
    val unidadesFaltantes: Int,
    val cantidadMaxima: Int = 20,
    val minutosRestantes: Int,
    val tiempoRestanteTexto: String = "",
    val miembrosActivos: Int = 0,
    val stockDisponible: Int = 0,
    val lote: String = "",
    val prioridad: String = "PRIORIDAD ALTA",
    val origen: String = "",
    val acidez: String = "",
    val crecimientoPorcentaje: Int = 0,
    val estado: EstadoGrupo = EstadoGrupo.FORMANDOSE
)

fun Oportunidad.toGrupoActivo() = GrupoActivo(
    id = id,
    nombreProducto = nombre,
    proveedor = proveedor,
    lote = lote,
    progreso = progresoActual / 100f,
    unidadesFaltantes = unidadesFaltantes,
    horasRestantes = minutosRestantes / 60,
    prioridad = prioridad
)

fun Oportunidad.toGrupoRecomendado() = GrupoRecomendado(
    id = id,
    nombre = nombre,
    proveedor = proveedor,
    descuento = descuentoPorcentaje,
    estado = estado
)

fun Oportunidad.toGrupoResumen() = GrupoResumen(
    id = id,
    nombre = nombre,
    proveedor = proveedor,
    categoria = categoria,
    imagenRes = imagenRes,
    precioGrupal = precioMayorista,
    precioOriginal = precioUnitario,
    descuentoPorcentaje = descuentoPorcentaje,
    progresoActual = progresoActual,
    tiempoRestante = tiempoRestanteTexto,
    estado = estado
)

fun Oportunidad.toGrupoDetalle() = GrupoDetalle(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    imagenRes = imagenRes,
    imagenUri = imagenUri,
    precioUnitario = precioUnitario,
    precioMayorista = precioMayorista,
    progresoActual = progresoActual,
    unidadesFaltantes = unidadesFaltantes,
    minutosRestantes = minutosRestantes,
    miembrosActivos = miembrosActivos,
    origen = origen,
    acidez = acidez,
    stockDisponible = stockDisponible,
    proveedorNombre = proveedor,
    proveedorDescripcion = proveedorDescripcion,
    crecimientoPorcentaje = crecimientoPorcentaje
)

fun Oportunidad.toProductoParticipacion() = ProductoParticipacion(
    nombre = nombre,
    proveedor = proveedor,
    categoria = categoria,
    precioUnitario = precioUnitario.toInt(),
    precioMayorista = precioMayorista.toInt(),
    descuentoPorcentaje = descuentoPorcentaje,
    progresoGrupo = progresoActual / 100f,
    cajasFaltantes = unidadesFaltantes,
    cantidadMaxima = cantidadMaxima
)
