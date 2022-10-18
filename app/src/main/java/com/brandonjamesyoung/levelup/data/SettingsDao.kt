package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM Settings WHERE id = :id")
    fun observeById(id: Int): Flow<Settings>

    @Query("SELECT * FROM Settings WHERE id = :id")
    fun getById(id: Int): Settings

    @Insert
    suspend fun insert(settings: Settings)

    @Update
    suspend fun update(settings: Settings)
}