package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import com.brandonjamesyoung.levelup.constants.Difficulty as DifficultyCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val questDao: QuestDao,
    private val difficultyDao: DifficultyDao,
    private val questHistoryDao: QuestHistoryDao,
) {
    fun observe(id: Int) = questDao.observe(id)

    fun observeAll() = questDao.observeAll()

    fun get(id: Int) = questDao.get(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDifficulties(ids: Set<Int>) = questDao.getDifficulties(ids)

    @WorkerThread
    suspend fun insert(activeQuest: ActiveQuest) = externalScope.launch {
        questDao.insert(activeQuest)
    }

    @WorkerThread
    suspend fun update(activeQuest: ActiveQuest) = externalScope.launch {
        questDao.update(activeQuest)
    }

    @WorkerThread
    suspend fun delete(ids: Set<Int>) = externalScope.launch {
        questDao.delete(ids)
    }

    private fun convertToCompletedQuest(
        activeQuest: ActiveQuest,
        difficultyMap: Map<DifficultyCode, Difficulty>
    ) : CompletedQuest {
        val questDifficulty = difficultyMap[activeQuest.difficulty]

        return CompletedQuest(
            name = activeQuest.name,
            difficulty = activeQuest.difficulty,
            iconId = activeQuest.iconId,
            expEarned = questDifficulty?.expReward,
            pointsEarned = questDifficulty?.pointsReward,
            dateCreated = activeQuest.dateCreated
        )
    }

    @WorkerThread
    suspend fun complete(ids: Set<Int>) = externalScope.launch {
        val activeQuests: List<ActiveQuest> = questDao.get(ids)
        val difficulties: List<Difficulty> = difficultyDao.getAll()
        val difficultyMap = difficulties.associateBy { it.code }

        val completedQuests: List<CompletedQuest> = activeQuests.map {
            convertToCompletedQuest(it, difficultyMap)
        }

        questDao.delete(ids)

        for (quest in completedQuests) {
            questHistoryDao.insert(quest)
        }

        val numCompletedQuests = questHistoryDao.getNumQuests()

        if (numCompletedQuests > COMPLETED_QUEST_LIMIT) {
            val numQuestsToDelete = numCompletedQuests - COMPLETED_QUEST_LIMIT
            questHistoryDao.deleteLatest(numQuestsToDelete)
        }
    }

    companion object {
        private const val COMPLETED_QUEST_LIMIT = 200
    }
}