package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewQuestViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository
) : ViewModel() {
    fun getQuest(id: Int): LiveData<Quest> {
        return questRepository.observe(id).asLiveData()
    }

    private fun logQuestSave(quest: Quest, isEdit: Boolean = false) {
        var logMessage = if (!isEdit) {
            "Add new ${quest.difficulty} quest with "
        } else {
            "Edit quest to ${quest.difficulty} difficulty with "
        }

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
        logQuestSave(quest)
    }

    fun update(quest: Quest) = viewModelScope.launch(ioDispatcher) {
        val currentQuest = questRepository.get(quest.id)

        if (quest.dateCreated == null) {
            quest.dateCreated = currentQuest.dateCreated
        }

        questRepository.update(quest)
        logQuestSave(quest = quest, isEdit = true)
    }

    companion object {
        private const val TAG = "NewQuestViewModel"
    }
}