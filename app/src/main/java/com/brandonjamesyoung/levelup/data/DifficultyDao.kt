package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DifficultyDao {
    @Query("SELECT * FROM Difficulty")
    fun observeAll(): Flow<List<Difficulty>>

    @Query("SELECT * FROM Difficulty")
    fun getAll(): List<Difficulty>

    @Insert
    suspend fun insert(difficulty: Difficulty)

    @Update
    suspend fun update(difficulties: List<Difficulty>)
}