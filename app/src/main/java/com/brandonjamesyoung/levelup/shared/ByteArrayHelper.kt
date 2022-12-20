package com.brandonjamesyoung.levelup.shared

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import java.io.ByteArrayOutputStream

class ByteArrayHelper {
    companion object {
        fun convertDrawableToByteArray(drawable: Drawable) : ByteArray {
            val bitmap = drawable.toBitmap()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun convertByteArrayToDrawable(byteArray: ByteArray, resources: Resources) : Drawable {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            return bitmap.toDrawable(resources)
        }
    }
}