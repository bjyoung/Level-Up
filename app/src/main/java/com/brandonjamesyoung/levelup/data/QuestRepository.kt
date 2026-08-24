package com.brandonjamesyoung.levelup.data

import android.util.Log
import androidx.annotation.WorkerThread
import com.brandonjamesyoung.levelup.constants.QUEST_HISTORY_LIMIT
import com.brandonjamesyoung.levelup.constants.QUEST_LIST_LIMIT
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

    fun getWithIcon(id: Int) = questDao.getWithIcon(id)

    @WorkerThread
    suspend fun questLimitReached() : Boolean {
        val numQuests = questDao.getNumQuests()
        return numQuests >= QUEST_LIST_LIMIT
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDifficulties(ids: Set<Int>) = questDao.getDifficulties(ids)

    @WorkerThread
    fun insert(activeQuest: ActiveQuest) = externalScope.launch {
        val numActiveQuests = questDao.getNumQuests()

        if (numActiveQuests < QUEST_LIST_LIMIT) {
            questDao.insert(activeQuest)
        } else {
            Log.d(TAG, "Max quest list limit reached. Cannot add new quests")
        }
    }

    @WorkerThread
    fun update(activeQuest: ActiveQuest) = externalScope.launch {
        questDao.update(activeQuest)
    }

    @WorkerThread
    fun delete(ids: Set<Int>) = externalScope.launch {
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
    fun complete(ids: Set<Int>) = externalScope.launch {
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

        if (numCompletedQuests > QUEST_HISTORY_LIMIT) {
            val numQuestsToDelete = numCompletedQuests - QUEST_HISTORY_LIMIT

            val questHistoryLimitMessage = "Quest History limit reached. Deleting " +
                    "oldest $numQuestsToDelete recorded quests"

            Log.d(TAG, questHistoryLimitMessage)
            questHistoryDao.deleteOldest(numQuestsToDelete)
        }
    }

    companion object {
        private const val TAG = "QuestRepository"
    }
}