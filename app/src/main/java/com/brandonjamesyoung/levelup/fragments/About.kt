package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.brandonjamesyoung.levelup.BuildConfig
import com.brandonjamesyoung.levelup.utility.InsetHandler

@AndroidEntryPoint
class About : Fragment(R.layout.about) {
    private fun navigateToAdvancedSettings() {
        findNavController().navigate(R.id.action_about_to_advancedSettings)
        Log.i(TAG, "Going from About to Advanced Settings")
    }

    private fun setupBackButton() {
        val view = requireView()
        val backButton = view.findViewById<Button>(R.id.BackButton)

        backButton.setOnClickListener{
            navigateToAdvancedSettings()
        }
    }

    private fun loadVersion() {
        val view = requireView()
        val versionText = view.findViewById<TextView>(R.id.VersionDescription)
        val currVersion = BuildConfig.VERSION_NAME
        versionText.text = getString(R.string.app_version, currVersion)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On $TAG page")
            setupBackButton()
            loadVersion()
        }
    }

    companion object {
        private const val TAG = "About"
    }
}