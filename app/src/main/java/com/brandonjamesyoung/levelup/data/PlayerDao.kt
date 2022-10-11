package com.brandonjamesyoung.levelup.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM Player WHERE id = :id")
    fun findById(id: Int): Flow<Player>

    @Insert
    suspend fun insert(player: Player)

    @Update
    suspend fun update(player: Player)
}