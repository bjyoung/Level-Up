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
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.viewmodels.IconEditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IconEditor : Fragment(R.layout.icon_editor) {
    private val viewModel: IconEditorViewModel by activityViewModels()

    private fun navigateToIconSelect() {
        findNavController().navigate(R.id.action_iconEditor_to_newIcon)
        Log.i(TAG, "Going from Icon Editor to New Icon")
    }

    private fun setupCancelButton() {
        val view = requireView()
        val cancelButton = view.findViewById<Button>(R.id.CancelButton)

        cancelButton.setOnClickListener {
            navigateToIconSelect()
        }
    }

    private fun setupSaveButton() {
        val view = requireView()
        val saveButton = view.findViewById<Button>(R.id.SaveButton)

        saveButton.setOnClickListener {
            navigateToIconSelect()
        }
    }

    private fun setupButtons() {
        setupCancelButton()
        setupSaveButton()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On New Item page")
            setupButtons()
        }
    }

    companion object {
        private const val TAG = "IconEditor"
    }
}