package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.QuestRepository
import kotlinx.coroutines.launch

class QuestViewModel (private val repository: QuestRepository) : ViewModel() {
    fun findById(id: Int): LiveData<Quest> = repository.findById(id).asLiveData()

    fun insert(quest: Quest) = viewModelScope.launch {
        repository.insert(quest)
    }

    fun update(quest: Quest) = viewModelScope.launch {
        repository.update(quest)
    }
}

class QuestViewModelFactory(private val repository: QuestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestViewModel::class.java)) {
            return QuestViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}