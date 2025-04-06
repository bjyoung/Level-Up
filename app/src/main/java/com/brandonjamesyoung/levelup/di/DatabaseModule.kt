package com.brandonjamesyoung.levelup.di

import android.content.Context
import com.brandonjamesyoung.levelup.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideQuestDao(appDatabase: AppDatabase): QuestDao {
        return appDatabase.questDao()
    }

    @Provides
    fun providePlayerDao(appDatabase: AppDatabase): PlayerDao {
        return appDatabase.playerDao()
    }

    @Provides
    fun provideSettingsDao(appDatabase: AppDatabase): SettingsDao {
        return appDatabase.settingsDao()
    }

    @Provides
    fun provideDifficultyDao(appDatabase: AppDatabase): DifficultyDao {
        return appDatabase.difficultyDao()
    }

    @Provides
    fun provideShopItemDao(appDatabase: AppDatabase): ShopItemDao {
        return appDatabase.shopItemDao()
    }

    @Provides
    fun provideItemHistoryDao(appDatabase: AppDatabase): ItemHistoryDao {
        return appDatabase.itemHistoryDao()
    }

    @Provides
    fun provideIconDao(appDatabase: AppDatabase): IconDao {
        return appDatabase.iconDao()
    }

    @Provides
    fun provideQuestHistoryDao(appDatabase: AppDatabase): QuestHistoryDao {
        return appDatabase.questHistoryDao()
    }
}