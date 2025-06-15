package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.ui.IconWorkspaceCreator
import com.brandonjamesyoung.levelup.utility.IconWorkspace
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.viewmodels.IconEditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IconEditor : Fragment(R.layout.icon_editor) {
    private val viewModel: IconEditorViewModel by activityViewModels()

    @Inject lateinit var workspaceCreator: IconWorkspaceCreator

    private fun navigateToIconSelect() {
        findNavController().navigate(R.id.action_iconEditor_to_newIcon)
        Log.i(TAG, "Going from Icon Editor to New Icon")
    }

    private fun setupCancelButton() {
        val view = requireView()
        val cancelButton = view.findViewById<Button>(R.id.CancelButton)

        cancelButton.setOnClickListener {
            viewModel.reset()
            navigateToIconSelect()
        }
    }

    private fun setupSaveButton() {
        val view = requireView()
        val saveButton = view.findViewById<Button>(R.id.SaveButton)

        saveButton.setOnClickListener {
            viewModel.reset()
            navigateToIconSelect()
        }
    }

    private fun setupButtons() {
        setupCancelButton()
        setupSaveButton()
    }

    private fun reloadPixelGrid(
        workspace: IconWorkspace?,
    ) {
        if (workspace == null) return
        val composeView = requireView().findViewById<ComposeView>(R.id.IconEditorComposeView)

        composeView.setContent {
            workspaceCreator.IconWorkspaceView(workspace, viewModel::paintPixel)
        }
    }

    private fun setupWorkspace() {
        viewModel.workspace.observe(viewLifecycleOwner) {
            reloadPixelGrid(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On New Item page")
            setupButtons()
            setupWorkspace()
        }
    }

    companion object {
        private const val TAG = "IconEditor"
    }
}