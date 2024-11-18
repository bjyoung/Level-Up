package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import com.brandonjamesyoung.levelup.constants.Difficulty as DifficultyCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DifficultyRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val difficultyDao: DifficultyDao
) {
    fun observeAll() = difficultyDao.observeAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAll() = difficultyDao.getAll()

    @WorkerThread
    suspend fun update(difficulties: List<Difficulty>) = externalScope.launch {
        difficultyDao.update(difficulties)
    }

    @WorkerThread
    suspend fun resetToDefault() = externalScope.launch {
        val existingDifficulties: List<Difficulty> = getAll()
        val initDb = InitDatabase()

        val defaultDifficulties: Map<DifficultyCode, Difficulty> =
            initDb.getInitDifficulties().associateBy({it.code}, {it})

        for (currDifficulty in existingDifficulties) {
            val defaultDiff: Difficulty = defaultDifficulties[currDifficulty.code] ?: continue
            currDifficulty.expReward = defaultDiff.expReward
            currDifficulty.pointsReward = defaultDiff.pointsReward
        }

        update(existingDifficulties)
    }
}