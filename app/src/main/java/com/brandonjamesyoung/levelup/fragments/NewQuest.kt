package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.utility.TypeConverter.Companion.convertByteArrayToDrawable
import com.brandonjamesyoung.levelup.constants.Difficulty
import com.brandonjamesyoung.levelup.utility.IconHelper.Companion.getDefaultIcon
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.utility.TypeConverter
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.NewQuestViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class NewQuest : Fragment(R.layout.new_quest) {
    private val viewModel: NewQuestViewModel by activityViewModels()

    private val args: NewQuestArgs by navArgs()

    @Inject lateinit var validator: InputValidator

    private val buttonIdToDifficultyMap = mapOf(
        R.id.EasyButton to Difficulty.EASY,
        R.id.MediumButton to Difficulty.MEDIUM,
        R.id.HardButton to Difficulty.HARD,
        R.id.ExpertButton to Difficulty.EXPERT
    )

    private val difficultyToButtonIdMap = buttonIdToDifficultyMap.entries
        .associateBy({ it.value }) { it.key }

    private fun setupNameInput() {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.NameInput)

        if (viewModel.name != null) {
            nameInput.setText(viewModel.name)
        }

        nameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name = s.toString()
                viewModel.name = name.ifEmpty { null }
            }
        })
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
        viewModel.selectedDifficulty = difficulty
        moveDifficultySelectBox(difficulty)
        Log.i(TAG, "Set quest difficulty to $difficulty")
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

    private fun setupDifficultyInput() {
        setDifficultyButtonListeners()
        setSelectedDifficulty(viewModel.selectedDifficulty)
    }

    private fun setupInputFields() {
        setupNameInput()
        setupDifficultyInput()
        setupIconSelectButton()
    }

    private fun validateInput() : Boolean {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.NameInput)

        if (!validator.validateQuestName(nameInput, TAG, this)) {
            return false
        }

        // TODO Check if icon is a valid id
        // val iconView = view.findViewById<FloatingActionButton>(R.id.IconButton)

        return true
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (validateInput()){
                viewModel.saveQuest()
                navigateToQuestList()
            }
        }
    }

    private fun navigateToQuestList() {
        viewModel.resetPage()
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

    // TODO Extract this duplicate method used across QuestList, New Quest, QuestHistory
    private fun changeIcon(iconId : Int?) {
        viewModel.iconId = iconId
        val view = requireView()
        val button = view.findViewById<FloatingActionButton>(R.id.IconButton)

        if (iconId == null) {
            val context = requireContext()
            val drawable = getDefaultIcon(context)
            button.setImageDrawable(drawable)
            return
        }

        lifecycleScope.launch(Dispatchers.Default) {
            val icon = withContext(Dispatchers.IO) {
                viewModel.getIcon(iconId)
            }

            val drawable = convertByteArrayToDrawable(icon.image, resources)
            button.setImageDrawable(drawable)
        }
    }

    private fun setupIconSelectButton() {
        val view = requireView()
        val button = view.findViewById<FloatingActionButton>(R.id.IconButton)

        if (args.iconId != INVALID_ICON_ID) {
            viewModel.iconId = args.iconId
            changeIcon(args.iconId)
        } else if (viewModel.iconId != null) {
            changeIcon(viewModel.iconId as Int)
        }

        button.setOnClickListener{
            navigateToIconSelect()
        }
    }

    private fun setupButtons() {
        setupConfirmButton()
        setupCancelButton()
    }

    private fun setupDateCreatedLabel(dateCreated: Instant?) {
        if (dateCreated == null) return
        viewModel.dateCreated = dateCreated
        val dateCreatedView = requireView().findViewById<TextView>(R.id.DateCreatedLabel)
        val dateString = TypeConverter.convertInstantToString(dateCreated)
        dateCreatedView.text = getString(R.string.date_created_label, dateString)
        dateCreatedView.visibility = View.VISIBLE
    }

    private fun loadQuest(quest: Quest) {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.NameInput)
        nameInput.setText(quest.name)
        setSelectedDifficulty(quest.difficulty)
        changeIcon(quest.iconId)
        setupDateCreatedLabel(quest.dateCreated)
    }

    private fun activateEditMode() {
        val view = requireView()
        val pageLabel = view.findViewById<TextView>(R.id.NewQuestLabel)
        pageLabel.text = getString(R.string.edit_quest_label)

        if (!viewModel.questDataLoaded) {
            viewModel.getQuest(viewModel.editQuestId as Int).observe(viewLifecycleOwner) { quest ->
                loadQuest(quest)
                viewModel.questDataLoaded = true
            }
        } else {
            setupDateCreatedLabel(viewModel.dateCreated)
        }
    }

    private fun setupMode() {
        if (needToLoadQuest()) {
            viewModel.switchToEditMode()
            viewModel.editQuestId = args.questId
        }

        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> Unit
                Mode.EDIT -> activateEditMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    private fun needToLoadQuest() : Boolean {
        return args.questId != INVALID_QUEST_ID
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On New Quest page")
            setupInputFields()
            setupButtons()
            setupMode()
        }
    }

    companion object {
        private const val TAG = "NewQuest"
        private const val INVALID_QUEST_ID = 0
        private const val INVALID_ICON_ID = 0
    }
}