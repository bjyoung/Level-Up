package com.brandonjamesyoung.levelup.data

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.constants.ICON_SCALE_UP_RATE
import com.brandonjamesyoung.levelup.constants.IconGroup
import java.time.Instant
import androidx.core.graphics.scale

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
    override fun equals(other: Any?) : Boolean {
        return (other is Icon)
                && id == other.id
                && name == other.name
                && image.contentEquals(other.image)
                && imageWidth == other.imageWidth
                && imageHeight == other.imageHeight
                && iconGroup == other.iconGroup
                && dateCreated == other.dateCreated
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + imageWidth
        result = 31 * result + imageHeight
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + image.contentHashCode()
        result = 31 * result + iconGroup.hashCode()
        result = 31 * result + (dateCreated?.hashCode() ?: 0)
        return result
    }

    fun scaleUpByteArray(
        image: ByteArray,
        width: Int,
        height: Int,
        scaleUpRate: Int,
        resources: Resources
    ): Drawable {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        val scaledBitmap = bitmap.scale(width * scaleUpRate, height * scaleUpRate, false)
        return scaledBitmap.toDrawable(resources)
    }

    // TODO remove this if unneeded after the compose updates are all done
    // Return the icon image as a drawable
    fun getDrawable(resources: Resources): Drawable {
        return scaleUpByteArray(image, imageWidth, imageHeight, ICON_SCALE_UP_RATE, resources)
    }

    fun toImageBitmap() : ImageBitmap {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

        // Scale up so the icons are not blurry
        return bitmap.scale(
            imageWidth * ICON_SCALE_UP_RATE,
            imageHeight * ICON_SCALE_UP_RATE,
            false
        ).asImageBitmap()
    }
}