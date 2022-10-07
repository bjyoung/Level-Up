package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM Quest")
    fun getAll(): Flow<List<Quest>>

    @Query("SELECT * FROM Quest WHERE id = :id")
    fun findById(id: Int): Flow<Quest>

    @Insert
    suspend fun insert(quest: Quest)

    @Update
    suspend fun update(quest: Quest)

    @Query("DELETE FROM Quest WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}