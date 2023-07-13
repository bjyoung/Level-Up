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
        CompletedQuest::class,
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

    abstract fun questHistoryDao(): QuestHistoryDao

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
                val iconFileNameTriples = listOf(
                    Triple("Archive", R.drawable.archive_icon, IconGroup.HEARTS),
                    Triple("Bandage", R.drawable.bandage_icon, IconGroup.HEARTS),
                    Triple("Barcode", R.drawable.barcode_icon, IconGroup.DIAMONDS),
                    Triple("Battery", R.drawable.battery_icon, IconGroup.SPADES),
                    Triple("Beer", R.drawable.beer_icon, IconGroup.CLUBS),
                    Triple("Boat", R.drawable.boat_icon, IconGroup.CLUBS),
                    Triple("Book", R.drawable.book_icon, IconGroup.CLUBS),
                    Triple("Bookmark", R.drawable.bookmark_icon, IconGroup.CLUBS),
                    Triple("Bow", R.drawable.bow_and_arrow_icon, IconGroup.CLUBS),
                    Triple("Building", R.drawable.building_icon, IconGroup.HEARTS),
                    Triple("Bullet List", R.drawable.bullet_list_icon, IconGroup.DIAMONDS),
                    Triple("Calendar", R.drawable.calendar_icon, IconGroup.HEARTS),
                    Triple("Camera", R.drawable.camera_icon, IconGroup.HEARTS),
                    Triple("Cent", R.drawable.cent_icon, IconGroup.DIAMONDS),
                    Triple("Clock", R.drawable.clock_icon, IconGroup.HEARTS),
                    Triple("Clubs", R.drawable.clubs_icon, IconGroup.CLUBS),
                    Triple("Copy", R.drawable.copy_icon, IconGroup.SPADES),
                    Triple("Credit Card", R.drawable.credit_card_icon, IconGroup.HEARTS),
                    Triple("Crop", R.drawable.crop_icon, IconGroup.SPADES),
                    Triple("Dash", R.drawable.dash_icon, IconGroup.CLUBS),
                    Triple("Desk", R.drawable.desk_icon, IconGroup.HEARTS),
                    Triple("Diamond", R.drawable.diamond_icon, IconGroup.DIAMONDS),
                    Triple("Direction Signs", R.drawable.direction_signs_icon, IconGroup.HEARTS),
                    Triple("Dollar", R.drawable.dollar_sign_icon, IconGroup.DIAMONDS),
                    Triple("Door", R.drawable.door_icon, IconGroup.HEARTS),
                    Triple("Dot", R.drawable.open_dot_icon, IconGroup.DIAMONDS),
                    Triple("Dot (Filled)", R.drawable.filled_dot_icon, IconGroup.DIAMONDS),
                    Triple("Download", R.drawable.download_icon, IconGroup.SPADES),
                    Triple("Dumbbell", R.drawable.dumbbell_icon, IconGroup.HEARTS),
                    Triple("Envelope", R.drawable.envelope_icon, IconGroup.HEARTS),
                    Triple("Fast Forward", R.drawable.fast_forward_icon, IconGroup.SPADES),
                    Triple("File", R.drawable.file_icon, IconGroup.SPADES),
                    Triple("Film", R.drawable.film_icon, IconGroup.CLUBS),
                    Triple("Fish", R.drawable.fish_icon, IconGroup.CLUBS),
                    Triple("Gear", R.drawable.gear_icon, IconGroup.CLUBS),
                    Triple("Heart", R.drawable.heart_icon, IconGroup.HEARTS),
                    Triple("House", R.drawable.house_icon, IconGroup.HEARTS),
                    Triple("Hourglass", R.drawable.hourglass_icon, IconGroup.HEARTS),
                    Triple("Key", R.drawable.key_icon, IconGroup.HEARTS),
                    Triple("Light Bulb", R.drawable.light_bulb_icon_off, IconGroup.HEARTS),
                    Triple("Lock", R.drawable.lock_icon, IconGroup.HEARTS),
                    Triple("Magnifying Glass", R.drawable.magnifying_glass_icon, IconGroup.CLUBS),
                    Triple("Map Marker", R.drawable.map_marker_icon, IconGroup.CLUBS),
                    Triple("Martini Glass", R.drawable.martini_glass_icon, IconGroup.CLUBS),
                    Triple("Med Pack", R.drawable.med_pack_icon, IconGroup.CLUBS),
                    Triple("Minus", R.drawable.minus_icon, IconGroup.DIAMONDS),
                    Triple("Play", R.drawable.play_icon, IconGroup.SPADES),
                    Triple("Plus", R.drawable.plus_icon, IconGroup.DIAMONDS),
                    Triple("Pointer Arrow", R.drawable.white_arrow_icon, IconGroup.SPADES),
                    Triple("Potion", R.drawable.potion_icon, IconGroup.CLUBS),
                    Triple("Power", R.drawable.power_button_icon, IconGroup.SPADES),
                    Triple("Road", R.drawable.road_icon, IconGroup.CLUBS),
                    Triple("Save", R.drawable.save_icon, IconGroup.SPADES),
                    Triple("Spades", R.drawable.spades_icon, IconGroup.SPADES),
                    Triple("Storage", R.drawable.storage_icon, IconGroup.HEARTS),
                    Triple("Target", R.drawable.target_icon, IconGroup.DIAMONDS),
                    Triple("Terminal", R.drawable.terminal_icon, IconGroup.SPADES),
                    Triple("Upload", R.drawable.upload_icon, IconGroup.SPADES),
                    Triple("Window", R.drawable.window_icon, IconGroup.HEARTS),
                )

                for (triple in iconFileNameTriples) {
                    val iconId = triple.second
                    val drawable = getDrawable(iconId)
                    val byteArray = convertDrawableToByteArray(drawable)

                    val icon = Icon(
                        name = triple.first,
                        image = byteArray,
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