package com.walhalla.appextractor.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.extractor.R
import com.walhalla.ui.DLog.d
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/** Shared save-icon path used by legacy MainActivity and Compose. */
object IconSaver {

    fun saveAppIcon(context: Context, meta: PackageMeta): Result {
        val name = meta.label
        val newFile = File(
            Environment.getExternalStorageDirectory().absolutePath + "/Download/",
            "$name.png"
        )
        if (newFile.exists()) {
            val deleted = newFile.delete()
            d("@@$deleted")
        }
        val bitmap = PackageMetaUtils.drw(context, meta.packageName) ?: return Result.Error(
            context.getString(R.string.err_unable_saved_icon) + " $name"
        )
        return try {
            FileOutputStream(newFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            }
            Result.Success(
                context.getString(R.string.savedicon) + " $name ✔",
                newFile
            )
        } catch (_: FileNotFoundException) {
            Result.Error(context.getString(R.string.err_unable_saved_icon) + " $name")
        } catch (_: IOException) {
            Result.Error(context.getString(R.string.err_unable_saved_icon) + " $name")
        }
    }

    sealed class Result {
        data class Success(val message: String, val file: File) : Result()
        data class Error(val message: String) : Result()
    }
}
