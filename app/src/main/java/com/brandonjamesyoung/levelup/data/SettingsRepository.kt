package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val settingsDao: SettingsDao) {
    fun observe() = settingsDao.observeById(1)

    fun get() = settingsDao.getById(1)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(settings: Settings) = settingsDao.update(settings)
}