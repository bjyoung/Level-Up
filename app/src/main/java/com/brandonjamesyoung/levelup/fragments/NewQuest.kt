package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.NavigationHelper
import com.brandonjamesyoung.levelup.viewmodels.NewQuestViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant

const val MAX_QUEST_NAME_LENGTH = 40
val NAME_VALIDATION_REGEX = Regex("^[0-9a-zA-Z'\"!#$%&:?,.() @_+/*-]+$")

@AndroidEntryPoint
class NewQuest : Fragment(R.layout.new_quest) {
    private val viewModel: NewQuestViewModel by activityViewModels()
    private var selectedDifficulty: Difficulty? = null
    private var defaultDifficulty = Difficulty.EASY

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
        val nameTextView = view.findViewById<TextView>(R.id.NameInput)
        var questName: String? = nameTextView.text.trim().toString()

        if (questName == "") {
            questName = null
        }

        // TODO get icon file name here and store in saved quest
        val iconButton = view.findViewById<FloatingActionButton>(R.id.IconButton)
        val iconDrawable = iconButton.drawable
        val iconFileName = resources.getResourceEntryName(R.drawable.question_mark_icon)

        val quest = Quest(
            name = questName,
            difficulty = selectedDifficulty!!,
            dateCreated = Instant.now()
        )

        viewModel.insert(quest)
    }

    private fun validate(nameField : EditText) : Boolean {
        val name = nameField.text.trim().toString()
        val hasValidLength = name.length <= MAX_QUEST_NAME_LENGTH

        if (name == "") {
            return true
        }

        if (!hasValidLength) {
            nameField.error = resources.getString(R.string.name_length_error)
            return false
        }

        val hasValidCharacters = NAME_VALIDATION_REGEX.matches(name)

        if (!hasValidCharacters) {
            nameField.error = resources.getString(R.string.name_invalid_char_error)
            return false
        }

        return true
    }

    private fun validateInput(view: View) : Boolean {
        val nameView = view.findViewById<EditText>(R.id.NameInput)

        if (!validate(nameView)) {
            return false
        }

        // TODO Check if icon is a valid icon name
        // val iconView = view.findViewById<FloatingActionButton>(R.id.IconButton)

        return true
    }

    private fun setSaveButtonListener(view: View) {
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (validateInput(view)){
                createQuest(view)
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_newQuest_to_questList)
            }
        }
    }

    private fun selectDefaultDifficulty(view: View) {
        val difficultyButtonId = difficultyToButtonIdMap[defaultDifficulty]!!
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