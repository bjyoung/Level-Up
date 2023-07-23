package com.brandonjamesyoung.levelup.utility

import android.util.Log

class ThreadHelper {
    companion object {
        fun logCurrentThread(tag: String) {
            Log.i(tag, "Current thread is: ${Thread.currentThread().name}")
        }
    }
}