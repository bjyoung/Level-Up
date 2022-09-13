package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.QuestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestListViewModel @Inject constructor(
    private val repository: QuestRepository
) : ViewModel() {
    val questList: LiveData<List<Quest>> = repository.getAll().asLiveData()

    fun findById(id: Int): LiveData<Quest> = repository.findById(id).asLiveData()

    fun delete(quest: Quest) = viewModelScope.launch {
        repository.delete(quest)
    }

    fun insert(quest: Quest) = viewModelScope.launch {
        repository.insert(quest)
    }

    fun update(quest: Quest) = viewModelScope.launch {
        repository.update(quest)
    }
}