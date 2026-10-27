package com.vhsnik.pethelper.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vhsnik.pethelper.data.dao.ActivityDao
import com.vhsnik.pethelper.data.dao.FoodItemDao
import com.vhsnik.pethelper.data.dao.HealthObjectDao
import com.vhsnik.pethelper.data.dao.NoteDao
import com.vhsnik.pethelper.data.dao.OtherNutritionDao
import com.vhsnik.pethelper.data.dao.PetsDao
import com.vhsnik.pethelper.data.dao.ReminderDao
import com.vhsnik.pethelper.data.entity.Activity
import com.vhsnik.pethelper.data.entity.FoodItem
import com.vhsnik.pethelper.data.entity.HealthObject
import com.vhsnik.pethelper.data.entity.Note
import com.vhsnik.pethelper.data.entity.OtherNutrition
import com.vhsnik.pethelper.data.entity.Pet
import com.vhsnik.pethelper.data.entity.Reminder

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