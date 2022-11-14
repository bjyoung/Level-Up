package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM Item")
    fun observeAll(): Flow<List<Item>>

    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("DELETE FROM Item WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}