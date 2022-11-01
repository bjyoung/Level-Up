package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.shared.Difficulty as DifficultyCode
import com.brandonjamesyoung.levelup.data.Difficulty
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.shared.NavigationHelper
import com.brandonjamesyoung.levelup.shared.ToastHelper.Companion.showToast
import com.brandonjamesyoung.levelup.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

const val MAX_ACRONYM_LENGTH = 3
val ACRONYM_VALIDATION_REGEX = Regex("^[a-zA-Z]+$")

@AndroidEntryPoint
class SettingsPage : Fragment(R.layout.settings) {
    private val viewModel: SettingsViewModel by activityViewModels()

    private fun setupBackupButton() {
        val view = requireView()
        val backupButton = view.findViewById<Button>(R.id.BackupButton)
        val context = requireContext()

        backupButton.setOnClickListener{
            showToast("Not implemented yet", context)
        }
    }

    private fun setupCancelButton() {
        NavigationHelper.addNavigationToView(
            this,
            requireView(),
            R.id.SettingsCancelButton,
            R.id.action_settings_to_questList
        )
    }

    private fun isNumber(str : String) : Boolean {
        return str.toIntOrNull() != null
    }

    private fun validateNumInput(
        editText : EditText,
        minNumber : Int? = null,
        maxNumber : Int? = null,
    ) : Boolean {
        val textInput = editText.text.toString()

        if (textInput.isBlank()) {
            editText.error = resources.getString(R.string.not_a_number_error)
            return false
        }

        if (!isNumber(textInput)) {
            editText.error = resources.getString(R.string.not_a_number_error)
            return false
        }

        val numInput = textInput.toInt()

        if (minNumber != null && maxNumber != null && numInput !in minNumber..maxNumber) {
            editText.error = resources.getString(
                R.string.num_out_of_range_error,
                minNumber,
                maxNumber
            )

            return false
        }

        if (minNumber != null && maxNumber == null && numInput < minNumber) {
            editText.error = resources.getString(R.string.num_too_small_error, minNumber)
            return false
        }

        if (minNumber == null && maxNumber != null && numInput > maxNumber) {
            editText.error = resources.getString(R.string.num_too_large_error, maxNumber)
            return false
        }

        return true
    }

    private fun validateDifficultySettings() : Boolean {
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
            val isValid = validateNumInput(editText, 0, 9999)
            if (!isValid) difficultySettingsAreValid = false
        }

        return difficultySettingsAreValid
    }

    private fun validateAcronym() : Boolean {
        val view = requireView()
        val editText : EditText = view.findViewById(R.id.PointsAcronymInput)
        val textInput = editText.text.toString()

        if (textInput.isBlank()) {
            editText.error = resources.getString(R.string.no_acronym_error)
            return false
        }

        val hasOnlyAlphabet = ACRONYM_VALIDATION_REGEX.matches(textInput)

        if (!hasOnlyAlphabet) {
            editText.error = resources.getString(R.string.only_alpha_allowed_error)
            return false
        }

        if (textInput.length > MAX_ACRONYM_LENGTH) {
            editText.error = resources.getString(R.string.three_characters_limit_error)
            return false
        }

        return true
    }

    private fun validateLvlUpBonus() : Boolean {
        val view = requireView()
        val lvlUpBonusInput = view.findViewById<EditText>(R.id.LevelUpBonusInput)
        return validateNumInput(lvlUpBonusInput, 0, 999)
    }

    private fun validateInput() : Boolean {
        val difficultySettingsAreValid = validateDifficultySettings()
        val acronymIsValid = validateAcronym()
        val bonusIsValid = validateLvlUpBonus()
        return difficultySettingsAreValid && acronymIsValid && bonusIsValid
    }

    private fun saveSettings() {
        val view = requireView()

        val difficultyInputMap = listOf(
            Triple(DifficultyCode.EASY, R.id.EasyExpInput, R.id.EasyRtInput),
            Triple(DifficultyCode.MEDIUM, R.id.MediumExpInput, R.id.MediumRtInput),
            Triple(DifficultyCode.HARD, R.id.HardExpInput, R.id.HardRtInput),
            Triple(DifficultyCode.EXPERT, R.id.ExpertExpInput, R.id.ExpertRtInput)
        )

        val newDifficulties = mutableListOf<Difficulty>()

        for (triple in difficultyInputMap) {
            val expInput : EditText = view.findViewById(triple.second)
            val rtInput : EditText = view.findViewById(triple.third)

            val newDifficulty = Difficulty(
                code = triple.first,
                expReward = Integer.parseInt(expInput.text.toString()),
                rtReward = Integer.parseInt(rtInput.text.toString())
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
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val confirmButton = view.findViewById<Button>(R.id.SettingsConfirmButton)

        confirmButton.setOnClickListener{
            if (validateInput()) {
                saveSettings()
                // TODO Settings should send player back to the page they came from
                //  Default to questList page if no prev page is found
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_settings_to_questList)
            }
        }
    }

    private fun setupButtons() {
        setupBackupButton()
        setupCancelButton()
        setupConfirmButton()
    }

    private fun updateDifficultyUi(difficulties: List<Difficulty?>) {
        val view = this.requireView()

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

            expInput?.setText(difficulty?.expReward.toString())
            rtInput?.setText(difficulty?.rtReward.toString())
        }
    }

    private fun updateSettingsUi(settings: Settings?) {
        if (settings == null) {
            Log.e("SettingsPage.updateUi", "No settings to load")
            return
        }

        val view = this.requireView()

        val rtLabelIds = listOf(
            R.id.EasyRtLabel,
            R.id.MediumRtLabel,
            R.id.HardRtLabel,
            R.id.ExpertRtLabel,
            R.id.LevelUpBonusRtLabel
        )

        for (id in rtLabelIds) {
            val rtLabel = view.findViewById<TextView>(id)
            rtLabel.text = settings.pointsAcronym
        }

        val acronymInput = view.findViewById<EditText>(R.id.PointsAcronymInput)
        acronymInput.setText(settings.pointsAcronym)
        val lvlUpBonusInput = view.findViewById<EditText>(R.id.LevelUpBonusInput)
        lvlUpBonusInput.setText(settings.lvlUpBonus.toString())
    }

    private fun setupObservables() {
        viewModel.difficulties.observe(viewLifecycleOwner) { difficulties ->
            updateDifficultyUi(difficulties)
        }

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            updateSettingsUi(settings)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupObservables()
    }
}