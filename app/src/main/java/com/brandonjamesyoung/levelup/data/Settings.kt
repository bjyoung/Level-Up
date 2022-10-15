package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Settings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val easyExpReward: Int = 100,
    @ColumnInfo val mediumExpReward: Int = 250,
    @ColumnInfo val hardExpReward: Int = 600,
    @ColumnInfo val expertExpReward: Int = 1500,
    @ColumnInfo val easyRtReward: Int = 1,
    @ColumnInfo val mediumRtReward: Int = 3,
    @ColumnInfo val hardRtReward: Int = 6,
    @ColumnInfo val expertRtReward: Int = 12,
)