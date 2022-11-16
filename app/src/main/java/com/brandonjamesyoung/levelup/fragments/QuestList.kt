package com.brandonjamesyoung.levelup.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
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
import com.brandonjamesyoung.levelup.ui.ButtonHelper.Companion.convertButton
import com.brandonjamesyoung.levelup.ui.ButtonHelper.Companion.getDrawable
import com.brandonjamesyoung.levelup.ui.QuestCard.Companion.createQuestCard
import com.brandonjamesyoung.levelup.ui.QuestCard.Companion.getQuestDrawable
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "QuestList"

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

    private fun addNavigation(view: View) {
        val buttonNavMap = mapOf(
            R.id.AddNewQuestButton to ::setupNewQuestNavigation,
            R.id.SettingsButton to ::setupSettingsNavigation,
            R.id.ShopButton to ::setupShopNavigation,
        )

        for ((buttonId, navAction) in buttonNavMap) {
            val button = view.findViewById<View>(buttonId)

            button.setOnClickListener{
                navAction()
            }
        }
    }

    private fun isSelected(questId: Int) : Boolean {
        return selectedQuestIds.contains(questId)
    }

    private fun completeQuests() {
        viewModel.completeQuests(selectedQuestIds.toSet())
        mode.value = Mode.DEFAULT
        // TODO show toast of exp and rt earned
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

    private fun setupNewQuestNavigation() {
        NavHostFragment.findNavController(this).navigate(R.id.action_questList_to_newQuest)
        Log.i(TAG, "Going from Quest List to New Quest")
    }

    // Change Complete Quests button to New Quest button
    private fun activateNewQuestButton() {
        convertButton(
            targetId = R.id.AddNewQuestButton,
            iconDrawableId = R.drawable.plus_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::setupNewQuestNavigation,
            view = requireView(),
            resources = resources
        )
    }

    private fun setupShopNavigation() {
        NavHostFragment.findNavController(this).navigate(R.id.action_questList_to_shop)
        Log.i(TAG, "Going from Quest List to Shop")
    }

    // Change Delete button to Shop button
    private fun activateShopButton() {
        convertButton(
            targetId = R.id.ShopButton,
            iconDrawableId = R.drawable.star_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::setupShopNavigation,
            view = requireView(),
            resources = resources
        )
    }

    private fun setupSettingsNavigation() {
        NavHostFragment.findNavController(this).navigate(R.id.action_questList_to_settings)
        Log.i(TAG, "Going from Quest List to Settings")
    }

    // Change Cancel button to the Settings button
    private fun activateSettingsButton() {
        convertButton(
            targetId = R.id.SettingsButton,
            iconDrawableId = R.drawable.gear_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::setupSettingsNavigation,
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

    private fun selectQuestIcon(questId: Int, icon: FloatingActionButton, iconFileName: String) {
        val view = requireView()

        if (!isSelected(questId)) {
            selectedQuestIds.add(questId)
            selectedQuestIconIds.add(icon.id)

            val selectIcon = getDrawable(
                drawableId = R.drawable.check_icon_green,
                theme = view.context.theme,
                resources = resources
            )

            icon.setImageDrawable(selectIcon)
        } else {
            selectedQuestIds.remove(questId)
            selectedQuestIconIds.remove(icon.id)
            val originalDrawable = getQuestDrawable(view, iconFileName, this)
            icon.setImageDrawable(originalDrawable)
        }

        mode.value = if (selectedQuestIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
    }

    // TODO should be able to pass in a Quest class here, to reduce # parameters
    private fun addCard(
        questId: Int,
        questName: String? = resources.getString(R.string.placeholder_text),
        difficulty: Difficulty = Difficulty.EASY,
        questIconFileName: String? = "question_mark_icon",
    ) {
        val view = requireView()
        val difficultyColorId = difficultyColorMap[difficulty]
            ?: throw IllegalArgumentException("Given card difficulty is not a valid value.")

        val name = questName ?: resources.getString(R.string.placeholder_text)
        val iconName = questIconFileName ?: "question_mark_icon"
        val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)

        val newCard = createQuestCard(
            questId = questId,
            questName = name,
            questColorId = difficultyColorId,
            questIconFileName = iconName,
            iconClickMethod = ::selectQuestIcon,
            parentLayout = questListLayout,
            inflater = layoutInflater,
            view = view,
            fragment = this
        )

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

        viewModel.questList.observe(viewLifecycleOwner) { questList ->
            val questListLayout = view.findViewById<LinearLayout>(R.id.QuestLinearLayout)
            questListLayout.removeAllViews()
            val sortedQuestList = questList.sortedBy { it.dateCreated }

            for (quest in sortedQuestList) {
                addCard(
                    questId = quest.id,
                    questName = quest.name,
                    difficulty = quest.difficulty,
                    questIconFileName = quest.iconName
                )
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            Log.i(TAG, "On Quest List page")
            addNavigation(view)
            setupObservables(view)

            setFragmentResult(
                "PREV_FRAGMENT",
                bundleOf("FRAGMENT_ID" to R.id.QuestList)
            )
        }
    }
}