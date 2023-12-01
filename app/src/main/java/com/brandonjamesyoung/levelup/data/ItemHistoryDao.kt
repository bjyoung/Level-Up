package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemHistoryDao {
    @Query("SELECT * FROM PurchasedItem")
    fun observeAll(): Flow<List<PurchasedItem>>

    @Insert
    suspend fun insert(purchasedItem: PurchasedItem)

    @Query("DELETE FROM PurchasedItem WHERE id IN (SELECT id FROM PurchasedItem ORDER BY datePurchased ASC LIMIT (:numToDelete))")
    suspend fun deleteLatest(numToDelete: Int = 1)

    @Query("SELECT COUNT(*) FROM PurchasedItem")
    suspend fun getNumItems(): Int
}