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
    var name: String? = null

    var selectedDifficulty: Difficulty = Difficulty.EASY

    var iconId: Int? = null

    var editQuestId: Int? = null

    var questDataLoaded: Boolean = false

    var dateCreated: Instant? = null

    init {
        validModes = listOf(Mode.DEFAULT, Mode.EDIT)
    }

    fun getQuest(id: Int): LiveData<ActiveQuest> {
        return questRepository.observe(id).asLiveData()
    }

    override suspend fun getIcon(id: Int): Icon = withContext(ioDispatcher){
        iconRepository.get(id)
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

    fun resetPage() {
        name = null
        selectedDifficulty = Difficulty.EASY
        iconId = null
        dateCreated = null
        editQuestId = null
        switchMode(Mode.DEFAULT)
        questDataLoaded = false
    }

    companion object {
        private const val TAG = "NewQuestViewModel"
    }
}