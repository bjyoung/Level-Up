package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.interfaces.IconReader
import com.brandonjamesyoung.levelup.utility.TypeConverter.Companion.convertByteArrayToDrawable
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ButtonConverter {
    // Change Material button's icon and on click method
    fun convertNavButton(
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

    // Change quest's floating action button icon
    fun changeQuestIcon(
        button: FloatingActionButton,
        iconId : Int?,
        iconReader: IconReader,
        context: Context,
        lifecycleScope: LifecycleCoroutineScope
    ) {
        if (iconId == null) {
            val drawable = IconHelper.getDefaultIcon(context)
            button.setImageDrawable(drawable)
            return
        }

        lifecycleScope.launch(Dispatchers.Default + CoroutineName("Load Icons")) {
            val icon = iconReader.getIcon(iconId)
            val resources = context.resources
            val drawable = convertByteArrayToDrawable(icon.image, resources)
            button.setImageDrawable(drawable)
        }
    }
}