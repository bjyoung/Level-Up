package com.brandonjamesyoung.levelup.data

import androidx.room.*
import com.brandonjamesyoung.levelup.shared.Difficulty
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM Quest")
    fun observeAll(): Flow<List<Quest>>

    @Query("SELECT * FROM Quest WHERE id = :id")
    fun observe(id: Int): Flow<Quest>

    @Query("SELECT * FROM Quest WHERE id = :id")
    fun get(id: Int): Quest

    @Query("SELECT difficulty FROM Quest WHERE id IN (:ids)")
    fun getDifficulties(ids: Set<Int>): List<Difficulty>

    @Insert
    suspend fun insert(quest: Quest)

    @Update
    suspend fun update(quest: Quest)

    @Query("DELETE FROM Quest WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}