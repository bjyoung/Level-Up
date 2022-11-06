package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DifficultyRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val difficultyDao: DifficultyDao
) {
    fun observeAll() = difficultyDao.observeAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAll() = difficultyDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(difficulties: List<Difficulty>) = externalScope.launch {
        difficultyDao.update(difficulties)
    }
}