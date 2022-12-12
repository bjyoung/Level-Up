package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IconRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val iconDao: IconDao
) {
    fun observeAll() = iconDao.observeAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(icon: Icon) = externalScope.launch {
        iconDao.insert(icon)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(icon: Icon) = externalScope.launch {
        iconDao.update(icon)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(ids: Set<Int>) = externalScope.launch {
        iconDao.delete(ids)
    }
}