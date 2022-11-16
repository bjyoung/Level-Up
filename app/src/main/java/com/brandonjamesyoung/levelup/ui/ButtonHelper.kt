package com.brandonjamesyoung.levelup.ui

import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.brandonjamesyoung.levelup.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ButtonHelper {
    companion object {
        fun getDrawable(drawableId: Int, theme: Theme, resources: Resources) : Drawable? {
            return ResourcesCompat.getDrawable(resources, drawableId, theme)
        }

        private fun getColor(colorId: Int, theme: Theme, resources:Resources) : Int {
            return resources.getColor(colorId, theme)
        }

        // Change button's icon and on click method
        fun convertButton(
            targetId: Int,
            iconDrawableId: Int = R.drawable.question_mark_icon,
            iconColorId: Int = R.color.icon_primary,
            buttonMethod: () -> Unit,
            view: View,
            resources: Resources
        ) {
            val button = view.findViewById<FloatingActionButton>(targetId)
            val drawable = getDrawable(iconDrawableId, view.context.theme, resources)
            button.setImageDrawable(drawable)
            val drawableColor = getColor(iconColorId, view.context.theme, resources)
            button.imageTintList = ColorStateList.valueOf(drawableColor)

            button.setOnClickListener{
                buttonMethod()
            }
        }
    }
}