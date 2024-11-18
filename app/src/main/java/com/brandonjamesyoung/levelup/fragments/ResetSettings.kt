package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.viewmodels.ResetSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetSettings : Fragment(R.layout.reset_settings){
    private val viewModel: ResetSettingsViewModel by activityViewModels()

    private fun navigateToRestoreDefaults() {
        findNavController().navigate(R.id.action_resetSettings_to_restoreDefaults)
        Log.i(TAG, "Going from Reset Settings to Restore Defaults")
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val confirmButton = view.findViewById<Button>(R.id.ConfirmButton)

        confirmButton.setOnClickListener {
            viewModel.resetSettings()
            navigateToRestoreDefaults()
        }
    }

    private fun setupCancelButton() {
        val view = requireView()
        val cancelButton = view.findViewById<Button>(R.id.CancelButton)

        cancelButton.setOnClickListener {
            navigateToRestoreDefaults()
        }
    }

    private fun setupButtons() {
        setupConfirmButton()
        setupCancelButton()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On Reset Settings page")
            setupButtons()
        }
    }

    companion object {
        private const val TAG = "ResetSettings"
    }
}