package net.eltiburon.minimax.ui.common

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import net.eltiburon.minimax.R
import androidx.exifinterface.media.ExifInterface
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory

/**
 * Carga el [uri] y lo muestra. Soporta dos orígenes:
 *  - URLs http(s) (las imágenes mock del catálogo demo, ver MockImagenes): se delegan a Coil.
 *  - Archivos internos `content://`/`file://` (una foto tomada con la cámara): se decodifican a
 *    bitmap respetando la orientación EXIF.
 *
 * Antes vivía solo dentro de NuevaOportunidadScreen; se extrajo a ui.common porque
 * GrupoDetalleScreen también necesita mostrar la foto real del producto cuando la
 * oportunidad fue publicada con una imagen propia (en vez del drawable mock).
 */
@Composable
fun UriImage(uri: String, modifier: Modifier = Modifier) {
    if (uri.startsWith("http")) {
        // loremflickr (catálogo demo) responde 500 de forma intermitente; ante un fallo de
        // red mostramos el drawable genérico en lugar de un hueco vacío.
        val fallback = painterResource(id = R.drawable.aceite)
        AsyncImage(
            model = uri,
            contentDescription = "Imagen del producto",
            modifier = modifier,
            contentScale = ContentScale.Crop,
            placeholder = fallback,
            error = fallback
        )
        return
    }

    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                val parsed = Uri.parse(uri)
                val decoded = context.contentResolver.openInputStream(parsed)?.use { input ->
                    BitmapFactory.decodeStream(input)
                } ?: return@runCatching null
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
