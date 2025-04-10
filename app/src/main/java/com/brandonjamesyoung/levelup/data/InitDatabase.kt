package com.brandonjamesyoung.levelup.data

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.core.content.res.ResourcesCompat
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.Difficulty.EASY
import com.brandonjamesyoung.levelup.constants.Difficulty.EXPERT
import com.brandonjamesyoung.levelup.constants.Difficulty.HARD
import com.brandonjamesyoung.levelup.constants.Difficulty.MEDIUM
import com.brandonjamesyoung.levelup.constants.DEFAULT_EASY_EXP
import com.brandonjamesyoung.levelup.constants.DEFAULT_EASY_POINTS
import com.brandonjamesyoung.levelup.constants.DEFAULT_EXPERT_EXP
import com.brandonjamesyoung.levelup.constants.DEFAULT_EXPERT_POINTS
import com.brandonjamesyoung.levelup.constants.DEFAULT_HARD_EXP
import com.brandonjamesyoung.levelup.constants.DEFAULT_HARD_POINTS
import com.brandonjamesyoung.levelup.constants.DEFAULT_MEDIUM_EXP
import com.brandonjamesyoung.levelup.constants.DEFAULT_MEDIUM_POINTS
import com.brandonjamesyoung.levelup.constants.IconGroup
import com.brandonjamesyoung.levelup.utility.TypeConverter.Companion.convertDrawableToByteArray

class InitDatabase {
    fun getInitDifficulties() : List<Difficulty> {
        val easy = Difficulty(
            code = EASY,
            expReward = DEFAULT_EASY_EXP,
            pointsReward = DEFAULT_EASY_POINTS
        )

        val medium = Difficulty(
            code = MEDIUM,
            expReward = DEFAULT_MEDIUM_EXP,
            pointsReward = DEFAULT_MEDIUM_POINTS
        )

        val hard = Difficulty(
            code = HARD,
            expReward = DEFAULT_HARD_EXP,
            pointsReward = DEFAULT_HARD_POINTS
        )

        val expert = Difficulty(
            code = EXPERT,
            expReward = DEFAULT_EXPERT_EXP,
            pointsReward = DEFAULT_EXPERT_POINTS
        )

        return listOf(easy, medium, hard, expert)
    }

