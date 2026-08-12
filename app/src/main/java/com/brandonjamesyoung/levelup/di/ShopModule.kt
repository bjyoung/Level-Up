package com.brandonjamesyoung.levelup.di

import android.content.Context
import com.brandonjamesyoung.levelup.compose.ItemTableCreator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ShopModule {
    @Singleton
    @Provides
    fun provideItemTableCreator(@ApplicationContext context: Context): ItemTableCreator {
        return ItemTableCreator(context)
    }
}