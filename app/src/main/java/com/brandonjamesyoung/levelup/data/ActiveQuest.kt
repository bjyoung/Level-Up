package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.constants.Difficulty
import java.time.Instant

@Entity
data class ActiveQuest(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
    @ColumnInfo override var name: String? = null,
    @ColumnInfo override var difficulty: Difficulty = Difficulty.EASY,
    @ColumnInfo override var iconId: Int? = null,
    @ColumnInfo override val dateCreated: Instant? = Instant.now(),
) : Quest()