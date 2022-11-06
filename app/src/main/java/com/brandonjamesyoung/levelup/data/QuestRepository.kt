package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val questDao: QuestDao
) {
    fun observeAll() = questDao.observeAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDifficulties(ids: Set<Int>) = questDao.getDifficulties(ids)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(quest: Quest) = externalScope.launch {
        questDao.insert(quest)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(quest: Quest) = externalScope.launch {
        questDao.update(quest)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(ids: Set<Int>) = externalScope.launch {
        questDao.delete(ids)
    }
}