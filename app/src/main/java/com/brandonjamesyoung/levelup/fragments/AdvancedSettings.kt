package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.utility.SnackbarHelper
import com.brandonjamesyoung.levelup.viewmodels.AdvancedSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdvancedSettings : Fragment(R.layout.advanced_settings) {
    private val viewModel: AdvancedSettingsViewModel by activityViewModels()

    private fun setupAboutButton() {
        val view = requireView()
        val aboutButton = view.findViewById<Button>(R.id.AboutButton)

        aboutButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun setupBackupButton() {
        val view = requireView()
        val backupButton = view.findViewById<Button>(R.id.BackupButton)

        backupButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun setupImportButton() {
        val view = requireView()
        val importButton = view.findViewById<Button>(R.id.ImportButton)

        importButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun navigateToSettings() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_advancedSettings_to_settings)
        Log.i(TAG, "Going from Advanced Settings to Settings")
    }

    private fun setupBackButton() {
        val view = requireView()
        val backButton = view.findViewById<Button>(R.id.AdvancedSettingsBackButton)

        backButton.setOnClickListener{
            navigateToSettings()
        }
    }

    private fun setupRestoredDefaultsButton() {
        val view = requireView()
        val restoreDefaultsButton = view.findViewById<Button>(R.id.RestoreDefaultsButton)

        restoreDefaultsButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun setupButtons() {
        setupBackButton()
        setupAboutButton()
        setupBackupButton()
        setupImportButton()
        setupRestoredDefaultsButton()
    }

    private fun setupObservables() {
        val view = requireView()

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val confirmButton: View = view.findViewById(R.id.AdvancedSettingsBackButton)
                SnackbarHelper.showSnackbar(it, view, confirmButton)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On Advanced Settings page")
            setupButtons()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "AdvancedSettings"
    }
}