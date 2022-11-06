package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NewQuestViewModel"

@HiltViewModel
class NewQuestViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository
) : ViewModel() {
    private fun logQuestCreation(quest: Quest) {
        var logMessage = "Add new ${quest.difficulty} quest with "
        logMessage += if (quest.name != null) "name '${quest.name}'" else "no name"
        logMessage += " and "

        logMessage += if (quest.iconName != null) {
            "icon '${quest.iconName}'"
        } else {
            "no icon"
        }

        Log.i(TAG, logMessage)
    }

    fun insert(quest: Quest) = viewModelScope.launch(ioDispatcher) {
        questRepository.insert(quest)
        logQuestCreation(quest)
    }
}