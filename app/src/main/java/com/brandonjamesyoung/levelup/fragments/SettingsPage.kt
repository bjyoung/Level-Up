package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.Difficulty as DifficultyCode
import com.brandonjamesyoung.levelup.data.Difficulty
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.utility.SnackbarHelper
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SettingsPage : Fragment(R.layout.settings) {
    private val args: SettingsPageArgs by navArgs()

    private val viewModel: SettingsViewModel by activityViewModels()

    @Inject lateinit var validator: InputValidator

    private fun setupAdvancedSettingsButton() {
        val view = requireView()
        val advancedSettingsButton = view.findViewById<Button>(R.id.AdvancedSettingsButton)

        advancedSettingsButton.setOnClickListener{
            navigateToAdvancedSettings()
        }
    }

    private fun navigateToAdvancedSettings() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_settings_to_advancedSettings)
        Log.i(TAG, "Going from Settings to Advanced Settings")
    }

    private fun navigateToQuestList() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_settings_to_questList)
        Log.i(TAG, "Going from Settings to Quest List")
    }

    private fun navigateToShop() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_settings_to_shop)
        Log.i(TAG, "Going from Settings to Shop")
    }

    private fun navigateToPrevFragment() {
        val prevFragmentIdCopy: Int? = viewModel.prevFragmentId
        viewModel.prevFragmentId = null

        when (prevFragmentIdCopy) {
            R.id.Shop -> navigateToShop()
            else -> navigateToQuestList()
        }
    }

    private fun setupCancelButton() {
        val view = requireView()
        val button = view.findViewById<View>(R.id.CancelButton)

        button.setOnClickListener{
            navigateToPrevFragment()
        }
    }

    private fun difficultySettingsAreValid() : Boolean {
        val difficultyInputIds = listOf(
            R.id.EasyExpInput,
            R.id.EasyRtInput,
            R.id.MediumExpInput,
            R.id.MediumRtInput,
            R.id.HardExpInput,
            R.id.HardRtInput,
            R.id.ExpertExpInput,
            R.id.ExpertRtInput
        )

        val view = requireView()
        var difficultySettingsAreValid = true

        for (id in difficultyInputIds) {
            val editText = view.findViewById<EditText>(id)

            val isValid = validator.isValidNum(
                editText = editText,
                minNumber = -999,
                maxNumber = 9999,
                resources = resources
            )

            if (!isValid) difficultySettingsAreValid = false
        }

        return difficultySettingsAreValid
    }

    private fun acronymIsValid() : Boolean {
        val view = requireView()
        val acronymField : EditText = view.findViewById(R.id.PointsAcronymInput)
        return validator.isValidAcronym(acronymField, resources)
    }

    private fun lvlUpBonusIsValid() : Boolean {
        val view = requireView()
        val lvlUpBonusInput = view.findViewById<EditText>(R.id.LevelUpBonusInput)

        return validator.isValidNum(
            editText = lvlUpBonusInput,
            minNumber = -99,
            maxNumber = 999,
            resources = resources
        )
    }

    private fun isValidInput() : Boolean {
        val difficultySettingsAreValid = difficultySettingsAreValid()
        val acronymIsValid = acronymIsValid()
        val bonusIsValid = lvlUpBonusIsValid()
        return difficultySettingsAreValid && acronymIsValid && bonusIsValid
    }

    private fun saveSettings() {
        val difficultyInputMap = mapOf(
            DifficultyCode.EASY to Pair(R.id.EasyExpInput, R.id.EasyRtInput),
            DifficultyCode.MEDIUM to Pair(R.id.MediumExpInput, R.id.MediumRtInput),
            DifficultyCode.HARD to Pair(R.id.HardExpInput, R.id.HardRtInput),
            DifficultyCode.EXPERT to Pair(R.id.ExpertExpInput, R.id.ExpertRtInput)
        )

        val view = requireView()
        val newDifficulties = mutableListOf<Difficulty>()

        for ((code, inputIdPair) in difficultyInputMap) {
            val expInput : EditText = view.findViewById(inputIdPair.first)
            val rtInput : EditText = view.findViewById(inputIdPair.second)

            val newDifficulty = Difficulty(
                code = code,
                expReward = Integer.parseInt(expInput.text.toString()),
                pointsReward = Integer.parseInt(rtInput.text.toString())
            )

            newDifficulties.add(newDifficulty)
        }

        val pointsAcronymInput = view.findViewById<EditText>(R.id.PointsAcronymInput)
        val levelUpBonusInput = view.findViewById<EditText>(R.id.LevelUpBonusInput)

        val newSettings = Settings(
            pointsAcronym = pointsAcronymInput.text.toString().uppercase(),
            lvlUpBonus = Integer.parseInt(levelUpBonusInput.text.toString())
        )

        viewModel.update(newSettings, newDifficulties)
        Log.i(TAG, "Update settings")
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val confirmButton = view.findViewById<Button>(R.id.ConfirmButton)

        confirmButton.setOnClickListener{
            if (isValidInput()) {
                saveSettings()
                navigateToPrevFragment()
            }
        }
    }

    private fun setupButtons() {
        setupAdvancedSettingsButton()
        setupCancelButton()
        setupConfirmButton()
    }

    private fun updateDifficultyUi(
        difficulties: List<Difficulty?>
    ) = lifecycleScope.launch(Dispatchers.IO) {
        val view = requireView()

        for (difficulty in difficulties) {
            var expInput : EditText?
            var rtInput : EditText?

            when (difficulty?.code) {
                DifficultyCode.EASY -> {
                    expInput = view.findViewById(R.id.EasyExpInput)
                    rtInput = view.findViewById(R.id.EasyRtInput)
                }
                DifficultyCode.MEDIUM -> {
                    expInput = view.findViewById(R.id.MediumExpInput)
                    rtInput = view.findViewById(R.id.MediumRtInput)
                }
                DifficultyCode.HARD -> {
                    expInput = view.findViewById(R.id.HardExpInput)
                    rtInput = view.findViewById(R.id.HardRtInput)
                }
                DifficultyCode.EXPERT -> {
                    expInput = view.findViewById(R.id.ExpertExpInput)
                    rtInput = view.findViewById(R.id.ExpertRtInput)
                }
                else -> {
                    expInput = null
                    rtInput = null
                }
            }

            withContext(Dispatchers.Main) {
                expInput?.setText(difficulty?.expReward.toString())
                rtInput?.setText(difficulty?.pointsReward.toString())
            }
        }
    }

    private suspend fun loadDifficultyData() = lifecycleScope.launch(Dispatchers.IO) {
        val difficulties: List<Difficulty> = viewModel.getDifficulties()
        updateDifficultyUi(difficulties)
    }

    private suspend fun updateSettingsUi(settings: Settings?) {
        if (settings == null) {
            Log.e(TAG, "No settings to load")
            return
        }

        val rtLabelIds = listOf(
            R.id.EasyRtLabel,
            R.id.MediumRtLabel,
            R.id.HardRtLabel,
            R.id.ExpertRtLabel,
            R.id.LevelUpBonusRtLabel
        )

        val view = requireView()

        withContext(Dispatchers.Main) {
            for (id in rtLabelIds) {
                val rtLabel = view.findViewById<TextView>(id)
                rtLabel.text = settings.pointsAcronym
            }

            val acronymInput = view.findViewById<EditText>(R.id.PointsAcronymInput)
            acronymInput.setText(settings.pointsAcronym)
            val lvlUpBonusInput = view.findViewById<EditText>(R.id.LevelUpBonusInput)
            lvlUpBonusInput.setText(settings.lvlUpBonus.toString())
        }
    }

    private fun loadSettings() = lifecycleScope.launch(Dispatchers.IO) {
        val settings = viewModel.getSettings()
        updateSettingsUi(settings)
    }

    private fun updateAcronymLabels(newAcronym: String) {
        val acronymTextViewIds: List<Int> = listOf(
            R.id.EasyRtLabel,
            R.id.MediumRtLabel,
            R.id.HardRtLabel,
            R.id.ExpertRtLabel,
            R.id.LevelUpBonusRtLabel
        )

        val view = requireView()
        val acronymTextViews: List<TextView> = acronymTextViewIds.map { view.findViewById(it) }
        acronymTextViews.forEach { it.text = newAcronym.uppercase() }
    }

    private fun setupPointsAcronym() {
        val view = requireView()
        val acronymInput: EditText = view.findViewById(R.id.PointsAcronymInput)

        acronymInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newAcronym = s.toString()
                if (newAcronym.isBlank() || acronymIsValid()) updateAcronymLabels(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun setupObservables() {
        val view = requireView()

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val confirmButton: View = view.findViewById(R.id.ConfirmButton)
                SnackbarHelper.showSnackbar(it, view, confirmButton)
            }
        }

        setupPointsAcronym()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On Settings page")
            if (args.fragmentId != 0) viewModel.prevFragmentId = args.fragmentId
            setupButtons()
            loadDifficultyData()
            loadSettings()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "SettingsPage"
    }
}