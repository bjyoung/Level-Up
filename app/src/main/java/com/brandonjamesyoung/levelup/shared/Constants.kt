package com.brandonjamesyoung.levelup.shared

const val DATABASE_NAME = "levelup-db"
const val BASE_EXP = 2500
const val MAX_LEVEL = 99
const val MAX_NUM_LOOPS = 300
const val MAX_TOTAL_EXP = 999999999999999999

enum class Difficulty {
    EASY, MEDIUM, HARD, EXPERT
}

enum class FontSize {
    SMALL, MEDIUM, LARGE
}

enum class Mode {
    DEFAULT, SELECT
}
