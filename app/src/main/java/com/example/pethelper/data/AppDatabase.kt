package com.example.pethelper.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pethelper.data.dao.ActivityDao
import com.example.pethelper.data.dao.FoodItemDao
import com.example.pethelper.data.dao.HealthObjectDao
import com.example.pethelper.data.dao.NoteDao
import com.example.pethelper.data.dao.OtherNutritionDao
import com.example.pethelper.data.dao.PetsDao
import com.example.pethelper.data.dao.ReminderDao
import com.example.pethelper.data.entity.Activity
import com.example.pethelper.data.entity.FoodItem
import com.example.pethelper.data.entity.HealthObject
import com.example.pethelper.data.entity.Note
import com.example.pethelper.data.entity.OtherNutrition
import com.example.pethelper.data.entity.Pet
import com.example.pethelper.data.entity.Reminder

@Database(
    entities = [Pet::class, Note::class, FoodItem::class,
        Reminder::class, Activity::class, OtherNutrition::class,
        HealthObject::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petsDao(): PetsDao
    abstract fun notesDao(): NoteDao
    abstract fun FoodItemDao(): FoodItemDao
    abstract fun ReminderDao(): ReminderDao
    abstract fun ActivityDao(): ActivityDao
    abstract fun otherNutritionDao(): OtherNutritionDao
    abstract fun healthObjectDao(): HealthObjectDao
}