package com.brandonjamesyoung.levelup.di

import android.content.Context
import com.brandonjamesyoung.levelup.utility.DateLabelManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LabelModule {
    @Singleton
    @Provides
    fun provideDateLabelManager(@ApplicationContext context: Context): DateLabelManager {
        return DateLabelManager(context)
    }
}