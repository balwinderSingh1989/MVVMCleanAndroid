package com.sample.core.utility.imagecompressor

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.WorkerThread
import com.sample.core.di.qualifier.ApplicationContext
import com.sample.core.di.scope.PerApplication
import com.sample.core.utility.extensions.TAG
import com.sample.core.utility.logger.AppLogger
import java.io.*
import javax.inject.Inject

@PerApplication
class AppImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val FILE_PREFIX = "Image_"
        const val FILE_SUFFIX = ".jpg"
        const val FILE_DIR = "ImageDir"

    }


    /**
     * Compress Camera's Captured bitmap to [Bitmap.CompressFormat.JPEG]
     */
    @WorkerThread
  suspend  fun getCompressedJpeg(imageBitmap: Bitmap, userId: String): File {
        lateinit var outputStream: OutputStream
        lateinit var file: File
        try {
            file = File(
                context.cacheDir,
                FILE_PREFIX +
                        userId +
                        FILE_SUFFIX
            )
            file.createNewFile()
            val byteArrayOutputStream = ByteArrayOutputStream()
            outputStream =
                BufferedOutputStream(FileOutputStream(file))
            imageBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                byteArrayOutputStream
            )
            outputStream.write(byteArrayOutputStream.toByteArray())
            outputStream.flush()
        } catch (e: IOException) {
            AppLogger.e(TAG, "Exception occured in getCompressedJpeg " + e.message)
        } finally {
            outputStream.close()

            return file
        }
    }


    fun getSavedAvatar(
        userId: String
    ): File? {
        var file: File? = null
        val avatarFile = File(
            context.cacheDir,
            FILE_PREFIX +
                    userId +
                    FILE_SUFFIX
        )
        file = if (avatarFile.exists()) avatarFile else null
        return file
    }

}