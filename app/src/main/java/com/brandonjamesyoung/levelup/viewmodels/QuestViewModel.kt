package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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