package com.vhsnik.pethelper.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.*
import com.vhsnik.pethelper.data.repository.interfaces.*
import com.vhsnik.pethelper.ui.compose.utils.timeParser
import com.vhsnik.pethelper.workers.deleteNotifReminder
import com.vhsnik.pethelper.workers.scheduleNotif
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val healthObjectRepository: HealthObjectRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val curPetId = MutableStateFlow<Int?>(null)
    fun setCurPet(petId: Int) {
        curPetId.value = petId
    }

    fun addReminder(
        name: String,
        note: String?,
        date: String,
        time: String?,
        icon: Int,
        isNotif: Boolean,
        notifDate: String,
        notifTime: String,
        petName: String,
        animal: String
    ) {
        val petId = curPetId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val id = reminderRepository.addReminder(
                Reminder(
                    name,
                    note,
                    date,
                    time,
                    icon,
                    isNotif,
                    notifDate,
                    notifTime,
                    petId
                )
            )
            if (isNotif) {
                scheduleNotifReminder(
                    id, name, date, notifDate, notifTime, animal, petName, false
                )
            }
        }
    }

    fun updateReminder(r: Reminder, animal: String, petName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = reminderRepository.updateReminder(r)
            if (r.isNotif) {
                scheduleNotifReminder(
                    id,
                    r.name,
                    r.date,
                    r.notifDate,
                    r.notifTime,
                    animal,
                    petName,
                    true
                )
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNotifReminder(context, id)
            reminderRepository.deleteReminder(id)
        }
    }

    fun scheduleNotifReminder(
        id: Long,
        name: String,
        date: String,
        notifDate: String,
        notifTime: String,
        animal: String,
        petName: String,
        isUpdating: Boolean
    ) {
        if (isUpdating)
            deleteNotifReminder(context, id)
        scheduleNotif(
            context,
            id,
            name,
            date,
            notifDate,
            notifTime,
            "Event",
            if (animal == "Dog") R.drawable.dog else R.drawable.cat,
            petName
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllRemindersByPetSelectedDay(date: String): StateFlow<List<Any>> {
        return curPetId.filterNotNull().flatMapLatest {
            val reminders = reminderRepository.getAllRemindersByDate(it, date)
            val healthObjects = healthObjectRepository.getAllHealthObject(it).map { hos ->
                hos.filter { ho ->
                    if (ho.category == HealthCategory.MEDICINE) {
                        LocalDateTime.parse(ho.nextTaking).toLocalDate()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE) == date
                    } else ho.date == date
                }
            }
            combine(reminders, healthObjects) { r, ho ->
                val combined = mutableListOf<Any>()
                combined.addAll(r)
                combined.addAll(ho)
                combined
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allRemindersSorted: StateFlow<List<Any>> =
        curPetId.filterNotNull().flatMapLatest {
            val reminders = reminderRepository.getAllReminders(it).map { rems ->
                val now = LocalDateTime.now()
                rems.filter { r ->
                    val date = LocalDate.parse(r.date, DateTimeFormatter.ISO_LOCAL_DATE)
                    val time = if (!r.time.isNullOrEmpty())
                        LocalTime.parse(r.time, timeParser)
                    else LocalTime.parse("23:59", timeParser)
                    LocalDateTime.of(date, time) >= now
                }
            }
            val healthObjects = healthObjectRepository.getAllHealthObjectIsDone(it, false)
            combine(reminders, healthObjects) { r, ho ->
                val allReminders = mutableListOf<Any>()
                allReminders.addAll(r)
                allReminders.addAll(ho)
                allReminders.sortedBy {rem ->
                    when (rem) {
                        is Reminder -> {
                            val date = LocalDate.parse(rem.date, DateTimeFormatter.ISO_LOCAL_DATE)
                            val time = if (!rem.time.isNullOrEmpty())
                                LocalTime.parse(rem.time, timeParser)
                            else LocalTime.parse("23:59", timeParser)
                            LocalDateTime.of(date, time)
                        }

                        is HealthObject -> {
                            if (rem.category == HealthCategory.MEDICINE) {
                                LocalDateTime.parse(
                                    rem.nextTaking,
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                )
                            } else {
                                val date =
                                    LocalDate.parse(rem.date, DateTimeFormatter.ISO_LOCAL_DATE)
                                val time = if (!rem.time.isNullOrEmpty()) LocalTime.parse(
                                    rem.time,
                                    timeParser
                                )
                                else LocalTime.parse("23:59", timeParser)
                                LocalDateTime.of(date, time)
                            }
                        }

                        else -> null
                    }
                }

            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}