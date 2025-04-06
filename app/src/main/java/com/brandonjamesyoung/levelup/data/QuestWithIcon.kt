package com.brandonjamesyoung.levelup.data

import androidx.room.Embedded
import androidx.room.Relation

data class QuestWithIcon(
    @Embedded val activeQuest: ActiveQuest,
    @Relation(
        parentColumn = "iconId",
        entityColumn = "id"
    ) val icon: Icon?
)