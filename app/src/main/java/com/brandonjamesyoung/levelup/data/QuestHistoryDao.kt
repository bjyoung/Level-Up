package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestHistoryDao {
    @Query("SELECT * FROM CompletedQuest")
    fun observeAll(): Flow<List<CompletedQuest>>

    @Insert
    suspend fun insert(completedQuest: CompletedQuest)

    @Query("UPDATE CompletedQuest SET iconId = null WHERE iconId IN (:iconIds)")
    suspend fun clearDeletedIcons(iconIds: Set<Int>)
}