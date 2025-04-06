package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.interfaces.IconReader
import com.brandonjamesyoung.levelup.constants.Difficulty
import com.brandonjamesyoung.levelup.constants.MAX_EXP_EARNED
import com.brandonjamesyoung.levelup.constants.MAX_POINTS_EARNED
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.constants.SortOrder
import com.brandonjamesyoung.levelup.constants.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class QuestListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val questRepository: QuestRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val difficultyRepository: DifficultyRepository,
    private val iconRepository: IconRepository
) : BaseViewModel(), IconReader {
    private val _questList: LiveData<List<QuestWithIcon>> =
        questRepository.observeAll().asLiveData()

    val questList: LiveData<List<QuestWithIcon>>
        get() = _questList

    private val _player: LiveData<Player> = playerRepository.observe().asLiveData()

    val player: LiveData<Player>
        get() = _player

    private var _settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    val settings: LiveData<Settings>
        get() = _settings

    private var sortTypes: List<SortType> = listOf(
        SortType.DATE_CREATED,
        SortType.NAME,
        SortType.DIFFICULTY
    )

    val selectedQuestIds: MutableSet<Int> = mutableSetOf()

    init {
        validModes = listOf(Mode.DEFAULT, Mode.SELECT)
    }

    suspend fun getSettings() : Settings {
        return settingsRepository.get()
    }

    override suspend fun getIcon(id: Int): Icon = withContext(ioDispatcher){
        iconRepository.get(id)
    }

    private suspend fun calculateRewards(questDifficulties: List<Difficulty>) : Reward {
        var expEarned = 0
        var pointsEarned = 0
        val difficulties = difficultyRepository.getAll()

        val difficultyMap = difficulties.associateBy { it.code }

        for (difficulty in questDifficulties) {
            val difficultyEntity = difficultyMap[difficulty]

            if (difficultyEntity != null) {
                expEarned += if (expEarned + difficultyEntity.expReward > MAX_EXP_EARNED) {
                    MAX_EXP_EARNED - expEarned
                } else {
                    difficultyEntity.expReward
                }

                val estimatedPointsEarned =  pointsEarned + difficultyEntity.pointsReward

                pointsEarned += if (estimatedPointsEarned > MAX_POINTS_EARNED) {
                    MAX_POINTS_EARNED - pointsEarned
                } else {
                    difficultyEntity.pointsReward
                }
            } else {
                Log.e(TAG, "Difficulty enum value not found")
            }
        }

        return Reward(expEarned, pointsEarned)
    }

    private fun getQuestCompleteMessage(
        levelsEarned: Int,
        reward: Reward,
        pointsAcronym : String
    ) : String {
        val introMessage: String = if (levelsEarned > 0) {
            "You levelled up and"
        } else if (levelsEarned == 0) {
            "You"
        } else {
            "You levelled down and"
        }

        val displayedRewards = Reward(abs(reward.exp), abs(reward.points))
        val expAmountMessage = "${displayedRewards.exp} exp"

        val expMessage = if (reward.exp >= 0) {
            "earned $expAmountMessage"
        } else {
            "lost $expAmountMessage"
        }

        val pointsAmountMessage = "${displayedRewards.points} $pointsAcronym"

        val pointsMessage = if ((reward.points >= 0 && reward.exp >= 0)
            || (reward.points < 0 && reward.exp < 0)) {
            pointsAmountMessage
        } else if (reward.points >= 0) {
            "earned $pointsAmountMessage"
        } else {
            "lost $pointsAmountMessage"
        }

        val punctuation: String = if (levelsEarned > 0) {
            "!"
        } else if (levelsEarned == 0) {
            ""
        } else {
            "..."
        }

        return "$introMessage $expMessage and $pointsMessage$punctuation"
    }

    private fun calculateLevelUpBonus(startingLvl: Int, levelsEarned: Int, lvlUpBonus: Int) : Int {
        var totalLvlUpBonus = 0
        val endingLvl = startingLvl + levelsEarned

        val lowerLvl: Int
        val higherLvl: Int

        if (startingLvl <= endingLvl) {
            lowerLvl = startingLvl
            higherLvl = endingLvl
        } else {
            lowerLvl = endingLvl
            higherLvl = startingLvl
        }

        for (lvl in lowerLvl until higherLvl) totalLvlUpBonus += lvlUpBonus * lvl
        val gainedLevels: Boolean = levelsEarned >= 0
        if (!gainedLevels) totalLvlUpBonus *= -1

        return totalLvlUpBonus
    }

    private fun getLevelUpBonusLogMessage(
        playerName: String?,
        levelsEarned: Int,
        lvlUpBonus: Int,
        pointsAcronym: String
    ) : String {
        var lvlUpBonusLogMessage: String = playerName ?: "???"
        val gainedLevels: Boolean = levelsEarned >= 0
        lvlUpBonusLogMessage += if (gainedLevels) " earns" else " loses"
        lvlUpBonusLogMessage += " $lvlUpBonus $pointsAcronym for levelling"
        lvlUpBonusLogMessage += if (gainedLevels) " up" else " down"
        return lvlUpBonusLogMessage
    }

    fun completeQuests(questIds: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        val difficulties = questRepository.getDifficulties(questIds)
        val reward = calculateRewards(difficulties)
        val player = playerRepository.get()
        val settings = settingsRepository.get()
        val startingLvl = player.lvl
        val levelsEarned = player.gainExp(reward.exp, settings.lvlUpBonus)
        player.gainPoints(reward.points)
        playerRepository.update(player)
        questRepository.complete(questIds)
        val numQuestsCompleted = questIds.count()
        val pointsAcronym = settings.pointsAcronym

        // Calculate level up bonus (bonus * level)
        if (levelsEarned != 0) {
            val levelUpBonus = calculateLevelUpBonus(startingLvl, levelsEarned, settings.lvlUpBonus)

            val lvlUpBonusLogMessage = getLevelUpBonusLogMessage(
                player.name,
                levelsEarned,
                levelUpBonus,
                pointsAcronym
            )

            Log.i(TAG, lvlUpBonusLogMessage)
            reward.points += levelUpBonus
        }

        val logMessage = "${player.name} completes $numQuestsCompleted quest(s) " +
                "and earns ${reward.exp} exp and ${reward.points} $pointsAcronym"

        Log.i(TAG, logMessage)
        val displayedMessage = getQuestCompleteMessage(levelsEarned, reward, pointsAcronym)
        showSnackbar(displayedMessage)
    }

    fun deleteQuests(questIds: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        questRepository.delete(questIds)
        val numDeleted = questIds.count()
        Log.i(TAG, "Delete $numDeleted quest(s)")
    }

    private fun saveSortProperties(sortType: SortType? = null, sortOrder: SortOrder? = null) {
        if (sortType == null && sortOrder == null) return

        viewModelScope.launch(ioDispatcher) {
            val settings = settingsRepository.get()
            if (sortType != null) settings.questListSortType = sortType
            if (sortOrder != null) settings.questListSortOrder = sortOrder
            settingsRepository.update(settings)
        }
    }

    // Change how the shop items are sorted
    suspend fun switchSort() {
        val settings: Settings = settingsRepository.get()
        val sortType: SortType = settings.questListSortType

        if (settings.questListSortOrder == SortOrder.DESC) {
            Log.i(TAG, "Sort items by $sortType in ascending order")
            saveSortProperties(sortOrder = SortOrder.ASC)
            return
        }

        // Go down the sort list and if end is reached loop back to beginning sort type
        var currSortTypeIndex: Int = sortTypes.indexOf(sortType)
        currSortTypeIndex += 1
        if (currSortTypeIndex >= sortTypes.size) currSortTypeIndex = 0
        val newSortType = sortTypes[currSortTypeIndex]
        Log.i(TAG, "Sort items by $newSortType in descending order")
        saveSortProperties(newSortType, SortOrder.DESC)
    }

    companion object {
        private const val TAG = "QuestListViewModel"
    }
}