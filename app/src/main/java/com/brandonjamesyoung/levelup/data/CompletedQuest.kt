package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.constants.Difficulty
import java.time.Instant

@Entity(indices = [Index(value = ["iconId"])])
data class CompletedQuest(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
    @ColumnInfo override var name: String? = null,
    @ColumnInfo override var difficulty: Difficulty,
    @ColumnInfo override var iconId: Int? = null,
    @ColumnInfo val expEarned: Int? = null,
    @ColumnInfo val pointsEarned: Int? = null,
    @ColumnInfo override val dateCreated: Instant? = null,
    @ColumnInfo val dateCompleted: Instant = Instant.now()
) : Quest()