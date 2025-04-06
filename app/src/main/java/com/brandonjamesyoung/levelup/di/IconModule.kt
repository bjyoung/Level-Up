package com.brandonjamesyoung.levelup.di

import android.content.Context
import com.brandonjamesyoung.levelup.utility.IconGridCreator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class IconModule {
    @Singleton
    @Provides
    fun provideIconGridCreator(@ApplicationContext context: Context): IconGridCreator {
        return IconGridCreator(context)
    }
}