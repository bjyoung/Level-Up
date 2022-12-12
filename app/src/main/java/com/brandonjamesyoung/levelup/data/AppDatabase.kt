package com.brandonjamesyoung.levelup.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brandonjamesyoung.levelup.shared.*
import kotlinx.coroutines.*
import com.brandonjamesyoung.levelup.shared.Difficulty.EASY
import com.brandonjamesyoung.levelup.shared.Difficulty.MEDIUM
import com.brandonjamesyoung.levelup.shared.Difficulty.HARD
import com.brandonjamesyoung.levelup.shared.Difficulty.EXPERT

@Database(
    entities = [
        Quest::class,
        Player::class,
        Settings::class,
        Difficulty::class,
        Item::class,
        Icon::class
    ],
    version = 1,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun playerDao(): PlayerDao
    abstract fun settingsDao(): SettingsDao
    abstract fun difficultyDao(): DifficultyDao
    abstract fun itemDao(): ItemDao
    abstract fun iconDao(): IconDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this){
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(AppDatabaseCallback())
                .build()
        }

        private class AppDatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        initializeDatabase(
                            database.playerDao(),
                            database.settingsDao(),
                            database.difficultyDao()
                        )
                    }
                }
            }

            suspend fun initializeDatabase(
                playerDao: PlayerDao,
                settingsDao: SettingsDao,
                difficultyDao: DifficultyDao
            ) {
                val initPlayer = Player()
                playerDao.insert(initPlayer)
                val initSettings = Settings()
                settingsDao.insert(initSettings)
                val difficulties = getInitDifficulties()

                for (difficulty in difficulties) {
                    difficultyDao.insert(difficulty)
                }
            }

            private fun getInitDifficulties() : List<Difficulty> {
                val easy = Difficulty(
                    code = EASY,
                    expReward = INIT_EASY_EXP,
                    pointsReward = INIT_EASY_POINTS
                )

                val medium = Difficulty(
                    code = MEDIUM,
                    expReward = INIT_MEDIUM_EXP,
                    pointsReward = INIT_MEDIUM_POINTS
                )

                val hard = Difficulty(
                    code = HARD,
                    expReward = INIT_HARD_EXP,
                    pointsReward = INIT_HARD_POINTS
                )

                val expert = Difficulty(
                    code = EXPERT,
                    expReward = INIT_EXPERT_EXP,
                    pointsReward = INIT_EXPERT_POINTS
                )

                return listOf(easy, medium, hard, expert)
            }
        }
    }
}