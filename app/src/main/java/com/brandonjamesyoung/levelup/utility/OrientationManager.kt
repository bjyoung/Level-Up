package com.brandonjamesyoung.levelup.utility

import android.content.res.Configuration
import android.content.res.Resources

class OrientationManager {
    companion object {
        // To darken, the factor should be between 0 and 1
        fun inPortraitMode(resources: Resources): Boolean {
            return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }
    }
}