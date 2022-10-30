package com.brandonjamesyoung.levelup.shared

import android.content.Context
import android.widget.Toast

class ToastHelper {
    companion object {
        fun showToast(message: String, context: Context, displayTime: Int = Toast.LENGTH_SHORT) {
            val toast = Toast.makeText(
                context,
                message,
                displayTime
            )

            toast.show()
        }
    }
}