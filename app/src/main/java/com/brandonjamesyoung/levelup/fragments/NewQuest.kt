package com.brandonjamesyoung.levelup.fragments

import android.graphics.drawable.Drawable
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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.ActiveQuest
import com.brandonjamesyoung.levelup.constants.Difficulty
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.interfaces.Resettable
import com.brandonjamesyoung.levelup.utility.DateLabelManager
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.NewQuestViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class NewQuest : Fragment(R.layout.new_quest), Resettable {
    private val viewModel: NewQuestViewModel by activityViewModels()

    private val args: NewQuestArgs by navArgs()

    @Inject lateinit var validator: InputValidator

    @Inject lateinit var dateLabelManager: DateLabelManager

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
        return validator.isValidQuestName(nameInput, TAG, this)
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

        val questLimitReached: Boolean

        runBlocking {
            questLimitReached = viewModel.questLimitReached()
        }

        if (questLimitReached) {
            saveButton.isEnabled = false
        }
    }

    private fun navigateToQuestList() {
        viewModel.resetPage()
        findNavController().navigate(R.id.action_newQuest_to_questList)
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
        findNavController().navigate(R.id.action_newQuest_to_iconSelect)
        Log.i(TAG, "Going from New Quest to Icon Select")
    }

    private fun getDefaultIcon(): Drawable? {
        val context = requireContext()

        return ResourcesCompat.getDrawable(
            resources,
            R.drawable.question_mark_icon_large,
            context.theme
        )
    }

    private fun setIcon(icon: Icon?) {
        val view = requireView()
        val button = view.findViewById<FloatingActionButton>(R.id.IconButton)
        val drawable = icon?.getDrawable(view.resources) ?: getDefaultIcon()
        button.setImageDrawable(drawable)
    }

    private fun setupIconSelectButton() {
        val view = requireView()
        val button = view.findViewById<FloatingActionButton>(R.id.IconButton)

        if (args.iconId != INVALID_ICON_ID) {
            viewModel.iconId = args.iconId
            viewModel.loadIcon(args.iconId)
        } else if (viewModel.iconId != null) {
            viewModel.loadIcon(viewModel.iconId as Int)
        }

        viewModel.questIcon.observe(viewLifecycleOwner) { icon ->
            setIcon(icon)
        }

        button.setOnClickListener{
            navigateToIconSelect()
        }
    }

    private fun setupButtons() {
        setupConfirmButton()
        setupCancelButton()
    }

    private fun setupDate(dateCreated: Instant?) {
        if (dateCreated == null) return
        viewModel.dateCreated = dateCreated
        val dateCreatedView = requireView().findViewById<TextView>(R.id.DateCreatedLabel)
        dateLabelManager.setupDateCreatedLabel(dateCreated, dateCreatedView)
    }

    private fun fillInFields(quest: ActiveQuest?) {
        if (quest == null) return
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.NameInput)
        nameInput.setText(quest.name)
        setSelectedDifficulty(quest.difficulty)
        setupDate(quest.dateCreated)
    }

    private fun activateDefaultMode() {
        Log.d(TAG, "Changing to Default mode in New Quest")

        val pageLabel = requireView().findViewById<TextView>(R.id.NewQuestLabel)
        pageLabel.text = getString(R.string.new_quest_label)

        // Only need to reset when moving from Quest List to New Quest
        val mustResetIcon = !viewModel.questDataLoaded
                && args.questId == INVALID_QUEST_ID
                && args.iconId == INVALID_ICON_ID

        if (mustResetIcon) {
            viewModel.questIcon.postValue(null)
        }
    }

    private fun activateEditMode() {
        Log.d(TAG, "Changing to Edit mode in New Quest")
        val view = requireView()
        val pageLabel = view.findViewById<TextView>(R.id.NewQuestLabel)
        pageLabel.text = getString(R.string.edit_quest_label)

        if (!viewModel.questDataLoaded) {
            if (viewModel.editQuestId == null) {
                Log.d(TAG, "Edit Quest's quest id is null. Switching to DEFAULT mode.")
                viewModel.switchMode(Mode.DEFAULT)
                return
            }

            viewModel.loadQuestWithIcon(viewModel.editQuestId as Int)

            viewModel.quest.observe(viewLifecycleOwner) { quest ->
                fillInFields(quest)
            }

            viewModel.questDataLoaded = true
        } else {
            setupDate(viewModel.dateCreated)
        }
    }

    // Determines when quest data should be loaded
    // Should only be triggered when navigating from Quest List to New/Edit Quest
    private fun needToLoadQuest() : Boolean {
        return args.questId != INVALID_QUEST_ID
    }

    private fun setupMode() {
        if (needToLoadQuest()) {
            viewModel.switchMode(Mode.EDIT)
            viewModel.editQuestId = args.questId
        } else if (viewModel.editQuestId != INVALID_QUEST_ID) {
            viewModel.switchMode(Mode.EDIT)
        } else {
            viewModel.switchMode(Mode.DEFAULT)
        }

        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.EDIT -> activateEditMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    private fun setupBackNavigation() {
        onBackNavigation(
            viewModel::resetPage,
            requireActivity(),
            viewLifecycleOwner,
            findNavController()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On New Quest page")
            setupInputFields()
            setupButtons()
            setupMode()
            setupBackNavigation()
        }
    }

    companion object {
        private const val TAG = "NewQuest"
        private const val INVALID_QUEST_ID = 0
        private const val INVALID_ICON_ID = 0
    }
}