    private fun getInitIconData() : List<Triple<String, Int, IconGroup>> {
        return listOf(
            Triple("Apple", R.drawable.apple_icon, IconGroup.DIAMONDS),
            Triple("Approve", R.drawable.approve_icon, IconGroup.SPADES),
            Triple("Archive", R.drawable.archive_icon, IconGroup.HEARTS),
            Triple("Arrow (Left)", R.drawable.arrow_left_icon, IconGroup.SPADES),
            Triple("Arrow (Right)", R.drawable.arrow_right_icon, IconGroup.SPADES),
            Triple("Arrow (Up)", R.drawable.arrow_up_icon, IconGroup.SPADES),
            Triple("Arrow (Down)", R.drawable.arrow_down_icon, IconGroup.SPADES),
            Triple("Bandage", R.drawable.bandage_icon, IconGroup.HEARTS),
            Triple("Bank", R.drawable.bank_icon, IconGroup.CLUBS),
            Triple("Barcode", R.drawable.barcode_icon, IconGroup.DIAMONDS),
            Triple("Battery", R.drawable.battery_icon, IconGroup.HEARTS),
            Triple("Beer", R.drawable.beer_icon, IconGroup.DIAMONDS),
            Triple("Bike", R.drawable.bike_icon, IconGroup.CLUBS),
            Triple("Boat", R.drawable.boat_icon, IconGroup.CLUBS),
            Triple("Bone", R.drawable.bone_icon, IconGroup.DIAMONDS),
            Triple("Book", R.drawable.book_icon, IconGroup.HEARTS),
            Triple("Bookmark", R.drawable.bookmark_icon, IconGroup.HEARTS),
            Triple("Bow", R.drawable.bow_and_arrow_icon, IconGroup.CLUBS),
            Triple("Bug", R.drawable.bug_icon, IconGroup.SPADES),
            Triple("Building", R.drawable.building_icon, IconGroup.CLUBS),
            Triple("Bullet List", R.drawable.bullet_list_icon, IconGroup.SPADES),
            Triple("Button", R.drawable.button_icon, IconGroup.SPADES),
            Triple("Cactus", R.drawable.cactus_icon, IconGroup.CLUBS),
            Triple("Cake Slice", R.drawable.cake_slice_icon, IconGroup.DIAMONDS),
            Triple("Calendar", R.drawable.calendar_icon, IconGroup.HEARTS),
            Triple("Car", R.drawable.car_icon, IconGroup.CLUBS),
            Triple("Camera", R.drawable.camera_icon, IconGroup.DIAMONDS),
            Triple("Cent", R.drawable.cent_icon, IconGroup.SPADES),
            Triple("Chat", R.drawable.chat_icon, IconGroup.SPADES),
            Triple("Cheese", R.drawable.cheese_icon, IconGroup.DIAMONDS),
            Triple("Clock", R.drawable.clock_icon, IconGroup.HEARTS),
            Triple("Clubs", R.drawable.clubs_icon, IconGroup.CLUBS),
            Triple("Cookie", R.drawable.cookie_icon, IconGroup.DIAMONDS),
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
            Triple("Dice", R.drawable.dice_icon, IconGroup.DIAMONDS),
            Triple("Direction Signs", R.drawable.direction_signs_icon, IconGroup.CLUBS),
            Triple("Dollar", R.drawable.dollar_sign_icon, IconGroup.SPADES),
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
            Triple("Fire", R.drawable.fire_icon, IconGroup.CLUBS),
            Triple("Fish", R.drawable.fish_icon, IconGroup.DIAMONDS),
            Triple("Flask", R.drawable.flask_icon, IconGroup.CLUBS),
            Triple("Folder", R.drawable.folder_icon, IconGroup.SPADES),
            Triple("Gate", R.drawable.gate_icon, IconGroup.CLUBS),
            Triple("Gear", R.drawable.gear_icon, IconGroup.SPADES),
            Triple("Glasses", R.drawable.glasses_icon, IconGroup.HEARTS),
            Triple("Group", R.drawable.group_icon, IconGroup.SPADES),
            Triple("Headphones", R.drawable.headphones_icon, IconGroup.HEARTS),
            Triple("Heart", R.drawable.heart_icon, IconGroup.HEARTS),
            Triple("Helmet", R.drawable.helmet_icon, IconGroup.CLUBS),
            Triple("House", R.drawable.house_icon, IconGroup.HEARTS),
            Triple("Hourglass", R.drawable.hourglass_icon, IconGroup.HEARTS),
            Triple("Ice Cream", R.drawable.ice_cream_icon, IconGroup.HEARTS),
            Triple("Key", R.drawable.key_icon, IconGroup.HEARTS),
            Triple("Landscape", R.drawable.landscape_icon, IconGroup.CLUBS),
            Triple("Layers", R.drawable.layers_icon, IconGroup.SPADES),
            Triple("Leaf", R.drawable.leaf_icon, IconGroup.CLUBS),
            Triple("Lightning", R.drawable.lightning_icon, IconGroup.CLUBS),
            Triple("Light Bulb", R.drawable.light_bulb_icon_off, IconGroup.HEARTS),
            Triple("Lock", R.drawable.lock_icon, IconGroup.HEARTS),
            Triple("Search", R.drawable.search_icon, IconGroup.SPADES),
            Triple("Magnet", R.drawable.magnet_icon, IconGroup.HEARTS),
            Triple("Map Marker", R.drawable.map_marker_icon, IconGroup.CLUBS),
            Triple("Martini Glass", R.drawable.martini_glass_icon, IconGroup.DIAMONDS),
            Triple("Med Pack", R.drawable.med_pack_icon, IconGroup.HEARTS),
            Triple("Microphone", R.drawable.microphone_icon, IconGroup.SPADES),
            Triple("Minus", R.drawable.minus_icon, IconGroup.SPADES),
            Triple("Music Note", R.drawable.music_note_icon, IconGroup.DIAMONDS),
            Triple("Monitor", R.drawable.monitor_icon, IconGroup.SPADES),
            Triple("Moon", R.drawable.moon_icon, IconGroup.CLUBS),
            Triple("Notepad", R.drawable.notepad_icon, IconGroup.HEARTS),
            Triple("Outlet", R.drawable.outlet_icon, IconGroup.HEARTS),
            Triple("Pawn", R.drawable.pawn_icon, IconGroup.DIAMONDS),
            Triple("Pencil", R.drawable.pencil_icon, IconGroup.HEARTS),
            Triple("Phone", R.drawable.phone_icon, IconGroup.HEARTS),
            Triple("Picture Frame", R.drawable.picture_frame_icon, IconGroup.HEARTS),
            Triple("Pie", R.drawable.pie_icon, IconGroup.DIAMONDS),
            Triple("Play", R.drawable.play_icon, IconGroup.SPADES),
            Triple("Plug", R.drawable.plug_icon, IconGroup.HEARTS),
            Triple("Plus", R.drawable.plus_icon, IconGroup.SPADES),
            Triple("Pointer Arrow", R.drawable.arrow_pointer_icon, IconGroup.SPADES),
            Triple("Pot", R.drawable.pot_icon, IconGroup.DIAMONDS),
            Triple("Potion", R.drawable.potion_icon, IconGroup.CLUBS),
            Triple("Power", R.drawable.power_button_icon, IconGroup.SPADES),
            Triple("Puzzle", R.drawable.puzzle_icon, IconGroup.DIAMONDS),
            Triple("Print", R.drawable.print_icon, IconGroup.SPADES),
            Triple("Prism", R.drawable.prism_icon, IconGroup.SPADES),
            Triple("Question Mark", R.drawable.question_mark_icon, IconGroup.SPADES),
            Triple("Rain", R.drawable.rain_icon, IconGroup.CLUBS),
            Triple("Ramen", R.drawable.ramen_icon, IconGroup.DIAMONDS),
            Triple("Ranking", R.drawable.ranking_icon, IconGroup.SPADES),
            Triple("Rectangular Marquee", R.drawable.rectangular_marquee_icon, IconGroup.SPADES),
            Triple("Refresh", R.drawable.refresh_icon, IconGroup.SPADES),
            Triple("Replace", R.drawable.replace_icon, IconGroup.SPADES),
            Triple("Road", R.drawable.road_icon, IconGroup.CLUBS),
            Triple("Save", R.drawable.save_icon, IconGroup.SPADES),
            Triple("Scissors", R.drawable.scissors_icon, IconGroup.DIAMONDS),
            Triple("Shield", R.drawable.shield_icon, IconGroup.CLUBS),
            Triple("Shine", R.drawable.shine_icon, IconGroup.SPADES),
            Triple("Shirt Button", R.drawable.shirt_button_icon, IconGroup.HEARTS),
            Triple("Smart Phone", R.drawable.smart_phone_icon, IconGroup.SPADES),
            Triple("Soap", R.drawable.soap_icon, IconGroup.HEARTS),
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
            Triple("Triple Bars", R.drawable.triple_bars_icon, IconGroup.SPADES),
            Triple("Trophy", R.drawable.trophy_icon, IconGroup.CLUBS),
            Triple("Unlock", R.drawable.unlock_icon, IconGroup.HEARTS),
            Triple("Update", R.drawable.update_file_icon, IconGroup.SPADES),
            Triple("Upload", R.drawable.upload_icon, IconGroup.SPADES),
            Triple("Vaccine", R.drawable.vaccine_icon, IconGroup.HEARTS),
            Triple("Video Camera", R.drawable.video_camera_icon, IconGroup.DIAMONDS),
            Triple("Water", R.drawable.water_icon, IconGroup.SPADES),
            Triple("Window", R.drawable.window_icon, IconGroup.HEARTS),
            Triple("Wrench", R.drawable.wrench_icon, IconGroup.DIAMONDS)
        )
    }

    private fun getDrawable(id: Int, context: Context) : Drawable {
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

    private fun getDrawableWidth(resourceId: Int, resources: Resources): Dimensions {
        val options = BitmapFactory.Options()
        options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
        val bitmap = BitmapFactory.decodeResource(resources, resourceId, options)
        return Dimensions(bitmap.width, bitmap.height)
    }

    // Clear icon database and add all default icons back in
    suspend fun initializeDefaultIcons(iconDao: IconDao, context: Context) {
        iconDao.deleteAll()
        val iconFileNameTriples = getInitIconData()

        for (triple in iconFileNameTriples) {
            val iconId: Int = triple.second
            val drawable: Drawable = getDrawable(iconId, context)
            val dimensions = getDrawableWidth(iconId, context.resources)

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
}