package com.brandonjamesyoung.levelup.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brandonjamesyoung.levelup.shared.DATABASE_NAME
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Database(
    entities = [Quest::class, Player::class, Settings::class],
    version = 4,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun playerDao(): PlayerDao
    abstract fun settingsDao(): SettingsDao

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
                .addMigrations(MIGRATION_3_4)
                .build()
        }

        private class AppDatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                instance?.let { database ->
                    MainScope().launch {
                        initializeDatabase(database.playerDao(), database.settingsDao())
                    }
                }
            }

            suspend fun initializeDatabase(playerDao: PlayerDao, settingsDao: SettingsDao) {
                val initPlayer = Player()
                playerDao.insert(initPlayer)

                val initSettings = Settings()
                settingsDao.insert(initSettings)
            }
        }
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `PlayerTemp` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, " +
                    "`rt` INTEGER NOT NULL, " +
                    "`lvl` INTEGER NOT NULL, " +
                    "`totalExp` INTEGER NOT NULL, " +
                    "`currentLvlExp` INTEGER NOT NULL, " +
                    "`expToLvlUp` INTEGER NOT NULL)"
        )

        database.execSQL(
            "INSERT INTO `PlayerTemp` (`id`, `name`, `rt`, `lvl`, `totalExp`, `currentLvlExp`, `expToLvlUp`) " +
                    "SELECT `id`, `name`, `rp`, `lvl`, 0, `exp`, `expToLvlUp` from `Player`"
        )

        database.execSQL("DROP TABLE `Player`")
        database.execSQL("ALTER TABLE `PlayerTemp` RENAME TO `Player`")
    }
}