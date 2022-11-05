package com.brandonjamesyoung.levelup.fragments

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.LevelUpHelper.Companion.getExpToLvlUp
import com.brandonjamesyoung.levelup.shared.Mode
import com.brandonjamesyoung.levelup.shared.PROGRESS_BAR_ANIMATE_DURATION
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "QuestList"

@AndroidEntryPoint
class QuestList : Fragment(R.layout.quest_list) {
    private val questListViewModel: QuestListViewModel by activityViewModels()
    private val selectedQuestIds: MutableSet<Int> = mutableSetOf()
    private val selectedQuestIconIds: MutableSet<Int> = mutableSetOf()
    private var mode: MutableLiveData<Mode> = MutableLiveData<Mode>()

    private val difficultyColorMap = mapOf(
        Difficulty.EASY to R.color.easy,
        Difficulty.MEDIUM to R.color.medium,
        Difficulty.HARD to R.color.hard,
        Difficulty.EXPERT to R.color.expert
    )

    private fun addNavigation(view: View) {
        val buttonNavMap = mapOf(
            R.id.AddNewQuestButton to
                    Pair(R.id.action_questList_to_newQuest, "Going from Quest List to New Quest"),
            R.id.SettingsButton to
                    Pair(R.id.action_questList_to_settings, "Going from Quest List to Settings"),
            R.id.ShopButton to
                    Pair(R.id.action_questList_to_shop, "Going from Quest List to Shop"),
        )

        for ((buttonId, navIdPair) in buttonNavMap) {
            val button = view.findViewById<View>(buttonId)
            val navId = navIdPair.first
            val logMessage = navIdPair.second

            button.setOnClickListener{
                NavHostFragment.findNavController(this).navigate(navId)
                Log.i(TAG, logMessage)
            }
        }
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
        questListViewModel.completeQuests(selectedQuestIds.toSet())
        mode.value = Mode.DEFAULT
        // TODO show toast of exp and rt earned
//        showToast("Earned $expEarned exp and $rtEarned RT")
    }

    private fun deleteQuests() {
        questListViewModel.deleteQuests(selectedQuestIds.toSet())
        mode.value = Mode.DEFAULT
    }

    // Change New Quest button to Complete Quests button
    private fun activateCompleteQuestsButton() {
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
    }

    // Change Shop button to Delete button
    private fun activateDeleteButton() {
        val view = requireView()
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

    private fun cancelSelectedQuests() {
        val view = requireView()

        for (id in selectedQuestIconIds) {
            // TODO Need an easier way to swap between icon modes
            val questCardIcon : FloatingActionButton = view.findViewById(id)
            questCardIcon.callOnClick()
        }

        mode.value = Mode.DEFAULT
    }

    // Switch Settings button to Cancel button
    private fun activateCancelButton() {
        val view = requireView()
        val settingsButton = view.findViewById<FloatingActionButton>(R.id.SettingsButton)

        val cancelIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.cancel_icon,
            view.context.theme
        )

        settingsButton.setImageDrawable(cancelIcon)

        val primaryIconColor : Int = resources.getColor(
            R.color.icon_primary,
            view.context.theme
        )

        settingsButton.imageTintList = ColorStateList.valueOf(primaryIconColor)

        settingsButton.setOnClickListener{
            cancelSelectedQuests()
        }
    }

    private fun activateSelectMode() {
        activateCompleteQuestsButton()
        activateDeleteButton()
        activateCancelButton()
    }

