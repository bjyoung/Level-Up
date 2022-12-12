package com.brandonjamesyoung.levelup.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.shared.IconGroup
import java.time.Instant

@Entity
data class Icon(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo var name: String? = null,
    @ColumnInfo var image: ByteArray,
    @ColumnInfo var iconGroup: IconGroup = IconGroup.SPADES,
    @ColumnInfo val dateCreated: Instant? = Instant.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Icon

        if (id != other.id) return false
        if (name != other.name) return false
        if (!image.contentEquals(other.image)) return false
        if (iconGroup != other.iconGroup) return false
        if (dateCreated != other.dateCreated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + image.contentHashCode()
        result = 31 * result + iconGroup.hashCode()
        result = 31 * result + (dateCreated?.hashCode() ?: 0)
        return result
    }
}