package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.brandonjamesyoung.levelup.R
import androidx.core.graphics.scale
import androidx.core.graphics.drawable.toDrawable

class IconHelper {
    companion object {
        fun getDefaultIcon(context: Context) : Drawable? {
            return ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.question_mark_icon_large,
                context.theme
            )
        }

        fun scaleUpByteArray(
            image: ByteArray,
            width: Int,
            height: Int,
            scaleUpRate: Int,
            resources: Resources
        ): Drawable {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            val scaledBitmap = bitmap.scale(width * scaleUpRate, height * scaleUpRate, false)
            return scaledBitmap.toDrawable(resources)
        }
    }
}