package com.brandonjamesyoung.levelup

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class LevelUpApplication : Application() {
    val applicationScope  = CoroutineScope(SupervisorJob())
}
