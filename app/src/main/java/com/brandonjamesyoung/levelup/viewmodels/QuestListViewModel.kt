package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.QuestRepository
import kotlinx.coroutines.launch

class QuestListViewModel (private val repository: QuestRepository) : ViewModel() {
    val allQuests: LiveData<List<Quest>> = repository.getAll().asLiveData()

    fun delete(quest: Quest) = viewModelScope.launch {
        repository.delete(quest)
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