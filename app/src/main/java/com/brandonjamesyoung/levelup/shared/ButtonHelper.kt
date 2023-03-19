package com.brandonjamesyoung.levelup.shared

import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.brandonjamesyoung.levelup.R
import com.google.android.material.button.MaterialButton

class ButtonHelper {
    companion object {
        private fun getColor(colorId: Int, theme: Theme, resources:Resources) : Int {
            return resources.getColor(colorId, theme)
        }

        // Change button's icon and on click method
        fun convertButton(
            targetId: Int,
            iconDrawableId: Int = R.drawable.question_mark_icon,
            iconColorId: Int = R.color.icon_secondary,
            buttonMethod: () -> Unit,
            view: View,
            resources: Resources
        ) {
            val button = view.findViewById<MaterialButton>(targetId)

            val drawable = ResourcesCompat.getDrawable(
                resources,
                iconDrawableId,
                view.context.theme
            )

            button.icon = drawable
            val drawableColor = getColor(iconColorId, view.context.theme, resources)
            button.iconTint = ColorStateList.valueOf(drawableColor)

            button.setOnClickListener{
                buttonMethod()
            }
        }
    }
}