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
import com.brandonjamesyoung.levelup.constants.Difficulty.*
import com.brandonjamesyoung.levelup.constants.INIT_EASY_EXP
import com.brandonjamesyoung.levelup.constants.INIT_EASY_POINTS
import com.brandonjamesyoung.levelup.constants.INIT_EXPERT_EXP
import com.brandonjamesyoung.levelup.constants.INIT_EXPERT_POINTS
import com.brandonjamesyoung.levelup.constants.INIT_HARD_EXP
import com.brandonjamesyoung.levelup.constants.INIT_HARD_POINTS
import com.brandonjamesyoung.levelup.constants.INIT_MEDIUM_EXP
import com.brandonjamesyoung.levelup.constants.INIT_MEDIUM_POINTS
import com.brandonjamesyoung.levelup.constants.IconGroup
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

            private fun getDrawableWidth(resourceId: Int): Dimensions {
                val options = BitmapFactory.Options()
                options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
                val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
                return Dimensions(bitmap.width, bitmap.height)
            }

            private suspend fun initializeIcons(iconDao: IconDao) {
                val iconFileNameTriples = listOf(
                    Triple("Apple", R.drawable.apple_icon, IconGroup.HEARTS),
                    Triple("Archive", R.drawable.archive_icon, IconGroup.HEARTS),
                    Triple("Arrow (Left)", R.drawable.arrow_left_icon, IconGroup.DIAMONDS),
                    Triple("Arrow (Right)", R.drawable.arrow_right_icon, IconGroup.DIAMONDS),
                    Triple("Arrow (Up)", R.drawable.arrow_up_icon, IconGroup.DIAMONDS),
                    Triple("Arrow (Down)", R.drawable.arrow_down_icon, IconGroup.DIAMONDS),
                    Triple("Bandage", R.drawable.bandage_icon, IconGroup.HEARTS),
                    Triple("Bank", R.drawable.bank_icon, IconGroup.HEARTS),
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
                    Triple("Car", R.drawable.car_icon, IconGroup.CLUBS),
                    Triple("Camera", R.drawable.camera_icon, IconGroup.HEARTS),
                    Triple("Cent", R.drawable.cent_icon, IconGroup.DIAMONDS),
                    Triple("Clock", R.drawable.clock_icon, IconGroup.HEARTS),
                    Triple("Clubs", R.drawable.clubs_icon, IconGroup.CLUBS),
                    Triple("Commit", R.drawable.commit_icon, IconGroup.SPADES),
                    Triple("Copy", R.drawable.copy_icon, IconGroup.SPADES),
                    Triple("Credit Card", R.drawable.credit_card_icon, IconGroup.HEARTS),
                    Triple("Crop", R.drawable.crop_icon, IconGroup.SPADES),
                    Triple("Cross Stitch", R.drawable.cross_stitch_icon, IconGroup.CLUBS),
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
                    Triple("Egg", R.drawable.egg_icon, IconGroup.HEARTS),
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
                    Triple("Leaf", R.drawable.leaf_icon, IconGroup.CLUBS),
                    Triple("Light Bulb", R.drawable.light_bulb_icon_off, IconGroup.HEARTS),
                    Triple("Lock", R.drawable.lock_icon, IconGroup.HEARTS),
                    Triple("Magnifying Glass", R.drawable.magnifying_glass_icon, IconGroup.CLUBS),
                    Triple("Map Marker", R.drawable.map_marker_icon, IconGroup.CLUBS),
                    Triple("Martini Glass", R.drawable.martini_glass_icon, IconGroup.CLUBS),
                    Triple("Med Pack", R.drawable.med_pack_icon, IconGroup.CLUBS),
                    Triple("Minus", R.drawable.minus_icon, IconGroup.DIAMONDS),
                    Triple("Moon", R.drawable.moon_icon, IconGroup.CLUBS),
                    Triple("Outlet", R.drawable.outlet_icon, IconGroup.HEARTS),
                    Triple("Play", R.drawable.play_icon, IconGroup.SPADES),
                    Triple("Plug", R.drawable.plug_icon, IconGroup.HEARTS),
                    Triple("Plus", R.drawable.plus_icon, IconGroup.DIAMONDS),
                    Triple("Pointer Arrow", R.drawable.arrow_pointer_icon, IconGroup.SPADES),
                    Triple("Potion", R.drawable.potion_icon, IconGroup.CLUBS),
                    Triple("Power", R.drawable.power_button_icon, IconGroup.SPADES),
                    Triple("Print", R.drawable.print_icon, IconGroup.SPADES),
                    Triple("Ranking", R.drawable.ranking_icon, IconGroup.CLUBS),
                    Triple("Road", R.drawable.road_icon, IconGroup.CLUBS),
                    Triple("Save", R.drawable.save_icon, IconGroup.SPADES),
                    Triple("Shield", R.drawable.shield_icon, IconGroup.CLUBS),
                    Triple("Shine", R.drawable.shine_icon, IconGroup.DIAMONDS),
                    Triple("Spades", R.drawable.spades_icon, IconGroup.SPADES),
                    Triple("Storage", R.drawable.storage_icon, IconGroup.HEARTS),
                    Triple("Sun", R.drawable.sun_icon, IconGroup.CLUBS),
                    Triple("Sword", R.drawable.sword_icon, IconGroup.CLUBS),
                    Triple("Target", R.drawable.target_icon, IconGroup.DIAMONDS),
                    Triple("Terminal", R.drawable.terminal_icon, IconGroup.SPADES),
                    Triple("Top Hat", R.drawable.top_hat_icon, IconGroup.HEARTS),
                    Triple("Tower", R.drawable.tower_icon, IconGroup.CLUBS),
                    Triple("Train Car", R.drawable.train_car_icon, IconGroup.CLUBS),
                    Triple("Upload", R.drawable.upload_icon, IconGroup.SPADES),
                    Triple("Window", R.drawable.window_icon, IconGroup.HEARTS),
                )

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