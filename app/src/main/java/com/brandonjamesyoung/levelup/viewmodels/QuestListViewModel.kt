package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.LevelUpHelper.Companion.getExpToLvlUp
import com.brandonjamesyoung.levelup.shared.MAX_LEVEL
import com.brandonjamesyoung.levelup.shared.MAX_NUM_LOOPS
import com.brandonjamesyoung.levelup.shared.MAX_TOTAL_EXP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "QuestListViewModel"

@HiltViewModel
class QuestListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val difficultyRepository: DifficultyRepository
) : ViewModel() {
    val questList: LiveData<List<Quest>> = questRepository.observeAll().asLiveData()
    val player: LiveData<Player> = playerRepository.observe().asLiveData()
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    private suspend fun calculateRewards(questDifficulties: List<Difficulty>) : Pair<Int, Int> {
        var expEarned = 0
        var rtEarned = 0
        val difficulties = difficultyRepository.getAll()

        val difficultyMap = difficulties.associateBy { it.code }

        for (difficulty in questDifficulties) {
            val difficultyEntity = difficultyMap[difficulty]

            if (difficultyEntity != null) {
                expEarned += difficultyEntity.expReward
                rtEarned += difficultyEntity.rtReward
            } else {
                Log.e(TAG, "Difficulty enum value not found")
            }
        }

        return Pair(expEarned, rtEarned)
    }

    private fun getExpToNextLvl(player: Player): Int {
        val totalExpToLvlUp = getExpToLvlUp(player.lvl)
        return totalExpToLvlUp - player.currentLvlExp
    }

    private fun canLevelUp(player: Player, expEarned: Int) : Boolean {
        val expToNextLvl = getExpToNextLvl(player)
        return expEarned > 0 && expEarned >= expToNextLvl && player.lvl < MAX_LEVEL
    }

    private suspend fun levelUp(player: Player): Player {
        val nextLvl = player.lvl + 1
        val rtBonus = settingsRepository.get().lvlUpBonus
        val newPlayer = player.copy()
        Log.i(TAG, "Player levels up to lvl ${nextLvl}!")

        return newPlayer.apply {
            rt += rtBonus
            lvl = nextLvl
            currentLvlExp = 0
        }
    }

    private fun getTotalExpEarned(player: Player, expEarned: Int) : Long {
        return if (player.totalExp + expEarned > MAX_TOTAL_EXP) {
            MAX_TOTAL_EXP - player.totalExp
        } else {
            expEarned.toLong()
        }
    }

    fun completeQuests(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        val difficulties = questRepository.getDifficulties(ids)
        val (expEarned, rtEarned) = calculateRewards(difficulties)

        // Update player level based on exp earned
        var player = playerRepository.get()
        var expLeft = expEarned
        var numLoops = 0

        while (canLevelUp(player, expLeft)) {
            val expToNextLvl = getExpToNextLvl(player)
            expLeft -= expToNextLvl
            player = levelUp(player)
            numLoops++

            if (numLoops > MAX_NUM_LOOPS) {
                Log.e(TAG, "Exceeded num loops")
            }
        }

        val currLvlExpEarned = if (player.lvl < MAX_LEVEL) expLeft else 0

        player.apply {
            rt += rtEarned
            totalExp += getTotalExpEarned(player, expEarned)
            currentLvlExp += currLvlExpEarned
        }

        playerRepository.update(player)
        questRepository.delete(ids)
        val numQuestsCompleted = ids.count()

        Log.i(TAG, "Player completes $numQuestsCompleted quest(s) " +
                    "and earns $currLvlExpEarned exp and $rtEarned points"
        )
    }

    fun deleteQuests(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        questRepository.delete(ids)
        val numQuestsDeleted = ids.count()
        Log.i(TAG, "Delete $numQuestsDeleted quest(s)")
    }
}