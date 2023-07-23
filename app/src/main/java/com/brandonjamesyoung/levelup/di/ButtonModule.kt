package com.brandonjamesyoung.levelup.di

import com.brandonjamesyoung.levelup.utility.ButtonConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ButtonModule {
    @Singleton
    @Provides
    fun provideButtonConverter(): ButtonConverter {
        return ButtonConverter()
    }
}