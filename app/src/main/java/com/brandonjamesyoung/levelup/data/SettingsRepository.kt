package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val settingsDao: SettingsDao
) {
    fun observe() = settingsDao.observeById(1)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun get() = settingsDao.getById(1)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(settings: Settings) = externalScope.launch {
        settingsDao.update(settings)
    }
}