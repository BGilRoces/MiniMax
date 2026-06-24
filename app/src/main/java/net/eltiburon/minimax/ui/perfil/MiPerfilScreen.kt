package net.eltiburon.minimax.ui.perfil

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import net.eltiburon.minimax.model.formatearPesos
import net.eltiburon.minimax.ui.camera.CameraCaptureScreen
import net.eltiburon.minimax.ui.common.UriImage
import net.eltiburon.minimax.ui.proveedor.MetricaProveedor
import net.eltiburon.minimax.ui.proveedor.metricasProveedor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPerfilScreen(
    viewModel: MiPerfilViewModel = viewModel()
) {
    val usuario by viewModel.usuario.collectAsState()
    val estadisticas by viewModel.estadisticas.collectAsState()
    val modoEdicion by viewModel.modoEdicion.collectAsState()
    val fotoUri by viewModel.fotoUri.collectAsState()
    val esProveedor by viewModel.esProveedor.collectAsState()
    val rolLabel by viewModel.rolLabel.collectAsState()
    val nombre by viewModel.nombreEdit.collectAsState()
    val email by viewModel.emailEdit.collectAsState()
    val telefono by viewModel.telefonoEdit.collectAsState()
    val negocio by viewModel.negocioEdit.collectAsState()
    val direccion by viewModel.direccionEdit.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ── Estado de cámara para la foto de perfil (mismo flujo que Nueva Oportunidad) ──────────
    var mostrarCamara by remember { mutableStateOf(false) }
    var mostrarPermisoDenegado by remember { mutableStateOf(false) }

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

    if (mostrarCamara) {
        CameraCaptureScreen(
            onImageCaptured = { uri ->
                viewModel.onFotoTomada(uri.toString())
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
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) { Text("Entendido") }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.NoPhotography,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            title = { Text("Permiso de cámara necesario") },
            text = {
                Text(
                    "Necesitamos acceso a la cámara para tomar tu foto de perfil. " +
                        "Podés habilitar el permiso desde los ajustes de la aplicación."
                )
            }
        )
    }

    // La top bar y la bottom bar las dibuja el Scaffold persistente del NavHost; acá solo el contenido
    // (más un SnackbarHost propio para el aviso de guardado). El botón "Editar" pasó a la cabecera.
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item {
                AvatarSection(
                    nombre = usuario.nombre,
                    rolLabel = rolLabel,
                    fotoUri = fotoUri,
                    modoEdicion = modoEdicion,
                    onToggleEdicion = viewModel::toggleEdicion,
                    onEditarFoto = pedirCamara
                )
            }
            item {
                // El proveedor no tiene ahorro ni pedidos: reutiliza las métricas del dashboard.
                if (esProveedor) {
                    ProveedorStatsCard(metricas = metricasProveedor())
                } else {
                    StatsCard(
                        totalAhorrado = estadisticas.totalAhorrado,
                        gruposCompletados = estadisticas.gruposCompletados,
                        pedidosRealizados = estadisticas.pedidosRealizados
                    )
                }
            }
            item {
                FormSection(
                    modoEdicion = modoEdicion,
                    nombre = nombre,
                    email = email,
                    telefono = telefono,
                    negocio = negocio,
                    direccion = direccion,
                    onNombreChange = viewModel::onNombreChange,
                    onEmailChange = viewModel::onEmailChange,
                    onTelefonoChange = viewModel::onTelefonoChange,
                    onNegocioChange = viewModel::onNegocioChange,
                    onDireccionChange = viewModel::onDireccionChange
                )
            }
            if (modoEdicion) {
                item {
                    Button(
                        onClick = {
                            viewModel.guardarCambios()
                            scope.launch {
                                snackbarHostState.showSnackbar("Cambios guardados correctamente")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Guardar cambios",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Avatar ───────────────────────────────────────────────────────────────────

@Composable
private fun AvatarSection(
    nombre: String,
    rolLabel: String,
    fotoUri: String?,
    modoEdicion: Boolean,
    onToggleEdicion: () -> Unit,
    onEditarFoto: () -> Unit
) {
    val initials = nombre.trim().split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // El botón Editar/Cancelar vivía en la top bar; al unificarla pasó a la cabecera del perfil.
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onToggleEdicion) {
                Text(
                    text = if (modoEdicion) "Cancelar" else "Editar",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .border(3.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.35f), CircleShape)
                .clickable(onClick = onEditarFoto),
            contentAlignment = Alignment.Center
        ) {
            if (fotoUri != null) {
                UriImage(uri = fotoUri, modifier = Modifier.fillMaxSize())
            } else {
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }
        }

        TextButton(onClick = onEditarFoto) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Editar foto",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                fontSize = 13.sp
            )
        }

        Text(
            text = nombre,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text = rolLabel,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Estadísticas ─────────────────────────────────────────────────────────────

@Composable
private fun StatsCard(
    totalAhorrado: Double,
    gruposCompletados: Int,
    pedidosRealizados: Int
) {
    val ahorradoStr = formatearPesos(totalAhorrado)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = ahorradoStr, label = "Total\nahorrado", color = MaterialTheme.colorScheme.tertiary)

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(Color(0xFFE0E0E0))
            )

            StatItem(value = "$gruposCompletados", label = "Grupos\ncompletados", color = MaterialTheme.colorScheme.primary)

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(Color(0xFFE0E0E0))
            )

            StatItem(value = "$pedidosRealizados", label = "Pedidos\nrealizados", color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
    }
}

// ── Estadísticas del proveedor (reutiliza las métricas del dashboard) ─────────

@Composable
private fun ProveedorStatsCard(metricas: List<MetricaProveedor>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            metricas.chunked(2).forEachIndexed { index, fila ->
                if (index > 0) Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    fila.forEach { metrica ->
                        ProveedorStatItem(metrica = metrica, modifier = Modifier.weight(1f))
                    }
                    if (fila.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ProveedorStatItem(metrica: MetricaProveedor, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(metrica.color.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = metrica.icon,
                contentDescription = null,
                tint = metrica.color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = metrica.valor,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = metrica.titulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 13.sp
            )
        }
    }
}

// ── Formulario ───────────────────────────────────────────────────────────────

@Composable
private fun FormSection(
    modoEdicion: Boolean,
    nombre: String,
    email: String,
    telefono: String,
    negocio: String,
    direccion: String,
    onNombreChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onNegocioChange: (String) -> Unit,
    onDireccionChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Datos personales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            PerfilField(
                label = "Nombre completo",
                value = nombre,
                onValueChange = onNombreChange,
                editable = modoEdicion,
                leadingIcon = Icons.Filled.AccountCircle
            )
            PerfilField(
                label = "Email",
                value = email,
                onValueChange = onEmailChange,
                editable = modoEdicion,
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            PerfilField(
                label = "Teléfono",
                value = telefono,
                onValueChange = onTelefonoChange,
                editable = modoEdicion,
                leadingIcon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone
            )
            PerfilField(
                label = "Nombre del negocio",
                value = negocio,
                onValueChange = onNegocioChange,
                editable = modoEdicion,
                leadingIcon = Icons.Filled.Business
            )
            PerfilField(
                label = "Dirección",
                value = direccion,
                onValueChange = onDireccionChange,
                editable = modoEdicion,
                leadingIcon = Icons.Filled.LocationOn
            )
        }
    }
}

@Composable
private fun PerfilField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    editable: Boolean,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        enabled = true,
        readOnly = !editable,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = if (editable) MaterialTheme.colorScheme.outlineVariant else Color(0xFFEEEEEE),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = if (editable) MaterialTheme.colorScheme.surface else Color(0xFFF8F8F8),
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
