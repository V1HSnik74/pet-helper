package com.vhsnik.pethelper.ui.viewModel

import android.content.Context
import androidx.lifecycle.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.*
import com.vhsnik.pethelper.data.repository.interfaces.HealthObjectRepository
import com.vhsnik.pethelper.ui.compose.health.*
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.workers.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val curPetId = MutableStateFlow<Int?>(null)
    fun setPetId(id: Int) {
        curPetId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allUpcomingHealthObjects: StateFlow<List<HealthObject>> = curPetId.filterNotNull()
        .flatMapLatest {
            healthObjectRepository.getAllHealthObjectIsDone(it, false)
                .map {
                    it.sortedBy { ho ->
                        if (ho.category == HealthCategory.MEDICINE) {
                            LocalDateTime.parse(
                                ho.nextTaking,
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            )
                        } else {
                            val datePart =
                                LocalDate.parse(ho.date, DateTimeFormatter.ISO_LOCAL_DATE)
                            val timePart = if (!ho.time.isNullOrEmpty()) {
                                LocalTime.parse(ho.time, timeParser)
                            } else LocalTime.of(23, 59)
                            LocalDateTime.of(datePart, timePart)
                        }
                    }
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllUpcomingCategory(category: HealthCategory): StateFlow<List<HealthObject>> {
        return curPetId.filterNotNull().flatMapLatest {
            val flow = if (category == HealthCategory.MEDICINE) {
                healthObjectRepository.getCategoryHealthObject(it, category)
            } else {
                healthObjectRepository.getCategoryHealthObjectIsDone(
                    it,
                    category,
                    false
                )
            }
            flow.map { list ->
                if (category == HealthCategory.MEDICINE)
                    list.sortedBy { it.nextTaking }
                else list.sortedBy { it.date }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllHistoryCategory(category: HealthCategory): StateFlow<List<HealthObject>> {
        return curPetId.filterNotNull().flatMapLatest {
            healthObjectRepository.getCategoryHealthObjectIsDone(
                it,
                category,
                true
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCategoryHealthObjects(category: HealthCategory): StateFlow<List<HealthObject>> {
        return curPetId.filterNotNull().flatMapLatest {
            healthObjectRepository.getCategoryHealthObject(it, category)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val preventionPercentage: StateFlow<PreventionStatus> = curPetId.filterNotNull().flatMapLatest {
        val upcoming =
            healthObjectRepository.getCategoryHealthObjectIsDone(
                it,
                HealthCategory.PREVENTION,
                false
            )
        val history = healthObjectRepository.getCategoryHealthObjectIsDone(
            it,
            HealthCategory.PREVENTION,
            true
        )
        combine(history, upcoming) { h, u ->
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
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PreventionStatus(
            0f,
            isProtectedFleas = false,
            isProtectedWorms = false,
            isProtectedTicks = false
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getVaccinationPercentage(animal: String): StateFlow<Float> {
        return curPetId.filterNotNull().flatMapLatest {
            val upcoming = healthObjectRepository.getCategoryHealthObjectIsDone(
                it,
                HealthCategory.VACCINE,
                false
            )
            val history = healthObjectRepository.getCategoryHealthObjectIsDone(
                it,
                HealthCategory.VACCINE,
                true
            )
            combine(upcoming, history) { u, h ->
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
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val checkUpStatus: StateFlow<Boolean> =
        curPetId.filterNotNull().flatMapLatest {
            healthObjectRepository.getCategoryHealthObjectIsDone(
                it,
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
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun markAsDone(ho: HealthObject) {
        deleteNotifHealthObject(context, ho.id, "health")
        viewModelScope.launch {
            val curDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val curTime = LocalTime.now().format(timeParser)
            healthObjectRepository.updateHealthObject(
                ho.copy(
                    isDone = true,
                    date = curDate,
                    time = if (ho.category == HealthCategory.PREVENTION) null else curTime
                )
            )
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


