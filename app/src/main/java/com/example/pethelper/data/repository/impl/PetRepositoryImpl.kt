package com.example.pethelper.data.repository.impl

import com.example.pethelper.data.dao.PetsDao
import com.example.pethelper.data.entity.Pet
import com.example.pethelper.data.repository.interfaces.PetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor(
    private val petsDao: PetsDao)
    : PetRepository {
    override val allPets: Flow<List<Pet>> = petsDao.getAllPets()

    override fun getPetById(petId: Int): Flow<Pet?> = petsDao.getPetById(petId)

    override suspend fun addPet(pet: Pet) = petsDao.upsertPet(pet)

    override suspend fun updatePet(pet: Pet) = petsDao.upsertPet(pet)

    override suspend fun deletePet(petId: Int) = petsDao.deletePet(petId)

}