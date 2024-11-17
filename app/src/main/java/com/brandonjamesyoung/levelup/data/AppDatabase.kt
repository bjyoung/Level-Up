package com.brandonjamesyoung.levelup.data

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.core.content.res.ResourcesCompat
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.DATABASE_NAME
import com.brandonjamesyoung.levelup.utility.TypeConverter.Companion.convertDrawableToByteArray
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

            private fun getDrawable(id: Int) : Drawable {
                val drawable = try {
                    ResourcesCompat.getDrawable(context.resources, id, context.theme)
                } catch(e: NotFoundException) {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.question_mark_icon,
                        context.theme
                    )
                }

                return drawable!!
            }

            private fun getDrawableWidth(resourceId: Int): Dimensions {
                val options = BitmapFactory.Options()
                options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
                val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
                return Dimensions(bitmap.width, bitmap.height)
            }

            private suspend fun initializeIcons(iconDao: IconDao) {
                val iconFileNameTriples = initDb.getInitIconData()

                for (triple in iconFileNameTriples) {
                    val iconId: Int = triple.second
                    val drawable: Drawable = getDrawable(iconId)
                    val dimensions = getDrawableWidth(iconId)

                    val byteArray = convertDrawableToByteArray(
                        drawable, dimensions.width, dimensions.height
                    )

                    val icon = Icon(
                        name = triple.first,
                        image = byteArray,
                        imageWidth = dimensions.width,
                        imageHeight = dimensions.height,
                        iconGroup = triple.third
                    )

                    iconDao.insert(icon)
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
                initializeIcons(iconDao)
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