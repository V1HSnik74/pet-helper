package com.example.pethelper.di

import com.example.pethelper.data.repository.impl.ActivityRepositoryImpl
import com.example.pethelper.data.repository.impl.FoodItemRepositoryImpl
import com.example.pethelper.data.repository.impl.HealthObjectRepositoryImpl
import com.example.pethelper.data.repository.impl.NotesRepositoryImpl
import com.example.pethelper.data.repository.impl.OtherNutritionRepositoryImpl
import com.example.pethelper.data.repository.impl.PetRepositoryImpl
import com.example.pethelper.data.repository.impl.ReminderRepositoryImpl
import com.example.pethelper.data.repository.interfaces.ActivityRepository
import com.example.pethelper.data.repository.interfaces.FoodItemRepository
import com.example.pethelper.data.repository.interfaces.HealthObjectRepository
import com.example.pethelper.data.repository.interfaces.NotesRepository
import com.example.pethelper.data.repository.interfaces.OtherNutritionRepository
import com.example.pethelper.data.repository.interfaces.PetRepository
import com.example.pethelper.data.repository.interfaces.ReminderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPetRepository(
        petRepositoryImpl: PetRepositoryImpl
    ): PetRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        activityRepositoryImpl: ActivityRepositoryImpl
    ): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        notesRepositoryImpl: NotesRepositoryImpl
    ): NotesRepository

    @Binds
    @Singleton
    abstract fun bindFoodItemRepository(
        foodItemRepositoryImpl: FoodItemRepositoryImpl
    ): FoodItemRepository

    @Binds
    @Singleton
    abstract fun bindOtherNutritionRepository(
        otherNutritionRepositoryImpl: OtherNutritionRepositoryImpl
    ): OtherNutritionRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        reminderRepositoryImpl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindHealthObject(
        healthObjectRepositoryImpl: HealthObjectRepositoryImpl
    ): HealthObjectRepository
}