package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.shared.Difficulty
import java.time.Instant

@Entity
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String? = null,
    @ColumnInfo val difficulty: Difficulty = Difficulty.EASY,
    @ColumnInfo val iconName: String? = null,
    @ColumnInfo val dateCreated: Instant? = null,
)