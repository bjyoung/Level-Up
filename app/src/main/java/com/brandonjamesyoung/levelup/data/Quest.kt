package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.shared.Difficulty

@Entity
data class Quest(
    @PrimaryKey val id: Int,
    @ColumnInfo val name: String?,
    @ColumnInfo val difficulty: Difficulty,
    @ColumnInfo val iconId: Int?,
)