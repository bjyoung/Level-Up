package com.brandonjamesyoung.levelup.data

import androidx.room.*

@Dao
interface QuestDao {
    @Query("SELECT * FROM Quest")
    fun getAll(): List<Quest>

    @Query("SELECT * FROM Quest WHERE id = :id")
    fun findById(id: Int): Quest

    @Insert
    fun insert(quest: Quest)

    @Update
    fun update(quest: Quest)

    @Delete
    fun delete(quest: Quest)
}