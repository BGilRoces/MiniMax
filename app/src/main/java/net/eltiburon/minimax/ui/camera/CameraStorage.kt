package net.eltiburon.minimax.ui.camera

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** Carpeta interna de la app donde se guardan las fotos de los productos. */
fun Context.productImagesDir(): File =
    File(filesDir, "product_images").apply { if (!exists()) mkdirs() }

/** Genera un archivo destino con nombre único dentro del almacenamiento interno. */
fun Context.newProductImageFile(): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(System.currentTimeMillis())
    return File(productImagesDir(), "IMG_$timestamp.jpg")
}

/** Obtiene el [ProcessCameraProvider] de forma suspendida (en vez de usar el listener crudo). */
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { cont ->
    val future = ProcessCameraProvider.getInstance(this)
    future.addListener(
        { cont.resume(future.get()) },
        ContextCompat.getMainExecutor(this)
    )
}
