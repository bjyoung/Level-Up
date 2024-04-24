package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.brandonjamesyoung.levelup.R

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

            // Scale up so the icons are not blurry
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                width * scaleUpRate,
                height * scaleUpRate,
                false
            )

            return BitmapDrawable(resources, scaledBitmap)
        }
    }
}