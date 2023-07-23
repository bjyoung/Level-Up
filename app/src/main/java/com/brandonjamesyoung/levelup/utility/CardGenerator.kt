package com.brandonjamesyoung.levelup.utility

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import com.brandonjamesyoung.levelup.interfaces.IconReader
import com.brandonjamesyoung.levelup.views.QuestCardView

class CardGenerator {
    private val buttonConverter: ButtonConverter = ButtonConverter()

    fun createSimpleCard(
        name: String,
        backgroundColorInt: Int,
        iconId: Int?,
        view: View,
        iconReader: IconReader,
        lifecycleScope: LifecycleCoroutineScope
    ) : QuestCardView {
        val newCard = QuestCardView.make(view as ViewGroup)
        newCard.setCardBackgroundColor(backgroundColorInt)
        newCard.setName(name)

        buttonConverter.changeQuestIcon(
            button = newCard.iconButton,
            iconId = iconId,
            iconReader = iconReader,
            context = view.context,
            lifecycleScope = lifecycleScope
        )

        return newCard
    }
}