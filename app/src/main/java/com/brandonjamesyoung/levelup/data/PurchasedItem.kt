package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class PurchasedItem(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
    @ColumnInfo override var name: String? = null,
    @ColumnInfo override var cost: Int,
    @ColumnInfo override val dateCreated: Instant?,
    @ColumnInfo val datePurchased: Instant = Instant.now()
) : Item