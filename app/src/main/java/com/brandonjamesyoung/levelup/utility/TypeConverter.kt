package com.brandonjamesyoung.levelup.utility

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class TypeConverter {
    companion object {
        fun convertDrawableToByteArray(drawable: Drawable, width: Int, height: Int) : ByteArray {
            val bitmap = drawable.toBitmap(width, height)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun convertInstantToString(date: Instant) : String {
            val timeZoneId: ZoneId = TimeZone.getDefault().toZoneId()
            val zonedDate = date.atZone(timeZoneId)
            val dateFormatter = DateTimeFormatter.ofPattern("M/d/uuuu")
            return zonedDate.toLocalDate().format(dateFormatter)
        }
    }
}