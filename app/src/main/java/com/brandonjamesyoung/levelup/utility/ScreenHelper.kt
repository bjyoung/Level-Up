package com.brandonjamesyoung.levelup.utility

import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics

class ScreenHelper {
    companion object {
        // Get screen width in px
        fun getScreenWidth(windowManager: WindowManager): Int {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val windowInsets = windowMetrics.windowInsets
            val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            return windowMetrics.bounds.width() - insets.left - insets.right
        }

        // Get screen height in px
        fun getScreenHeight(windowManager: WindowManager): Int {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val windowInsets = windowMetrics.windowInsets
            val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            return windowMetrics.bounds.height() - insets.top - insets.bottom
        }
    }
}