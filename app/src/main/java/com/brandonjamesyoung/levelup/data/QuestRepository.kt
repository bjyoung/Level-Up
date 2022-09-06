package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread

class QuestRepository(private val questDao: QuestDao) {
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
    suspend fun delete(quest: Quest) = questDao.delete(quest)

    companion object {
        @Volatile private var instance: QuestRepository? = null

        fun getInstance(questDao: QuestDao) =
            instance ?: synchronized(this) {
                instance ?: QuestRepository(questDao).also { instance = it }
            }
    }
}