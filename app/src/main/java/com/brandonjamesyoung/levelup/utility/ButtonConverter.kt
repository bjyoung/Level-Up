package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.interfaces.IconReader
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ButtonConverter (val context: Context) {
    // Change Material button's icon and on click method
    fun convertNavButton(
        targetId: Int,
        iconDrawableId: Int = R.drawable.question_mark_icon,
        iconColorId: Int = R.color.nav_button_icon,
        buttonMethod: (() -> Unit)? = null,
        tooltip: String? = null,
        view: View
    ) {
        val button = view.findViewById<MaterialButton>(targetId)
        val theme = context.theme
        val resources = context.resources

        val drawable = ResourcesCompat.getDrawable(
            resources,
            iconDrawableId,
            theme
        )

        button.icon = drawable
        button.iconTint = resources.getColorStateList(iconColorId, theme)

        if (buttonMethod != null) {
            button.setOnClickListener{
                buttonMethod()
            }
        }

        button.tooltipText = tooltip
    }
}