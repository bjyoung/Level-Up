package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.Difficulty
import com.brandonjamesyoung.levelup.interfaces.IconReader
import com.brandonjamesyoung.levelup.views.QuestCardView

class CardGenerator (val context: Context) {
    private val buttonConverter: ButtonConverter = ButtonConverter(context)

    // Maps
    val difficultyColorMap = mapOf(
        Difficulty.EASY to R.color.easy,
        Difficulty.MEDIUM to R.color.medium,
        Difficulty.HARD to R.color.hard,
        Difficulty.EXPERT to R.color.expert
    )

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
            lifecycleScope = lifecycleScope
        )

        return newCard
    }
}