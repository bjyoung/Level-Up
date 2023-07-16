package com.brandonjamesyoung.levelup.data

import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.di.ApplicationScope
import com.brandonjamesyoung.levelup.shared.Difficulty as DifficultyCode
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
    fun observeAll() = questDao.observeAll()

    fun observe(id: Int) = questDao.observe(id)

    fun get(id: Int) = questDao.get(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDifficulties(ids: Set<Int>) = questDao.getDifficulties(ids)

    @WorkerThread
    suspend fun insert(quest: Quest) = externalScope.launch {
        questDao.insert(quest)
    }

    @WorkerThread
    suspend fun update(quest: Quest) = externalScope.launch {
        questDao.update(quest)
    }

    @WorkerThread
    suspend fun delete(ids: Set<Int>) = externalScope.launch {
        questDao.delete(ids)
    }

    private fun convertToCompletedQuest(
        quest: Quest,
        difficultyMap: Map<DifficultyCode, Difficulty>
    ) : CompletedQuest {
        val questDifficulty = difficultyMap[quest.difficulty]

        return CompletedQuest(
            name = quest.name,
            difficulty = quest.difficulty,
            iconId = quest.iconId,
            expEarned = questDifficulty?.expReward,
            pointsEarned = questDifficulty?.pointsReward,
            dateCreated = quest.dateCreated
        )
    }

    @WorkerThread
    suspend fun complete(ids: Set<Int>) = externalScope.launch {
        val quests: List<Quest> = questDao.get(ids)
        val difficulties: List<Difficulty> = difficultyDao.getAll()
        val difficultyMap = difficulties.associateBy { it.code }

        val completedQuests: List<CompletedQuest> = quests.map {
            convertToCompletedQuest(it, difficultyMap)
        }

        questDao.delete(ids)
        for (quest in completedQuests) questHistoryDao.insert(quest)
    }
}