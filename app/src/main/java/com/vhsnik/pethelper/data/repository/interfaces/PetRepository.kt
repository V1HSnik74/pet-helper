package com.vhsnik.pethelper.data.repository.interfaces

import com.vhsnik.pethelper.data.entity.Pet
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    val allPets: Flow<List<Pet>>
    fun getPetById(petId: Int):  Flow<Pet?>
    suspend fun addPet(pet: Pet)
    suspend fun updatePet(pet: Pet)
    suspend fun deletePet(petId: Int)
}