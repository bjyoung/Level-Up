package com.brandonjamesyoung.levelup.data

import com.brandonjamesyoung.levelup.constants.Difficulty

// Stores user input on the Settings page
class SettingsInput {
    var difficultySettingsMap: MutableMap<Difficulty, DifficultySetting>
    var pointsAcronym: String? = null
    var lvlUpBonus: String? = null

    init {
        val difficulties: List<Difficulty> = listOf(
            Difficulty.EASY,
            Difficulty.MEDIUM,
            Difficulty.HARD,
            Difficulty.EXPERT
        )

        val difficultySettings: List<DifficultySetting> = difficulties.map {
            DifficultySetting(code = it)
        }

        difficultySettingsMap = difficultySettings.associateBy{ it.code }.toMutableMap()
    }

    fun clear() {
        for (difficultyCode in difficultySettingsMap.keys) {
            val difficultySetting = difficultySettingsMap[difficultyCode]
            difficultySetting?.expReward = null
            difficultySetting?.pointsReward = null
        }

        pointsAcronym = null
        lvlUpBonus = null
    }
}