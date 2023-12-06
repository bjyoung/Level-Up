package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class ShopItem(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
    @ColumnInfo override val name: String? = null,
    @ColumnInfo override val cost: Int,
    @ColumnInfo override val dateCreated: Instant? = Instant.now()
) : Item