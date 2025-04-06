package com.brandonjamesyoung.levelup.data

import android.content.Context
import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import com.brandonjamesyoung.levelup.constants.IconGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IconRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val iconDao: IconDao,
    private val questDao: QuestDao,
    private val questHistoryDao: QuestHistoryDao,
) {
    @WorkerThread
    fun get(id: Int): Icon {
        return iconDao.get(id)
    }

    @WorkerThread
    fun getIcons(iconGroup: IconGroup): List<Icon> {
        return iconDao.getIcons(iconGroup)
    }

    @WorkerThread
    fun insert(icon: Icon) = externalScope.launch {
        iconDao.insert(icon)
    }

    @WorkerThread
    fun update(icon: Icon) = externalScope.launch {
        iconDao.update(icon)
    }

    @WorkerThread
    fun moveToNewIconGroup(iconIds: Set<Int>, iconGroup: IconGroup) = externalScope.launch {
        iconDao.moveToNewIconGroup(iconIds, iconGroup)
    }

    @WorkerThread
    fun delete(ids: Set<Int>) = externalScope.launch {
        questDao.clearDeletedIcons(ids)
        questHistoryDao.clearDeletedIcons(ids)
        iconDao.delete(ids)
    }

    @WorkerThread
    fun resetToDefault(context: Context) = externalScope.launch {
        val initDatabase = InitDatabase()
        initDatabase.initializeDefaultIcons(iconDao, context)
        questDao.clearAllIcons()
        questHistoryDao.clearAllIcons()
    }
}