package com.example.pethelper.data.repository.impl

import com.example.pethelper.data.dao.OtherNutritionDao
import com.example.pethelper.data.entity.OtherNutrition
import com.example.pethelper.data.repository.interfaces.OtherNutritionRepository
import javax.inject.Inject

class OtherNutritionRepositoryImpl @Inject constructor(
    private val otherNutritionDao: OtherNutritionDao
) : OtherNutritionRepository {
    override fun getAllOtherNutritionCategory(petId: Int, category: String) =
        otherNutritionDao.getOtherNutritionCategory(petId, category)

    override suspend fun addOtherNutrition(otherNutrition: OtherNutrition) =
        otherNutritionDao.upsertOtherNutrition(otherNutrition)

    override suspend fun updateOtherNutrition(otherNutrition: OtherNutrition) =
        otherNutritionDao.upsertOtherNutrition(otherNutrition)

    override suspend fun deleteOtherNutrition(id: Long) =
        otherNutritionDao.deleteOtherNutrition(id)
}