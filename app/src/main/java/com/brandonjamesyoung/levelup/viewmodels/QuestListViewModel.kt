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
    val player: LiveData<Player> = playerRepository.findById(1).asLiveData()
    val settings: LiveData<Settings> = settingsRepository.findById(1).asLiveData()
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

    fun update(player: Player) = viewModelScope.launch {
        playerRepository.update(player)
    }

    fun update(settings: Settings) = viewModelScope.launch {
        settingsRepository.update(settings)
    }
}