package com.vhsnik.pethelper.di

import com.vhsnik.pethelper.data.repository.impl.ActivityRepositoryImpl
import com.vhsnik.pethelper.data.repository.impl.FoodItemRepositoryImpl
import com.vhsnik.pethelper.data.repository.impl.HealthObjectRepositoryImpl
import com.vhsnik.pethelper.data.repository.impl.NotesRepositoryImpl
import com.vhsnik.pethelper.data.repository.impl.OtherNutritionRepositoryImpl
import com.vhsnik.pethelper.data.repository.impl.PetRepositoryImpl
import com.vhsnik.pethelper.data.repository.impl.ReminderRepositoryImpl
import com.vhsnik.pethelper.data.repository.interfaces.ActivityRepository
import com.vhsnik.pethelper.data.repository.interfaces.FoodItemRepository
import com.vhsnik.pethelper.data.repository.interfaces.HealthObjectRepository
import com.vhsnik.pethelper.data.repository.interfaces.NotesRepository
import com.vhsnik.pethelper.data.repository.interfaces.OtherNutritionRepository
import com.vhsnik.pethelper.data.repository.interfaces.PetRepository
import com.vhsnik.pethelper.data.repository.interfaces.ReminderRepository
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