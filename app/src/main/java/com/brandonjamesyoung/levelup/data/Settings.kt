package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.constants.SortOrder
import com.brandonjamesyoung.levelup.constants.SortType

@Entity
data class Settings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(defaultValue = "RT") var pointsAcronym: String = "RT",
    @ColumnInfo(defaultValue = "5") var lvlUpBonus: Int = 5,
    @ColumnInfo var nameEntered: Boolean = false,
    @ColumnInfo var shopSortType: SortType = SortType.DATE_CREATED,
    @ColumnInfo var shopSortOrder: SortOrder = SortOrder.ASC
)