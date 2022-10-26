package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    fun update(settings: Settings) = viewModelScope.launch {
        settingsRepository.update(settings)
    }
}