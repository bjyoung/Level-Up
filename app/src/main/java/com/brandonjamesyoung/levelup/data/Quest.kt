package com.brandonjamesyoung.levelup.data

import android.content.Context
import android.graphics.Color as GraphicsColor
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.Difficulty
import com.brandonjamesyoung.levelup.constants.EASY_COLOR
import com.brandonjamesyoung.levelup.constants.EXPERT_COLOR
import com.brandonjamesyoung.levelup.constants.HARD_COLOR
import com.brandonjamesyoung.levelup.constants.MEDIUM_COLOR
import java.time.Instant

abstract class Quest {
    abstract var id: Int
    abstract var name: String?
    abstract var difficulty: Difficulty
    abstract var iconId: Int?
    abstract val dateCreated: Instant?

    fun getName(context: Context) : String {
        return name ?: context.getString(R.string.placeholder_text)
    }

    fun getColorHex() : Long {
        return when(difficulty) {
            Difficulty.EASY -> EASY_COLOR
            Difficulty.MEDIUM -> MEDIUM_COLOR
            Difficulty.HARD -> HARD_COLOR
            Difficulty.EXPERT -> EXPERT_COLOR
        }
    }

    fun getGraphicsColor() : GraphicsColor {
        return GraphicsColor.valueOf(getColorHex().toInt())
    }
}