package com.brandonjamesyoung.levelup.utility

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

class InsetHandler {
    companion object {
        // Since Android 35+ enforces edge-to-edge, this applies padding based on system UI
        fun addInsetPadding(rootView: View) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

                v.updateLayoutParams<MarginLayoutParams> {
                    topMargin = insets.top
                    bottomMargin = insets.bottom
                    leftMargin = insets.left
                    rightMargin = insets.right
                }

                WindowInsetsCompat.CONSUMED
            }
        }
    }
}