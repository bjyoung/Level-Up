package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.constants.DEFAULT_LVL_UP_BONUS
import com.brandonjamesyoung.levelup.constants.DEFAULT_POINTS_ACRONYM
import com.brandonjamesyoung.levelup.constants.SortOrder
import com.brandonjamesyoung.levelup.constants.SortType

@Entity
data class Settings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(
        defaultValue = DEFAULT_POINTS_ACRONYM
    ) var pointsAcronym: String = DEFAULT_POINTS_ACRONYM,
    @ColumnInfo(
        defaultValue = DEFAULT_LVL_UP_BONUS.toString()
    ) var lvlUpBonus: Int = DEFAULT_LVL_UP_BONUS,
    @ColumnInfo var nameEntered: Boolean = false,
    @ColumnInfo var shopSortType: SortType = SortType.DATE_CREATED,
    @ColumnInfo var shopSortOrder: SortOrder = SortOrder.ASC,
    @ColumnInfo var questListSortType: SortType = SortType.DATE_CREATED,
    @ColumnInfo var questListSortOrder: SortOrder = SortOrder.ASC
)