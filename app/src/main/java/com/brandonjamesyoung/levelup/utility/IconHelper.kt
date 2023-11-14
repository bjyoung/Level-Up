package com.brandonjamesyoung.levelup.utility

import android.content.Context
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
    }
}