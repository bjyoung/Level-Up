package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.NavigationHelper
import com.brandonjamesyoung.levelup.shared.Settings
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewQuest : Fragment(R.layout.new_quest) {
    private val viewModel: QuestListViewModel by activityViewModels()
    private var selectedDifficulty: Difficulty? = null

    private var difficultyToButtonIdMap = mapOf(
        Difficulty.EASY to R.id.EasyButton,
        Difficulty.MEDIUM to R.id.MediumButton,
        Difficulty.HARD to R.id.HardButton,
        Difficulty.EXPERT to R.id.ExpertButton
    )

    private var buttonIdToDifficultyMap = mapOf(
        R.id.EasyButton to Difficulty.EASY,
        R.id.MediumButton to Difficulty.MEDIUM,
        R.id.HardButton to Difficulty.HARD,
        R.id.ExpertButton to Difficulty.EXPERT
    )

    private fun addNavigation(view: View) {
        NavigationHelper.addNavigationToView(
            this,
            view,
            R.id.CancelButton,
            R.id.action_newQuest_to_questList
        )
    }

    // Move selected difficulty box to the given button
    private fun moveDifficultySelectBox(button: AppCompatButton, parentView: View) {
        val constraintLayout = parentView.findViewById<ConstraintLayout>(R.id.NewQuest)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        val selectBoxId = R.id.DifficultySelectBox
        constraintSet.connect(selectBoxId, ConstraintSet.BOTTOM, button.id, ConstraintSet.BOTTOM)
        constraintSet.connect(selectBoxId, ConstraintSet.START, button.id, ConstraintSet.START)
        constraintSet.connect(selectBoxId, ConstraintSet.END, button.id, ConstraintSet.END)
        constraintSet.connect(selectBoxId, ConstraintSet.TOP, button.id, ConstraintSet.TOP)
        constraintSet.applyTo(constraintLayout)
    }

    private fun setSelectedDifficulty(buttonId: Int, view: View) {
        val button = view.findViewById<AppCompatButton>(buttonId)
        selectedDifficulty = buttonIdToDifficultyMap[buttonId]
        val selectBox = view.findViewById<ImageView>(R.id.DifficultySelectBox)
        selectBox.visibility = ImageView.INVISIBLE
        moveDifficultySelectBox(button, view)
        selectBox.visibility = ImageView.VISIBLE
    }

    private fun setDifficultyButtonListeners(view: View) {
        for (id in difficultyToButtonIdMap.values) {
            val button = view.findViewById<AppCompatButton>(id)

            button.setOnClickListener {
                setSelectedDifficulty(id, view)
            }
        }
    }

    private fun createQuest(view: View) {
        // Get quest data
        val nameTextView = view.findViewById<TextView>(R.id.NameInput)
        val questName = nameTextView.text.toString()

        // TODO get icon file here and store in saved quest

        val quest = Quest(
            name = questName,
            difficulty = selectedDifficulty!!
        )

        // Save quest to database
        viewModel.insert(quest)
    }

    private fun setSaveButtonListener(view: View) {
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            createQuest(view)
        }
    }

    private fun selectDefaultDifficulty(view: View) {
        // TODO Persist settings in app data
        val settingsObj = Settings()
        val difficultyButtonId = difficultyToButtonIdMap[settingsObj.DefaultDifficulty]!!
        setSelectedDifficulty(difficultyButtonId, view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNavigation(view)
        setDifficultyButtonListeners(view)
        setSaveButtonListener(view)
        selectDefaultDifficulty(view)
    }
}