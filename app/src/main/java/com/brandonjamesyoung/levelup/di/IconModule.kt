package com.brandonjamesyoung.levelup.di

import android.content.Context
import com.brandonjamesyoung.levelup.compose.IconGridCreator
import com.brandonjamesyoung.levelup.compose.IconWorkspaceCreator
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

    @Singleton
    @Provides
    fun provideIconWorkspaceCreator(@ApplicationContext context: Context): IconWorkspaceCreator {
        return IconWorkspaceCreator(context)
    }
}