package com.brandonjamesyoung.levelup.di

import android.content.Context
import com.brandonjamesyoung.levelup.utility.ButtonConverter
import com.brandonjamesyoung.levelup.utility.SortButtonManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ButtonModule {
    @Singleton
    @Provides
    fun provideButtonConverter(@ApplicationContext context: Context): ButtonConverter {
        return ButtonConverter(context)
    }

    @Singleton
    @Provides
    fun provideSortButtonManager(@ApplicationContext context: Context): SortButtonManager {
        return SortButtonManager(context)
    }
}