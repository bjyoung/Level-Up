package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemHistoryRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val itemHistoryDao: ItemHistoryDao,
    private val shopItemDao: ShopItemDao
) {
    fun observeAll() = itemHistoryDao.observeAll()

    @WorkerThread
    suspend fun insert(purchasedItem: PurchasedItem) = externalScope.launch {
        itemHistoryDao.insert(purchasedItem)
    }

    private fun convertToPurchasedItem(
        shopItem: ShopItem
    ) : PurchasedItem {
        return PurchasedItem(
            name = shopItem.name,
            cost = shopItem.cost,
            dateCreated = shopItem.dateCreated
        )
    }

    @WorkerThread
    suspend fun record(itemIds: Set<Int>) = externalScope.launch {
        val shopItems: List<ShopItem> = shopItemDao.get(itemIds)

        val purchasedItems: List<PurchasedItem> = shopItems.map {
            convertToPurchasedItem(it)
        }

        for (purchasedItem in purchasedItems) {
            itemHistoryDao.insert(purchasedItem)
        }

        val numItems = itemHistoryDao.getNumItems()

        if (numItems > PURCHASED_ITEMS_LIMIT) {
            val numItemsToDelete = numItems - PURCHASED_ITEMS_LIMIT
            itemHistoryDao.deleteLatest(numItemsToDelete)
        }
    }

    companion object {
        private const val PURCHASED_ITEMS_LIMIT = 200
    }
}