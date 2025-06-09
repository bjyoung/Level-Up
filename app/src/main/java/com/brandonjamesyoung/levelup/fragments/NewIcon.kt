package com.brandonjamesyoung.levelup.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.interfaces.Resettable
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.utility.SnackbarHelper
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.NewIconViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class NewIcon : Fragment(R.layout.new_icon), Resettable {
    private val viewModel: NewIconViewModel by activityViewModels()

    private val args: NewIconArgs by navArgs()

    @Inject lateinit var validator: InputValidator

    private fun setupNameInput() {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.IconNameInput)

        if (viewModel.name != null) {
            nameInput.setText(viewModel.name)
        }

        nameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name = s.toString()
                viewModel.name = name.ifEmpty { null }
            }
        })
    }

    private fun navigateToIconSelect() {
        viewModel.resetPage()
        findNavController().navigate(R.id.action_newIcon_to_iconSelect)
        Log.i(TAG, "Going from New Icon to Icon Select")
    }

    private fun setupCancelButton() {
        val view = requireView()
        val cancelButton = view.findViewById<Button>(R.id.CancelButton)

        cancelButton.setOnClickListener{
            viewModel.resetPage()
            navigateToIconSelect()
        }
    }

    private fun validateInput() : Boolean {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.IconNameInput)
        return validator.isValidIconName(nameInput, TAG, this)
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (validateInput()){
                viewModel.saveIcon()
                navigateToIconSelect()
            }
        }
    }

    private fun navigateToIconEditor() {
        findNavController().navigate(R.id.action_newIcon_to_iconEditor)
        Log.i(TAG, "Going from New Icon to Icon Editor")
    }

    private fun setupIconEditorButton() {
        val view = requireView()
        val iconEditorButton = view.findViewById<FloatingActionButton>(R.id.IconPreviewButton)

        iconEditorButton.setOnClickListener {
            navigateToIconEditor()
        }
    }

    private fun setupIconPreviewButton() {
        val view = requireView()
        val button = view.findViewById<FloatingActionButton>(R.id.IconPreviewButton)

        if (args.iconId != INVALID_ICON_ID) {
            viewModel.editIconId = args.iconId
            viewModel.loadIcon(args.iconId)
        } else if (viewModel.editIconId != null) {
            viewModel.loadIcon(viewModel.editIconId as Int)
        }

        viewModel.loadedIcon.observe(viewLifecycleOwner) { icon ->
            setDisplayedIcon(icon)
        }

        button.setOnClickListener{
            navigateToIconSelect()
        }
    }

    private fun setupButtons() {
        setupCancelButton()
        setupConfirmButton()
        setupIconEditorButton()
        setupIconPreviewButton()
    }

    private fun setupToasts() {
        // TODO Can make base fragment observe view model message to reduce repeat code
        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val view = requireView()
                val confirmButton: View = view.findViewById<Button>(R.id.ConfirmButton)
                SnackbarHelper.showSnackbar(it, view, confirmButton)
            }
        }
    }

    // Should only be triggered when navigating from Icon Select to New/Edit Icon
    private fun needToLoadIcon() : Boolean {
        return args.iconId != INVALID_ICON_ID
    }

    private fun activateDefaultMode() {
        Log.d(TAG, "Changing to Default mode in New Icon")

        val pageLabel = requireView().findViewById<TextView>(R.id.NewIconLabel)
        pageLabel.text = getString(R.string.new_icon_label)

        // Only need to reset when moving from Icon Select to New Icon
        val mustResetIcon = !viewModel.iconDataLoaded && args.iconId == INVALID_ICON_ID

        if (mustResetIcon) {
            viewModel.loadedIcon.postValue(null)
        }
    }

    private fun getDefaultIcon(): Drawable? {
        val context = requireContext()

        return ResourcesCompat.getDrawable(
            resources,
            R.drawable.question_mark_icon_large,
            context.theme
        )
    }

    private fun setDisplayedIcon(icon: Icon?) {
        val view = requireView()
        val button = view.findViewById<FloatingActionButton>(R.id.IconPreviewButton)
        val drawable = icon?.getDrawable(view.resources) ?: getDefaultIcon()
        button.setImageDrawable(drawable)
    }

    private fun fillInFields(icon: Icon?) {
        if (icon == null) return
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.IconNameInput)
        nameInput.setText(icon.name)
        setDisplayedIcon(icon)
    }

    private fun activateEditMode() {
        Log.d(TAG, "Changing to Edit mode in New Icon")

        val pageLabel = requireView().findViewById<TextView>(R.id.NewIconLabel)
        pageLabel.text = getString(R.string.edit_icon_label)

        if (viewModel.iconDataLoaded) {
            return
        }

        if (viewModel.editIconId == null) {
            Log.d(TAG, "Edit Icon's icon id is null. Switching to DEFAULT mode.")
            viewModel.switchMode(Mode.DEFAULT)
            return
        }

        viewModel.loadIcon(viewModel.editIconId as Int)

        viewModel.loadedIcon.observe(viewLifecycleOwner) { icon ->
            fillInFields(icon)
        }

        viewModel.iconDataLoaded = true
    }

    private fun setupMode() {
        if (needToLoadIcon()) {
            viewModel.switchMode(Mode.EDIT)
            viewModel.editIconId = args.iconId
        } else if (viewModel.editIconId != INVALID_ICON_ID) {
            viewModel.switchMode(Mode.EDIT)
        } else {
            viewModel.switchMode(Mode.DEFAULT)
        }

        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.EDIT -> activateEditMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    private fun setupBackNavigation() {
        onBackNavigation(
            viewModel::resetPage,
            requireActivity(),
            viewLifecycleOwner,
            findNavController()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On New Icon page")
            setupButtons()
            setupNameInput()
            setupToasts()
            setupMode()
            setupBackNavigation()
        }
    }

    companion object {
        private const val TAG = "NewIcon"
        private const val INVALID_ICON_ID = 0
    }
}