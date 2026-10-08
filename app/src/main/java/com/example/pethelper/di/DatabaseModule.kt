package com.example.pethelper.di

import android.content.Context
import androidx.room.Room
import com.example.pethelper.data.AppDatabase
import com.example.pethelper.data.dao.ActivityDao
import com.example.pethelper.data.dao.FoodItemDao
import com.example.pethelper.data.dao.HealthObjectDao
import com.example.pethelper.data.dao.NoteDao
import com.example.pethelper.data.dao.OtherNutritionDao
import com.example.pethelper.data.dao.PetsDao
import com.example.pethelper.data.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pet_helper.db"
        ).build()
    }
    @Provides
    fun providePetDao(db: AppDatabase): PetsDao = db.petsDao()

    @Provides
    fun provideActivityDao(db: AppDatabase): ActivityDao = db.ActivityDao()

    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao = db.notesDao()

    @Provides
    fun provideFoodItemDao(db: AppDatabase): FoodItemDao = db.FoodItemDao()

    @Provides
    fun provideOtherNutritionDao(db: AppDatabase): OtherNutritionDao = db.otherNutritionDao()

    @Provides
    fun provideRemindersDao(db: AppDatabase): ReminderDao = db.ReminderDao()

    @Provides
    fun provideHealthObjectDao(db: AppDatabase): HealthObjectDao = db.healthObjectDao()
}