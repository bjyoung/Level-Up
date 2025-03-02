package com.brandonjamesyoung.levelup.di

import android.content.Context
import com.brandonjamesyoung.levelup.utility.CardGridCreator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class CardModule {
    @Singleton
    @Provides
    fun provideCardGridCreator(@ApplicationContext context: Context): CardGridCreator {
        return CardGridCreator(context)
    }
}