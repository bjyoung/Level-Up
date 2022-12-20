package com.brandonjamesyoung.levelup.data

import androidx.room.*
import com.brandonjamesyoung.levelup.shared.IconGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao {
    @Query("SELECT * FROM Icon WHERE iconGroup = :iconGroup")
    fun observeGroup(iconGroup: IconGroup): Flow<List<Icon>>

    @Insert
    suspend fun insert(icon: Icon)

    @Update
    suspend fun update(icon: Icon)

    @Query("DELETE FROM Icon WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)
}