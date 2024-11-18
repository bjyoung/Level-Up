package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetSettingsViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val settingsRepository: SettingsRepository,
    private val difficultyRepository: DifficultyRepository
) : BaseViewModel() {
    fun resetSettings() = viewModelScope.launch(ioDispatcher) {
        settingsRepository.resetToDefault()
        Log.i(TAG, "Reset settings to default values")
        difficultyRepository.resetToDefault()
        Log.i(TAG, "Reset difficulties to default values")
    }

    companion object {
        private const val TAG = "ResetSettingsViewModel"
    }
}