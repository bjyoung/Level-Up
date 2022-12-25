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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.shared.ByteArrayHelper.Companion.convertByteArrayToDrawable
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.Mode
import com.brandonjamesyoung.levelup.viewmodels.NewQuestViewModel
import com.brandonjamesyoung.levelup.validation.Validation.Companion.validateName
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewQuest : Fragment(R.layout.new_quest) {
    private val viewModel: NewQuestViewModel by activityViewModels()
    private var selectedDifficulty: Difficulty? = null
    private var mode: MutableLiveData<Mode> = MutableLiveData<Mode>()
    private val args: NewQuestArgs by navArgs()

    private val buttonIdToDifficultyMap = mapOf(
        R.id.EasyButton to Difficulty.EASY,
        R.id.MediumButton to Difficulty.MEDIUM,
        R.id.HardButton to Difficulty.HARD,
        R.id.ExpertButton to Difficulty.EXPERT
    )

    private val difficultyToButtonIdMap = buttonIdToDifficultyMap.entries
        .associateBy({ it.value }) { it.key }

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

    private fun validateInput() : Boolean {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.NameInput)

        if (!validateName(nameInput, TAG, this)) {
            return false
        }

        // TODO Check if icon is a valid icon name
        // val iconView = view.findViewById<FloatingActionButton>(R.id.IconButton)

        return true
    }

    private fun saveQuest() {
        val view = requireView()
        val nameTextView = view.findViewById<EditText>(R.id.NameInput)
        var name: String? = nameTextView.text.trim().toString()

        if (name == "") {
            name = null
        }

        // TODO get icon file name here and store in saved quest
//        val iconButton = view.findViewById<FloatingActionButton>(R.id.IconButton)
//        val iconDrawable = iconButton.drawable
//        val iconFileName = resources.getResourceEntryName(R.drawable.question_mark_icon)

        val quest = Quest(
            name = name,
            difficulty = selectedDifficulty!!,
        )

        if (mode.value == Mode.DEFAULT) {
            viewModel.insert(quest)
        } else if (mode.value == Mode.EDIT) {
            quest.id = args.questId
            viewModel.update(quest)
        }
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (validateInput()){
                saveQuest()
                navigateToQuestList()
            }
        }
    }

    private fun navigateToQuestList() {
        NavHostFragment.findNavController(this).navigate(R.id.action_newQuest_to_questList)
        Log.i(TAG, "Going from New Quest to Quest List")
    }

    private fun setupCancelButton() {
        val view = requireView()
        val button = view.findViewById<View>(R.id.CancelButton)

        button.setOnClickListener{
            navigateToQuestList()
        }
    }

    private fun navigateToIconSelect() {
        NavHostFragment.findNavController(this).navigate(R.id.action_newQuest_to_iconSelect)
        Log.i(TAG, "Going from New Quest to Icon Select")
    }

    private fun setupIconSelectButton() {
        val view = requireView()
        val button = view.findViewById<FloatingActionButton>(R.id.IconButton)

        if (args.iconId != INVALID_ICON_ID) {
            viewModel.getIcon(args.iconId).observe(viewLifecycleOwner) { icon ->
                val drawable = convertByteArrayToDrawable(icon.image, resources)
                button.setImageDrawable(drawable)
            }
        }

        button.setOnClickListener{
            navigateToIconSelect()
        }
    }

    private fun setupButtons() {
        setDifficultyButtonListeners()
        setupConfirmButton()
        setupCancelButton()
        setupIconSelectButton()
    }

    private fun selectDefaultDifficulty() {
        setSelectedDifficulty(DEFAULT_DIFFICULTY)
    }

    private fun loadQuest(quest: Quest) {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.NameInput)
        nameInput.setText(quest.name)
        setSelectedDifficulty(quest.difficulty)
    }

    private fun activateEditMode() {
        val view = requireView()
        val pageLabel = view.findViewById<TextView>(R.id.NewQuestLabel)
        pageLabel.text = resources.getString(R.string.edit_quest_label)

        viewModel.getQuest(args.questId).observe(viewLifecycleOwner) { quest ->
            loadQuest(quest)
        }
    }

    private fun setupMode() {
        mode.value = Mode.DEFAULT

        if (args.questId != INVALID_QUEST_ID) {
            mode.value = Mode.EDIT
        }

        mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> Unit
                Mode.EDIT -> activateEditMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On New Quest page")
            setupButtons()
            selectDefaultDifficulty()
            setupMode()
        }
    }

    companion object {
        private const val TAG = "NewQuest"
        private const val INVALID_QUEST_ID = 0
        private const val INVALID_ICON_ID = 0
        private val DEFAULT_DIFFICULTY = Difficulty.EASY
    }
}