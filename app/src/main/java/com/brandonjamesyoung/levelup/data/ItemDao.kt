package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM Item")
    fun observeAll(): Flow<List<Item>>

    @Query("SELECT SUM(cost) FROM Item WHERE id IN (:ids)")
    fun getTotalCost(ids: Set<Int>): Int

    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("DELETE FROM Item WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}