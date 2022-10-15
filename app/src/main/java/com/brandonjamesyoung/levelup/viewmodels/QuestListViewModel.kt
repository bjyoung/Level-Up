package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestListViewModel @Inject constructor(
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val questList: LiveData<List<Quest>> = questRepository.getAll().asLiveData()

    fun getQuest(id: Int): LiveData<Quest> = questRepository.findById(id).asLiveData()

    fun deleteQuests(ids: Set<Int>) = viewModelScope.launch {
        questRepository.delete(ids)
    }

    fun insert(quest: Quest) = viewModelScope.launch {
        questRepository.insert(quest)
    }

    fun update(quest: Quest) = viewModelScope.launch {
        questRepository.update(quest)
    }

    fun getPlayer(id: Int): LiveData<Player> = playerRepository.findById(id).asLiveData()

    fun update(player: Player) = viewModelScope.launch {
        playerRepository.update(player)
    }

    fun getSettings(id: Int): LiveData<Settings> = settingsRepository.findById(id).asLiveData()

    fun update(settings: Settings) = viewModelScope.launch {
        settingsRepository.update(settings)
    }
}