package com.brandonjamesyoung.levelup.shared

import android.graphics.Color
import androidx.core.graphics.ColorUtils

class ColorHelper {
    companion object {
        // To darken, the factor should be between 0 and 1
        fun darken(color: Int, factor: Float): Int {
            return ColorUtils.blendARGB(color, Color.BLACK, factor)
        }
    }
}