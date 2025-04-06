package com.brandonjamesyoung.levelup.data

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brandonjamesyoung.levelup.constants.BASE_EXP
import com.brandonjamesyoung.levelup.constants.MAX_LEVEL
import com.brandonjamesyoung.levelup.constants.MAX_NUM_LOOPS
import com.brandonjamesyoung.levelup.constants.MAX_POINTS
import com.brandonjamesyoung.levelup.constants.MAX_TOTAL_EXP

@Entity
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var name: String? = null,
    @ColumnInfo var points: Int = 0,
    @ColumnInfo var lvl: Int = 1,
    @ColumnInfo var totalExp: Long = 0,
    @ColumnInfo var currentLvlExp: Int = 0,
) {
    // Get the exact amount of EXP needed to level up starting at the beginning
    // of the given level. Defaults to the player's current level.
    fun getExpToLvlUp(targetLvl: Int? = null) : Int {
        val levelToUse = targetLvl ?: lvl
        return BASE_EXP + (1750 * (levelToUse - 1))
    }

    // Get the amount of EXP the player needs to reach the next level
    fun getExpToNextLvl() : Int {
        val totalExpToLvlUp = getExpToLvlUp()
        return totalExpToLvlUp - currentLvlExp
    }

    private fun canLevelUp(expEarned: Int) : Boolean {
        val expToNextLvl = getExpToNextLvl()
        return expEarned > 0 && expEarned >= expToNextLvl && lvl < MAX_LEVEL
    }

    fun gainPoints(pointsEarned: Int) {
        val estimatedTotal = points + pointsEarned

        if (estimatedTotal > MAX_POINTS) {
            points += MAX_POINTS - points
            return
        }

        points += pointsEarned
    }

    private fun levelUp(bonusPoints: Int) {
        lvl += 1
        gainPoints(bonusPoints)
        currentLvlExp = 0
        Log.i(TAG, "Player levels up to lvl $lvl")
    }

    private fun canLevelDown(expLost: Int) : Boolean {
        return expLost >= currentLvlExp && lvl > 1
    }

    private fun levelDown(bonusPoints: Int) {
        lvl -= 1
        points -= bonusPoints
        currentLvlExp = getExpToLvlUp()
        Log.i(TAG, "Player levels down to lvl $lvl")
    }

    private fun gainTotalExp(expEarned: Int) {
        totalExp += if (totalExp + expEarned > MAX_TOTAL_EXP) {
            MAX_TOTAL_EXP - totalExp
        } else {
            expEarned.toLong()
        }
    }

    // Give EXP to the player
    // Returns the amount of levels earned (which can be negative)
    private fun addExp(expEarned: Int, bonusPoints: Int) : Int {
        var expLeft = expEarned
        var numLoops = 0

        while (canLevelUp(expLeft)) {
            val expToNextLvl = getExpToNextLvl()
            expLeft -= expToNextLvl
            levelUp(bonusPoints)
            numLoops++

            if (numLoops > MAX_NUM_LOOPS) {
                Log.e(TAG, "Exceeded maximum num loops")
            }
        }

        val currLvlExpEarned = if (lvl < MAX_LEVEL) expLeft else 0
        currentLvlExp += currLvlExpEarned
        gainTotalExp(expEarned)
        return numLoops
    }

    // Remove EXP from the player, which can de-level them
    // Returns the amount of levels earned (which can be negative)
    private fun subtractExp(expEarned: Int, bonusPoints: Int) : Int {
        var expLost = -expEarned
        var numLoops = 0

        while (canLevelDown(expLost)) {
            val expToLowerLvl = currentLvlExp
            expLost += expToLowerLvl
            levelDown(bonusPoints)
            numLoops++

            if (numLoops > MAX_NUM_LOOPS) {
                Log.e(TAG, "Exceeded maximum num loops")
            }
        }

        val currLvlExpLost = if (lvl == 1 && expLost > currentLvlExp) {
            currentLvlExp
        } else {
            expLost
        }

        gainTotalExp(expEarned)
        currentLvlExp -= currLvlExpLost
        return numLoops * -1
    }

    // Give the player EXP
    // Returns the amount of levels earned (which can be negative)
    fun gainExp(expEarned: Int, bonusPoints: Int) : Int {
        return if (expEarned >= 0) {
            addExp(expEarned, bonusPoints)
        } else {
            subtractExp(expEarned, bonusPoints)
        }
    }

    companion object {
        private const val TAG = "Player"
    }
}