package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestHistoryRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val questHistoryDao: QuestHistoryDao
) {
    fun observeAll() = questHistoryDao.observeAll()

    @WorkerThread
    suspend fun insert(completedQuest: CompletedQuest) = externalScope.launch {
        questHistoryDao.insert(completedQuest)
    }
}