package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(private val playerDao: PlayerDao) {
    fun findById(id: Int) = playerDao.findById(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(player: Player) = playerDao.update(player)
}