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
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.constants.PROGRESS_BAR_ANIM_DURATION
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.utility.*
import com.brandonjamesyoung.levelup.utility.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import com.brandonjamesyoung.levelup.views.QuestCardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class QuestList: Fragment(R.layout.quest_list) {
    private val viewModel: QuestListViewModel by activityViewModels()

    @Inject lateinit var cardGenerator: CardGenerator

    @Inject lateinit var buttonConverter: ButtonConverter

    @Inject lateinit var pointsDisplay: PointsDisplay

    private var pointsLoaded: Boolean = false

    private fun setupUsernameNavigation() {
        val view = requireView()
        val username = view.findViewById<TextView>(R.id.Username)

        username.setOnLongClickListener {
            navigateToNameEntry()
            true
        }

        val experienceBar = view.findViewById<ProgressBar>(R.id.ProgressBar)

        experienceBar.setOnLongClickListener {
            navigateToNameEntry()
            true
        }
    }

    private fun navigateToNameEntry() {
        findNavController().navigate(R.id.action_questList_to_nameEntry)
        Log.i(TAG, "Going from Quest List to Name Entry")
    }

    private suspend fun updatePointsAcronym(settings: Settings?) {
        if (settings == null) return
        val view = requireView()
        val pointsLabel: TextView = view.findViewById(R.id.PointsLabel)

        withContext(Dispatchers.Main) {
            pointsLabel.text = settings.pointsAcronym
        }
    }

    private fun setupSettings() = lifecycleScope.launch(Dispatchers.IO) {
        val settings = viewModel.getSettings()
        updatePointsAcronym(settings)
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
            iconDrawableId = R.drawable.check_icon_green_large,
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
            iconDrawableId = R.drawable.trash_bin_icon_large,
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
            iconDrawableId = R.drawable.cancel_icon_large,
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

        findNavController().navigate(action)
        Log.i(TAG, "Going from Quest List to New Quest")
    }

    // Change Complete Quests button to New Quest button
    private fun activateNewQuestButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.AddNewQuestButton,
            iconDrawableId = R.drawable.plus_icon_large,
            buttonMethod = ::navigateToNewQuest,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToShop() {
        findNavController().navigate(R.id.action_questList_to_shop)
        Log.i(TAG, "Going from Quest List to Shop")
    }

    // Change Delete button to Shop button
    private fun activateShopButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.ShopButton,
            iconDrawableId = R.drawable.shopping_bag_icon_large,
            buttonMethod = ::navigateToShop,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToSettings() {
        val action = QuestListDirections.actionQuestListToSettings(R.id.QuestList)
        findNavController().navigate(action)
        Log.i(TAG, "Going from Quest List to Settings")
    }

    // Change Cancel button to the Settings button
    private fun activateSettingsButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.SettingsButton,
            iconDrawableId = R.drawable.gear_icon_large,
            buttonMethod = ::navigateToSettings,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToQuestHistory() {
        findNavController().navigate(R.id.action_questList_to_questHistory)
        Log.i(TAG, "Going from Quest List to Quest History")
    }

    private fun activateQuestHistoryButton() {
        val view = requireView()
        val questHistoryButton = view.findViewById<MaterialButton>(R.id.QuestHistoryButton)

        questHistoryButton.setOnClickListener {
            navigateToQuestHistory()
        }
    }

    private fun activateDefaultMode() {
        viewModel.selectedQuestIds.clear()
        viewModel.selectedQuestIconIds.clear()
        activateNewQuestButton()
        activateShopButton()
        activateSettingsButton()
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
            R.drawable.check_icon_green_large,
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

    private fun showNoQuestsMessage() {
        val view = requireView()
        val noIconsMessage = view.findViewById<TextView>(R.id.NoQuestsMessage)
        noIconsMessage.visibility = View.VISIBLE
    }

    private fun hideNoQuestsMessage() {
        val view = requireView()
        val noIconsMessage = view.findViewById<TextView>(R.id.NoQuestsMessage)
        noIconsMessage.visibility = View.GONE
    }

    private fun updateUsername(player: Player?) {
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
        val usernameView = requireView().findViewById<TextView>(R.id.Username)
        usernameView.text = levelHeader
    }

    private fun updatePointsDisplay(player: Player?) {
        pointsDisplay.updatePointsText(
            player,
            R.id.PointsAmount,
            pointsLoaded,
            requireView(),
            resources
        )

        pointsLoaded = true
    }

    private fun updateProgressBar(player: Player?) {
        val progressBar =  requireView().findViewById<ProgressBar>(R.id.ProgressBar)
        var progressInt = 0

        if (player != null) {
            val currExp = player.currentLvlExp.toDouble()
            val expNeededForCurrentLvl = player.getExpToLvlUp().toDouble()
            val progressPercent = currExp / expNeededForCurrentLvl
            progressInt = (progressPercent * 100).toInt()
        }

        ObjectAnimator.ofInt(progressBar, "progress", progressInt)
            .setDuration(PROGRESS_BAR_ANIM_DURATION)
            .start()
    }

    private fun updateNextLvlProgress(player: Player?) {
        if (player == null) return
        val nextLvlExpView : TextView = requireView().findViewById(R.id.NextLvlExp)
        val expToNextLvl = player.getExpToNextLvl()
        nextLvlExpView.text = expToNextLvl.toString()
    }

    private fun setupPlayerUi(player: Player?) {
        if (player == null) return
        updateUsername(player)
        updatePointsDisplay(player)
        updateProgressBar(player)
        updateNextLvlProgress(player)
    }

    private fun setupObservables() {
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
            if (it.isEmpty()) showNoQuestsMessage() else hideNoQuestsMessage()
        }

        viewModel.player.observe(viewLifecycleOwner) {
            setupPlayerUi(it)
        }

        val view = requireView()

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
            setupSettings()
            setupUsernameNavigation()
            activateQuestHistoryButton()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "QuestList"
    }
}