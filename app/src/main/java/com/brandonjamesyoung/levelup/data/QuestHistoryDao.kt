package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestHistoryDao {
    @Query("SELECT * FROM CompletedQuest")
    fun observeAll(): Flow<List<CompletedQuest>>

    @Insert
    suspend fun insert(completedQuest: CompletedQuest)
}