package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.viewmodels.NewQuestViewModel
import com.brandonjamesyoung.levelup.validation.Validation.Companion.validateName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant

private const val TAG = "NewQuest"

@AndroidEntryPoint
class NewQuest : Fragment(R.layout.new_quest) {
    private val viewModel: NewQuestViewModel by activityViewModels()
    private var selectedDifficulty: Difficulty? = null
    private var defaultDifficulty = Difficulty.EASY

    private val buttonIdToDifficultyMap = mapOf(
        R.id.EasyButton to Difficulty.EASY,
        R.id.MediumButton to Difficulty.MEDIUM,
        R.id.HardButton to Difficulty.HARD,
        R.id.ExpertButton to Difficulty.EXPERT
    )

    private val difficultyToButtonIdMap = buttonIdToDifficultyMap.entries
        .associateBy({ it.value }) { it.key }

    private fun addNavigation() {
        val view = requireView()
        val button = view.findViewById<View>(R.id.CancelButton)

        button.setOnClickListener{
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_newQuest_to_questList)
            Log.i(TAG, "Going from New Quest to Quest List")
        }
    }

    private fun moveDifficultySelectBox(difficulty: Difficulty) {
        val view = requireView()
        val buttonId = difficultyToButtonIdMap[difficulty]!!
        val button = view.findViewById<AppCompatButton>(buttonId)
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.NewQuest)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        val selectBoxId = R.id.DifficultySelectBox
        constraintSet.connect(selectBoxId, ConstraintSet.BOTTOM, button.id, ConstraintSet.BOTTOM)
        constraintSet.connect(selectBoxId, ConstraintSet.START, button.id, ConstraintSet.START)
        constraintSet.connect(selectBoxId, ConstraintSet.END, button.id, ConstraintSet.END)
        constraintSet.connect(selectBoxId, ConstraintSet.TOP, button.id, ConstraintSet.TOP)
        constraintSet.applyTo(constraintLayout)
    }

    private fun setSelectedDifficulty(difficulty: Difficulty) {
        selectedDifficulty = difficulty
        moveDifficultySelectBox(difficulty)
        Log.i(TAG, "Set quest difficulty to $selectedDifficulty")
    }

    private fun setDifficultyButtonListeners() {
        val view = requireView()

       buttonIdToDifficultyMap.forEach { entry ->
            val button = view.findViewById<AppCompatButton>(entry.key)

            button.setOnClickListener {
                setSelectedDifficulty(entry.value)
            }
        }
    }

    private fun createQuest() {
        val view = requireView()
        val nameTextView = view.findViewById<TextView>(R.id.NameInput)
        var questName: String? = nameTextView.text.trim().toString()

        if (questName == "") {
            questName = null
        }

        // TODO get icon file name here and store in saved quest
//        val iconButton = view.findViewById<FloatingActionButton>(R.id.IconButton)
//        val iconDrawable = iconButton.drawable
//        val iconFileName = resources.getResourceEntryName(R.drawable.question_mark_icon)

        val quest = Quest(
            name = questName,
            difficulty = selectedDifficulty!!,
            dateCreated = Instant.now()
        )

        viewModel.insert(quest)
    }

    private fun validateInput() : Boolean {
        val view = requireView()
        val nameView = view.findViewById<EditText>(R.id.NameInput)

        if (!validateName(nameView, TAG, this)) {
            return false
        }

        // TODO Check if icon is a valid icon name
        // val iconView = view.findViewById<FloatingActionButton>(R.id.IconButton)

        return true
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (validateInput()){
                createQuest()
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_newQuest_to_questList)
                Log.i(TAG, "Going from New Quest to Quest List")
            }
        }
    }

    private fun selectDefaultDifficulty() {
        setSelectedDifficulty(defaultDifficulty)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On New Quest page")
            addNavigation()
            setDifficultyButtonListeners()
            setupConfirmButton()
            selectDefaultDifficulty()
        }
    }
}