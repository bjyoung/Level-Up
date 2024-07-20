package com.brandonjamesyoung.levelup.di

import com.brandonjamesyoung.levelup.utility.PointsDisplay
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PointsDisplayModule {
    @Singleton
    @Provides
    fun providePointsDisplay(): PointsDisplay {
        return PointsDisplay()
    }
}