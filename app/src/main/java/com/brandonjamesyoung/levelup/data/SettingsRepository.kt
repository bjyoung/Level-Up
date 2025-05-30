package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.constants.DEFAULT_LVL_UP_BONUS
import com.brandonjamesyoung.levelup.constants.DEFAULT_POINTS_ACRONYM
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

    @WorkerThread
    suspend fun get(): Settings {
        var settings = settingsDao.getById(1)

        if (settings == null) {
            insertDefaultSettings()
            settings = Settings()
        }

        return settings
    }

    suspend fun insertDefaultSettings() {
        val settings = Settings()
        settingsDao.insert(settings)
    }

    @WorkerThread
    fun update(settings: Settings) = externalScope.launch {
        settingsDao.update(settings)
    }

    @WorkerThread
    fun resetToDefault() = externalScope.launch {
        val settings: Settings = get()
        settings.lvlUpBonus = DEFAULT_LVL_UP_BONUS
        settings.pointsAcronym = DEFAULT_POINTS_ACRONYM
        update(settings)
    }
}