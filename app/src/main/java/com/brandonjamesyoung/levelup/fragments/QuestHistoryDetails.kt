package com.brandonjamesyoung.levelup.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.EASY_COLOR
import com.brandonjamesyoung.levelup.data.CompletedQuestWithIcon
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.interfaces.Resettable
import com.brandonjamesyoung.levelup.utility.DateLabelManager
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.utility.TypeConverter
import com.brandonjamesyoung.levelup.viewmodels.QuestHistoryDetailsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuestHistoryDetails : Fragment(R.layout.quest_history_details), Resettable {
    private val viewModel: QuestHistoryDetailsViewModel by activityViewModels()

    private val args: QuestHistoryDetailsArgs by navArgs()

    @Inject lateinit var dateLabelManager: DateLabelManager

    private fun navigateToQuestHistory() {
        findNavController().navigate(R.id.action_questHistoryDetails_to_questHistory)
        Log.i(TAG, "Going from Quest History Details to Quest History")
    }

    private fun setupBackButton() {
        val view = requireView()
        val button = view.findViewById<View>(R.id.HistoryDetailsBackButton)

        button.setOnClickListener{
            navigateToQuestHistory()
        }
    }

    private fun updateCompletedQuestLabels(completedQuestWithIcon: CompletedQuestWithIcon?) {
        if (completedQuestWithIcon == null) {
            return
        }

        val completedQuest = completedQuestWithIcon.completedQuest
        val dateCreatedText = TypeConverter.convertInstantToString(completedQuest.dateCreated)
        val dateCompletedText = TypeConverter.convertInstantToString(completedQuest.dateCompleted)

        val textViewIdValueMap: Map<Int, String?> = mapOf(
            R.id.QuestHistoryDetailsTitle to completedQuest.name,
            R.id.CreatedOnValue to dateCreatedText,
            R.id.CompletedOnValue to dateCompletedText,
            R.id.ExpEarnedValue to completedQuest.expEarned.toString(),
            R.id.PointsEarnedValue to completedQuest.pointsEarned.toString()
        )

        for (textViewIdValuePair in textViewIdValueMap) {
            val textViewId: Int = textViewIdValuePair.key
            val text: String? = textViewIdValuePair.value ?: ""
            val textView: TextView = requireView().findViewById(textViewId)
            textView.text = text
        }
    }

    fun updateBorderColor(colorHex: Long?) {
        val border: ImageView = requireView().findViewById(R.id.HistoryDetailsBorder)
        val colorInt: Int = colorHex?.toInt() ?: EASY_COLOR.toInt()
        border.setColorFilter(colorInt)
    }

    fun setIcon(icon: Icon?) {
        if (icon == null) return
        val iconButtonId: Int = R.id.QuestHistoryDetailsIconButton
        val actionButton: FloatingActionButton = requireView().findViewById(iconButtonId)
        val drawable: Drawable? = icon.getDrawable(resources)
        actionButton.setImageDrawable(drawable)
    }

    fun setCompletedQuestData(completedQuestWithIcon: CompletedQuestWithIcon?) {
        updateCompletedQuestLabels(completedQuestWithIcon)
        val colorHex: Long? = completedQuestWithIcon?.completedQuest?.getColorHex()
        updateBorderColor(colorHex)
        setIcon(completedQuestWithIcon?.icon)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On Quest History Details page")
            setupBackButton()

            if (args.completedQuestId == INVALID_COMPLETED_QUEST_ID) {
                return@launch
            }

            viewModel.loadCompletedQuest(args.completedQuestId)

            viewModel.completedQuestWithIcon.observe(viewLifecycleOwner) { completedQuestWithIcon ->
                setCompletedQuestData(completedQuestWithIcon)
            }
        }
    }

    companion object {
        private const val TAG = "QuestHistoryDetails"
        private const val INVALID_COMPLETED_QUEST_ID = 0
    }
}