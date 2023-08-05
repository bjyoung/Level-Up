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

    private var _mode: MutableLiveData<Mode> = MutableLiveData<Mode>(Mode.DEFAULT)

    val mode: LiveData<Mode>
        get() = _mode

    var editQuestId: Int? = null

    var questDataLoaded: Boolean = false

    var dateCreated: Instant? = null

    fun switchToEditMode() {
        _mode.value = Mode.EDIT
    }

    fun getQuest(id: Int): LiveData<Quest> {
        return questRepository.observe(id).asLiveData()
    }

    override suspend fun getIcon(id: Int): Icon = withContext(ioDispatcher){
        iconRepository.get(id)
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
        dateCreated = null
        editQuestId = null
        _mode.value = Mode.DEFAULT
        questDataLoaded = false
    }

    companion object {
        private const val TAG = "NewQuestViewModel"
    }
}