package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String? = null,
    @ColumnInfo val cost: Int,
    @ColumnInfo val dateCreated: Instant? = null,
)