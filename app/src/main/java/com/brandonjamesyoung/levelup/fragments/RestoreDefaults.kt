package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.utility.InsetHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestoreDefaults : Fragment(R.layout.restore_defaults) {
    private fun navigateToAdvancedSettings() {
        findNavController().navigate(R.id.action_restoreDefaults_to_advancedSettings)
        Log.i(TAG, "Going from Restore Defaults to Advanced Settings")
    }

    private fun navigateToResetIcons() {
        findNavController().navigate(R.id.action_restoreDefaults_to_resetIcons)
        Log.i(TAG, "Going from Restore Defaults to Reset Icons")
    }

    private fun navigateToResetSettings() {
        findNavController().navigate(R.id.action_restoreDefaults_to_resetSettings)
        Log.i(TAG, "Going from Restore Defaults to Reset Settings")
    }

    private fun setupResetIconsButton() {
        val view = requireView()
        val resetIconsButton = view.findViewById<Button>(R.id.ResetIconsButton)

        resetIconsButton.setOnClickListener {
            navigateToResetIcons()
        }
    }

    private fun setupResetSettingsButton() {
        val view = requireView()
        val resetSettingsButton = view.findViewById<Button>(R.id.ResetSettingsButton)

        resetSettingsButton.setOnClickListener {
            navigateToResetSettings()
        }
    }

    private fun setupBackButton() {
        val view = requireView()
        val backButton = view.findViewById<Button>(R.id.RestoreDefaultsBackButton)

        backButton.setOnClickListener {
            navigateToAdvancedSettings()
        }
    }

    private fun setupButtons() {
        setupResetIconsButton()
        setupResetSettingsButton()
        setupBackButton()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On Restore Defaults page")
            setupButtons()
        }
    }

    companion object {
        private const val TAG = "RestoreDefaults"
    }
}