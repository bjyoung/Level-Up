package com.brandonjamesyoung.levelup.data

import androidx.room.*
import com.brandonjamesyoung.levelup.shared.Difficulty
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM Quest")
    fun getAll(): Flow<List<Quest>>

    @Query("SELECT difficulty FROM Quest WHERE id IN (:ids)")
    fun getDifficulties(ids: Set<Int>): List<Difficulty>

    @Query("SELECT * FROM Quest WHERE id = :id")
    fun findById(id: Int): Flow<Quest>

    @Insert
    suspend fun insert(quest: Quest)

    @Update
    suspend fun update(quest: Quest)

    @Query("DELETE FROM Quest WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}