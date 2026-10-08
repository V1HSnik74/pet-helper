package com.example.pethelper.di

import com.example.pethelper.BuildConfig
import com.example.pethelper.ai.GeneratedRecommendation
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