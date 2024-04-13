package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.CompletedQuest
import com.brandonjamesyoung.levelup.utility.ButtonConverter
import com.brandonjamesyoung.levelup.utility.CardGenerator
import com.brandonjamesyoung.levelup.utility.ColorHelper.Companion.darken
import com.brandonjamesyoung.levelup.viewmodels.QuestHistoryViewModel
import com.brandonjamesyoung.levelup.views.QuestCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuestHistory: Fragment(R.layout.quest_history) {
    private val viewModel: QuestHistoryViewModel by activityViewModels()

    @Inject lateinit var cardGenerator: CardGenerator

    @Inject lateinit var buttonConverter: ButtonConverter

    private fun navigateToQuestList() {
        findNavController().navigate(R.id.action_questHistory_to_questList)
        Log.i(TAG, "Going from Quest History to Quest List")
    }

    private fun activateQuestListButton() {
        val view = requireView()
        val questListButton = view.findViewById<Button>(R.id.QuestListQuestHistoryButton)

        questListButton.setOnClickListener {
            navigateToQuestList()
        }
    }

    // TODO similar to createQuestCard() in QuestList.kt, possible simplification opportunity
    private fun createQuestHistoryCard(completedQuest: CompletedQuest) : QuestCardView {
        val view = requireView()
        val questName = completedQuest.name ?: getString(R.string.placeholder_text)

        val difficultyColorId = cardGenerator.difficultyColorMap[completedQuest.difficulty]
            ?: throw IllegalArgumentException("Given card difficulty is not a valid value.")

        val theme = view.context.theme
        val difficultyColor: Int = resources.getColor(difficultyColorId, theme)
        val backgroundColor: Int = darken(difficultyColor, DARKNESS_FACTOR)

        val newCard = cardGenerator.createSimpleCard(
            name = questName,
            backgroundColorInt = backgroundColor,
            iconId = completedQuest.iconId,
            view = view,
            iconReader = viewModel,
            lifecycleScope = lifecycleScope
        )

        newCard.disableClickableIcon()
        return newCard
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

    private fun setupObservables() {
        viewModel.questHistoryList.observe(viewLifecycleOwner) { questList ->
            val view = requireView()
            val questListLayout = view.findViewById<LinearLayout>(R.id.QuestHistoryLinearLayout)
            questListLayout.removeAllViews()
            val sortedQuestHistory = questList.sortedByDescending { it.dateCompleted }

            for (completedQuest in sortedQuestHistory) {
                val newCard = createQuestHistoryCard(completedQuest)
                questListLayout.addView(newCard)
            }

            if (questList.isEmpty()) showNoQuestsMessage() else hideNoQuestsMessage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            Log.i(TAG, "On Quest History page")
            activateQuestListButton()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "QuestHistory"
        private const val DARKNESS_FACTOR = 0.15F
    }
}