package com.brandonjamesyoung.levelup.constants

const val DATABASE_NAME = "levelup-db"
const val BASE_EXP = 2500

// Enums
enum class Difficulty {
    EASY, MEDIUM, HARD, EXPERT
}

enum class Mode {
    DEFAULT, SELECT, EDIT, MOVE
}

enum class IconGroup {
    SPADES, DIAMONDS, HEARTS, CLUBS
}

enum class SortType {
    DATE_CREATED, NAME, PRICE, DIFFICULTY
}

enum class SortOrder {
    ASC, DESC
}

enum class BackupDbError {
    OUTPUT_STREAM_ERROR, COPY_ERROR
}

enum class RestoreDbError {
    INVALID_FILE, LOCAL_BACKUP_FAILED, INPUT_STREAM_ERROR, COPY_ERROR
}

// Animation Constants
const val PROGRESS_BAR_ANIM_DURATION: Long = 450
const val POINT_UPDATE_ANIM_DURATION: Long = 2500
const val POP_UP_BUTTON_WAIT_PERIOD: Long = 4500

// Initialization Constants
const val INIT_EASY_EXP = 100
const val INIT_EASY_POINTS = 1

const val INIT_MEDIUM_EXP = 250
const val INIT_MEDIUM_POINTS = 4

const val INIT_HARD_EXP = 600
const val INIT_HARD_POINTS = 10

const val INIT_EXPERT_EXP = 2500
const val INIT_EXPERT_POINTS = 40

// Maximum Constants
const val INT_LIMIT = 999999999
const val MAX_LEVEL = 99
const val MAX_NUM_LOOPS = 300
const val MAX_TOTAL_EXP: Long = 999999999999999999
const val MAX_EXP_EARNED: Int = INT_LIMIT
const val MAX_POINTS = 999999
const val MAX_POINTS_EARNED: Int = INT_LIMIT
const val MAX_POINTS_PER_PURCHASE: Int = INT_LIMIT

// Icon constants
const val ICON_SCALE_UP_RATE: Int = 4

// Shop/Item History constants
const val ITEM_ROW_LANDSCAPE_WIDTH_DP = 500f
