package com.brandonjamesyoung.levelup.ui

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
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

        private fun createQuestIcon(
            iconFileName: String,
            questId: Int,
            iconClickMethod: (
                questId: Int,
                icon: FloatingActionButton,
                iconFileName: String
            ) -> Unit,
            view: View,
            fragment: Fragment
        ) : FloatingActionButton {
            val context = view.context
            val icon = FloatingActionButton(context)
            icon.id = View.generateViewId()

            val iconLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            icon.layoutParams = iconLayoutParams
            icon.contentDescription = fragment.resources.getString(R.string.quest_icon_description)
            val iconDrawable = getQuestDrawable(view, iconFileName, fragment)
            icon.setImageDrawable(iconDrawable)
            icon.compatElevation = 0f
            val pxCustomFABSize = fragment.resources.getDimension(R.dimen.big_round_button).toInt()
            icon.customSize = pxCustomFABSize

            val backgroundColor : Int = fragment.resources.getColor(
                R.color.quest_card_icon_background,
                context.theme
            )

            icon.backgroundTintList = ColorStateList.valueOf(backgroundColor)

            icon.setOnClickListener{
                iconClickMethod(questId, icon, iconFileName)
            }

            return icon
        }

        private fun createQuestTitle(view: View, questName: String, fragment: Fragment) : TextView {
            val context = view.context
            val title = TextView(context)
            title.id = View.generateViewId()
            title.text = questName

            val titleLayoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            title.layoutParams = titleLayoutParams
            title.setTextColor(fragment.resources.getColor(R.color.text_secondary, context.theme))
            title.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            val pressStart2PFont = fragment.resources.getFont(R.font.press_start_2p)
            title.typeface = pressStart2PFont

            title.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                fragment.resources.getDimension(R.dimen.quest_card_font_size)
            )

            return title
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
            view: View,
            fragment: Fragment
        ) : CardView {
            val context = view.context
            val newCard = CardView(context)
            newCard.id = View.generateViewId()

            // Set card view properties
            val cardWidth = fragment.resources.getDimension(R.dimen.quest_card_width).toInt()
            val cardHeight = fragment.resources.getDimension(R.dimen.quest_card_height).toInt()

            val questCardLayoutParams = ViewGroup.MarginLayoutParams(
                cardWidth,
                cardHeight
            )

            val cardMargin = fragment.resources.getDimension(R.dimen.quest_card_margin).toInt()
            questCardLayoutParams.setMargins(cardMargin)
            newCard.layoutParams = questCardLayoutParams
            newCard.setCardBackgroundColor(fragment.resources.getColor(questColorId, context.theme))
            val cardRadius = fragment.resources.getDimension(R.dimen.quest_card_corner_radius)
            newCard.radius = cardRadius

            // Add constraint layout
            val cardConstraintLayout = ConstraintLayout(context)
            cardConstraintLayout.id = View.generateViewId()
            newCard.addView(cardConstraintLayout)

            // Create icon and link to the constraint layout
            val cardIcon = createQuestIcon(
                iconFileName = questIconFileName,
                questId = questId,
                iconClickMethod = iconClickMethod,
                view = view,
                fragment = fragment
            )

            cardConstraintLayout.addView(cardIcon)
            val cardConstraintSet = ConstraintSet()
            cardConstraintSet.clone(cardConstraintLayout)

            cardConstraintSet.connect(
                cardIcon.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )

            cardConstraintSet.connect(
                cardIcon.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )

            cardConstraintSet.connect(
                cardIcon.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )

            cardConstraintSet.connect(
                cardIcon.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )

            cardConstraintSet.applyTo(cardConstraintLayout)

            // Create text view for title and link it to the constraint layout
            val cardTitle = createQuestTitle(view, questName, fragment)
            cardConstraintLayout.addView(cardTitle)
            cardConstraintSet.clone(cardConstraintLayout)

            cardConstraintSet.connect(
                cardTitle.id,
                ConstraintSet.BOTTOM,
                cardIcon.id,
                ConstraintSet.TOP
            )

            cardConstraintSet.connect(
                cardTitle.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )

            cardConstraintSet.connect(
                cardTitle.id,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )

            cardConstraintSet.connect(
                cardTitle.id,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )

            cardConstraintSet.applyTo(cardConstraintLayout)
            return newCard
        }
    }
}