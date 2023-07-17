package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestHistoryViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    questHistoryRepository: QuestHistoryRepository,
    private val iconRepository: IconRepository
) : BaseViewModel() {
    val questHistoryList: LiveData<List<CompletedQuest>> =
        questHistoryRepository.observeAll().asLiveData()

    // TODO Extract this duplicate method used across QuestList, Shop, QuestHistory view models
    suspend fun getIcon(id: Int): Icon = withContext(Dispatchers.IO){
        iconRepository.get(id)
    }

    companion object {
        private const val TAG = "QuestHistoryViewModel"
    }
}