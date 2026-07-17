package com.example.pethelper.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PetsViewModel(application: Application): AndroidViewModel(application) {
    val petsDao = AppDatabase.getInstance(application).petsDao()
    val allPets: Flow<List<Pet>> = petsDao.getAllPets()
    private val selectedPetPrivate = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = selectedPetPrivate.asStateFlow()

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

    fun getPetById(petId: Int){
        viewModelScope.launch {
            selectedPetPrivate.value =  petsDao.getPetById(petId)
        }
    }

    fun cleanSelectedPet() {
        selectedPetPrivate.value = null
    }
}