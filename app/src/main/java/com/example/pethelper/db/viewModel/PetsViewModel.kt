package com.example.pethelper.db.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Pet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    fun updateBirthdayDate(birthday: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateBirthdayDate(birthday, petId)
        }
    }

    fun updateAbout(about: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateAbout(about, petId)
        }
    }

    fun updateAnimal(animal: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            petsDao.updateAnimal(animal, petId)
        }
    }

    fun getPetById(petId: Int): Flow<Pet?> = petsDao.getPetById(petId)

    var currPet by mutableStateOf<PetFields?>(null)
        private set

    private var petJob: Job? = null

    fun setCurPet(id: Int) {
        petJob?.cancel()
        petJob = viewModelScope.launch {
            petsDao.getPetById(id).collect {
                currPet = it?.getInfoForCompose()
            }
        }
    }

    fun clearCurPet() {
        petJob?.cancel()
        currPet = null
    }

    data class PetFields(
        val id: Int,
        val name: String,
        val breed: String,
        val gender: String,
        val animal: String,
        val photo: String?,
        val age: String,
        val weight: String,
        val height: String,
        val birthday: String,
        val about: String,
        val chipId: String,
        val color: String,
        val neutered: String,
        val weightFloat: Float,
        val heightFloat: Float
    )

    private fun Pet.getInfoForCompose(): PetFields {
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
        val age = if (!birthday.isNullOrEmpty()) {
            val period = Period.between(
                LocalDate.parse(birthday, formatter),
                LocalDate.now()
            )
            if (period.years == 1) "1 year old" else "${period.years} years old"
        } else "N/A years old"
        return PetFields(
            this.id,
            this.name,
            this.breed,
            this.sex,
            this.animal,
            this.photo,
            age,
            if (this.weight != null) "${this.weight} kg" else "N/A kg",
            if (this.height != null) "${this.height} cm" else "N/A cm",
            this.birthday ?: "N/A",
            this.about ?: "No description added yet",
            this.microchipId ?: "N/A",
            this.color ?: "N/A",
            this.neutered ?: "N/A",
            this.weight ?: 0f,
            this.height ?: 0f
        )
    }
}