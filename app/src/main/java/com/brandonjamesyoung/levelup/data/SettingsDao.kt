package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM Settings WHERE id = :id")
    fun findById(id: Int): Flow<Settings>

    @Insert
    suspend fun insert(settings: Settings)

    @Update
    suspend fun update(settings: Settings)
}