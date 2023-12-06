package com.brandonjamesyoung.levelup.di

import com.brandonjamesyoung.levelup.utility.ItemTableManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ItemModule {
    @Singleton
    @Provides
    fun provideItemTableManager(): ItemTableManager {
        return ItemTableManager()
    }
}