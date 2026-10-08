package com.example.pethelper.data.repository.impl

import com.example.pethelper.data.dao.HealthObjectDao
import com.example.pethelper.data.entity.HealthCategory
import com.example.pethelper.data.entity.HealthObject
import com.example.pethelper.data.repository.interfaces.HealthObjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HealthObjectRepositoryImpl @Inject constructor(
    private val healthObjectDao: HealthObjectDao
) : HealthObjectRepository {
    override fun getAllHealthObject(petId: Int): Flow<List<HealthObject>> =
        healthObjectDao.getAllHealthObjects(petId)

    override fun getCategoryHealthObject(
        petId: Int,
        category: HealthCategory
    ): Flow<List<HealthObject>> =
        healthObjectDao.getCategoryHealthObjects(petId, category)

    override fun getCategoryHealthObjectIsDone(
        petId: Int,
        category: HealthCategory,
        isDone: Boolean
    ): Flow<List<HealthObject>> =
        healthObjectDao.getCategoryHealthObjectsIsDone(petId, category, isDone)

    override fun getAllHealthObjectIsDone(
        petId: Int,
        isDone: Boolean
    ): Flow<List<HealthObject>> =
        healthObjectDao.getAllHealthObjectsIsDone(petId, isDone)

    override fun getHealthObjectById(id: Long): Flow<HealthObject?> =
        healthObjectDao.getHealthObjectById(id)

    override suspend fun addHealthObject(healthObject: HealthObject): Long =
        healthObjectDao.upsertHealthObject(healthObject)

    override suspend fun updateHealthObject(healthObject: HealthObject): Long =
        healthObjectDao.upsertHealthObject(healthObject)

    override suspend fun markAsDone(id: Long) =
        healthObjectDao.markAsDone(id)

    override suspend fun updateNextTaking(nextTaking: String, id: Long) =
        healthObjectDao.updateNextTaking(nextTaking, id)

    override suspend fun deleteHealthObject(id: Long) =
        healthObjectDao.deleteHealthObject(id)
}