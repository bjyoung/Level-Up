package com.brandonjamesyoung.levelup.utility

import android.content.res.Configuration
import android.content.res.Resources

class OrientationManager {
    companion object {
        fun inPortraitMode(resources: Resources): Boolean {
            return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }
    }
}