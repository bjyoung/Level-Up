package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.shared.BASE_EXP

@Entity
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String? = null,
    @ColumnInfo val rp: Int = 0,
    @ColumnInfo val lvl: Int = 1,
    @ColumnInfo val exp: Int = 0,
    @ColumnInfo val expToLvlUp: Int = BASE_EXP,
)