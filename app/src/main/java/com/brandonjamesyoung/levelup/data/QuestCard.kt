package com.brandonjamesyoung.levelup.data

data class QuestCard(
    var quest: Quest,
    var icon: Icon?,
    var selected: Boolean = false
)