package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val itemDao: ItemDao
) {
    fun observeAll() = itemDao.observeAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getTotalCost(ids: Set<Int>) = itemDao.getTotalCost(ids)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Item) = externalScope.launch {
        itemDao.insert(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(item: Item) = externalScope.launch {
        itemDao.update(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(ids: Set<Int>) = externalScope.launch {
        itemDao.delete(ids)
    }
}