package com.brandonjamesyoung.levelup.shared

const val DATABASE_NAME = "levelup-db"
const val BASE_EXP = 2500
const val MAX_LEVEL = 99
const val MAX_NUM_LOOPS = 300
const val MAX_TOTAL_EXP = 999999999999999999
const val PROGRESS_BAR_ANIMATE_DURATION : Long = 450

enum class Difficulty {
    EASY, MEDIUM, HARD, EXPERT
}

const val INIT_EASY_EXP = 100
const val INIT_EASY_POINTS = 1

const val INIT_MEDIUM_EXP = 250
const val INIT_MEDIUM_POINTS = 3

const val INIT_HARD_EXP = 600
const val INIT_HARD_POINTS = 6

const val INIT_EXPERT_EXP = 1500
const val INIT_EXPERT_POINTS = 12

enum class FontSize {
    SMALL, MEDIUM, LARGE
}

enum class Mode {
    DEFAULT, SELECT
}