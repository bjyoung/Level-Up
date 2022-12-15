package com.brandonjamesyoung.levelup.shared

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream

class ByteArrayHelper {
    companion object {
        fun convertDrawableToByteArray(drawable: Drawable) : ByteArray {
            val bitmap = drawable.toBitmap()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun convertByteArrayToDrawable(byteArray: ByteArray, context: Context) : Drawable {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            return BitmapDrawable(context.resources, bitmap)
        }
    }
}