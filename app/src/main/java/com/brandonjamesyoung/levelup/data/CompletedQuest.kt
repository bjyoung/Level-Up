package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.shared.Difficulty
import java.time.Instant

@Entity
data class CompletedQuest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String? = null,
    @ColumnInfo val difficulty: Difficulty,
    @ColumnInfo val iconId: Int? = null,
    @ColumnInfo val expEarned: Int? = null,
    @ColumnInfo val pointsEarned: Int? = null,
    @ColumnInfo val dateCreated: Instant? = null,
    @ColumnInfo val dateCompleted: Instant = Instant.now()
)