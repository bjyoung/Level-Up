package com.brandonjamesyoung.levelup.data

import androidx.room.*
import com.brandonjamesyoung.levelup.constants.Difficulty
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Transaction
    @Query("SELECT * FROM ActiveQuest")
    fun observeAll(): Flow<List<QuestWithIcon>>

    @Query("SELECT * FROM ActiveQuest WHERE id = :id")
    fun observe(id: Int): Flow<ActiveQuest>

    // TODO Add LIMIT 1 (and test) to ensure that only one quest is returned
    //  Also update similar queries elsewhere
    @Query("SELECT * FROM ActiveQuest WHERE id = :id")
    fun get(id: Int): ActiveQuest

    @Query("SELECT * FROM ActiveQuest WHERE id IN (:ids)")
    fun get(ids: Set<Int>): List<ActiveQuest>

    @Query("SELECT difficulty FROM ActiveQuest WHERE id IN (:ids)")
    fun getDifficulties(ids: Set<Int>): List<Difficulty>

    @Insert
    suspend fun insert(activeQuest: ActiveQuest)

    @Update
    suspend fun update(activeQuest: ActiveQuest)

    @Query("UPDATE ActiveQuest SET iconId = null WHERE iconId IN (:iconIds)")
    suspend fun clearDeletedIcons(iconIds: Set<Int>)

    @Query("UPDATE ActiveQuest SET iconId = null")
    suspend fun clearAllIcons()

    @Query("DELETE FROM ActiveQuest WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}