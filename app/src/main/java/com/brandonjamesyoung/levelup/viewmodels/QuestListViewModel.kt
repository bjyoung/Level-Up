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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestListViewModel @Inject constructor(
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val questList: LiveData<List<Quest>> = questRepository.getAll().asLiveData()
    val player: LiveData<Player> = playerRepository.observe().asLiveData()
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()
    fun getQuest(id: Int): LiveData<Quest> = questRepository.findById(id).asLiveData()

    private fun calculateRewards(difficulties: List<Difficulty>) : Pair<Int, Int> {
        var expEarned = 0
        var rtEarned = 0
        val settings = settingsRepository.get()

        for (difficulty in difficulties) {
            when (difficulty) {
                Difficulty.EASY -> {
                    expEarned += settings.easyExpReward
                    rtEarned += settings.easyRtReward
                }

                Difficulty.MEDIUM -> {
                    expEarned += settings.mediumExpReward
                    rtEarned += settings.mediumRtReward
                }

                Difficulty.HARD -> {
                    expEarned += settings.hardExpReward
                    rtEarned += settings.hardRtReward
                }

                Difficulty.EXPERT -> {
                    expEarned += settings.expertExpReward
                    rtEarned += settings.expertRtReward
                }
            }
        }

        return Pair(expEarned, rtEarned)
    }

    private fun canLevelUp(player: Player, expEarned: Int) : Boolean {
        return expEarned > 0 && expEarned >= player.expToLvlUp && player.lvl < MAX_LEVEL
    }

    private fun levelUp(player: Player): Player {
        val nextLvl = player.lvl + 1

        return Player(
            id = player.id,
            name = player.name,
            rt = player.rt,
            lvl = nextLvl,
            totalExp = player.totalExp,
            currentLvlExp = 0,
            expToLvlUp = getExpToLvlUp(nextLvl),
        )
    }

    fun completeQuests(ids: Set<Int>) = viewModelScope.launch(Dispatchers.IO) {
        val difficulties = questRepository.getDifficulties(ids)
        val (expEarned, rtEarned) = calculateRewards(difficulties)

        // Update player level based on exp earned
        var player = playerRepository.get()
        var expLeft = expEarned
        var numLoops = 0

        while (canLevelUp(player, expLeft)) {
            expLeft -= player.expToLvlUp
            player = levelUp(player)
            numLoops++

            if (numLoops > MAX_NUM_LOOPS) {
                Log.e("QuestListViewModel.completeQuests", "Exceeded num loops")
            }
        }

        player.apply {
            rt += rtEarned
            totalExp += expEarned
            currentLvlExp += expLeft
            expToLvlUp -= expLeft
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