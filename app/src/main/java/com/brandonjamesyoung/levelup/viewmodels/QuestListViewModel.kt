package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.LevelUpHelper.Companion.getExpToLvlUp
import com.brandonjamesyoung.levelup.shared.MAX_LEVEL
import com.brandonjamesyoung.levelup.shared.MAX_NUM_LOOPS
import com.brandonjamesyoung.levelup.shared.MAX_TOTAL_EXP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestListViewModel @Inject constructor(
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val difficultyRepository: DifficultyRepository,
) : ViewModel() {
    val questList: LiveData<List<Quest>> = questRepository.observeAll().asLiveData()
    val player: LiveData<Player> = playerRepository.observe().asLiveData()
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    private fun calculateRewards(questDifficulties: List<Difficulty>) : Pair<Int, Int> {
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
                Log.e(
                    "QuestListViewModel.calculateRewards",
                    "Difficulty enum value not found"
                )
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

    private fun levelUp(player: Player): Player {
        val nextLvl = player.lvl + 1
        val rtBonus = settingsRepository.get().lvlUpBonus
        val newPlayer = player.copy()

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

    fun completeQuests(ids: Set<Int>) = viewModelScope.launch(Dispatchers.IO) {
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
                Log.e("QuestListViewModel.completeQuests", "Exceeded num loops")
            }
        }

        player.apply {
            rt += rtEarned
            totalExp += getTotalExpEarned(this, expEarned)
            currentLvlExp += if (this.lvl < MAX_LEVEL) expLeft else 0
        }

        playerRepository.update(player)
        questRepository.delete(ids)
    }

    fun deleteQuests(ids: Set<Int>) = viewModelScope.launch {
        questRepository.delete(ids)
    }

    fun insert(quest: Quest) = viewModelScope.launch {
        questRepository.insert(quest)
    }

    fun update(quest: Quest) = viewModelScope.launch {
        questRepository.update(quest)
    }

    fun update(player: Player) = viewModelScope.launch {
        playerRepository.update(player)
    }

    fun update(settings: Settings) = viewModelScope.launch {
        settingsRepository.update(settings)
    }
}