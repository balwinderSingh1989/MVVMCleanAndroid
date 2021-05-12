package com.sample.core.utility.imagecompressor

import android.graphics.Bitmap
import java.io.File

private val separator = File.separator

/**
 * Maximum image size allowed by the backend to be uploaded via API.
 *
 * Currently its 1 MB.
 */
const val MAX_ALLOWED_UPLOAD_IMAGE_SIZE = 1024 * 1024 * 1L

/**
 * Check whether the file size is within the dedicated range
 */
fun File.sizeAllowed() = sizeInBytes() in 1..MAX_ALLOWED_UPLOAD_IMAGE_SIZE

/**
 * Calculate file size in bytes
 */
fun File.sizeInBytes() =
    if (exists()) {
        length()
    } else {
        0L
    }

fun Bitmap.CompressFormat.extension() =
    when (this) {
        Bitmap.CompressFormat.PNG -> "png"
        Bitmap.CompressFormat.WEBP -> "webp"
        else -> "jpg"
    }



