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

    fun observe(id: Int) = itemDao.observe(id)

    fun get(id: Int) = itemDao.get(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getTotalCost(ids: Set<Int>) = itemDao.getTotalCost(ids)

    @WorkerThread
    suspend fun insert(item: Item) = externalScope.launch {
        itemDao.insert(item)
    }

    @WorkerThread
    suspend fun update(item: Item) = externalScope.launch {
        itemDao.update(item)
    }

    @WorkerThread
    suspend fun delete(ids: Set<Int>) = externalScope.launch {
        itemDao.delete(ids)
    }
}