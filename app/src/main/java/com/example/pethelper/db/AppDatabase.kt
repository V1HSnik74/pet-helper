package com.example.pethelper.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Pet::class, Note::class, FoodItem::class,
        Allergy::class, Supplement::class, Treat::class,
        NoteNutrition::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petsDao(): PetsDao
    abstract fun notesDao(): NoteDao
    abstract fun FoodItemDao(): FoodItemDao
    abstract fun AllergyDao(): AllergyDao
    abstract fun SupplementDao(): SupplementDao
    abstract fun TreatDao(): TreatDao
    abstract fun NoteNutritionDao(): NoteNutritionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            return synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pet_helper.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}