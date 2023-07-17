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

    // TODO Add LIMIT 1 (and test) to ensure that only one quest is returned
    //  Also update similar queries elsewhere
    @Query("SELECT * FROM Quest WHERE id = :id")
    fun get(id: Int): Quest

    @Query("SELECT * FROM Quest WHERE id IN (:ids)")
    fun get(ids: Set<Int>): List<Quest>

    @Query("SELECT difficulty FROM Quest WHERE id IN (:ids)")
    fun getDifficulties(ids: Set<Int>): List<Difficulty>

    @Insert
    suspend fun insert(quest: Quest)

    @Update
    suspend fun update(quest: Quest)

    @Query("UPDATE Quest SET iconId = null WHERE iconId IN (:iconIds)")
    suspend fun clearDeletedIcons(iconIds: Set<Int>)

    @Query("DELETE FROM Quest WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}