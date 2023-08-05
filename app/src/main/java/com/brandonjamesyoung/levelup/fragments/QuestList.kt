package com.brandonjamesyoung.levelup.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.constants.PROGRESS_BAR_ANIMATE_DURATION
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.utility.CardGenerator
import com.brandonjamesyoung.levelup.utility.*
import com.brandonjamesyoung.levelup.utility.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import com.brandonjamesyoung.levelup.views.QuestCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuestList: Fragment(R.layout.quest_list) {
    private val viewModel: QuestListViewModel by activityViewModels()

    @Inject lateinit var cardGenerator: CardGenerator

    @Inject lateinit var buttonConverter: ButtonConverter

    private val args: QuestListArgs by navArgs()

    private fun navigateToNameEntry() {
        val navController: NavController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_questList_to_nameEntry)
        Log.i(TAG, "Going from Quest List to Name Entry")
    }

    private fun updatePointsAcronym(settings: Settings?) {
        if (settings == null) return
        val view = requireView()
        val pointsLabel : TextView = view.findViewById(R.id.PointsLabel)
        pointsLabel.text = settings.pointsAcronym
    }

    private fun setupSettings(settings: Settings?) {
        if (settings == null) return
        if (!settings.nameEntered && !args.fromNameEntry) navigateToNameEntry()

        lifecycleScope.launch(
            Dispatchers.Default + CoroutineName("Update Points Acronym")
        ) {
            updatePointsAcronym(settings)
        }
    }

    private fun isSelected(questId: Int) : Boolean {
        return viewModel.selectedQuestIds.contains(questId)
    }

    private fun completeQuests() {
        viewModel.completeQuests(viewModel.selectedQuestIds.toSet())
        viewModel.switchToDefaultMode()
    }

    // Change New Quest button to Complete Quests button
    private fun activateCompleteQuestsButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.AddNewQuestButton,
            iconDrawableId = R.drawable.check_icon_green,
            iconColorId = R.color.confirm_icon,
            buttonMethod = ::completeQuests,
            view = requireView(),
            resources = resources
        )
    }

    private fun deleteQuests() {
        viewModel.deleteQuests(viewModel.selectedQuestIds.toSet())
        viewModel.switchToDefaultMode()
    }

    // Change Shop button to Delete button
    private fun activateDeleteButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.ShopButton,
            iconDrawableId = R.drawable.trash_bin_icon,
            iconColorId = R.color.warning_icon,
            buttonMethod = ::deleteQuests,
            view = requireView(),
            resources = resources
        )
    }

    private fun deselectAllQuests() {
        val view = requireView()
        val selectedIconIdsCopy = viewModel.selectedQuestIconIds.toList()

        for (id in selectedIconIdsCopy) {
            val questCardIcon : FloatingActionButton = view.findViewById(id)
            questCardIcon.callOnClick()
        }
    }

    // Switch Settings button to Cancel button
    private fun activateCancelButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.SettingsButton,
            iconDrawableId = R.drawable.cancel_icon,
            buttonMethod = ::deselectAllQuests,
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
        buttonConverter.convertNavButton(
            targetId = R.id.AddNewQuestButton,
            iconDrawableId = R.drawable.plus_icon,
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
        buttonConverter.convertNavButton(
            targetId = R.id.ShopButton,
            iconDrawableId = R.drawable.shopping_bag_icon,
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
        buttonConverter.convertNavButton(
            targetId = R.id.SettingsButton,
            iconDrawableId = R.drawable.gear_icon,
            buttonMethod = ::navigateToSettings,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToQuestHistory() {
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_questList_to_questHistory)
        Log.i(TAG, "Going from Quest List to Quest History")
    }

    private fun activateQuestHistoryButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.QuestHistoryButton,
            iconDrawableId = R.drawable.clock_icon,
            buttonMethod = ::navigateToQuestHistory,
            view = requireView(),
            resources = resources
        )
    }

    private fun activateDefaultMode() {
        viewModel.selectedQuestIds.clear()
        viewModel.selectedQuestIconIds.clear()
        activateNewQuestButton()
        activateShopButton()
        activateSettingsButton()
        activateQuestHistoryButton()
    }

    private fun editQuest(questId: Int) {
        if (viewModel.mode.value == Mode.DEFAULT) navigateToNewQuest(questId)
    }

    private fun checkQuest(quest: Quest, button: FloatingActionButton) {
        Log.i(TAG, "Select quest ${quest.name}")
        val context = requireContext()
        viewModel.selectedQuestIds.add(quest.id)
        viewModel.selectedQuestIconIds.add(button.id)

        val selectIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.check_icon_green,
            context.theme
        )

        button.setImageDrawable(selectIcon)
    }

    private fun changeButtonIcon(button: FloatingActionButton, iconId : Int?) {
        buttonConverter.changeQuestIcon(
            button = button,
            iconId = iconId,
            iconReader = viewModel,
            context = requireContext(),
            lifecycleScope = lifecycleScope
        )
    }

    private fun uncheckQuest(quest: Quest, button: FloatingActionButton) {
        Log.i(TAG, "De-select quest ${quest.name}")
        viewModel.selectedQuestIds.remove(quest.id)
        viewModel.selectedQuestIconIds.remove(button.id)
        changeButtonIcon(button, quest.iconId)
    }

    private fun selectQuestIcon(quest: Quest, button: FloatingActionButton) {
        if (!isSelected(quest.id)) {
            checkQuest(quest, button)
        } else {
            uncheckQuest(quest, button)
        }

        if (viewModel.selectedQuestIds.isNotEmpty()) {
            viewModel.switchToSelectMode()
        } else {
            viewModel.switchToDefaultMode()
        }
    }

    private fun createQuestCard(quest: Quest) : QuestCardView {
        val difficultyColorId = cardGenerator.difficultyColorMap[quest.difficulty]
            ?: throw IllegalArgumentException("Given card difficulty is not a valid value.")

        val view = requireView()
        val colorInt: Int = resources.getColor(difficultyColorId, view.context.theme)
        val questName = quest.name ?: getString(R.string.placeholder_text)

        val newCard = cardGenerator.createSimpleCard(
            name = questName,
            backgroundColorInt = colorInt,
            iconId = quest.iconId,
            view = view,
            iconReader = viewModel,
            lifecycleScope = lifecycleScope
        )

        newCard.setOnCardClickListener {
            editQuest(quest.id)
        }

        newCard.setOnQuestLongClickListener{
            editQuest(quest.id)
            true
        }

        newCard.setOnIconClickListener {
            selectQuestIcon(quest, newCard.iconButton)
        }

        return newCard
    }

    private fun setupQuestList(questList: List<Quest>) {
        val view = requireView()
        val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)
        questListLayout.removeAllViews()
        val sortedQuestList = questList.sortedBy { it.dateCreated }

        for (quest in sortedQuestList) {
            val newCard = createQuestCard(quest)
            questListLayout.addView(newCard)
        }
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
            val expNeededForCurrentLvl = player.getExpToLvlUp().toDouble()
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
        val expToNextLvl = player.getExpToNextLvl()
        nextLvlExpView.text = expToNextLvl.toString()
    }

    private fun setupPlayerUi(player: Player?) {
        if (player == null) return
        val view = requireView()
        updateUsername(view, player)
        updatePoints(view, player)
        updateProgressBar(view, player)
        updateNextLvlProgress(view, player)
    }

    private fun setupObservables() {
        val view = requireView()

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            setupSettings(settings)
        }

        viewModel.switchToDefaultMode()

        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.SELECT -> activateSelectMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }

        viewModel.questList.observe(viewLifecycleOwner) {
            setupQuestList(it)
        }

        viewModel.player.observe(viewLifecycleOwner) {
            setupPlayerUi(it)
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

        lifecycleScope.launch(Dispatchers.Main) {
            Log.i(TAG, "On Quest List page")
            viewModel.selectedQuestIds.clear()
            viewModel.selectedQuestIconIds.clear()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "QuestList"
    }
}