    // Change Complete Quests button to New Quest button
    private fun activateNewQuestButton() {
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

        completeQuestsButton.setOnClickListener{
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_questList_to_newQuest)
            Log.i(TAG, "Going from Quest List to New Quest")
        }
    }

    // Change Delete button to Shop button
    private fun activateShopButton() {
        val view = requireView()
        val deleteButton = view.findViewById<FloatingActionButton>(R.id.ShopButton)

        val shopIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.star_icon,
            view.context.theme
        )

        deleteButton.setImageDrawable(shopIcon)

        val primaryIconColor : Int = resources.getColor(
            R.color.icon_primary,
            view.context.theme
        )

        deleteButton.imageTintList = ColorStateList.valueOf(primaryIconColor)

        deleteButton.setOnClickListener{
            NavHostFragment.findNavController(this).navigate(R.id.action_questList_to_shop)
            Log.i(TAG, "Going from Quest List to Shop")
        }
    }

    // Change Cancel button to the Settings button
    private fun activateSettingsButton() {
        val view = requireView()
        val settingsButton = view.findViewById<FloatingActionButton>(R.id.SettingsButton)

        val gearIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.gear_icon,
            view.context.theme
        )

        settingsButton.setImageDrawable(gearIcon)

        val primaryIconColor : Int = resources.getColor(
            R.color.icon_primary,
            view.context.theme
        )

        settingsButton.imageTintList = ColorStateList.valueOf(primaryIconColor)

        settingsButton.setOnClickListener{
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_questList_to_settings)
            Log.i(TAG, "Going from Quest List to Settings")
        }
    }

    private fun activateDefaultMode() {
        selectedQuestIds.clear()
        selectedQuestIconIds.clear()
        activateNewQuestButton()
        activateShopButton()
        activateSettingsButton()
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
                selectedQuestIconIds.add(icon.id)

                val selectIcon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.check_icon_green,
                    context.theme
                )

                icon.setImageDrawable(selectIcon)
            } else {
                selectedQuestIds.remove(questId)
                selectedQuestIconIds.remove(icon.id)
                val originalDrawable = getQuestDrawable(view, iconFileName)
                icon.setImageDrawable(originalDrawable)
            }

            mode.value = if (selectedQuestIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
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

    private fun updateUsername(view: View, player: Player?) {
        val placeholderText = getString(R.string.placeholder_text)
        val lvlLabel = getString(R.string.level_label)
        val name: String
        val lvlStr: String

        if (player != null) {
            name = player.name ?: placeholderText
            lvlStr = player.lvl.toString()
        } else {
            name = placeholderText
            lvlStr = placeholderText
        }

        val usernameView = view.findViewById<TextView>(R.id.Username)
        usernameView.text = "$lvlLabel $lvlStr $name"
    }

    private fun updatePoints(view: View, player: Player?) {
        val placeholderText = getString(R.string.placeholder_text)
        val rtStr = player?.rt?.toString() ?: placeholderText
        val pointsAmount = view.findViewById<TextView>(R.id.PointsAmount)
        pointsAmount.text = rtStr
    }

    private fun updateProgressBar(view: View, player: Player?) {
        val progressBar =  view.findViewById<ProgressBar>(R.id.ProgressBar)
        var progressInt = 0

        if (player != null) {
            val currExp = player.currentLvlExp.toDouble()
            val expNeededForCurrentLvl = getExpToLvlUp(player.lvl).toDouble()
            val progressPercent = currExp / expNeededForCurrentLvl
            progressInt = (progressPercent * 100).toInt()
        }

        ObjectAnimator.ofInt(progressBar, "progress", progressInt)
            .setDuration(PROGRESS_BAR_ANIMATE_DURATION)
            .start()
    }

    private fun updateNextLvlProgress(view: View, player: Player?) {
        if (player == null) return
        val nextLvlExpView : TextView =  view.findViewById(R.id.NextLvlExp)
        val totalExpForCurrentLvl = getExpToLvlUp(player.lvl)
        val expToNextLvl = totalExpForCurrentLvl - player.currentLvlExp
        nextLvlExpView.text = expToNextLvl.toString()
    }

    private fun updatePointsAcronym(settings: Settings?) {
        if (settings == null) return
        val view = requireView()
        val pointsLabel : TextView = view.findViewById(R.id.PointsLabel)
        pointsLabel.text = settings.pointsAcronym
    }

    private fun setupObservables(view: View) {
        mode.value = Mode.DEFAULT

        mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.SELECT -> activateSelectMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }

        questListViewModel.questList.observe(viewLifecycleOwner) { questList ->
            val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)
            questListLayout.removeAllViews()
            val sortedQuestList = questList.sortedBy { it.dateCreated }

            for (quest in sortedQuestList) {
                addCard(
                    view = view,
                    questId = quest.id,
                    questName = quest.name,
                    difficulty = quest.difficulty,
                    questIconFileName = quest.iconName
                )
            }
        }

        questListViewModel.player.observe(viewLifecycleOwner) { player ->
            updateUsername(view, player)
            updatePoints(view, player)
            updateProgressBar(view, player)
            updateNextLvlProgress(view, player)
        }

        questListViewModel.settings.observe(viewLifecycleOwner) { settings ->
            updatePointsAcronym(settings)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            Log.i(TAG, "On Quest List page")
            addNavigation(view)
            setupObservables(view)
        }
    }
}