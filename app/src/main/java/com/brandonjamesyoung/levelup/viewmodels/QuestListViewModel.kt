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

    fun completeQuests(ids: Set<Int>) = viewModelScope.launch(Dispatchers.IO) {
        val difficulties = questRepository.getDifficulties(ids)
        val settings = settingsRepository.get()
        var expEarned = 0
        var rtEarned = 0

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

        // Calculate # level ups
        val currPlayer = playerRepository.get()
        var currLvl = currPlayer.lvl
        var currLvlExp = currPlayer.currentLvlExp
        var expToLvlUp = currPlayer.expToLvlUp
        var numLvlUps = 0
        var expLeft = expEarned
        var numLoops = 0

        while (expLeft > 0 && expLeft > expToLvlUp && currLvl < MAX_LEVEL) {
            expLeft -= expToLvlUp
            numLvlUps++
            currLvl++
            currLvlExp = 0
            expToLvlUp = getExpToLvlUp(currLvl)
            numLoops++

            if (numLoops > MAX_NUM_LOOPS) {
                Log.e("QuestListViewModel.completeQuests", "Exceeded num loops")
            }
        }

        currLvlExp += expLeft
        expToLvlUp -= expLeft

        // Update player
        val newPlayer = Player(
            id = currPlayer.id,
            name = currPlayer.name,
            rt = currPlayer.rt + rtEarned,
            lvl = currPlayer.lvl + numLvlUps,
            totalExp = currPlayer.totalExp + expEarned,
            currentLvlExp = currLvlExp,
            expToLvlUp = expToLvlUp,
        )

        playerRepository.update(newPlayer)
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