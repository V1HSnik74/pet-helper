package com.example.pethelper.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.data.entity.OtherNutrition
import kotlinx.coroutines.flow.Flow

@Dao
interface OtherNutritionDao {
    @Query("SELECT * from other_nutrition WHERE petId=:petId AND category=:category")
    fun getOtherNutritionCategory(petId: Int, category: String): Flow<List<OtherNutrition>>

    @Query("DELETE FROM other_nutrition WHERE id=:id")
    suspend fun deleteOtherNutrition(id: Long)

    @Upsert
    suspend fun upsertOtherNutrition(otherNutrition: OtherNutrition)

}