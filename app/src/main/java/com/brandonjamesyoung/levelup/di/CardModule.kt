package com.brandonjamesyoung.levelup.di

import com.brandonjamesyoung.levelup.utility.CardGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class CardModule {
    @Singleton
    @Provides
    fun provideCardGenerator(): CardGenerator {
        return CardGenerator()
    }
}