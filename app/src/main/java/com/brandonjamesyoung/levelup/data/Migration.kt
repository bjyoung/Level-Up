package com.brandonjamesyoung.levelup.data

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration {
    @DeleteColumn(tableName = "Player", columnName = "expToLvlUp")
    class MigrationSpec6To7 : AutoMigrationSpec

    @DeleteColumn(tableName = "Settings", columnName = "easyExpReward")
    @DeleteColumn(tableName = "Settings", columnName = "mediumExpReward")
    @DeleteColumn(tableName = "Settings", columnName = "hardExpReward")
    @DeleteColumn(tableName = "Settings", columnName = "expertExpReward")
    @DeleteColumn(tableName = "Settings", columnName = "easyRtReward")
    @DeleteColumn(tableName = "Settings", columnName = "mediumRtReward")
    @DeleteColumn(tableName = "Settings", columnName = "hardRtReward")
    @DeleteColumn(tableName = "Settings", columnName = "expertRtReward")
    class MigrationSpec7To8 : AutoMigrationSpec

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `PlayerTemp` " +
                            "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT, " +
                            "`rt` INTEGER NOT NULL, " +
                            "`lvl` INTEGER NOT NULL, " +
                            "`totalExp` INTEGER NOT NULL, " +
                            "`currentLvlExp` INTEGER NOT NULL, " +
                            "`expToLvlUp` INTEGER NOT NULL)"
                )

                database.execSQL(
                    "INSERT INTO `PlayerTemp` (`id`, `name`, `rt`, `lvl`, " +
                            "`totalExp`, `currentLvlExp`, `expToLvlUp`) " +
                            "SELECT `id`, `name`, `rp`, `lvl`, 0, `exp`, `expToLvlUp` from `Player`"
                )

                database.execSQL("DROP TABLE `Player`")
                database.execSQL("ALTER TABLE `PlayerTemp` RENAME TO `Player`")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Settings RENAME COLUMN easyRpReward TO easyRtReward")
                database.execSQL("ALTER TABLE Settings RENAME COLUMN mediumRpReward TO mediumRtReward")
                database.execSQL("ALTER TABLE Settings RENAME COLUMN hardRpReward TO hardRtReward")
                database.execSQL("ALTER TABLE Settings RENAME COLUMN expertRpReward TO expertRtReward")
            }
        }
    }
}