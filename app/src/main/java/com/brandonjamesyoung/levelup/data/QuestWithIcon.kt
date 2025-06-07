package com.brandonjamesyoung.levelup.data

import androidx.room.Embedded
import androidx.room.Relation

data class QuestWithIcon(
    @Embedded val activeQuest: ActiveQuest,
    @Relation(
        parentColumn = "iconId",
        entityColumn = "id"
    ) val icon: Icon?
) {
    override fun equals(other: Any?): Boolean {
        return (other is QuestWithIcon)
                && activeQuest == other.activeQuest
                && icon == other.icon
    }

    override fun hashCode(): Int {
        var result = activeQuest.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        return result
    }
}