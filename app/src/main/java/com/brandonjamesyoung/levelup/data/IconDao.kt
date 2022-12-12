package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao {
    @Query("SELECT * FROM Icon")
    fun observeAll(): Flow<List<Icon>>

    @Insert
    suspend fun insert(icon: Icon)

    @Update
    suspend fun update(icon: Icon)

    @Query("DELETE FROM Icon WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}