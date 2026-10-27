package com.vhsnik.pethelper.di

import com.vhsnik.pethelper.BuildConfig
import com.vhsnik.pethelper.ai.GeneratedRecommendation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    @Provides
    @Singleton
    fun provideGeneratedRecommendation(): GeneratedRecommendation {
        return GeneratedRecommendation(BuildConfig.GEMINI_API_KEY)
    }
}