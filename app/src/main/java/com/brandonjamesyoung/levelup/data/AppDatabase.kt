package com.brandonjamesyoung.levelup.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brandonjamesyoung.levelup.shared.DATABASE_NAME
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Database(
    entities = [Quest::class, Player::class],
    version = 2,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun playerDao(): PlayerDao

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
                    MainScope().launch {
                        initializeDatabase(database.playerDao())
                    }
                }
            }

            suspend fun initializeDatabase(playerDao: PlayerDao) {
                val initPlayer = Player()
                playerDao.insert(initPlayer)
            }
        }
    }
}