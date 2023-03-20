package com.brandonjamesyoung.levelup.shared

import android.content.res.Resources
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.brandonjamesyoung.levelup.R
import com.google.android.material.button.MaterialButton

class ButtonHelper {
    companion object {
        // Change button's icon and on click method
        fun convertButton(
            targetId: Int,
            iconDrawableId: Int = R.drawable.question_mark_icon,
            iconColorId: Int = R.color.nav_button_icon,
            buttonMethod: () -> Unit,
            view: View,
            resources: Resources
        ) {
            val button = view.findViewById<MaterialButton>(targetId)
            val theme = view.context.theme

            val drawable = ResourcesCompat.getDrawable(
                resources,
                iconDrawableId,
                theme
            )

            button.icon = drawable
            button.iconTint = resources.getColorStateList(iconColorId, theme)

            button.setOnClickListener{
                buttonMethod()
            }
        }
    }
}