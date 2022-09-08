package com.brandonjamesyoung.levelup

import android.app.Application
import com.brandonjamesyoung.levelup.data.AppDatabase
import com.brandonjamesyoung.levelup.data.QuestRepository

class LevelUpApplication : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { QuestRepository(database.questDao()) }
}
