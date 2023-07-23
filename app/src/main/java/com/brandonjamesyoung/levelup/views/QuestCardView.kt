package com.brandonjamesyoung.levelup.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.brandonjamesyoung.levelup.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.ContentViewCallback

class QuestCardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defaultStyle: Int = 0
) : CardView(context, attributeSet, defaultStyle), ContentViewCallback {
    private val nameView: TextView

    val iconButton: FloatingActionButton

    init {
        val innerView = View.inflate(context, R.layout.quest_card_inner, this)
        nameView = innerView.findViewById(R.id.CardName)
        iconButton = innerView.findViewById(R.id.CardIcon)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        // TODO("Use some animation")
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        // TODO("Use some animation")
    }

    fun disableClickableIcon() {
        iconButton.isClickable = false
    }

    fun setName(name: String) {
        nameView.text = name
    }

    fun setOnCardClickListener(listener: OnClickListener) {
        super.setOnClickListener(listener)
    }

    fun setOnIconClickListener(listener: OnClickListener) {
        iconButton.setOnClickListener(listener)
    }

    fun setOnQuestLongClickListener(listener: OnLongClickListener) {
        iconButton.setOnLongClickListener(listener)
        setOnLongClickListener(listener)
    }

    companion object {
        fun make(viewGroup: ViewGroup): QuestCardView {
            return LayoutInflater.from(viewGroup.context).inflate(
                R.layout.quest_card,
                viewGroup,
                false
            ) as QuestCardView
        }
    }
}