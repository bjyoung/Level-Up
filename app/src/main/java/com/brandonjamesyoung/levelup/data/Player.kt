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
    fun getExpToLvlUp() : Int {
        return BASE_EXP + (1750 * (lvl - 1))
    }

    fun getExpToNextLvl() : Int {
        val totalExpToLvlUp = getExpToLvlUp()
        return totalExpToLvlUp - currentLvlExp
    }

    fun canLevelUp(expEarned: Int) : Boolean {
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

    private fun addExp(expEarned: Int, bonusPoints: Int) {
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
    }

    private fun subtractExp(expEarned: Int, bonusPoints: Int) {
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
    }

    fun gainExp(expEarned: Int, bonusPoints: Int) {
        if (expEarned >= 0) addExp(expEarned, bonusPoints) else subtractExp(expEarned, bonusPoints)
    }

    companion object {
        private const val TAG = "Player"
    }
}