package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM Item")
    fun observeAll(): Flow<List<Item>>

    @Query("SELECT * FROM Item WHERE id = :id")
    fun observe(id: Int): Flow<Item>

    @Query("SELECT * FROM Item WHERE id = :id")
    fun get(id: Int): Item

    @Query("SELECT * FROM Item WHERE id IN (:ids)")
    fun get(ids: Set<Int>): List<Item>

    @Query("SELECT cost FROM Item WHERE id IN (:ids)")
    fun getCosts(ids: Set<Int>): List<Int>

    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("DELETE FROM Item WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}