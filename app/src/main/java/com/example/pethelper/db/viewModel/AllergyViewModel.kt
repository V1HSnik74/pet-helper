package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Allergy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AllergyViewModel(application: Application) : AndroidViewModel(application) {
    val allergyDao = AppDatabase.getInstance(application).AllergyDao()

    fun getAllAllergiesByPet(petId: Int): Flow<List<Allergy>> {
        return allergyDao.getAllAllergiesByPet(petId)
    }

    fun getAllergyById(id: Long): Flow<Allergy?> {
        return allergyDao.getAllergyById(id)
    }

    fun addAllergy(name: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            allergyDao.addAllergy(Allergy(name, petId))
        }
    }

    fun deleteAllergy(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            allergyDao.deleteAllergy(id)
        }
    }
}