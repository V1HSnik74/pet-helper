package com.example.pethelper.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SupplementViewModel(application: Application) : AndroidViewModel(application) {
    val supplementDao = AppDatabase.getInstance(application).SupplementDao()

    fun getAllSupplementsByPet(petId: Int): Flow<List<Supplement>> {
        return supplementDao.getAllSupplementsByPet(petId)
    }

    fun getSupplementById(id: Int): Flow<Supplement?> {
        return supplementDao.getSupplementById(id)
    }

    fun addSupplement(name: String, petId: Int) {
        viewModelScope.launch {
            supplementDao.addSupplement(Supplement(name, petId))
        }
    }

    fun deleteSupplement(id: Int) {
        viewModelScope.launch {
            supplementDao.deleteSupplement(id)
        }
    }
}