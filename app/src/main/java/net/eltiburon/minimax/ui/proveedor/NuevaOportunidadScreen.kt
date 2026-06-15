package net.eltiburon.minimax.ui.proveedor

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.eltiburon.minimax.ui.camera.CameraCaptureScreen
import net.eltiburon.minimax.ui.theme.*

private val categorias = listOf(
    "Alimentos", "Electrónica", "Decoración", "Cafetería", "Textil", "Gadgets", "Otros"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaOportunidadScreen(
    onBackClick: () -> Unit = {},
    onPublicadoOk: () -> Unit = {},
    viewModel: NuevaOportunidadViewModel = viewModel()
) {
    val nombre          by viewModel.nombre.collectAsState()
    val categoria       by viewModel.categoria.collectAsState()
    val descripcion     by viewModel.descripcion.collectAsState()
    val precioMayorista by viewModel.precioMayorista.collectAsState()
    val precioReferencia by viewModel.precioReferencia.collectAsState()
    val cantidadMinima  by viewModel.cantidadMinima.collectAsState()
    val stockDisponible by viewModel.stockDisponible.collectAsState()
    val fechaLimite     by viewModel.fechaLimite.collectAsState()
    val imagenUri       by viewModel.imagenUri.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope             = rememberCoroutineScope()

    var categoriaExpandida by remember { mutableStateOf(false) }
    var mostrarDatePicker  by remember { mutableStateOf(false) }
    val datePickerState    = rememberDatePickerState()

    // ── Estado de cámara / imagen ────────────────────────────────────────────
    var mostrarCamara        by remember { mutableStateOf(false) }
    var mostrarPermisoDenegado by remember { mutableStateOf(false) }

    // Permiso de cámara en runtime.
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) mostrarCamara = true else mostrarPermisoDenegado = true
    }

    val pedirCamara: () -> Unit = {
        val concedido = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (concedido) mostrarCamara = true
        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // ── Overlay de cámara a pantalla completa ────────────────────────────────
    if (mostrarCamara) {
        CameraCaptureScreen(
            onImageCaptured = { uri ->
                viewModel.onImagenChange(uri.toString())
                mostrarCamara = false
            },
            onClose = { mostrarCamara = false }
        )
        return
    }

    if (mostrarPermisoDenegado) {
        AlertDialog(
            onDismissRequest = { mostrarPermisoDenegado = false },
            confirmButton = {
                TextButton(
                    onClick = { mostrarPermisoDenegado = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MiniMaxAccent)
                ) { Text("Entendido") }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.NoPhotography,
                    contentDescription = null,
                    tint = MiniMaxAccent
                )
            },
            title = { Text("Permiso de cámara necesario") },
            text = {
                Text(
                    "Necesitamos acceso a la cámara para tomar la foto del producto. " +
                        "Podés habilitar el permiso desde los ajustes de la aplicación."
                )
            }
        )
    }

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = java.time.Instant.ofEpochMilli(millis)
                            val date = instant.atZone(java.time.ZoneId.of("UTC")).toLocalDate()
                            viewModel.onFechaLimiteChange(
                                "%02d/%02d/%04d".format(date.dayOfMonth, date.monthValue, date.year)
                            )
                        }
                        mostrarDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MiniMaxAccent)
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) { Text("Cancelar") }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = MiniMaxAccent,
                    todayDateBorderColor = MiniMaxAccent
                )
            )
        }
    }

    Scaffold(
        containerColor = MiniMaxBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            item { NuevaOportunidadHeader(onBackClick = onBackClick) }

            item { TituloBlock() }

            item {
                ImagenProductoCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    imagenUri = imagenUri,
                    onAgregarImagen = { pedirCamara() },
                    onEliminarImagen = { viewModel.onImagenChange(null) }
                )
            }

            item {
                FormularioCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    nombre = nombre,
                    onNombreChange = viewModel::onNombreChange,
                    categoria = categoria,
                    categoriaExpandida = categoriaExpandida,
                    onCategoriaExpand = { categoriaExpandida = it },
                    onCategoriaSelect = {
                        viewModel.onCategoriaChange(it)
                        categoriaExpandida = false
                    },
                    descripcion = descripcion,
                    onDescripcionChange = viewModel::onDescripcionChange,
                    precioMayorista = precioMayorista,
                    onPrecioMayoristaChange = viewModel::onPrecioMayoristaChange,
                    precioReferencia = precioReferencia,
                    onPrecioReferenciaChange = viewModel::onPrecioReferenciaChange,
                    cantidadMinima = cantidadMinima,
                    onCantidadMinimaChange = viewModel::onCantidadMinimaChange,
                    stockDisponible = stockDisponible,
                    onStockDisponibleChange = viewModel::onStockDisponibleChange,
                    fechaLimite = fechaLimite,
                    onFechaLimiteChange = viewModel::onFechaLimiteChange,
                    onAbrirDatePicker = { mostrarDatePicker = true }
                )
            }

            item {
                BotonesAccion(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    onGuardar = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Borrador guardado")
                        }
                    },
                    onPublicar = {
                        scope.launch {
                            if (viewModel.camposObligatoriosCompletos()) {
                                snackbarHostState.showSnackbar("Oportunidad publicada correctamente")
                                onPublicadoOk()
                            } else {
                                snackbarHostState.showSnackbar("Completá los campos obligatorios")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NuevaOportunidadHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiniMaxPrimary)
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MiniMaxAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MiniMax",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun TituloBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Nueva oportunidad",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MiniMaxTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Completá los datos del producto para publicarlo como compra grupal.",
            fontSize = 14.sp,
            color = MiniMaxTextPrimary.copy(alpha = 0.60f),
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ImagenProductoCard(
    modifier: Modifier = Modifier,
    imagenUri: String?,
    onAgregarImagen: () -> Unit,
    onEliminarImagen: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Imagen del producto",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MiniMaxTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (imagenUri != null) {
                // Vista previa de la imagen seleccionada con opción de cambiar/eliminar.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MiniMaxPrimary.copy(alpha = 0.04f))
                ) {
                    ProductoThumbnail(
                        uri = imagenUri,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = onEliminarImagen,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(34.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Black.copy(alpha = 0.45f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar imagen",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onAgregarImagen,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MiniMaxAccent),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MiniMaxAccent)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cambiar imagen", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MiniMaxPrimary.copy(alpha = 0.04f))
                        .border(
                            width = 1.5.dp,
                            color = MiniMaxPrimary.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onAgregarImagen),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudUpload,
                            contentDescription = null,
                            tint = MiniMaxAccent,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agregar imagen",
                            color = MiniMaxAccent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tomá una foto del producto con la cámara.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/** Carga el [uri] (archivo interno) y lo muestra como thumbnail decodificado a bitmap. */
@Composable
private fun ProductoThumbnail(uri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                val parsed = Uri.parse(uri)
                val decoded = context.contentResolver.openInputStream(parsed)?.use { input ->
                    BitmapFactory.decodeStream(input)
                } ?: return@runCatching null
                // BitmapFactory ignora la orientación EXIF, así que la aplicamos manualmente
                // para que la foto se muestre tal como fue tomada (sin rotarla).
                val orientation = context.contentResolver.openInputStream(parsed)?.use { input ->
                    ExifInterface(input).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                } ?: ExifInterface.ORIENTATION_NORMAL
                decoded.applyExifOrientation(orientation).asImageBitmap()
            }.getOrNull()
        }
    }

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Imagen del producto",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

/** Devuelve el bitmap rotado/espejado según la orientación EXIF indicada. */
private fun Bitmap.applyExifOrientation(orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postRotate(90f); matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postRotate(270f); matrix.postScale(-1f, 1f)
        }
        else -> return this
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioCard(
    modifier: Modifier = Modifier,
    nombre: String,
    onNombreChange: (String) -> Unit,
    categoria: String,
    categoriaExpandida: Boolean,
    onCategoriaExpand: (Boolean) -> Unit,
    onCategoriaSelect: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    precioMayorista: String,
    onPrecioMayoristaChange: (String) -> Unit,
    precioReferencia: String,
    onPrecioReferenciaChange: (String) -> Unit,
    cantidadMinima: String,
    onCantidadMinimaChange: (String) -> Unit,
    stockDisponible: String,
    onStockDisponibleChange: (String) -> Unit,
    fechaLimite: String,
    onFechaLimiteChange: (String) -> Unit,
    onAbrirDatePicker: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoTexto(
                value = nombre,
                onValueChange = onNombreChange,
                label = "Nombre del producto",
                obligatorio = true,
                leadingIcon = Icons.Filled.Inventory2
            )

            ExposedDropdownMenuBox(
                expanded = categoriaExpandida,
                onExpandedChange = onCategoriaExpand
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { LabelObligatorio("Categoría", obligatorio = true) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Category,
                            contentDescription = null,
                            tint = MiniMaxAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = campoColors()
                )
                ExposedDropdownMenu(
                    expanded = categoriaExpandida,
                    onDismissRequest = { onCategoriaExpand(false) }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, fontSize = 14.sp) },
                            onClick = { onCategoriaSelect(cat) }
                        )
                    }
                }
            }

            CampoTexto(
                value = descripcion,
                onValueChange = onDescripcionChange,
                label = "Descripción",
                obligatorio = false,
                leadingIcon = Icons.Filled.Description,
                singleLine = false,
                minLines = 3,
                placeholder = "Describí el producto, sus características y beneficios..."
            )

            CampoTexto(
                value = precioMayorista,
                onValueChange = onPrecioMayoristaChange,
                label = "Precio mayorista",
                obligatorio = true,
                leadingIcon = Icons.Filled.AttachMoney,
                keyboardType = KeyboardType.Decimal,
                placeholder = "0.00"
            )

            CampoTexto(
                value = precioReferencia,
                onValueChange = onPrecioReferenciaChange,
                label = "Precio de referencia",
                obligatorio = false,
                leadingIcon = Icons.Filled.Payments,
                keyboardType = KeyboardType.Decimal,
                placeholder = "0.00"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoTexto(
                    value = cantidadMinima,
                    onValueChange = onCantidadMinimaChange,
                    label = "Cant. mínima",
                    obligatorio = true,
                    leadingIcon = Icons.Filled.ShoppingCart,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                CampoTexto(
                    value = stockDisponible,
                    onValueChange = onStockDisponibleChange,
                    label = "Stock disponible",
                    obligatorio = false,
                    leadingIcon = Icons.Filled.Store,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = fechaLimite,
                onValueChange = onFechaLimiteChange,
                label = { LabelObligatorio("Fecha límite", obligatorio = true) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MiniMaxAccent,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onAbrirDatePicker) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = "Abrir calendario",
                            tint = MiniMaxAccent
                        )
                    }
                },
                placeholder = { Text("DD/MM/AAAA", color = Color.LightGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = campoColors()
            )

            Text(
                text = "* Campos obligatorios",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun CampoTexto(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    obligatorio: Boolean,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { LabelObligatorio(label, obligatorio) },
        leadingIcon = {
            if (!singleLine) {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = MiniMaxAccent,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(20.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MiniMaxAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, color = Color.LightGray, fontSize = 14.sp) }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        minLines = minLines,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = campoColors()
    )
}

@Composable
private fun LabelObligatorio(texto: String, obligatorio: Boolean) {
    Row {
        Text(texto)
        if (obligatorio) {
            Text(" *", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun campoColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MiniMaxAccent,
    unfocusedBorderColor = Color(0xFFDDD8F0),
    focusedLabelColor = MiniMaxAccent,
    cursorColor = MiniMaxAccent
)

@Composable
private fun BotonesAccion(
    modifier: Modifier = Modifier,
    onGuardar: () -> Unit,
    onPublicar: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onGuardar,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MiniMaxAccent),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MiniMaxAccent)
        ) {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Guardar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        Button(
            onClick = onPublicar,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MiniMaxTeal)
        ) {
            Icon(
                imageVector = Icons.Filled.Publish,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Publicar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NuevaOportunidadScreenPreview() {
    MiniMaxTheme {
        NuevaOportunidadScreen()
    }
}
