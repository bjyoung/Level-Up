package com.brandonjamesyoung.levelup.fragments

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.Mode
import com.brandonjamesyoung.levelup.shared.NavigationHelper
import com.brandonjamesyoung.levelup.shared.StringHelper
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestList : Fragment(R.layout.quest_list) {
    private val questListViewModel: QuestListViewModel by activityViewModels()
    private val selectedQuestIds: MutableSet<Int> = mutableSetOf()
    private var mode: Mode = Mode.DEFAULT

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

    private fun showToast(message: String) {
        val toast = Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        )

        toast.show()
    }

    private fun isSelected(questId: Int) : Boolean {
        return selectedQuestIds.contains(questId)
    }

    private fun getQuestDrawable(view: View, iconFileName : String) : Drawable? {
        val iconId = resources.getIdentifier(
            iconFileName,
            "drawable",
            view.context.packageName
        )

        val drawable = if (iconId == 0) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.question_mark_icon,
                view.context.theme
            )
        } else {
            ResourcesCompat.getDrawable(resources, iconId, view.context.theme)
        }

        return drawable
    }

    private fun completeQuests() {
        // TODO sum exp and RP earned for each quest completed
        // TODO grant user the exp and the RP
        val idCopies = selectedQuestIds.toSet()
        questListViewModel.deleteQuests(idCopies)
        selectedQuestIds.clear()
        mode = Mode.DEFAULT
        activateDefaultMode()
    }

    private fun deleteQuests() {
        val idCopies = selectedQuestIds.toSet()
        questListViewModel.deleteQuests(idCopies)
        selectedQuestIds.clear()
        mode = Mode.DEFAULT
        activateDefaultMode()
    }

    private fun activateSelectMode() {
        // Change New Quest button to Complete Quests button
        val view = this.requireView()
        val newQuestButton = view.findViewById<FloatingActionButton>(R.id.AddNewQuestButton)

        val checkIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.check_icon_green,
            view.context.theme
        )

        newQuestButton.setImageDrawable(checkIcon)

        val confirmColor : Int = resources.getColor(
            R.color.confirm,
            view.context.theme
        )

        newQuestButton.imageTintList = ColorStateList.valueOf(confirmColor)

        newQuestButton.setOnClickListener{
            completeQuests()
        }

        // Change Shop button to Delete button
        val shopButton = view.findViewById<FloatingActionButton>(R.id.ShopButton)

        val deleteIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.trash_bin_icon,
            view.context.theme
        )

        shopButton.setImageDrawable(deleteIcon)

        val deleteColor : Int = resources.getColor(
            R.color.cancel,
            view.context.theme
        )

        shopButton.imageTintList = ColorStateList.valueOf(deleteColor)

        shopButton.setOnClickListener{
            deleteQuests()
        }
    }

    private fun activateDefaultMode() {
        // Change Complete Quests button to New Quest button
        val view = this.requireView()
        val completeQuestsButton = view.findViewById<FloatingActionButton>(R.id.AddNewQuestButton)

        val newQuestIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.plus_icon,
            view.context.theme
        )

        completeQuestsButton.setImageDrawable(newQuestIcon)

        val primaryIconColor : Int = resources.getColor(
            R.color.icon_primary,
            view.context.theme
        )

        completeQuestsButton.imageTintList = ColorStateList.valueOf(primaryIconColor)

        NavigationHelper.addNavigationToView(
            this,
            view,
            R.id.AddNewQuestButton,
            R.id.action_questList_to_newQuest
        )

        // Change Delete button to Shop button
        val deleteButton = view.findViewById<FloatingActionButton>(R.id.ShopButton)

        val shopIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.star_icon,
            view.context.theme
        )

        deleteButton.setImageDrawable(shopIcon)
        deleteButton.imageTintList = ColorStateList.valueOf(primaryIconColor)

        NavigationHelper.addNavigationToView(
            this,
            view,
            R.id.ShopButton,
            R.id.action_questList_to_shop
        )
    }

    private fun createQuestIcon(
        view: View,
        iconFileName: String,
        questId: Int
    ) : FloatingActionButton {
        val context = view.context
        val icon = FloatingActionButton(context)
        icon.id = View.generateViewId()

        val iconLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        icon.layoutParams = iconLayoutParams
        icon.contentDescription = resources.getString(R.string.quest_icon_description)
        val iconDrawable = getQuestDrawable(view, iconFileName)
        icon.setImageDrawable(iconDrawable)
        icon.compatElevation = 0f
        val pxCustomFABSize = resources.getDimension(R.dimen.big_round_button).toInt()
        icon.customSize = pxCustomFABSize

        val backgroundColor : Int = resources.getColor(
            R.color.quest_card_icon_background,
            context.theme
        )

        icon.backgroundTintList = ColorStateList.valueOf(backgroundColor)

        icon.setOnClickListener{
            if (!isSelected(questId)) {
                selectedQuestIds.add(questId)

                val selectIcon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.check_icon_green,
                    context.theme
                )

                icon.setImageDrawable(selectIcon)
            } else {
                selectedQuestIds.remove(questId)
                val originalDrawable = getQuestDrawable(view, iconFileName)
                icon.setImageDrawable(originalDrawable)
            }

            mode = if (selectedQuestIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
            if (mode == Mode.SELECT) activateSelectMode() else activateDefaultMode()
        }

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
        questId: Int,
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
        val cardIcon = createQuestIcon(view, questIconFileName, questId)
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

    private fun addCard(
        view: View,
        questId: Int,
        questName: String? = resources.getString(R.string.placeholder_text),
        difficulty: Difficulty = Difficulty.EASY,
        questIconFileName: String? = "question_mark_icon",
    ) {
        val difficultyColorId = difficultyColorMap[difficulty]
            ?: throw IllegalArgumentException("Given card difficulty is not a valid value.")

        val name = questName ?: resources.getString(R.string.placeholder_text)
        val iconName = questIconFileName ?: "question_mark_icon"
        val newCard = createQuestCard(view, questId, name, difficultyColorId, iconName)
        val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)
        questListLayout.addView(newCard)
    }

    private fun updateUsername(view: View, player: Player) {
        val placeholderText = getString(R.string.placeholder_text)
        val name = player.name ?: placeholderText
        StringHelper.substituteText(view, R.id.Username, player.lvl.toString(), name)
    }

    private fun updatePoints(view: View, player: Player) {
        val pointsAcronym = getString(R.string.points_acronym)
        StringHelper.substituteText(view, R.id.PointsLabel, player.rp.toString(), pointsAcronym)
    }

    private fun updateProgressBar(view: View, player: Player) {
        val progressBar =  view.findViewById<ProgressBar>(R.id.ProgressBar)
        val progressPercent = player.exp.toDouble() / player.expToLvlUp.toDouble()
        val progressInt = (progressPercent * 100).toInt()
        progressBar.progress = progressInt
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNavigation(view)

        questListViewModel.questList.observe(viewLifecycleOwner) { questList ->
            val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)
            questListLayout.removeAllViews()

            for (quest in questList) {
                addCard(
                    view = view,
                    questId = quest.id,
                    questName = quest.name,
                    difficulty = quest.difficulty,
                    questIconFileName = quest.iconName
                )
            }
        }

        questListViewModel.getPlayer(1).observe(viewLifecycleOwner) { player ->
            updateUsername(view, player)
            updatePoints(view, player)
            updateProgressBar(view, player)
        }
    }
}