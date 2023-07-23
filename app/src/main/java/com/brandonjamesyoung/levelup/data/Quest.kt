package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.constants.Difficulty
import java.time.Instant

@Entity
data class Quest(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo var name: String? = null,
    @ColumnInfo var difficulty: Difficulty = Difficulty.EASY,
    @ColumnInfo var iconId: Int? = null,
    @ColumnInfo val dateCreated: Instant? = Instant.now(),
)