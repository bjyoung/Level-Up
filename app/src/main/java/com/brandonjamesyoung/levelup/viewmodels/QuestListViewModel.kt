package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.QuestRepository
import kotlinx.coroutines.launch

class QuestListViewModel (private val repository: QuestRepository) : ViewModel() {
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

class QuestListViewModelFactory(private val repository: QuestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestListViewModel::class.java)) {
            return QuestListViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}