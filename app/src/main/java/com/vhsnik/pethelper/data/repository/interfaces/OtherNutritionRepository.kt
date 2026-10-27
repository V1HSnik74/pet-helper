package com.vhsnik.pethelper.data.repository.interfaces

import com.vhsnik.pethelper.data.entity.OtherNutrition
import kotlinx.coroutines.flow.Flow

interface OtherNutritionRepository {
    fun getAllOtherNutritionCategory(petId: Int, category: String): Flow<List<OtherNutrition>>
    suspend fun addOtherNutrition(otherNutrition: OtherNutrition)
    suspend fun deleteOtherNutrition(id: Long)
}