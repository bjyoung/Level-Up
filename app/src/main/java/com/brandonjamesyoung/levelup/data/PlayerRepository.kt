package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(private val playerDao: PlayerDao) {
    fun observe() = playerDao.observeById(1)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun get() = playerDao.getById(1)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(player: Player) = playerDao.update(player)
}