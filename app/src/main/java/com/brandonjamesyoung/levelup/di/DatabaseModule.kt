package com.brandonjamesyoung.levelup.di

import android.content.Context
import com.brandonjamesyoung.levelup.data.AppDatabase
import com.brandonjamesyoung.levelup.data.QuestDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun providePlantDao(appDatabase: AppDatabase): QuestDao {
        return appDatabase.questDao()
    }
}