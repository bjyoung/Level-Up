package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewQuestViewModel @Inject constructor(
    private val questRepository: QuestRepository,
) : ViewModel() {
    fun insert(quest: Quest) = viewModelScope.launch {
        questRepository.insert(quest)
    }
}