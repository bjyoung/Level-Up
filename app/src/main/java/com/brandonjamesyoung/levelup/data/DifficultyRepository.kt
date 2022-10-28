package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DifficultyRepository @Inject constructor(private val difficultyDao: DifficultyDao) {
    fun observeAll() = difficultyDao.observeAll()

    fun getAll() = difficultyDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(difficulty: Difficulty) = difficultyDao.update(difficulty)
}