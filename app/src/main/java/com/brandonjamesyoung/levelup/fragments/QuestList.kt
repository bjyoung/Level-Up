package com.brandonjamesyoung.levelup.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.NavigationHelper
import com.brandonjamesyoung.levelup.shared.StringHelper
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestList : Fragment(R.layout.quest_list) {
    private val viewModel: QuestListViewModel by activityViewModels()

    private val difficultyColorMap = mapOf(
        Difficulty.EASY to R.color.easy,
        Difficulty.MEDIUM to R.color.medium,
        Difficulty.HARD to R.color.hard,
        Difficulty.EXPERT to R.color.expert
    )

    private fun addNavigation(view: View) {
        val buttonNavMap = mapOf(
            R.id.AddNewQuestButton to R.id.action_questList_to_newQuest,
            R.id.SettingsButton to R.id.action_questList_to_settings,
            R.id.ShopButton to R.id.action_questList_to_shop
        )

        for ((buttonId, navId) in buttonNavMap) {
            NavigationHelper.addNavigationToView(this, view, buttonId, navId)
        }
    }

    private fun substitutePlaceholderText(view: View) {
        val placeholderText = getString(R.string.placeholder_text)
        val pointsAcronym = getString(R.string.points_acronym)
        StringHelper.substituteText(view, R.id.Username, placeholderText, placeholderText)
        StringHelper.substituteText(view, R.id.PointsLabel, placeholderText, pointsAcronym)
    }

    private fun createQuestIcon(view: View, iconFileName: String) : FloatingActionButton {
        val context = view.context
        val icon = FloatingActionButton(context)
        icon.id = View.generateViewId()

        val iconLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        icon.layoutParams = iconLayoutParams
        icon.contentDescription = resources.getString(R.string.quest_icon_description)

        val iconID = resources.getIdentifier(
            iconFileName,
            "drawable",
            context.packageName
        )

        val iconDrawable = if (iconID == 0) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.question_mark_icon,
                context.theme
            )
        } else {
            ResourcesCompat.getDrawable(resources, iconID, context.theme)
        }

        icon.setImageDrawable(iconDrawable)
        icon.compatElevation = 0f
        val pxCustomFABSize = resources.getDimension(R.dimen.big_round_button).toInt()
        icon.customSize = pxCustomFABSize

        val backgroundColor : Int = resources.getColor(
            R.color.quest_card_icon_background,
            context.theme
        )

        icon.backgroundTintList = ColorStateList.valueOf(backgroundColor)
        return icon
    }

    private fun createQuestTitle(view: View, questName: String) : TextView {
        val context = view.context
        val title = TextView(context)
        title.id = View.generateViewId()
        title.text = questName

        val titleLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        title.layoutParams = titleLayoutParams
        title.setTextColor(resources.getColor(R.color.text_secondary, context.theme))
        title.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        val pressStart2PFont = resources.getFont(R.font.press_start_2p)
        title.typeface = pressStart2PFont

        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.quest_card_font_size)
        )

        return title
    }

    private fun createQuestCard(
        view: View,
        questName: String,
        questColorId: Int,
        questIconFileName: String,
    ) : CardView {
        val context = view.context
        val newCard = CardView(context)
        newCard.id = View.generateViewId()

        // Set card view properties
        val cardWidth = resources.getDimension(R.dimen.quest_card_width).toInt()
        val cardHeight = resources.getDimension(R.dimen.quest_card_height).toInt()

        val questCardLayoutParams = ViewGroup.MarginLayoutParams(
            cardWidth,
            cardHeight
        )

        val cardMargin = resources.getDimension(R.dimen.quest_card_margin).toInt()
        questCardLayoutParams.setMargins(cardMargin)
        newCard.layoutParams = questCardLayoutParams
        newCard.setCardBackgroundColor(resources.getColor(questColorId, context.theme))
        val cardRadius = resources.getDimension(R.dimen.quest_card_corner_radius)
        newCard.radius = cardRadius

        // Add constraint layout
        val cardConstraintLayout = ConstraintLayout(context)
        cardConstraintLayout.id = View.generateViewId()
        newCard.addView(cardConstraintLayout)

        // Create icon and link to the constraint layout
        val cardIcon = createQuestIcon(view, questIconFileName)
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
        val cardTitle = createQuestTitle(view, questName)
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

    private fun addCardView(
        view: View,
        questName: String? = resources.getString(R.string.placeholder_text),
        difficulty: Difficulty = Difficulty.EASY,
        questIconFileName: String? = "question_mark_icon",
    ) {
        val difficultyColorId = difficultyColorMap[difficulty]
            ?: throw IllegalArgumentException("Given card difficulty is not a valid value.")

        val name = questName ?: resources.getString(R.string.placeholder_text)
        val iconName = questIconFileName ?: "question_mark_icon"
        val newCard = createQuestCard(view, name, difficultyColorId, iconName)
        val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)
        questListLayout.addView(newCard)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNavigation(view)
        substitutePlaceholderText(view)

        viewModel.questList.observe(viewLifecycleOwner) { questList ->
            for (quest in questList) {
                addCardView(
                    view = view,
                    questName = quest.name,
                    difficulty = quest.difficulty,
                    questIconFileName = quest.iconName
                )
            }
        }
    }
}