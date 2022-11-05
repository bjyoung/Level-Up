package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.Difficulty
import com.brandonjamesyoung.levelup.data.DifficultyRepository
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val difficultyRepository: DifficultyRepository,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val difficulties: LiveData<List<Difficulty>> = difficultyRepository.observeAll().asLiveData()
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    fun update(newSettings: Settings, newDifficulties: List<Difficulty>) = viewModelScope.launch(
        ioDispatcher
    ) {
        val currSettings = settingsRepository.get()

        currSettings.apply {
            pointsAcronym = newSettings.pointsAcronym
            lvlUpBonus = newSettings.lvlUpBonus
        }

        settingsRepository.update(currSettings)
        val currDifficulties = difficultyRepository.getAll()

        for (newDifficulty in newDifficulties) {
            currDifficulties.find { difficulty ->
                difficulty.code == newDifficulty.code
            }?.apply {
                expReward = newDifficulty.expReward
                rtReward = newDifficulty.rtReward
            }
        }

        difficultyRepository.update(currDifficulties)
    }
}