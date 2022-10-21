package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var name: String? = null,
    @ColumnInfo var rt: Int = 0,
    @ColumnInfo var lvl: Int = 1,
    @ColumnInfo var totalExp: Long = 0,
    @ColumnInfo var currentLvlExp: Int = 0,
)