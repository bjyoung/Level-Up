package com.brandonjamesyoung.levelup.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brandonjamesyoung.levelup.data.Migration.Companion.MIGRATION_3_4
import com.brandonjamesyoung.levelup.data.Migration.Companion.MIGRATION_4_5
import com.brandonjamesyoung.levelup.shared.DATABASE_NAME
import kotlinx.coroutines.*
import com.brandonjamesyoung.levelup.shared.Difficulty.EASY
import com.brandonjamesyoung.levelup.shared.Difficulty.MEDIUM
import com.brandonjamesyoung.levelup.shared.Difficulty.HARD
import com.brandonjamesyoung.levelup.shared.Difficulty.EXPERT

@Database(
    entities = [Quest::class, Player::class, Settings::class, Difficulty::class],
    version = 10,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3),
        AutoMigration (from = 5, to = 6),
        AutoMigration (from = 6, to = 7, spec = Migration.MigrationSpec6To7::class),
        AutoMigration (from = 7, to = 8, spec = Migration.MigrationSpec7To8::class),
        AutoMigration (from = 8, to = 9),
        AutoMigration (from = 9, to = 10),
    ],
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun playerDao(): PlayerDao
    abstract fun settingsDao(): SettingsDao
    abstract fun difficultyDao(): DifficultyDao

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
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
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
                val easy = Difficulty(code = EASY, expReward = 100, rtReward = 1)
                val medium = Difficulty(code = MEDIUM, expReward = 250, rtReward = 3)
                val hard = Difficulty(code = HARD, expReward = 600, rtReward = 6)
                val expert = Difficulty(code = EXPERT, expReward = 1500, rtReward = 12)
                val difficulties = listOf(easy, medium, hard, expert)

                for (difficulty in difficulties) {
                    difficultyDao.insert(difficulty)
                }
            }
        }
    }
}