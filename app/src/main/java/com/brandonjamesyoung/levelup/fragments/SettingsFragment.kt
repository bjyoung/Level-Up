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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Difficulty
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.interfaces.Resettable
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.utility.SnackbarHelper
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.brandonjamesyoung.levelup.constants.Difficulty as DifficultyCode

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings), Resettable {
    private val args: SettingsFragmentArgs by navArgs()

    private val viewModel: SettingsViewModel by activityViewModels()

    @Inject lateinit var validator: InputValidator

    private val difficultyInputMap = mapOf(
        DifficultyCode.EASY to Pair(R.id.EasyExpInput, R.id.EasyPointsInput),
        DifficultyCode.MEDIUM to Pair(R.id.MediumExpInput, R.id.MediumPointsInput),
        DifficultyCode.HARD to Pair(R.id.HardExpInput, R.id.HardPointsInput),
        DifficultyCode.EXPERT to Pair(R.id.ExpertExpInput, R.id.ExpertPointsInput)
    )

    private fun navigateToAdvancedSettings() {
        findNavController().navigate(R.id.action_settings_to_advancedSettings)
        Log.i(TAG, "Going from Settings to Advanced Settings")
    }

    private fun setupAdvancedSettingsButton() {
        val view = requireView()
        val advancedSettingsButton = view.findViewById<Button>(R.id.AdvancedSettingsButton)

        advancedSettingsButton.setOnClickListener{
            viewModel.clearUserInput()
            navigateToAdvancedSettings()
        }
    }

    private fun navigateToQuestList() {
        findNavController().navigate(R.id.action_settings_to_questList)
        Log.i(TAG, "Going from Settings to Quest List")
    }

    private fun navigateToShop() {
        findNavController().navigate(R.id.action_settings_to_shop)
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
            viewModel.clearUserInput()
            navigateToPrevFragment()
        }
    }

    private fun difficultySettingsAreValid() : Boolean {
        val difficultyInputIds = listOf(
            R.id.EasyExpInput,
            R.id.EasyPointsInput,
            R.id.MediumExpInput,
            R.id.MediumPointsInput,
            R.id.HardExpInput,
            R.id.HardPointsInput,
            R.id.ExpertExpInput,
            R.id.ExpertPointsInput
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

    private fun onConfirmTrigger() {
        if (!isValidInput()) return

        try {
            saveSettings()
            viewModel.clearUserInput()
            navigateToPrevFragment()
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
        }
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val confirmButton = view.findViewById<Button>(R.id.ConfirmButton)

        confirmButton.setOnClickListener{
            onConfirmTrigger()
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
            if (difficulty == null) continue
            val difficultyViewIdPair = difficultyInputMap[difficulty.code] ?: continue
            val expInputId = difficultyViewIdPair.first
            val pointsInputId = difficultyViewIdPair.second
            val expInput: EditText? = view.findViewById(expInputId)
            val rtInput: EditText? = view.findViewById(pointsInputId)

            withContext(Dispatchers.Main) {
                val userInput = viewModel.settingsInput.difficultySettingsMap[difficulty.code]
                val defaultExp = difficulty.expReward.toString()
                val userExpInput = userInput?.expReward
                val expFieldValue = userExpInput ?: defaultExp
                expInput?.setText(expFieldValue)

                val userPointsInput = userInput?.pointsReward
                val defaultPoints = difficulty.pointsReward.toString()
                val pointsFieldValue = userPointsInput ?: defaultPoints
                rtInput?.setText(pointsFieldValue)
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

        val pointLabelIds = listOf(
            R.id.EasyPointsLabel,
            R.id.MediumPointsLabel,
            R.id.HardPointsLabel,
            R.id.ExpertPointsLabel
        )

        val view = requireView()

        withContext(Dispatchers.Main) {
            for (id in pointLabelIds) {
                val rtLabel = view.findViewById<TextView>(id)
                rtLabel.text = settings.pointsAcronym
            }

            val acronymInput = view.findViewById<EditText>(R.id.PointsAcronymInput)
            val defaultAcronym = settings.pointsAcronym
            val userAcronymInput = viewModel.settingsInput.pointsAcronym
            val acronymFieldValue = userAcronymInput ?: defaultAcronym
            acronymInput.setText(acronymFieldValue)

            val lvlUpBonusInput = view.findViewById<EditText>(R.id.LevelUpBonusInput)
            val defaultLvlBonus = settings.lvlUpBonus.toString()
            val userLvlBonusInput = viewModel.settingsInput.lvlUpBonus
            val lvlBonusFieldValue = userLvlBonusInput ?: defaultLvlBonus
            lvlUpBonusInput.setText(lvlBonusFieldValue)
        }
    }

    private fun loadSettings() = lifecycleScope.launch(Dispatchers.IO) {
        val settings = viewModel.getSettings()
        updateSettingsUi(settings)
    }

    private fun updateAcronymLabels(newAcronym: String) {
        val acronymTextViewIds: List<Int> = listOf(
            R.id.EasyPointsLabel,
            R.id.MediumPointsLabel,
            R.id.HardPointsLabel,
            R.id.ExpertPointsLabel
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

    private fun addTextChangedListener(
        inputView: EditText,
        onTextChange: (s: CharSequence ) -> Unit
    ) {
        inputView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                onTextChange(s)
            }
        })
    }

    private fun expOnTextChangeGenerator(difficultyCode: DifficultyCode): (s:CharSequence) -> Unit {
        return { s: CharSequence ->
            val expInput = s.toString()
            val difficultySetting = viewModel.settingsInput.difficultySettingsMap[difficultyCode]
            difficultySetting?.expReward = expInput
        }
    }

    private fun pointsOnTextChangeGenerator(
        difficultyCode: DifficultyCode
    ): (s:CharSequence) -> Unit {
        return { s: CharSequence ->
            val pointsInput = s.toString()
            val difficultySetting = viewModel.settingsInput.difficultySettingsMap[difficultyCode]
            difficultySetting?.pointsReward = pointsInput
        }
    }

    private fun acronymOnTextChange(s: CharSequence) {
        val acronymInput = s.toString()
        viewModel.settingsInput.pointsAcronym = acronymInput
    }

    private fun lvlBonusOnTextChange(s: CharSequence) {
        val bonusInput = s.toString()
        viewModel.settingsInput.lvlUpBonus = bonusInput
    }

    private fun setupInputFieldListeners() {
        val view = requireView()

        for (difficultyCode in difficultyInputMap.keys) {
            val difficultyViewIdPair = difficultyInputMap[difficultyCode] ?: continue

            val expInputId = difficultyViewIdPair.first
            val expInput = view.findViewById<EditText>(expInputId)
            val expOnTextChange = expOnTextChangeGenerator(difficultyCode)
            addTextChangedListener(expInput, expOnTextChange)

            val pointsInputId = difficultyViewIdPair.second
            val pointsInput = view.findViewById<EditText>(pointsInputId)
            val pointsOnTextChange = pointsOnTextChangeGenerator(difficultyCode)
            addTextChangedListener(pointsInput, pointsOnTextChange)
        }

        val acronymInput = view.findViewById<EditText>(R.id.PointsAcronymInput)
        addTextChangedListener(acronymInput, ::acronymOnTextChange)

        val lvlBonusInput = view.findViewById<EditText>(R.id.LevelUpBonusInput)
        addTextChangedListener(lvlBonusInput, ::lvlBonusOnTextChange)
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
        setupInputFieldListeners()
    }

    private fun setupBackNavigation() {
        onBackNavigation(
            viewModel::clearUserInput,
            requireActivity(),
            viewLifecycleOwner,
            findNavController()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On Settings page")
            if (args.fragmentId != 0) viewModel.prevFragmentId = args.fragmentId
            setupButtons()
            loadDifficultyData()
            loadSettings()
            setupObservables()
            setupBackNavigation()
        }
    }

    companion object {
        private const val TAG = "SettingsPage"
    }
}