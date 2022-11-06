package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DifficultyRepository @Inject constructor(private val difficultyDao: DifficultyDao) {
    fun observeAll() = difficultyDao.observeAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAll() = difficultyDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(difficulties: List<Difficulty>) = difficultyDao.update(difficulties)
}