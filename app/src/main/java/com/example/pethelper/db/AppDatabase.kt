package com.example.pethelper.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pethelper.db.dao.AllergyDao
import com.example.pethelper.db.dao.CheckUpDao
import com.example.pethelper.db.dao.FoodItemDao
import com.example.pethelper.db.dao.NoteDao
import com.example.pethelper.db.dao.NoteNutritionDao
import com.example.pethelper.db.dao.PetsDao
import com.example.pethelper.db.dao.PreventionDao
import com.example.pethelper.db.dao.SupplementDao
import com.example.pethelper.db.dao.TreatDao
import com.example.pethelper.db.dao.VaccineDao
import com.example.pethelper.db.entity.Allergy
import com.example.pethelper.db.entity.CheckUp
import com.example.pethelper.db.entity.FoodItem
import com.example.pethelper.db.entity.Note
import com.example.pethelper.db.entity.NoteNutrition
import com.example.pethelper.db.entity.Pet
import com.example.pethelper.db.entity.Prevention
import com.example.pethelper.db.entity.Supplement
import com.example.pethelper.db.entity.Treat
import com.example.pethelper.db.entity.Vaccine

@Database(
    entities = [Pet::class, Note::class, FoodItem::class,
        Allergy::class, Supplement::class, Treat::class,
        NoteNutrition::class, Vaccine::class, Prevention::class,
        CheckUp::class],
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
    abstract fun VaccineDao(): VaccineDao
    abstract fun PreventionDao(): PreventionDao
    abstract fun CheckUpDao(): CheckUpDao

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