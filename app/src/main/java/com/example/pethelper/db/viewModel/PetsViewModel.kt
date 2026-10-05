package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Pet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PetsViewModel(application: Application) : AndroidViewModel(application) {
    val petsDao = AppDatabase.getInstance(application).petsDao()
    val allPets: Flow<List<Pet>> = petsDao.getAllPets()

    fun addPet(name: String, breed: String, sex: String, photo: String?, animal: String) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.upsertPet(Pet(name, breed, sex, animal, photo))
        }
    }

    fun updatePetPhoto(petId: Int, photo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updatePhoto(photo, petId)
        }
    }

    fun updateGender(petId: Int, gender: String) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateGender(gender, petId)
        }
    }

    fun updateName(name: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateName(name, petId)
        }
    }

    fun updateBreed(breed: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateBreed(breed, petId)
        }
    }

    fun updateChip(chip: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateChip(chip, petId)
        }
    }

    fun updateColor(color: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateColor(color, petId)
        }
    }

    fun updateIsNeutered(isNeutered: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateIsNeutered(isNeutered, petId)
        }
    }

    fun updateWeight(weight: Float, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateWeight(weight, petId)
        }
    }

    fun updateHeight(height: Float, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateHeight(height, petId)
        }
    }

    fun updateBirthdayDate(birthday: String, petId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateBirthdayDate(birthday, petId)
        }
    }

    fun updateAbout(about: String, petId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateAbout(about, petId)
        }
    }

    fun updateAnimal(animal: String, petId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateAnimal(animal, petId)
        }
    }

    fun getPetById(petId: Int): Flow<Pet?> = petsDao.getPetById(petId)
}