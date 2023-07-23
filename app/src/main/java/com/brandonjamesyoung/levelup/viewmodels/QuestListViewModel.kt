package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.interfaces.IconReader
import com.brandonjamesyoung.levelup.constants.Difficulty
import com.brandonjamesyoung.levelup.constants.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val difficultyRepository: DifficultyRepository,
    private val iconRepository: IconRepository
) : BaseViewModel(), IconReader {
    val questList: LiveData<List<Quest>> = questRepository.observeAll().asLiveData()

    val player: LiveData<Player> = playerRepository.observe().asLiveData()

    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    private var _mode: MutableLiveData<Mode> = MutableLiveData<Mode>(Mode.DEFAULT)

    val mode: LiveData<Mode>
        get() = _mode

    override suspend fun getIcon(id: Int): Icon = withContext(ioDispatcher){
        iconRepository.get(id)
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

    private fun getQuestCompleteMessage(
        willLevelUp: Boolean,
        reward: Reward,
        pointsAcronym : String
    ) : String {
        return if (willLevelUp) {
            "You levelled up and earned ${reward.exp} exp and ${reward.points} $pointsAcronym!"
        } else {
            "You earned ${reward.exp} exp and ${reward.points} $pointsAcronym"
        }
    }

    fun completeQuests(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        val difficulties = questRepository.getDifficulties(ids)
        val reward = calculateRewards(difficulties)
        val player = playerRepository.get()
        val settings = settingsRepository.get()
        val bonusPoints = settings.lvlUpBonus
        val willLevelUp = player.canLevelUp(reward.exp)
        player.gainExp(reward.exp, bonusPoints)
        player.gainPoints(reward.points)
        playerRepository.update(player)
        questRepository.complete(ids)
        val numQuestsCompleted = ids.count()
        val pointsAcronym = settings.pointsAcronym

        val logMessage = "${player.name} completes $numQuestsCompleted quest(s) " +
                "and earns ${reward.exp} exp and ${reward.points} $pointsAcronym"

        Log.i(TAG, logMessage)
        val displayedMessage = getQuestCompleteMessage(willLevelUp, reward, pointsAcronym)
        showSnackbar(displayedMessage)
    }

    fun deleteQuests(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        questRepository.delete(ids)
        val numDeleted = ids.count()
        Log.i(TAG, "Delete $numDeleted quest(s)")
    }

    fun switchToDefaultMode() {
        _mode.value = Mode.DEFAULT
    }

    fun switchToSelectMode() {
        _mode.value = Mode.SELECT
    }

    companion object {
        private const val TAG = "QuestListViewModel"
    }
}