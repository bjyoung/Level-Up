package com.brandonjamesyoung.levelup.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.constants.POP_UP_BUTTON_WAIT_PERIOD
import com.brandonjamesyoung.levelup.constants.PROGRESS_BAR_ANIM_DURATION
import com.brandonjamesyoung.levelup.constants.SortOrder
import com.brandonjamesyoung.levelup.constants.SortType
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.data.QuestCard
import com.brandonjamesyoung.levelup.data.QuestWithIcon
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.utility.*
import com.brandonjamesyoung.levelup.utility.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer

@AndroidEntryPoint
class QuestList: Fragment(R.layout.quest_list) {
    private val viewModel: QuestListViewModel by activityViewModels()

    private var latestQuests: List<QuestWithIcon> = mutableListOf()

    @Inject lateinit var cardCreator: CardGridCreator

    @Inject lateinit var buttonConverter: ButtonConverter

    @Inject lateinit var pointsDisplay: PointsDisplay

    @Inject lateinit var sorter: SortButtonManager

    private var pointsLoaded: Boolean = false

    private var sortTimer: Timer? = null

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
        return questId in viewModel.selectedQuestIds
    }

    private fun completeQuests() {
        viewModel.completeQuests(viewModel.selectedQuestIds.toSet())
        viewModel.selectedQuestIds.clear()
        viewModel.switchMode(Mode.DEFAULT)
    }

    // Change New Quest button to Complete Quests button
    private fun activateCompleteQuestsButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.AddNewQuestButton,
            iconDrawableId = R.drawable.check_icon_green_large,
            iconColorId = R.color.confirm_icon,
            buttonMethod = ::completeQuests,
            view = requireView(),
        )
    }

    private fun deleteQuests() {
        viewModel.deleteQuests(viewModel.selectedQuestIds.toSet())
        viewModel.selectedQuestIds.clear()
        viewModel.switchMode(Mode.DEFAULT)
    }

    // Change Shop button to Delete button
    private fun activateDeleteButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.ShopButton,
            iconDrawableId = R.drawable.trash_bin_icon_large,
            iconColorId = R.color.warning_icon,
            buttonMethod = ::deleteQuests,
            view = requireView(),
        )
    }

    private fun deselectAllQuests() {
        viewModel.selectedQuestIds.clear()
        val targetMode = if (viewModel.selectedQuestIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
        viewModel.switchMode(targetMode)
        reloadLazyQuestGrid(latestQuests)
    }

    // Switch Settings button to Cancel button
    private fun activateCancelButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.SettingsButton,
            iconDrawableId = R.drawable.cancel_icon_large,
            buttonMethod = ::deselectAllQuests,
            view = requireView()
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
            tooltip = getString(R.string.new_quest_button_tooltip),
            view = requireView()
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
            tooltip = getString(R.string.shop_button_tooltip),
            view = requireView()
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
            tooltip = getString(R.string.settings_button_tooltip),
            view = requireView()
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

    private fun startSortTimer() {
        val sortButton: Button = requireView().findViewById(R.id.SortButton)
        val sortTrigger: Button = requireView().findViewById(R.id.SortTrigger)
        val waitPeriod: Long = POP_UP_BUTTON_WAIT_PERIOD

        sortTimer = timer(initialDelay = waitPeriod, period = waitPeriod) {
            lifecycleScope.launch {
                sorter.hideSortButton(sortButton, sortTrigger)
                sortTimer?.cancel()
                sortTimer = null
            }
        }
    }

    private fun setupSortTrigger() {
        val view = requireView()
        val sortButton: Button = view.findViewById(R.id.SortButton)
        val sortTrigger: Button = view.findViewById(R.id.SortTrigger)

        sortTrigger.setOnClickListener {
            sorter.showSortButton(sortButton, sortTrigger)
            startSortTimer()
        }
    }

    private fun switchSortMode() {
        try {
            sortTimer?.cancel()
            startSortTimer()
            Log.d(TAG, "Sort hide timer reset")

            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.switchSort()
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            viewModel.showSnackbar("Sort failed")
        }
    }

    private fun setupSortButton() {
        val sortButton: Button = requireView().findViewById(R.id.SortButton)

        sortButton.setOnClickListener {
            switchSortMode()
        }
    }

    private fun changeSortIcon() {
        val sortType: SortType? = viewModel.settings.value?.questListSortType

        val possibleSortIcons: List<Int> = when (sortType) {
            SortType.NAME -> listOf(
                R.drawable.sort_alpha_up_icon,
                R.drawable.sort_alpha_down_icon
            )
            SortType.DIFFICULTY -> listOf(
                R.drawable.sort_difficulty_up_icon,
                R.drawable.sort_difficulty_down_icon
            )
            else -> listOf(
                R.drawable.sort_date_up_icon,
                R.drawable.sort_date_down_icon
            )
        }

        val sortOrder: SortOrder? = viewModel.settings.value?.questListSortOrder

        val sortIconId: Int = when (sortOrder) {
            SortOrder.ASC -> possibleSortIcons[1]
            else -> possibleSortIcons[0]
        }

        buttonConverter.convertNavButton(
            targetId = R.id.SortButton,
            iconDrawableId = sortIconId,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::switchSortMode,
            view = requireView(),
        )
    }

    private fun sortQuests(
        quests: List<QuestWithIcon>,
        sortType: SortType?,
        sortOrder: SortOrder?
    ) : List<QuestWithIcon> {
        if (sortType == null) {
            return quests
        }

        val questSortOrder = sortOrder ?: SortOrder.ASC

        return if (questSortOrder == SortOrder.ASC) {
            when (sortType) {
                SortType.NAME -> quests.sortedBy { it.activeQuest.name }
                SortType.DIFFICULTY -> quests.sortedBy { it.activeQuest.difficulty }
                else -> quests.sortedBy { it.activeQuest.dateCreated }
            }
        } else {
            when (sortType) {
                SortType.NAME -> quests.sortedByDescending { it.activeQuest.name }
                SortType.DIFFICULTY -> quests.sortedByDescending { it.activeQuest.difficulty }
                else -> quests.sortedByDescending { it.activeQuest.dateCreated }
            }
        }
    }

    private fun setupSort() {
        setupSortTrigger()
        setupSortButton()

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            changeSortIcon()

            viewModel.questList.value?.let { quests ->
                val sortType = settings?.questListSortType
                val sortOrder = settings?.questListSortOrder
                val sortedQuests: List<QuestWithIcon> = sortQuests(quests, sortType, sortOrder)
                reloadLazyQuestGrid(sortedQuests)
            }
        }
    }

    private fun activateDefaultMode() {
        viewModel.selectedQuestIds.clear()
        activateNewQuestButton()
        activateShopButton()
        activateSettingsButton()
    }

    private fun editQuest(questId: Int) {
        if (viewModel.mode.value == Mode.DEFAULT) navigateToNewQuest(questId)
    }

    private fun checkQuest(quest: Quest) {
        Log.i(TAG, "Select quest ${quest.name}")
        viewModel.selectedQuestIds.add(quest.id)
    }

    private fun uncheckQuest(quest: Quest) {
        Log.i(TAG, "De-select quest ${quest.name}")
        viewModel.selectedQuestIds.remove(quest.id)
    }

    private fun selectQuestCard(card: QuestCard) {
        card.selected = !card.selected
        val quest = card.quest
        if (!isSelected(quest.id)) checkQuest(quest) else uncheckQuest(quest)
        val targetMode = if (viewModel.selectedQuestIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
        viewModel.switchMode(targetMode)
    }

    // Add quest cards to lazy vertical grid
    private fun reloadLazyQuestGrid(questsWithIcon: List<QuestWithIcon>) {
        latestQuests = questsWithIcon.toMutableList()
        if (questsWithIcon.isEmpty()) showNoQuestsMessage() else hideNoQuestsMessage()

        val cards: List<QuestCard> = questsWithIcon.map {
            QuestCard(it.activeQuest, it.icon, isSelected(it.activeQuest.id))
        }

        val composeView = requireView().findViewById<ComposeView>(R.id.QuestListComposeView)

        composeView.setContent {
            cardCreator.QuestGridView(
                cards = cards,
                cardAction = ::editQuest,
                iconAction = ::selectQuestCard
            )
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
            requireView()
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
        viewModel.switchMode(Mode.DEFAULT)

        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.SELECT -> activateSelectMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }

        viewModel.questList.observe(viewLifecycleOwner) {
            reloadLazyQuestGrid(it)
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
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch(Dispatchers.Main) {
            Log.i(TAG, "On Quest List page")
            viewModel.selectedQuestIds.clear()
            setupSettings()
            setupUsernameNavigation()
            activateQuestHistoryButton()
            setupObservables()
            setupSort()
        }
    }

    companion object {
        private const val TAG = "QuestList"
    }
}