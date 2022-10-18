package com.brandonjamesyoung.levelup.shared

class LevelUpHelper {
    companion object {
        fun getExpToLvlUp(currLvl: Int) : Int {
            return BASE_EXP + (1750 * (currLvl - 1))
        }
    }
}