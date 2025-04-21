package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestHistoryDetailsViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questHistoryRepository: QuestHistoryRepository
) : BaseViewModel() {
    var completedQuestWithIcon: MutableLiveData<CompletedQuestWithIcon?> = MutableLiveData(null)

    init {
        validModes = listOf(Mode.SELECT, Mode.EDIT, Mode.MOVE)
    }

    fun loadCompletedQuest(id: Int) = viewModelScope.launch(ioDispatcher) {
        Log.i(TAG, "Loading completed quest with id: $id")
        completedQuestWithIcon.postValue(questHistoryRepository.get(id))
        Log.i(TAG, "Successfully loaded completed quest with id: $id")
    }

    companion object {
        private const val TAG = "QuestHistoryDetailsViewModel"
    }
}