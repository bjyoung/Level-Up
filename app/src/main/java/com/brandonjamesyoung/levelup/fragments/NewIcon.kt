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
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.NewIconViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewIcon : Fragment(R.layout.new_icon) {
    private val viewModel: NewIconViewModel by activityViewModels()

    @Inject lateinit var validator: InputValidator

    private fun navigateToIconSelect() {
        NavHostFragment.findNavController(this).navigate(R.id.action_newIcon_to_iconSelect)
        Log.i(TAG, "Going from New Icon to Icon Select")
    }

    private fun setupCancelButton() {
        val view = requireView()
        val cancelButton = view.findViewById<Button>(R.id.CancelButton)

        cancelButton.setOnClickListener{
            navigateToIconSelect()
        }
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val confirmButton = view.findViewById<Button>(R.id.ConfirmButton)

        confirmButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun setupButtons() {
        setupCancelButton()
        setupConfirmButton()
    }

    private fun setupObservables() {
        // TODO Can make base fragment observe view model message to reduce repeat code
        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val view = requireView()
                val confirmButton: View = view.findViewById<Button>(R.id.ConfirmButton)
                SnackbarHelper.showSnackbar(it, view, confirmButton)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On New Icon page")
            setupObservables()
            setupButtons()
        }
    }

    companion object {
        private const val TAG = "NewIcon"
    }
}