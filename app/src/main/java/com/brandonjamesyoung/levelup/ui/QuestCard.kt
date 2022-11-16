package com.brandonjamesyoung.levelup.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.brandonjamesyoung.levelup.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuestCard {
    companion object {
        fun getQuestDrawable(
            view: View,
            iconFileName : String,
            fragment: Fragment
        ) : Drawable? {
            val iconId = fragment.resources.getIdentifier(
                iconFileName,
                "drawable",
                view.context.packageName
            )

            val drawable = if (iconId == 0) {
                ResourcesCompat.getDrawable(
                    fragment.resources,
                    R.drawable.question_mark_icon,
                    view.context.theme
                )
            } else {
                ResourcesCompat.getDrawable(fragment.resources, iconId, view.context.theme)
            }

            return drawable
        }

        fun createQuestCard(
            questId: Int,
            questName: String,
            questColorId: Int,
            questIconFileName: String,
            iconClickMethod: (
                questId: Int,
                icon: FloatingActionButton,
                iconFileName: String
            ) -> Unit,
            parentLayout : ViewGroup,
            inflater: LayoutInflater,
            view: View,
            fragment: Fragment
        ) : CardView {
            val context = view.context

            val newCardLayout = inflater.inflate(
                R.layout.quest_card,
                parentLayout,
                false
            ) as LinearLayoutCompat

            // TODO instead of using getChildAt(), which relies on knowing the .xml file
            //  use findViewById instead on newCard
            val newCard = newCardLayout.getChildAt(0) as CardView
            newCardLayout.removeView(newCard)
            newCard.setCardBackgroundColor(fragment.resources.getColor(questColorId, context.theme))
            val cardConstraintLayout = newCard.getChildAt(0) as ConstraintLayout
            val cardTitle = cardConstraintLayout.getChildAt(0) as TextView
            cardTitle.text = questName
            val icon = cardConstraintLayout.getChildAt(1) as FloatingActionButton
            val iconDrawable = getQuestDrawable(view, questIconFileName, fragment)
            icon.setImageDrawable(iconDrawable)

            icon.setOnClickListener{
                iconClickMethod(questId, icon, questIconFileName)
            }

            return newCard
        }
    }
}