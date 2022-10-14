package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val settingsDao: SettingsDao) {
    fun findById(id: Int) = settingsDao.findById(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(settings: Settings) = settingsDao.insert(settings)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(settings: Settings) = settingsDao.update(settings)

    companion object {
        @Volatile private var instance: SettingsRepository? = null

        fun getInstance(settingsDao: SettingsDao) =
            instance ?: synchronized(this) {
                instance ?: SettingsRepository(settingsDao).also { instance = it }
            }
    }
}