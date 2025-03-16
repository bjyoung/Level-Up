package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.HISTORY_CARD_SHADER_SRC
import com.brandonjamesyoung.levelup.data.CompletedQuestWithIcon
import com.brandonjamesyoung.levelup.data.QuestCard
import com.brandonjamesyoung.levelup.utility.CardGridCreator
import com.brandonjamesyoung.levelup.viewmodels.QuestHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

@AndroidEntryPoint
class QuestHistory: Fragment(R.layout.quest_history) {
    private val viewModel: QuestHistoryViewModel by activityViewModels()

    @Inject lateinit var cardCreator: CardGridCreator

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

    // Add quest cards to lazy vertical grid
    private fun reloadLazyQuestGrid(quests: List<CompletedQuestWithIcon>) {
        if (quests.isEmpty()) showNoQuestsMessage() else hideNoQuestsMessage()
        val sortedQuests = quests.sortedByDescending { it.completedQuest.dateCompleted }
        val cards: List<QuestCard> = sortedQuests.map { QuestCard(it.completedQuest, it.icon) }
        val composeView = requireView().findViewById<ComposeView>(R.id.QuestHistoryComposeView)

        composeView.setContent {
            cardCreator.QuestGridView(
                cards = cards,
                cardShader = HISTORY_CARD_SHADER_SRC
            )
        }
    }

    private fun setupObservables() {
        viewModel.questHistoryList.observe(viewLifecycleOwner) {
            reloadLazyQuestGrid(it)
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
    }
}