package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.interfaces.IconReader
import com.brandonjamesyoung.levelup.constants.Difficulty
import com.brandonjamesyoung.levelup.constants.MAX_EXP_EARNED
import com.brandonjamesyoung.levelup.constants.MAX_POINTS_EARNED
import com.brandonjamesyoung.levelup.constants.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class QuestListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val difficultyRepository: DifficultyRepository,
    private val iconRepository: IconRepository
) : BaseViewModel(), IconReader {
    private val _questList: LiveData<List<Quest>> = questRepository.observeAll().asLiveData()

    val questList: LiveData<List<Quest>>
        get() = _questList

    private val _player: LiveData<Player> = playerRepository.observe().asLiveData()

    val player: LiveData<Player>
        get() = _player

    private var _mode: MutableLiveData<Mode> = MutableLiveData<Mode>(Mode.DEFAULT)

    val mode: LiveData<Mode>
        get() = _mode

    val selectedQuestIds: MutableSet<Int> = mutableSetOf()

    val selectedQuestIconIds: MutableSet<Int> = mutableSetOf()

    suspend fun getSettings() : Settings {
        return settingsRepository.get()
    }

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
                expEarned += if (expEarned + difficultyEntity.expReward > MAX_EXP_EARNED) {
                    MAX_EXP_EARNED - expEarned
                } else {
                    difficultyEntity.expReward
                }

                val estimatedPointsEarned =  pointsEarned + difficultyEntity.pointsReward

                pointsEarned += if (estimatedPointsEarned > MAX_POINTS_EARNED) {
                    MAX_POINTS_EARNED - pointsEarned
                } else {
                    difficultyEntity.pointsReward
                }
            } else {
                Log.e(TAG, "Difficulty enum value not found")
            }
        }

        return Reward(expEarned, pointsEarned)
    }

    private fun getQuestCompleteMessage(
        levelsEarned: Int,
        reward: Reward,
        pointsAcronym : String
    ) : String {
        val introMessage: String = if (levelsEarned > 0) {
            "You levelled up and"
        } else if (levelsEarned == 0) {
            "You"
        } else {
            "You levelled down and"
        }

        val displayedRewards = Reward(abs(reward.exp), abs(reward.points))
        val expAmountMessage = "${displayedRewards.exp} exp"

        val expMessage = if (reward.exp >= 0) {
            "earned $expAmountMessage"
        } else {
            "lost $expAmountMessage"
        }

        val pointsAmountMessage = "${displayedRewards.points} $pointsAcronym"

        val pointsMessage = if ((reward.points >= 0 && reward.exp >= 0)
            || (reward.points < 0 && reward.exp < 0)) {
            pointsAmountMessage
        } else if (reward.points >= 0) {
            "earned $pointsAmountMessage"
        } else {
            "lost $pointsAmountMessage"
        }

        val punctuation: String = if (levelsEarned > 0) {
            "!"
        } else if (levelsEarned == 0) {
            ""
        } else {
            "..."
        }

        return "$introMessage $expMessage and $pointsMessage$punctuation"
    }

    fun completeQuests(questIds: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        val difficulties = questRepository.getDifficulties(questIds)
        val reward = calculateRewards(difficulties)
        val player = playerRepository.get()
        val settings = settingsRepository.get()
        val levelsEarned = player.gainExp(reward.exp, settings.lvlUpBonus)
        player.gainPoints(reward.points)
        playerRepository.update(player)
        questRepository.complete(questIds)
        val numQuestsCompleted = questIds.count()
        val pointsAcronym = settings.pointsAcronym
        reward.points += settings.lvlUpBonus * levelsEarned

        val logMessage = "${player.name} completes $numQuestsCompleted quest(s) " +
                "and earns ${reward.exp} exp and ${reward.points} $pointsAcronym"

        Log.i(TAG, logMessage)
        val displayedMessage = getQuestCompleteMessage(levelsEarned, reward, pointsAcronym)
        showSnackbar(displayedMessage)
    }

    fun deleteQuests(questIds: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        questRepository.delete(questIds)
        val numDeleted = questIds.count()
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