package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.interfaces.IconReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.brandonjamesyoung.levelup.constants.Difficulty
import com.brandonjamesyoung.levelup.constants.Mode
import kotlinx.coroutines.withContext
import java.time.Instant

@HiltViewModel
class NewQuestViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository,
    private val iconRepository: IconRepository
) : BaseViewModel(), IconReader {
    var quest: MutableLiveData<ActiveQuest?> = MutableLiveData(null)

    var questIcon: MutableLiveData<Icon?> = MutableLiveData(null)

    var name: String? = null

    var selectedDifficulty: Difficulty = Difficulty.EASY

    var iconId: Int? = null

    var editQuestId: Int? = null

    var questDataLoaded: Boolean = false

    var dateCreated: Instant? = null

    init {
        validModes = listOf(Mode.DEFAULT, Mode.EDIT)
    }

    fun loadQuestWithIcon(questId: Int) = viewModelScope.launch(ioDispatcher) {
        Log.i(TAG, "Loading quest with id: $questId")
        val questWithIcon = questRepository.getWithIcon(questId)
        quest.postValue(questWithIcon.activeQuest)
        val icon: Icon? = questWithIcon.icon
        questIcon.postValue(icon)
        if (icon != null) iconId = icon.id
        Log.i(TAG, "Successfully loaded quest: ${questWithIcon.activeQuest.name}")
    }

    fun loadIcon(id: Int) = viewModelScope.launch(ioDispatcher) {
        Log.i(TAG, "Loading icon with id: $id")
        val icon: Icon = iconRepository.get(id)
        questIcon.postValue(icon)
        iconId = icon.id
        Log.i(TAG, "Successfully loaded icon: ${icon.name}")
    }

    override suspend fun getIcon(id: Int): Icon = withContext(ioDispatcher){
        iconRepository.get(id)
    }


    private fun logQuestSave(activeQuest: ActiveQuest, isEdit: Boolean = false) {
        var logMessage = if (!isEdit) {
            "Add new ${activeQuest.difficulty} quest with "
        } else {
            "Edit quest to ${activeQuest.difficulty} difficulty with "
        }

        logMessage += if (activeQuest.name != null) "name '${activeQuest.name}'" else "no name"
        logMessage += " and "

        logMessage += if (activeQuest.iconId != null) {
            "icon '${activeQuest.iconId}'"
        } else {
            "no icon"
        }

        Log.i(TAG, logMessage)
    }

    fun insert(activeQuest: ActiveQuest) = viewModelScope.launch(ioDispatcher) {
        questRepository.insert(activeQuest)
        logQuestSave(activeQuest)
    }

    fun update(activeQuest: ActiveQuest) = viewModelScope.launch(ioDispatcher) {
        val currentActiveQuest: ActiveQuest = questRepository.get(activeQuest.id)

        currentActiveQuest.apply {
            name = activeQuest.name
            difficulty = activeQuest.difficulty
            iconId = activeQuest.iconId
        }

        questRepository.update(currentActiveQuest)
        logQuestSave(activeQuest = currentActiveQuest, isEdit = true)
    }

    fun saveQuest() {
        val activeQuest = ActiveQuest(
            name = name,
            difficulty = selectedDifficulty,
            iconId = iconId
        )

        if (mode.value == Mode.DEFAULT) {
            insert(activeQuest)
        } else if (mode.value == Mode.EDIT) {
            activeQuest.id = editQuestId!!
            update(activeQuest)
        }
    }

    fun resetPage() {
        name = null
        selectedDifficulty = Difficulty.EASY
        iconId = null
        dateCreated = null
        editQuestId = null
        questDataLoaded = false
        editQuestId = INVALID_QUEST_ID
    }

    companion object {
        private const val TAG = "NewQuestViewModel"
        private const val INVALID_QUEST_ID = 0
    }
}