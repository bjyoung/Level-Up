package com.brandonjamesyoung.levelup.data

import androidx.room.Embedded
import androidx.room.Relation

data class CompletedQuestWithIcon(
    @Embedded val completedQuest: CompletedQuest,
    @Relation(
        parentColumn = "iconId",
        entityColumn = "id"
    ) val icon: Icon?
)