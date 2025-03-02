package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.interfaces.IconReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestHistoryViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    questHistoryRepository: QuestHistoryRepository,
    private val iconRepository: IconRepository
) : BaseViewModel(), IconReader {
    val questHistoryList: LiveData<List<CompletedQuestWithIcon>> =
        questHistoryRepository.observeAll().asLiveData()

    override suspend fun getIcon(id: Int): Icon = withContext(ioDispatcher){
        iconRepository.get(id)
    }

    companion object {
        private const val TAG = "QuestHistoryViewModel"
    }
}