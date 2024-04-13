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
                    Triple("Apple", R.drawable.apple_icon, IconGroup.DIAMONDS),
                    Triple("Archive", R.drawable.archive_icon, IconGroup.HEARTS),
                    Triple("Arrow (Left)", R.drawable.arrow_left_icon, IconGroup.SPADES),
                    Triple("Arrow (Right)", R.drawable.arrow_right_icon, IconGroup.SPADES),
                    Triple("Arrow (Up)", R.drawable.arrow_up_icon, IconGroup.SPADES),
                    Triple("Arrow (Down)", R.drawable.arrow_down_icon, IconGroup.SPADES),
                    Triple("Bandage", R.drawable.bandage_icon, IconGroup.HEARTS),
                    Triple("Bank", R.drawable.bank_icon, IconGroup.DIAMONDS),
                    Triple("Barcode", R.drawable.barcode_icon, IconGroup.DIAMONDS),
                    Triple("Battery", R.drawable.battery_icon, IconGroup.HEARTS),
                    Triple("Beer", R.drawable.beer_icon, IconGroup.DIAMONDS),
                    Triple("Bicycle", R.drawable.bicycle_icon, IconGroup.DIAMONDS),
                    Triple("Boat", R.drawable.boat_icon, IconGroup.CLUBS),
                    Triple("Bone", R.drawable.bone_icon, IconGroup.CLUBS),
                    Triple("Book", R.drawable.book_icon, IconGroup.HEARTS),
                    Triple("Bookmark", R.drawable.bookmark_icon, IconGroup.HEARTS),
                    Triple("Bow", R.drawable.bow_and_arrow_icon, IconGroup.CLUBS),
                    Triple("Building", R.drawable.building_icon, IconGroup.DIAMONDS),
                    Triple("Bullet List", R.drawable.bullet_list_icon, IconGroup.SPADES),
                    Triple("Cactus", R.drawable.cactus_icon, IconGroup.CLUBS),
                    Triple("Calendar", R.drawable.calendar_icon, IconGroup.HEARTS),
                    Triple("Car", R.drawable.car_icon, IconGroup.DIAMONDS),
                    Triple("Camera", R.drawable.camera_icon, IconGroup.DIAMONDS),
                    Triple("Cent", R.drawable.cent_icon, IconGroup.SPADES),
                    Triple("Clock", R.drawable.clock_icon, IconGroup.HEARTS),
                    Triple("Clubs", R.drawable.clubs_icon, IconGroup.CLUBS),
                    Triple("Coffee", R.drawable.coffee_icon, IconGroup.DIAMONDS),
                    Triple("Controller", R.drawable.controller_icon, IconGroup.CLUBS),
                    Triple("Commit", R.drawable.commit_icon, IconGroup.SPADES),
                    Triple("Copy", R.drawable.copy_icon, IconGroup.SPADES),
                    Triple("Credit Card", R.drawable.credit_card_icon, IconGroup.DIAMONDS),
                    Triple("Crop", R.drawable.crop_icon, IconGroup.SPADES),
                    Triple("Cross Stitch", R.drawable.cross_stitch_icon, IconGroup.DIAMONDS),
                    Triple("Cube", R.drawable.cube_icon, IconGroup.SPADES),
                    Triple("Dash", R.drawable.dash_icon, IconGroup.CLUBS),
                    Triple("Desk", R.drawable.desk_icon, IconGroup.HEARTS),
                    Triple("Diamond", R.drawable.diamond_icon, IconGroup.DIAMONDS),
                    Triple("Direction Signs", R.drawable.direction_signs_icon, IconGroup.CLUBS),
                    Triple("Dollar", R.drawable.dollar_sign_icon, IconGroup.DIAMONDS),
                    Triple("Door", R.drawable.door_icon, IconGroup.HEARTS),
                    Triple("Dot", R.drawable.open_dot_icon, IconGroup.SPADES),
                    Triple("Dot (Filled)", R.drawable.filled_dot_icon, IconGroup.SPADES),
                    Triple("Download", R.drawable.download_icon, IconGroup.SPADES),
                    Triple("Dumbbell", R.drawable.dumbbell_icon, IconGroup.HEARTS),
                    Triple("Egg", R.drawable.egg_icon, IconGroup.DIAMONDS),
                    Triple("Envelope", R.drawable.envelope_icon, IconGroup.HEARTS),
                    Triple("Fast Forward", R.drawable.fast_forward_icon, IconGroup.SPADES),
                    Triple("File", R.drawable.file_icon, IconGroup.SPADES),
                    Triple("Film", R.drawable.film_icon, IconGroup.DIAMONDS),
                    Triple("Fish", R.drawable.fish_icon, IconGroup.DIAMONDS),
                    Triple("Flask", R.drawable.flask_icon, IconGroup.CLUBS),
                    Triple("Gate", R.drawable.gate_icon, IconGroup.CLUBS),
                    Triple("Gear", R.drawable.gear_icon, IconGroup.DIAMONDS),
                    Triple("Group", R.drawable.group_icon, IconGroup.SPADES),
                    Triple("Headphones", R.drawable.headphones_icon, IconGroup.HEARTS),
                    Triple("Heart", R.drawable.heart_icon, IconGroup.HEARTS),
                    Triple("Helmet", R.drawable.helmet_icon, IconGroup.CLUBS),
                    Triple("House", R.drawable.house_icon, IconGroup.HEARTS),
                    Triple("Hourglass", R.drawable.hourglass_icon, IconGroup.HEARTS),
                    Triple("Key", R.drawable.key_icon, IconGroup.HEARTS),
                    Triple("Landscape", R.drawable.landscape_icon, IconGroup.CLUBS),
                    Triple("Leaf", R.drawable.leaf_icon, IconGroup.CLUBS),
                    Triple("Light Bulb", R.drawable.light_bulb_icon_off, IconGroup.HEARTS),
                    Triple("Lock", R.drawable.lock_icon, IconGroup.HEARTS),
                    Triple("Magnifying Glass", R.drawable.magnifying_glass_icon, IconGroup.DIAMONDS),
                    Triple("Map Marker", R.drawable.map_marker_icon, IconGroup.CLUBS),
                    Triple("Martini Glass", R.drawable.martini_glass_icon, IconGroup.DIAMONDS),
                    Triple("Med Pack", R.drawable.med_pack_icon, IconGroup.CLUBS),
                    Triple("Minus", R.drawable.minus_icon, IconGroup.SPADES),
                    Triple("Monitor", R.drawable.monitor_icon, IconGroup.SPADES),
                    Triple("Moon", R.drawable.moon_icon, IconGroup.CLUBS),
                    Triple("Notepad", R.drawable.notepad_icon, IconGroup.HEARTS),
                    Triple("Outlet", R.drawable.outlet_icon, IconGroup.HEARTS),
                    Triple("Pawn", R.drawable.pawn_icon, IconGroup.DIAMONDS),
                    Triple("Pencil", R.drawable.pencil_icon, IconGroup.DIAMONDS),
                    Triple("Phone", R.drawable.phone_icon, IconGroup.HEARTS),
                    Triple("Picture Frame", R.drawable.picture_frame_icon, IconGroup.HEARTS),
                    Triple("Pie", R.drawable.pie_icon, IconGroup.DIAMONDS),
                    Triple("Play", R.drawable.play_icon, IconGroup.SPADES),
                    Triple("Plug", R.drawable.plug_icon, IconGroup.HEARTS),
                    Triple("Plus", R.drawable.plus_icon, IconGroup.SPADES),
                    Triple("Pointer Arrow", R.drawable.arrow_pointer_icon, IconGroup.SPADES),
                    Triple("Pot", R.drawable.pot_icon, IconGroup.HEARTS),
                    Triple("Potion", R.drawable.potion_icon, IconGroup.CLUBS),
                    Triple("Power", R.drawable.power_button_icon, IconGroup.SPADES),
                    Triple("Print", R.drawable.print_icon, IconGroup.SPADES),
                    Triple("Ranking", R.drawable.ranking_icon, IconGroup.SPADES),
                    Triple("Refresh", R.drawable.refresh_icon, IconGroup.SPADES),
                    Triple("Road", R.drawable.road_icon, IconGroup.CLUBS),
                    Triple("Save", R.drawable.save_icon, IconGroup.SPADES),
                    Triple("Scissors", R.drawable.scissors_icon, IconGroup.DIAMONDS),
                    Triple("Shield", R.drawable.shield_icon, IconGroup.CLUBS),
                    Triple("Shine", R.drawable.shine_icon, IconGroup.SPADES),
                    Triple("Smart Phone", R.drawable.smart_phone_icon, IconGroup.SPADES),
                    Triple("Spades", R.drawable.spades_icon, IconGroup.SPADES),
                    Triple("Storage", R.drawable.storage_icon, IconGroup.HEARTS),
                    Triple("Sun", R.drawable.sun_icon, IconGroup.CLUBS),
                    Triple("Sushi", R.drawable.sushi_icon, IconGroup.DIAMONDS),
                    Triple("Sword", R.drawable.sword_icon, IconGroup.CLUBS),
                    Triple("Target", R.drawable.target_icon, IconGroup.CLUBS),
                    Triple("Terminal", R.drawable.terminal_icon, IconGroup.SPADES),
                    Triple("Tire", R.drawable.tire_icon, IconGroup.CLUBS),
                    Triple("Top Hat", R.drawable.top_hat_icon, IconGroup.CLUBS),
                    Triple("Tooth", R.drawable.tooth_icon, IconGroup.HEARTS),
                    Triple("Tower", R.drawable.tower_icon, IconGroup.CLUBS),
                    Triple("Train Car", R.drawable.train_car_icon, IconGroup.CLUBS),
                    Triple("Trash", R.drawable.trash_bin_icon, IconGroup.HEARTS),
                    Triple("Treasure Chest", R.drawable.treasure_chest_icon, IconGroup.CLUBS),
                    Triple("Trophy", R.drawable.trophy_icon, IconGroup.CLUBS),
                    Triple("Unlock", R.drawable.unlock_icon, IconGroup.HEARTS),
                    Triple("Update", R.drawable.update_file_icon, IconGroup.SPADES),
                    Triple("Upload", R.drawable.upload_icon, IconGroup.SPADES),
                    Triple("Video Camera", R.drawable.video_camera_icon, IconGroup.DIAMONDS),
                    Triple("Window", R.drawable.window_icon, IconGroup.HEARTS),
                    Triple("Wrench", R.drawable.wrench_icon, IconGroup.DIAMONDS)
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