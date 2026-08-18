package com.example.pethelper.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TreatViewModel(application: Application): AndroidViewModel(application) {
    val treatDao = AppDatabase.getInstance(application).TreatDao()

    fun getAllTreatsByPet(petId: Int): Flow<List<Treat>>{
        return treatDao.getAllTreatsByPet(petId)
    }

    fun getTreatById(id: Int): Flow<Treat?>{
        return treatDao.getTreatById(id)
    }

    fun addTreat(name: String, petId: Int){
        viewModelScope.launch {
            treatDao.addTreat(Treat(name, petId))
        }
    }

    fun deleteTreat(id: Int){
        viewModelScope.launch {
            treatDao.deleteTreat(id)
        }
    }
}