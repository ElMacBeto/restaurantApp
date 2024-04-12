package com.humbjorch.restaurantapp.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): Context {
        return appContext
    }
}