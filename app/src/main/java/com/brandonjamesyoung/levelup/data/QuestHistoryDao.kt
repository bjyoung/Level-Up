package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestHistoryDao {
    @Query("SELECT * FROM CompletedQuest")
    fun observeAll(): Flow<List<CompletedQuest>>

    @Insert
    suspend fun insert(completedQuest: CompletedQuest)

    @Query("DELETE FROM CompletedQuest WHERE id IN (SELECT id FROM CompletedQuest ORDER BY dateCompleted ASC LIMIT (:numToDelete))")
    suspend fun deleteLatest(numToDelete: Int = 1)

    @Query("UPDATE CompletedQuest SET iconId = null WHERE iconId IN (:iconIds)")
    suspend fun clearDeletedIcons(iconIds: Set<Int>)

    @Query("UPDATE Quest SET iconId = null")
    suspend fun clearAllIcons()

    @Query("SELECT COUNT(*) FROM CompletedQuest")
    suspend fun getNumQuests(): Int
}