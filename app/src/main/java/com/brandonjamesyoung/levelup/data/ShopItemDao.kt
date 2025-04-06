package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopItemDao {
    @Query("SELECT * FROM ShopItem")
    fun observeAll(): Flow<List<ShopItem>>

    @Query("SELECT * FROM ShopItem WHERE id = :id")
    fun observe(id: Int): Flow<ShopItem>

    @Query("SELECT * FROM ShopItem WHERE id = :id")
    fun get(id: Int): ShopItem

    @Query("SELECT * FROM ShopItem WHERE id IN (:ids)")
    fun get(ids: Set<Int>): List<ShopItem>

    @Query("SELECT cost FROM ShopItem WHERE id IN (:ids)")
    fun getCosts(ids: Set<Int>): List<Int>

    @Insert
    suspend fun insert(shopItem: ShopItem)

    @Update
    suspend fun update(shopItem: ShopItem)

    @Query("DELETE FROM ShopItem WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}