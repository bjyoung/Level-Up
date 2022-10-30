package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.shared.Difficulty as DifficultyCode
import com.brandonjamesyoung.levelup.data.Difficulty
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.shared.NavigationHelper
import com.brandonjamesyoung.levelup.shared.ToastHelper.Companion.showToast
import com.brandonjamesyoung.levelup.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

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

    private fun setupConfirmButton() {
        val view = requireView()
        val confirmButton = view.findViewById<Button>(R.id.SettingsConfirmButton)
        val context = requireContext()

        confirmButton.setOnClickListener{
            showToast("Not implemented yet", context)
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