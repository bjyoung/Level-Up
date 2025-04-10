package com.brandonjamesyoung.levelup.data

import androidx.room.*
import com.brandonjamesyoung.levelup.constants.IconGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao {
    @Query("SELECT * FROM Icon WHERE id = :id")
    fun observe(id: Int): Flow<Icon>

    @Query("SELECT * FROM Icon WHERE iconGroup = :iconGroup")
    fun getIcons(iconGroup: IconGroup): List<Icon>

    @Query("SELECT * FROM Icon WHERE id = :id")
    fun get(id: Int): Icon

    @Insert
    suspend fun insert(icon: Icon)

    @Update
    suspend fun update(icon: Icon)

    @Query("UPDATE Icon SET iconGroup = :iconGroup WHERE id IN (:iconIds)")
    suspend fun moveToNewIconGroup(iconIds: Set<Int>, iconGroup: IconGroup)

    @Query("DELETE FROM Icon WHERE id IN (:ids)")
    suspend fun delete(ids: Set<Int>)

    @Query("DELETE FROM Icon")
    suspend fun deleteAll()
}