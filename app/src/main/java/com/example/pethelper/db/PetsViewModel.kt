package com.example.pethelper.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PetsViewModel(application: Application): AndroidViewModel(application) {
    val petsDao = AppDatabase.getInstance(application).petsDao()
    val allPets: Flow<List<Pet>> = petsDao.getAllPets()

    fun addPet(name: String, breed: String, sex: String, photo: String?){
        viewModelScope.launch {
            petsDao.upsertPet(Pet(name, breed, sex, photo))
        }
    }

    fun updatePetPhoto(petId: Int, photo: String){
        viewModelScope.launch {
            petsDao.updatePhoto(photo, petId)
        }
    }

    fun updateGender(petId: Int, gender: String){
        viewModelScope.launch {
            petsDao.updateGender(gender, petId)
        }
    }

    fun updateName(name: String, petId: Int){
        viewModelScope.launch {
            petsDao.updateName(name, petId)
        }
    }

    fun updateBreed(breed: String, petId: Int){
        viewModelScope.launch {
            petsDao.updateBreed(breed, petId)
        }
    }

    fun updateChip(chip: String, petId: Int){
        viewModelScope.launch {
            petsDao.updateChip(chip, petId)
        }
    }

    fun updateColor(color: String, petId: Int){
        viewModelScope.launch {
            petsDao.updateColor(color, petId)
        }
    }

    fun updateIsNeutered(isNeutered: String, petId: Int){
        viewModelScope.launch {
            petsDao.updateIsNeutered(isNeutered, petId)
        }
    }

    fun getPetById(petId: Int): Flow<Pet?> = petsDao.getPetById(petId)
}