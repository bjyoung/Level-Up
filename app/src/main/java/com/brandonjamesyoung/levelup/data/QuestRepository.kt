package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestRepository @Inject constructor(private val questDao: QuestDao) {
    fun getAll() = questDao.getAll()

    fun findById(id: Int) = questDao.findById(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(quest: Quest) = questDao.insert(quest)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(quest: Quest) = questDao.update(quest)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(ids: Set<Int>) = questDao.delete(ids)
}