package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val playerDao: PlayerDao
) {
    fun observe() = playerDao.observeById(1)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun get() = playerDao.getById(1)

    @WorkerThread
    suspend fun update(player: Player) = externalScope.launch {
        playerDao.update(player)
    }
}