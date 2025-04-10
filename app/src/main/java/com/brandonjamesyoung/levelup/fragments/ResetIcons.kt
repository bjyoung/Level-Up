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
import com.brandonjamesyoung.levelup.utility.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.viewmodels.ResetIconsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetIcons : Fragment(R.layout.reset_icons){
    private val viewModel: ResetIconsViewModel by activityViewModels()

    private fun navigateToRestoreDefaults() {
        findNavController().navigate(R.id.action_resetIcons_to_restoreDefaults)
        Log.i(TAG, "Going from Reset Icons to Restore Defaults")
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val confirmButton = view.findViewById<Button>(R.id.ConfirmButton)

        confirmButton.setOnClickListener {
            val context = requireContext()
            viewModel.resetIcons(context)
            viewModel.showSnackbar("Icons reset to default")
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

    private fun setupSnackbar() {
        val view = requireView()

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val cancelButton: View = view.findViewById(R.id.CancelButton)
                showSnackbar(it, view, cancelButton)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On Reset Icons page")
            setupButtons()
            setupSnackbar()
        }
    }

    companion object {
        private const val TAG = "ResetIcons"
    }
}