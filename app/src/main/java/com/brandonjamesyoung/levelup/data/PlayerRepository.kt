package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(private val playerDao: PlayerDao) {
    fun findById(id: Int) = playerDao.findById(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(player: Player) = playerDao.insert(player)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(player: Player) = playerDao.update(player)

    companion object {
        @Volatile private var instance: PlayerRepository? = null

        fun getInstance(playerDao: PlayerDao) =
            instance ?: synchronized(this) {
                instance ?: PlayerRepository(playerDao).also { instance = it }
            }
    }
}