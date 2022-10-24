package com.brandonjamesyoung.levelup.data

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun toInstant(str: String?): Instant? {
        return if (str != null) Instant.parse(str) else null
    }

    @TypeConverter
    fun toString(instant: Instant?): String? {
        return instant?.toString()
    }
}