package com.example.pethelper.ui.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.data.entity.Pet
import com.example.pethelper.data.repository.interfaces.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PetsViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {
    val allPets: Flow<List<Pet>> = petRepository.allPets
    private val _isPhotoLoading = MutableStateFlow(false)
    val isPhotoLoading: StateFlow<Boolean> = _isPhotoLoading.asStateFlow()

    fun addPet(
        name: String,
        breed: String,
        sex: String,
        photoUri: Uri?,
        animal: String,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isPhotoLoading.value = true
            val path = photoUri?.let {
                saveImageToDisk(context, it)
            }
            petRepository.addPet(Pet(name, breed, sex, animal, path))
            _isPhotoLoading.value = false
        }
    }

    private fun saveImageToDisk(context: Context, uri: Uri): String {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, "pet_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return file.absolutePath
    }

    fun getPetById(petId: Int): Flow<Pet?> = petRepository.getPetById(petId)

    fun deletePet(petId: Int){
        viewModelScope.launch {
            petRepository.deletePet(petId)
        }
    }

    var currPet by mutableStateOf<PetFields?>(null)
        private set

    private var petJob: Job? = null

    fun setCurPet(id: Int) {
        petJob?.cancel()
        petJob = viewModelScope.launch {
            petRepository.getPetById(id).collect {
                currPet = it?.getInfoForCompose()
            }
        }
    }

    fun clearCurPet() {
        petJob?.cancel()
        currPet = null
    }

    fun updatePetField(petId: Int, transform: (Pet) -> Pet) {
        viewModelScope.launch(Dispatchers.IO) {
            val curPet = petRepository.getPetById(petId).first()
            if (curPet != null) petRepository.updatePet(transform(curPet))
        }
    }

    fun savePetPhoto(context: Context, uri: Uri, petId: Int) {
        _isPhotoLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val path = saveImageToDisk(context, uri)
            val curPet = petRepository.getPetById(petId).first()
            if (curPet != null)
                petRepository.updatePet(curPet.copy(photo = path))
            _isPhotoLoading.value = false
        }
    }
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
    val heightFloat: Float,
    val activityLevel: String,
    val favActivities: String,
    val limitations: String,
    val goals: String
)

fun Pet.getInfoForCompose(): PetFields {
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
        this.height ?: 0f,
        activityLevel ?: "N/A",
        favActivities ?: "None",
        limitations ?: "None",
        goals ?: "None"
    )
}