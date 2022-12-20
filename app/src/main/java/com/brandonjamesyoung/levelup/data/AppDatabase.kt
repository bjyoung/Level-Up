package com.brandonjamesyoung.levelup.data

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.shared.*
import com.brandonjamesyoung.levelup.shared.ByteArrayHelper.Companion.convertDrawableToByteArray
import com.brandonjamesyoung.levelup.shared.Difficulty.*
import kotlinx.coroutines.*

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
                .addCallback(AppDatabaseCallback(context))
                .build()
        }

        private class AppDatabaseCallback(val context: Context) : Callback() {
            private suspend fun initializeDifficulties(difficultyDao: DifficultyDao) {
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

                val difficulties = listOf(easy, medium, hard, expert)

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

            private suspend fun initializeIcons(iconDao: IconDao) {
                val iconFileNamePairs = listOf(
                    Pair("Bandage", R.drawable.bandage_icon),
                    Pair("Barcode", R.drawable.barcode_icon),
                    Pair("Battery", R.drawable.battery_icon),
                    Pair("Beer", R.drawable.beer_icon),
                    Pair("Boat", R.drawable.boat_icon),
                    Pair("Book", R.drawable.book_icon),
                    Pair("Bookmark", R.drawable.bookmark_icon),
                    Pair("Bow", R.drawable.bow_and_arrow_icon),
                    Pair("Bullet List", R.drawable.bullet_list_icon),
                    Pair("Calendar", R.drawable.calendar_icon),
                    Pair("Camera", R.drawable.camera_icon),
                    Pair("Cent", R.drawable.cent_icon),
                    Pair("Clubs", R.drawable.clubs_icon),
                    Pair("Copy", R.drawable.copy_icon),
                    Pair("Crop", R.drawable.crop_icon),
                    Pair("Dash", R.drawable.dash_icon),
                    Pair("Diamond", R.drawable.diamond_icon),
                    Pair("Dollar Sign", R.drawable.dollar_sign_icon),
                    Pair("Door", R.drawable.door_icon),
                    Pair("Download", R.drawable.download_icon),
                    Pair("Dumbbell", R.drawable.dumbbell_icon),
                    Pair("Envelope", R.drawable.envelope_icon),
                    Pair("Film", R.drawable.film_icon),
                    Pair("Gear", R.drawable.gear_icon),
                    Pair("Heart", R.drawable.heart_icon),
                    Pair("House", R.drawable.house_icon),
                    Pair("Power", R.drawable.power_button_icon),
                )

                for (pair in iconFileNamePairs) {
                    val iconId = pair.second
                    val drawable = getDrawable(iconId)
                    val byteArray = convertDrawableToByteArray(drawable)

                    val icon = Icon(
                        name = pair.first,
                        image = byteArray
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