package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.shared.Difficulty

@Entity
data class Difficulty(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val code: Difficulty,
    @ColumnInfo var expReward: Int,
    @ColumnInfo var pointsReward: Int,
)