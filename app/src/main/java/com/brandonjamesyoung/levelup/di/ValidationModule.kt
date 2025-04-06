package com.brandonjamesyoung.levelup.di

import com.brandonjamesyoung.levelup.validation.InputValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ValidationModule {
    @Singleton
    @Provides
    fun provideValidator(): InputValidator {
        return InputValidator()
    }
}