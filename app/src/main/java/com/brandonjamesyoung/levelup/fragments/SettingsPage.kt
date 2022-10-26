package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.shared.NavigationHelper
import com.brandonjamesyoung.levelup.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsPage : Fragment(R.layout.settings) {
    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationHelper.addNavigationToView(
            this,
            view,
            R.id.SettingsCancelButton,
            R.id.action_settings_to_questList
        )
    }
}