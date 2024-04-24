package com.brandonjamesyoung.levelup.data

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.constants.ICON_SCALE_UP_RATE
import com.brandonjamesyoung.levelup.constants.IconGroup
import com.brandonjamesyoung.levelup.utility.IconHelper.Companion.scaleUpByteArray
import java.time.Instant

@Entity
data class Icon(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo var name: String? = null,
    @ColumnInfo var image: ByteArray,
    @ColumnInfo var imageWidth: Int,
    @ColumnInfo var imageHeight: Int,
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
        if (imageWidth != other.imageWidth) return false
        if (imageHeight != other.imageHeight) return false
        if (iconGroup != other.iconGroup) return false
        if (dateCreated != other.dateCreated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + image.contentHashCode()
        result = 31 * result + imageWidth.hashCode()
        result = 31 * result + imageHeight.hashCode()
        result = 31 * result + iconGroup.hashCode()
        result = 31 * result + (dateCreated?.hashCode() ?: 0)
        return result
    }

    // Return the icon image as a drawable
    fun getDrawable(resources: Resources): Drawable {
        return scaleUpByteArray(image, imageWidth, imageHeight, ICON_SCALE_UP_RATE, resources)
    }
}