package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.CompletedQuest
import com.brandonjamesyoung.levelup.shared.ButtonHelper.Companion.convertButton
import com.brandonjamesyoung.levelup.shared.ByteArrayHelper
import com.brandonjamesyoung.levelup.shared.ColorHelper.Companion.darken
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.IconHelper
import com.brandonjamesyoung.levelup.viewmodels.QuestHistoryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestHistory : Fragment(R.layout.quest_history) {
    private val viewModel: QuestHistoryViewModel by activityViewModels()

    private val difficultyColorMap = mapOf(
        Difficulty.EASY to R.color.easy,
        Difficulty.MEDIUM to R.color.medium,
        Difficulty.HARD to R.color.hard,
        Difficulty.EXPERT to R.color.expert
    )

    private fun navigateToQuestList() {
        NavHostFragment.findNavController(this).navigate(R.id.action_questHistory_to_questList)
        Log.i(TAG, "Going from Quest History to Quest List")
    }

    private fun activateQuestListButton() {
        convertButton(
            targetId = R.id.QuestListQuestHistoryButton,
            iconDrawableId = R.drawable.bullet_list_icon,
            buttonMethod = ::navigateToQuestList,
            view = requireView(),
            resources = resources
        )
    }

    // TODO Extract out this duplicate method
    private fun changeButtonIcon(button: FloatingActionButton, iconId : Int?) {
        if (iconId == null) {
            val context = requireContext()
            val drawable = IconHelper.getDefaultIcon(context)
            button.setImageDrawable(drawable)
            return
        }

        lifecycleScope.launch(Dispatchers.Default) {
            val icon = withContext(Dispatchers.IO) {
                viewModel.getIcon(iconId)
            }

            val drawable = ByteArrayHelper.convertByteArrayToDrawable(icon.image, resources)
            button.setImageDrawable(drawable)
        }
    }

    // TODO similar to createQuestCard() in QuestList.kt, possible simplification opportunity
    private fun createQuestHistoryCard(quest: CompletedQuest) : CardView {
        val view = requireView()
        val parentLayout = view.findViewById<LinearLayout>(R.id.QuestHistoryLinearLayout)

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

        val difficultyColorId = difficultyColorMap[quest.difficulty]
            ?: throw IllegalArgumentException("Given card difficulty is not a valid value.")

        val theme = view.context.theme
        val difficultyColor: Int = resources.getColor(difficultyColorId, theme)
        val backgroundColor: Int = darken(difficultyColor, DARKNESS_FACTOR)
        newCard.setCardBackgroundColor(backgroundColor)
        val cardConstraintLayout = newCard.getChildAt(0) as ConstraintLayout

        val cardTitle = cardConstraintLayout.getChildAt(0) as TextView
        val questName = quest.name ?: getString(R.string.placeholder_text)
        cardTitle.text = questName

        val icon = cardConstraintLayout.getChildAt(1) as FloatingActionButton
        val normalIconBgColor = resources.getColor(R.color.icon_background, theme)
        val iconBackgroundColor =  darken(normalIconBgColor, DARKNESS_FACTOR)
        icon.setBackgroundColor(iconBackgroundColor)
        changeButtonIcon(icon, quest.iconId)
        icon.isClickable = false
        icon.id = View.generateViewId()

        return newCard
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