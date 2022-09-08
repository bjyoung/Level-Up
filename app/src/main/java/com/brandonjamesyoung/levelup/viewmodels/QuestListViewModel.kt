package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.QuestRepository
import kotlinx.coroutines.launch

class QuestListViewModel (private val repository: QuestRepository) : ViewModel() {
    val allQuests: LiveData<List<Quest>> = repository.getAll().asLiveData()

    fun delete(quest: Quest) = viewModelScope.launch {
        repository.delete(quest)
    }
}