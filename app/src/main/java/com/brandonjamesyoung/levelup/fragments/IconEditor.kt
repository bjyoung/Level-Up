package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.DEFAULT_GRID_SIZE
import com.brandonjamesyoung.levelup.constants.MAX_HEIGHT
import com.brandonjamesyoung.levelup.constants.MAX_WIDTH
import com.brandonjamesyoung.levelup.ui.IconWorkspaceCreator
import com.brandonjamesyoung.levelup.utility.IconWorkspace
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.IconEditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IconEditor : Fragment(R.layout.icon_editor) {
    private val viewModel: IconEditorViewModel by activityViewModels()

    @Inject lateinit var workspaceCreator: IconWorkspaceCreator

    @Inject lateinit var validator: InputValidator

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

    private fun resizeIsValid() : Boolean {
        val view = requireView()
        val widthField: EditText = view.findViewById(R.id.WidthInput)
        val heightField: EditText = view.findViewById(R.id.HeightInput)
        return dimensionIsValid(widthField, MAX_WIDTH) && dimensionIsValid(heightField, MAX_HEIGHT)
    }

    private fun resizeGrid() {
        if (resizeIsValid() == false) {
            Log.e(TAG, "Width or height are invalid so the grid can't be resized")
            return
        }

        val view = requireView()
        val widthField: EditText = view.findViewById(R.id.WidthInput)
        val width: Int = widthField.text.toString().toInt()
        val heightField: EditText = view.findViewById(R.id.HeightInput)
        val height: Int = heightField.text.toString().toInt()
        viewModel.resizeGrid(width, height)
    }

    private fun setupResizeButton() {
        val view = requireView()
        val resizeButton = view.findViewById<Button>(R.id.ResizeButton)

        resizeButton.setOnClickListener {
            resizeGrid()
        }
    }

    private fun setupButtons() {
        setupCancelButton()
        setupSaveButton()
        setupResizeButton()
    }

    private fun dimensionIsValid(dimensionInput: EditText, maxValue: Int) : Boolean {
        return validator.isValidNum(
            editText = dimensionInput,
            minNumber = 0,
            maxNumber = maxValue,
            resources = resources
        )
    }

    private fun onWidthChange(newWidthInput: String, widthField: EditText) {
        if (newWidthInput.isBlank() || dimensionIsValid(widthField, MAX_WIDTH)) {
            val newWidth = newWidthInput.toIntOrNull()
            viewModel.width = newWidth
        }
    }

    private fun setupWidthField() {
        val view = requireView()
        val widthField = view.findViewById<EditText>(R.id.WidthInput)

        // Reload data from view model
        if (viewModel.width != null) {
            val widthText: String = if (viewModel.width != null) viewModel.width.toString() else ""
            widthField.setText(widthText)
        }

        // If no value is set yet, set default
        if (widthField.text.isNullOrBlank()) {
            widthField.setText(DEFAULT_GRID_SIZE.toString())
        }

        widthField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) { }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                onWidthChange(s.toString(), widthField)
            }
        })
    }

    private fun onHeightChange(newHeightInput: String, heightField: EditText) {
        if (newHeightInput.isBlank() || dimensionIsValid(heightField, MAX_HEIGHT)) {
            val newHeight: Int? = newHeightInput.toIntOrNull()
            viewModel.height = newHeight
        }
    }

    private fun setupHeightField() {
        val view = requireView()
        val heightField = view.findViewById<EditText>(R.id.HeightInput)

        // Reload data from view model
        if (viewModel.height != null) {
            val heightText: String = if (viewModel.height != null) {
                viewModel.height.toString()
            } else {
                ""
            }

            heightField.setText(heightText)
        }

        // If no value is set yet, set default
        if (heightField.text.isNullOrBlank()) {
            heightField.setText(DEFAULT_GRID_SIZE.toString())
        }

        heightField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) { }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                onHeightChange(s.toString(), heightField)
            }
        })
    }

    private fun setupDimensionFields() {
        setupWidthField()
        setupHeightField()
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
            setupDimensionFields()
            setupWorkspace()
        }
    }

    companion object {
        private const val TAG = "IconEditor"
    }
}