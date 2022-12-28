package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.brandonjamesyoung.levelup.shared.Difficulty
import com.brandonjamesyoung.levelup.shared.Mode

@HiltViewModel
class NewQuestViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository,
    private val iconRepository: IconRepository
) : ViewModel() {
    var name: String? = null
    var selectedDifficulty: Difficulty = Difficulty.EASY
    var iconId: Int? = null
    var mode: MutableLiveData<Mode> = MutableLiveData<Mode>(Mode.DEFAULT)
    var editQuestId: Int? = null
    var questDataLoaded: Boolean = false

    fun getQuest(id: Int): LiveData<Quest> {
        return questRepository.observe(id).asLiveData()
    }

    fun getIcon(id: Int): LiveData<Icon> {
        return iconRepository.observe(id).asLiveData()
    }

    fun saveQuest() {
        val quest = Quest(
            name = name,
            difficulty = selectedDifficulty,
            iconId = iconId
        )

        if (mode.value == Mode.DEFAULT) {
            insert(quest)
        } else if (mode.value == Mode.EDIT) {
            quest.id = editQuestId!!
            update(quest)
        }
    }

    private fun logQuestSave(quest: Quest, isEdit: Boolean = false) {
        var logMessage = if (!isEdit) {
            "Add new ${quest.difficulty} quest with "
        } else {
            "Edit quest to ${quest.difficulty} difficulty with "
        }

        logMessage += if (quest.name != null) "name '${quest.name}'" else "no name"
        logMessage += " and "

        logMessage += if (quest.iconId != null) {
            "icon '${quest.iconId}'"
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
        val currentQuest: Quest = questRepository.get(quest.id)

        currentQuest.apply {
            name = quest.name
            difficulty = quest.difficulty
            iconId = quest.iconId
        }

        questRepository.update(currentQuest)
        logQuestSave(quest = currentQuest, isEdit = true)
    }

    fun resetPage() {
        name = null
        selectedDifficulty = Difficulty.EASY
        iconId = null
        editQuestId = null
        mode.value = Mode.DEFAULT
        questDataLoaded = false
    }

    companion object {
        private const val TAG = "NewQuestViewModel"
    }
}