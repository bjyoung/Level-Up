package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IconSelect : Fragment(R.layout.icon_select) {
    private fun navigateToQuestList() {
        NavHostFragment.findNavController(this).navigate(R.id.action_iconSelect_to_newQuest)
        Log.i(TAG, "Going from Icon Select to Quest List")
    }

    private fun setupBackButton() {
        val view = requireView()
        val button = view.findViewById<View>(R.id.BackButton)

        button.setOnClickListener{
            navigateToQuestList()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On Icon Select page")
            setupBackButton()
        }
    }

    companion object {
        private const val TAG = "Icon Select"
    }
}