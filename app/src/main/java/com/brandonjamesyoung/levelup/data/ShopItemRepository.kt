package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopItemRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val shopItemDao: ShopItemDao
) {
    fun observeAll() = shopItemDao.observeAll()

    fun observe(id: Int) = shopItemDao.observe(id)

    fun get(id: Int) = shopItemDao.get(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCosts(ids: Set<Int>) = shopItemDao.getCosts(ids)

    @WorkerThread
    suspend fun insert(shopItem: ShopItem) = externalScope.launch {
        shopItemDao.insert(shopItem)
    }

    @WorkerThread
    suspend fun update(shopItem: ShopItem) = externalScope.launch {
        shopItemDao.update(shopItem)
    }

    @WorkerThread
    suspend fun delete(ids: Set<Int>) = externalScope.launch {
        shopItemDao.delete(ids)
    }
}