package com.brandonjamesyoung.levelup.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.shared.*
import com.brandonjamesyoung.levelup.shared.LevelUpHelper.Companion.getExpToLvlUp
import com.brandonjamesyoung.levelup.shared.ButtonHelper.Companion.convertButton
import com.brandonjamesyoung.levelup.shared.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestList : Fragment(R.layout.quest_list) {
    private val viewModel: QuestListViewModel by activityViewModels()
    private val selectedQuestIds: MutableSet<Int> = mutableSetOf()
    private val selectedQuestIconIds: MutableSet<Int> = mutableSetOf()
    private var mode: MutableLiveData<Mode> = MutableLiveData<Mode>()

    private val difficultyColorMap = mapOf(
        Difficulty.EASY to R.color.easy,
        Difficulty.MEDIUM to R.color.medium,
        Difficulty.HARD to R.color.hard,
        Difficulty.EXPERT to R.color.expert
    )

    private fun isSelected(questId: Int) : Boolean {
        return selectedQuestIds.contains(questId)
    }

    private fun completeQuests() {
        viewModel.completeQuests(selectedQuestIds.toSet())
        mode.value = Mode.DEFAULT
    }

    // Change New Quest button to Complete Quests button
    private fun activateCompleteQuestsButton() {
        convertButton(
            targetId = R.id.AddNewQuestButton,
            iconDrawableId = R.drawable.check_icon_green,
            iconColorId = R.color.confirm,
            buttonMethod = ::completeQuests,
            view = requireView(),
            resources = resources
        )
    }

    private fun deleteQuests() {
        viewModel.deleteQuests(selectedQuestIds.toSet())
        mode.value = Mode.DEFAULT
    }

    // Change Shop button to Delete button
    private fun activateDeleteButton() {
        convertButton(
            targetId = R.id.ShopButton,
            iconDrawableId = R.drawable.trash_bin_icon,
            iconColorId = R.color.cancel,
            buttonMethod = ::deleteQuests,
            view = requireView(),
            resources = resources
        )
    }

    private fun cancelSelectedQuests() {
        val view = requireView()
        val selectedIconIdsCopy = selectedQuestIconIds.toMutableList()

        for (id in selectedIconIdsCopy) {
            // TODO Need an easier way to swap between icon modes
            val questCardIcon : FloatingActionButton = view.findViewById(id)
            questCardIcon.callOnClick()
        }
    }

    // Switch Settings button to Cancel button
    private fun activateCancelButton() {
        convertButton(
            targetId = R.id.SettingsButton,
            iconDrawableId = R.drawable.cancel_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::cancelSelectedQuests,
            view = requireView(),
            resources = resources
        )
    }

    private fun activateSelectMode() {
        activateCompleteQuestsButton()
        activateDeleteButton()
        activateCancelButton()
    }

    private fun navigateToNewQuest(questId: Int? = null) {
        val action = if (questId != null) {
            QuestListDirections.actionQuestListToNewQuest(questId)
        } else {
            QuestListDirections.actionQuestListToNewQuest()
        }

        NavHostFragment.findNavController(this).navigate(action)
        Log.i(TAG, "Going from Quest List to New Quest")
    }

    // Change Complete Quests button to New Quest button
    private fun activateNewQuestButton() {
        convertButton(
            targetId = R.id.AddNewQuestButton,
            iconDrawableId = R.drawable.plus_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::navigateToNewQuest,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToShop() {
        NavHostFragment.findNavController(this).navigate(R.id.action_questList_to_shop)
        Log.i(TAG, "Going from Quest List to Shop")
    }

    // Change Delete button to Shop button
    private fun activateShopButton() {
        convertButton(
            targetId = R.id.ShopButton,
            iconDrawableId = R.drawable.shopping_bag_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::navigateToShop,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToSettings() {
        val action = QuestListDirections.actionQuestListToSettings(R.id.QuestList)
        NavHostFragment.findNavController(this).navigate(action)
        Log.i(TAG, "Going from Quest List to Settings")
    }

    // Change Cancel button to the Settings button
    private fun activateSettingsButton() {
        convertButton(
            targetId = R.id.SettingsButton,
            iconDrawableId = R.drawable.gear_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::navigateToSettings,
            view = requireView(),
            resources = resources
        )
    }

    private fun activateDefaultMode() {
        selectedQuestIds.clear()
        selectedQuestIconIds.clear()
        activateNewQuestButton()
        activateShopButton()
        activateSettingsButton()
    }

    private fun editQuest(questId: Int) {
        if (mode.value == Mode.DEFAULT) navigateToNewQuest(questId)
    }

    private fun selectQuestIcon(questId: Int, button: FloatingActionButton, iconId: Int?) {
        val context = requireContext()

        if (!isSelected(questId)) {
            selectedQuestIds.add(questId)
            selectedQuestIconIds.add(button.id)

            val selectIcon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.check_icon_green,
                context.theme
            )

            button.setImageDrawable(selectIcon)
        } else {
            selectedQuestIds.remove(questId)
            selectedQuestIconIds.remove(button.id)
            changeButtonIcon(button, iconId)
        }

        mode.value = if (selectedQuestIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
    }

    // TODO Extract out this duplicate method
    private fun changeButtonIcon(button: FloatingActionButton, iconId : Int?) {
        // TODO add check for invalid id, if invalid then show default icon
        if (iconId == null) {
            val context = requireContext()
            val drawable = IconHelper.getDefaultIcon(context)
            button.setImageDrawable(drawable)
            return
        }

        val iconLiveData = viewModel.getIcon(iconId)

        iconLiveData.observe(viewLifecycleOwner) { icon ->
            val drawable = ByteArrayHelper.convertByteArrayToDrawable(icon.image, resources)
            button.setImageDrawable(drawable)
            iconLiveData.removeObservers(viewLifecycleOwner)
        }
    }

    private fun createQuestCard(quest: Quest) : CardView {
        val view = requireView()
        val parentLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)

        val newCardLayout = layoutInflater.inflate(
            R.layout.quest_card,
            parentLayout,
            false
        ) as LinearLayoutCompat

        // TODO instead of using getChildAt(), which relies on knowing the .xml file
        //  use findViewById instead on newCard
        val newCard = newCardLayout.getChildAt(0) as CardView
        newCard.id = View.generateViewId()
        newCardLayout.removeView(newCard)

        newCard.setOnClickListener {
            editQuest(quest.id)
        }

        newCard.setOnLongClickListener {
            editQuest(quest.id)
            true
        }

        val difficultyColorId = difficultyColorMap[quest.difficulty]
            ?: throw IllegalArgumentException("Given card difficulty is not a valid value.")

        newCard.setCardBackgroundColor(resources.getColor(difficultyColorId, view.context.theme))
        val cardConstraintLayout = newCard.getChildAt(0) as ConstraintLayout

        val cardTitle = cardConstraintLayout.getChildAt(0) as TextView
        val questName = quest.name ?: resources.getString(R.string.placeholder_text)
        cardTitle.text = questName

        val icon = cardConstraintLayout.getChildAt(1) as FloatingActionButton
        changeButtonIcon(icon, quest.iconId)
        icon.id = View.generateViewId()

        icon.setOnClickListener{
            selectQuestIcon(quest.id, icon, quest.iconId)
        }

        icon.setOnLongClickListener {
            editQuest(quest.id)
            true
        }

        return newCard
    }

    private fun addCard(quest: Quest) {
        val newCard = createQuestCard(quest)
        val view = requireView()
        val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)
        questListLayout.addView(newCard)
    }

    private fun updateUsername(view: View, player: Player?) {
        val placeholderText = getString(R.string.placeholder_text)
        val name: String
        val lvlStr: String

        if (player != null) {
            name = player.name ?: placeholderText
            lvlStr = player.lvl.toString()
        } else {
            name = placeholderText
            lvlStr = placeholderText
        }

        val levelHeader = getString(R.string.username_level_header, lvlStr, name)
        val usernameView = view.findViewById<TextView>(R.id.Username)
        usernameView.text = levelHeader
    }

    private fun updatePoints(view: View, player: Player?) {
        val placeholderText = getString(R.string.placeholder_text)
        val rtStr = player?.points?.toString() ?: placeholderText
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

    private fun setupObservables() {
        val view = requireView()
        mode.value = Mode.DEFAULT

        mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.SELECT -> activateSelectMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }

        viewModel.questList.observe(viewLifecycleOwner) { questList ->
            val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)
            questListLayout.removeAllViews()
            val sortedQuestList = questList.sortedBy { it.dateCreated }

            for (quest in sortedQuestList) {
                addCard(quest)
            }
        }

        viewModel.player.observe(viewLifecycleOwner) { player ->
            updateUsername(view, player)
            updatePoints(view, player)
            updateProgressBar(view, player)
            updateNextLvlProgress(view, player)
        }

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            updatePointsAcronym(settings)
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val addNewQuestButton: View = view.findViewById(R.id.AddNewQuestButton)
                showSnackbar(it, view, addNewQuestButton)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            Log.i(TAG, "On Quest List page")
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "QuestList"
    }
}