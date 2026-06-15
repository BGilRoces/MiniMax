package net.eltiburon.minimax.ui.camera

import android.net.Uri
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.Executors

/**
 * Pantalla a pantalla completa que muestra el preview de la cámara en tiempo real
 * y permite capturar una foto. La imagen se guarda en el almacenamiento interno
 * de la app y se devuelve su [Uri] mediante [onImageCaptured].
 *
 * El binding de los use cases es lifecycle-aware (usa [LocalLifecycleOwner]), por lo
 * que CameraX libera la cámara automáticamente cuando la pantalla deja de estar activa.
 */
@Composable
fun CameraCaptureScreen(
    onImageCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashEnabled by remember { mutableStateOf(false) }
    var capturando by remember { mutableStateOf(false) }

    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val captureExecutor = remember { Executors.newSingleThreadExecutor() }

    // Liberar el executor cuando se desmonta la pantalla.
    DisposableEffect(Unit) {
        onDispose { captureExecutor.shutdown() }
    }

    // (Re)bindea los use cases cuando cambia la cámara o el flash.
    LaunchedEffect(lensFacing, flashEnabled) {
        val cameraProvider = context.getCameraProvider()
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }
        val selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        imageCapture.flashMode =
            if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF

        runCatching {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
        }.onFailure {
            Toast.makeText(context, "No se pudo iniciar la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Barra superior: cerrar y flash.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(
                onClick = onClose,
                icon = Icons.Filled.Close,
                contentDescription = "Cerrar cámara"
            )
            CircleIconButton(
                onClick = { flashEnabled = !flashEnabled },
                icon = if (flashEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                contentDescription = "Flash"
            )
        }

        // Controles inferiores: cambiar cámara y botón de captura.
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(52.dp))

            // Botón de captura
            IconButton(
                onClick = {
                    if (capturando) return@IconButton
                    capturando = true
                    takePhoto(
                        context = context,
                        imageCapture = imageCapture,
                        executor = captureExecutor,
                        onImageCaptured = {
                            capturando = false
                            onImageCaptured(it)
                        },
                        onError = {
                            capturando = false
                            Toast.makeText(
                                context,
                                "Error al tomar la foto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Lens,
                    contentDescription = "Tomar foto",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            CircleIconButton(
                onClick = {
                    lensFacing =
                        if (lensFacing == CameraSelector.LENS_FACING_BACK)
                            CameraSelector.LENS_FACING_FRONT
                        else
                            CameraSelector.LENS_FACING_BACK
                },
                icon = Icons.Filled.Cameraswitch,
                contentDescription = "Cambiar cámara"
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White
        )
    }
}

/** Captura la foto y la guarda en el almacenamiento interno de la app. */
private fun takePhoto(
    context: android.content.Context,
    imageCapture: ImageCapture,
    executor: java.util.concurrent.Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = context.newProductImageFile()
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                androidx.core.content.ContextCompat.getMainExecutor(context).execute {
                    onImageCaptured(savedUri)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                androidx.core.content.ContextCompat.getMainExecutor(context).execute {
                    onError(exception)
                }
            }
        }
    )
}
