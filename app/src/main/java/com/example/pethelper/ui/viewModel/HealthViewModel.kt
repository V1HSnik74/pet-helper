package com.example.pethelper.ui.viewModel

import android.content.Context
import androidx.lifecycle.*
import com.example.pethelper.R
import com.example.pethelper.data.entity.*
import com.example.pethelper.data.repository.interfaces.HealthObjectRepository
import com.example.pethelper.ui.compose.health.*
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.workers.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PreventionStatus(
    val percentage: Float,
    val isProtectedFleas: Boolean,
    val isProtectedWorms: Boolean,
    val isProtectedTicks: Boolean
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val healthObjectRepository: HealthObjectRepository
) : ViewModel() {
    fun getAllUpcomingHealthObjects(petId: Int): Flow<List<HealthObject>> {
        return healthObjectRepository.getAllHealthObjectIsDone(petId, false)
            .map {
                it.sortedBy { ho ->
                    if (ho.category == HealthCategory.MEDICINE) {
                        LocalDateTime.parse(ho.nextTaking, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    } else {
                        val datePart = LocalDate.parse(ho.date, DateTimeFormatter.ISO_LOCAL_DATE)
                        val timePart = if (!ho.time.isNullOrEmpty()) {
                            LocalTime.parse(ho.time, timeParser)
                        } else LocalTime.of(23, 59)
                        LocalDateTime.of(datePart, timePart)
                    }
                }
            }
    }

    fun getAllUpcomingCategory(petId: Int, category: HealthCategory): Flow<List<HealthObject>> {
        val flow = if (category == HealthCategory.MEDICINE) {
            healthObjectRepository.getCategoryHealthObject(petId, category)
        } else {
            healthObjectRepository.getCategoryHealthObjectIsDone(
                petId,
                category,
                false
            )
        }
        return flow.map { list ->
            if (category == HealthCategory.MEDICINE)
                list.sortedBy { it.nextTaking }
            else list.sortedBy { it.date }
        }
    }

    fun getAllHistoryCategory(petId: Int, category: HealthCategory): Flow<List<HealthObject>> {
        return healthObjectRepository.getCategoryHealthObjectIsDone(
            petId,
            category,
            true
        )
    }

    fun getCategoryHealthObjects(petId: Int, category: HealthCategory): Flow<List<HealthObject>> {
        return healthObjectRepository.getCategoryHealthObject(petId, category)
    }

    fun addHealthObject(
        ho: HealthObject,
        animal: String,
        petName: String
    ) {
        viewModelScope.launch {
            val id = healthObjectRepository.addHealthObject(ho)
            val savedObject = ho.copy(id = id)
            scheduleNotifications(savedObject, animal, petName, true)
        }
    }

    fun updateHealthObject(healthObject: HealthObject, animal: String, petName: String) {
        viewModelScope.launch {
            healthObjectRepository.updateHealthObject(healthObject)
            scheduleNotifications(healthObject, animal, petName)
        }
    }

    fun deleteHealthObject(id: Long, category: HealthCategory, petName: String) {
        viewModelScope.launch {
            healthObjectRepository.deleteHealthObject(id)
            if (category == HealthCategory.MEDICINE) {
                cancelMedicineNotif(context, id, petName)
            } else
                deleteNotifHealthObject(context, id, "health")
        }
    }

    fun getPreventionPercentage(petId: Int): Flow<PreventionStatus> {
        val upcoming =
            healthObjectRepository.getCategoryHealthObjectIsDone(
                petId,
                HealthCategory.PREVENTION,
                false
            )
        val history = healthObjectRepository.getCategoryHealthObjectIsDone(
            petId,
            HealthCategory.PREVENTION,
            true
        )
        return combine(history, upcoming) { h, u ->
            if (h.isEmpty()) return@combine PreventionStatus(
                0f,
                isProtectedFleas = false,
                isProtectedWorms = false,
                isProtectedTicks = false
            )
            var isProtectedFleas = false
            var isProtectedWorms = false
            var isProtectedTicks = false
            val preventions = listOf("flea", "tick", "worm")
            val currDate = LocalDate.now()
            val donePreventions = h.filter {
                val date = LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
                val monthsToAdd = if (it.name.contains("worm", true)) 3L else 1L
                val expirationDateDef = date.plusMonths(monthsToAdd)
                val dateDue = u.find { prev ->
                    prev.name.contains(it.name, true)
                            || it.name.contains(prev.name, true)
                }?.date.orEmpty()
                val expirationDate = if (dateDue.isEmpty()) expirationDateDef
                else {
                    val dateDueLD = LocalDate.parse(dateDue, DateTimeFormatter.ISO_LOCAL_DATE)
                    if (dateDueLD.isBefore(expirationDateDef)) dateDueLD else expirationDateDef
                }
                !expirationDate.isBefore(currDate)
            }.map { it.name }

            var userCount = 0
            for (prev in preventions) {
                val isDone = donePreventions.any {
                    it.contains(prev, true)
                }
                if (isDone)
                    userCount += 1
            }
            if (donePreventions.any {
                    it.contains("flea", true)
                }) isProtectedFleas = true
            if (donePreventions.any {
                    it.contains("tick", true)
                }) isProtectedTicks = true
            if (donePreventions.any {
                    it.contains("worm", true)
                }) isProtectedWorms = true
            PreventionStatus(
                userCount.toFloat() / 3,
                isProtectedFleas,
                isProtectedWorms,
                isProtectedTicks
            )
        }
    }

    fun getVaccinationPercentage(petId: Int, animal: String): Flow<Float> {
        val upcoming = healthObjectRepository.getCategoryHealthObjectIsDone(
            petId,
            HealthCategory.VACCINE,
            false
        )
        val history = healthObjectRepository.getCategoryHealthObjectIsDone(
            petId,
            HealthCategory.VACCINE,
            true
        )
        return combine(upcoming, history) { u, h ->
            if (h.isEmpty()) return@combine 0f
            val targetVaccines = if (animal == "Dog") dogVaccines else catVaccines
            val curDate = LocalDate.now()
            val doneVaccines = h.filter {
                val date = LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
                val expirationDateDef = date.plusYears(1)
                val dateDue = (u.find { vac ->
                    it.name.contains(
                        vac.name,
                        ignoreCase = true
                    ) || vac.name.contains(it.name, ignoreCase = true)
                })?.date.orEmpty()
                val expirationDate = if (dateDue.isEmpty()) expirationDateDef
                else {
                    val dateDueLD = LocalDate.parse(dateDue, DateTimeFormatter.ISO_LOCAL_DATE)
                    if (dateDueLD.isBefore(expirationDateDef)) dateDueLD else expirationDateDef
                }
                !expirationDate.isBefore(curDate)
            }.map { it.name }
                .toList()
            var userCount = 0
            for (vac in targetVaccines) {
                val isDone = doneVaccines.any {
                    it.contains(vac, ignoreCase = true)
                }
                if (isDone)
                    userCount += 1
            }
            userCount.toFloat() / targetVaccines.size
        }
    }

    fun getCheckUpStatus(petId: Int): Flow<Boolean> {
        return healthObjectRepository.getCategoryHealthObjectIsDone(
            petId,
            HealthCategory.CHECKUP,
            true
        ).map {
            if (it.isEmpty()) return@map false
            val curDate = LocalDate.now()
            val relevantCheckUps = it.filter { c ->
                val checkUpDate = LocalDate.parse(c.date)
                checkUpDate.plusYears(1) >= curDate
            }
            relevantCheckUps.any { c ->
                c.name.contains("annual", true) || c.name.contains(
                    "year",
                    true
                )
            }
        }
    }

    fun markAsDone(ho: HealthObject, petName: String){
        if (ho.category != HealthCategory.MEDICINE)
            deleteNotifHealthObject(context, ho.id, "health")
        else
            cancelMedicineNotif(context, ho.id, petName)
        viewModelScope.launch {
            healthObjectRepository.updateHealthObject(ho.copy(isDone = true))
        }
    }

    private suspend fun scheduleNotifications(
        ho: HealthObject,
        animal: String,
        petName: String,
        isAdding: Boolean = false
    ) {
        val id = ho.id
        val icon = if (animal == "Dog") R.drawable.dog else R.drawable.cat
        if (ho.category != HealthCategory.MEDICINE) {
            if (!isAdding)
                deleteNotifHealthObject(context, id, "health")
            if (ho.isNotif) {
                scheduleNotif(
                    context,
                    id,
                    ho.name,
                    ho.date!!,
                    ho.dateNotif ?: LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                    ho.timeNotif ?: "0 minutes before",
                    "health",
                    icon,
                    petName
                )
                scheduleMarkAsDoneNotif(
                    context,
                    id,
                    ho.date,
                    "health",
                    icon,
                    ho.time ?: "12:00",
                    "Have you done ${ho.name} for $petName?"
                )
            }
        } else {
            if (!isAdding)
                cancelMedicineNotif(context, id, petName)
            val nextNotifDateTime = scheduleMedicineNotif(
                context,
                id,
                ho.name,
                ho.time!!,
                ho.intervalPeriod!!,
                ho.timeNotif ?: "0 minutes before",
                ho.intervalValue!!,
                petName,
                ho.isNotif,
                ho.intervalTime!!,
                animal
            ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            healthObjectRepository.updateNextTaking(nextNotifDateTime, id)
        }
    }
}


