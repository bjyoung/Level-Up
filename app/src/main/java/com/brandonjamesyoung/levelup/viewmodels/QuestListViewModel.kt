package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.shared.Difficulty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val difficultyRepository: DifficultyRepository,
    private val iconRepository: IconRepository
) : ViewModel() {
    val questList: LiveData<List<Quest>> = questRepository.observeAll().asLiveData()
    val player: LiveData<Player> = playerRepository.observe().asLiveData()
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    fun getIcon(id: Int): LiveData<Icon> {
        return iconRepository.observe(id).asLiveData()
    }

    private suspend fun calculateRewards(questDifficulties: List<Difficulty>) : Reward {
        var expEarned = 0
        var pointsEarned = 0
        val difficulties = difficultyRepository.getAll()

        val difficultyMap = difficulties.associateBy { it.code }

        for (difficulty in questDifficulties) {
            val difficultyEntity = difficultyMap[difficulty]

            if (difficultyEntity != null) {
                expEarned += difficultyEntity.expReward
                pointsEarned += difficultyEntity.pointsReward
            } else {
                Log.e(TAG, "Difficulty enum value not found")
            }
        }

        return Reward(expEarned, pointsEarned)
    }

    fun completeQuests(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        val difficulties = questRepository.getDifficulties(ids)
        val reward = calculateRewards(difficulties)
        val player = playerRepository.get()
        val settings = settingsRepository.get()
        val bonusPoints = settings.lvlUpBonus
        player.gainExp(reward.exp, bonusPoints)
        player.gainPoints(reward.points)
        playerRepository.update(player)
        deleteQuests(ids)
        val numQuestsCompleted = ids.count()
        val pointsAcronym = settings.pointsAcronym

        Log.i(TAG, "Player completes $numQuestsCompleted quest(s) " +
                "and earns ${reward.exp} exp and ${reward.points} $pointsAcronym"
        )
    }

    fun deleteQuests(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        questRepository.delete(ids)
        val numDeleted = ids.count()
        Log.i(TAG, "Delete $numDeleted quest(s)")
    }

    companion object {
        private const val TAG = "QuestListViewModel"
    }
}