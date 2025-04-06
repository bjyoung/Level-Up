package com.brandonjamesyoung.levelup.data

import com.brandonjamesyoung.levelup.constants.Difficulty

class DifficultySetting (
    val code: Difficulty,
    var expReward: String? = null,
    var pointsReward: String? = null
)