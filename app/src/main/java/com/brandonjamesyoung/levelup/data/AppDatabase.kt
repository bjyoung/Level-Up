package com.brandonjamesyoung.levelup.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brandonjamesyoung.levelup.constants.DATABASE_NAME
import kotlinx.coroutines.*

@Database(
    entities = [
        Quest::class,
        CompletedQuest::class,
        Player::class,
        Settings::class,
        Difficulty::class,
        ShopItem::class,
        PurchasedItem::class,
        Icon::class
    ],
    version = 1,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao

    abstract fun questHistoryDao(): QuestHistoryDao

    abstract fun playerDao(): PlayerDao

    abstract fun settingsDao(): SettingsDao

    abstract fun difficultyDao(): DifficultyDao

    abstract fun shopItemDao(): ShopItemDao

    abstract fun itemHistoryDao(): ItemHistoryDao

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
                .setJournalMode(JournalMode.TRUNCATE)
                .addCallback(AppDatabaseCallback(context))
                .build()
        }

        private class AppDatabaseCallback(val context: Context) : Callback() {
            private val initDb: InitDatabase = InitDatabase()

            private suspend fun initializeDifficulties(difficultyDao: DifficultyDao) {
                val difficulties = initDb.getInitDifficulties()

                for (difficulty in difficulties) {
                    difficultyDao.insert(difficulty)
                }
            }

            suspend fun initializeDatabase(
                playerDao: PlayerDao,
                settingsDao: SettingsDao,
                difficultyDao: DifficultyDao,
                iconDao: IconDao
            ) {
                val initPlayer = Player()
                playerDao.insert(initPlayer)
                val initSettings = Settings()
                settingsDao.insert(initSettings)
                initializeDifficulties(difficultyDao)
                initDb.initializeDefaultIcons(iconDao, context)
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        initializeDatabase(
                            database.playerDao(),
                            database.settingsDao(),
                            database.difficultyDao(),
                            database.iconDao(),
                        )
                    }
                }
            }
        }
    }
}