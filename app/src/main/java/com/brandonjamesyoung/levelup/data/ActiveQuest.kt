package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import com.brandonjamesyoung.levelup.constants.Difficulty
import java.time.Instant

@Entity(indices = [Index(value = ["iconId"])])
data class ActiveQuest(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
    @ColumnInfo override var name: String? = null,
    @ColumnInfo override var difficulty: Difficulty = Difficulty.EASY,
    @ColumnInfo override var iconId: Int? = null,
    @ColumnInfo override val dateCreated: Instant? = Instant.now(),
) : Quest() {
    override fun equals(other: Any?): Boolean {
        return (other is ActiveQuest)
                && id == other.id
                && name == other.name
                && iconId == other.iconId
                && dateCreated == other.dateCreated
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (iconId ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + (dateCreated?.hashCode() ?: 0)
        return result
    }
}