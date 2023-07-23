package com.brandonjamesyoung.levelup.data

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
    private val questHistoryDao: QuestHistoryDao
) {
    fun observeGroup(iconGroup: IconGroup) = iconDao.observeGroup(iconGroup)

    @WorkerThread
    fun get(id: Int): Icon {
        return iconDao.get(id)
    }

    @WorkerThread
    suspend fun insert(icon: Icon) = externalScope.launch {
        iconDao.insert(icon)
    }

    @WorkerThread
    suspend fun update(icon: Icon) = externalScope.launch {
        iconDao.update(icon)
    }

    @WorkerThread
    suspend fun moveToNewIconGroup(iconIds: List<Int>, iconGroup: IconGroup) = externalScope.launch {
        iconDao.moveToNewIconGroup(iconIds, iconGroup)
    }

    @WorkerThread
    suspend fun delete(ids: Set<Int>) = externalScope.launch {
        questDao.clearDeletedIcons(ids)
        questHistoryDao.clearDeletedIcons(ids)
        iconDao.delete(ids)
    }
}