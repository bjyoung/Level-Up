package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.Difficulty
import com.brandonjamesyoung.levelup.data.DifficultyRepository
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.data.SettingsRepository
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val difficultyRepository: DifficultyRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val difficulties: LiveData<List<Difficulty>> = difficultyRepository.observeAll().asLiveData()
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    private suspend fun updateSettings(newSettings: Settings) {
        val currSettings = settingsRepository.get()
        val oldAcronym = currSettings.pointsAcronym
        val newAcronym = newSettings.pointsAcronym

        if (currSettings.pointsAcronym != newSettings.pointsAcronym) {
            Log.i(TAG, "Update points acronym from $oldAcronym to $newAcronym")
        }

        if (currSettings.lvlUpBonus != newSettings.lvlUpBonus) {
            Log.i(TAG, "Update level up bonus" +
                    " from ${currSettings.lvlUpBonus} $oldAcronym" +
                    " to ${newSettings.lvlUpBonus} $newAcronym"
            )
        }

        currSettings.apply {
            pointsAcronym = newSettings.pointsAcronym
            lvlUpBonus = newSettings.lvlUpBonus
        }

        settingsRepository.update(currSettings)
    }

    private suspend fun updateDifficulties(
        newDifficulties: List<Difficulty>,
        oldAcronym: String,
        newAcronym: String
    ) {
        val currDifficulties = difficultyRepository.getAll()

        for (newDifficulty in newDifficulties) {
            val currDifficulty = currDifficulties.find { difficulty ->
                difficulty.code == newDifficulty.code
            }

            if (currDifficulty != null && currDifficulty.expReward != newDifficulty.expReward) {
                Log.i(TAG, "Update ${currDifficulty.code} quests reward" +
                        " from ${currDifficulty.expReward} exp" +
                        " to ${newDifficulty.expReward} exp"
                )
            }

            if (currDifficulty != null
                && currDifficulty.pointsReward != newDifficulty.pointsReward) {
                Log.i(TAG, "Update ${currDifficulty.code} quests reward" +
                        " from ${currDifficulty.pointsReward} $oldAcronym" +
                        " to ${newDifficulty.pointsReward} $newAcronym"
                )
            }

            currDifficulty?.apply {
                expReward = newDifficulty.expReward
                pointsReward = newDifficulty.pointsReward
            }
        }

        difficultyRepository.update(currDifficulties)
    }

    fun update(newSettings: Settings, newDifficulties: List<Difficulty>) = viewModelScope.launch(
        ioDispatcher
    ) {
        updateSettings(newSettings)
        val currSettings = settingsRepository.get()
        val oldAcronym = currSettings.pointsAcronym
        val newAcronym = newSettings.pointsAcronym
        updateDifficulties(newDifficulties, oldAcronym, newAcronym)
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}