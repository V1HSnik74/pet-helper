package com.example.pethelper.ui.compose.reminder

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pethelper.R
import com.example.pethelper.data.entity.*
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.ui.theme.*
import com.example.pethelper.ui.viewModel.*
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun RemindersScreen(
    reminderViewModel: ReminderViewModel = hiltViewModel(),
    petId: Int,
    onBackClick: () -> Unit,
    onNavigateToHealth: (HealthCategory) -> Unit,
    petsViewModel: PetsViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) {
        petsViewModel.setCurPet(petId)
    }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var isAddReminderOpened by remember { mutableStateOf(false) }
    val allEventsThisDate by remember(date, petId) {
        reminderViewModel.getAllRemindersByPetSelectedDay(
            date.format(
                DateTimeFormatter.ISO_LOCAL_DATE
            ), petId
        )
    }.collectAsStateWithLifecycle(emptyList())
    val pet = petsViewModel.currPet
    var isDeleteOpened by remember { mutableStateOf(false) }
    var actionToDelete by remember { mutableStateOf<(() -> Unit)?>(null) }
    var reminderToEdit by remember { mutableStateOf<Reminder?>(null) }
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(22.dp, 64.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.back), "back button",
                Modifier
                    .size(12.dp, 24.dp)
                    .clickable(indication = null, interactionSource = null) { onBackClick() }
            )
            TextMaker("Reminders", 18.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                painterResource(R.drawable.prime_plus), "add reminder",
                Modifier
                    .size(24.dp)
                    .clickable(indication = null, interactionSource = null) {
                        isAddReminderOpened = true
                    }
            )
        }
        Spacer(Modifier.height(24.dp))
        RemindersCalendar(date) {
            date = it
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (allEventsThisDate.isNotEmpty()) {
                items(allEventsThisDate) {
                    when (it) {
                        is Reminder -> {
                            ReminderCard(
                                it.icon, it.name, it.note, it.date, it.time,
                                {
                                    isDeleteOpened = true
                                    actionToDelete = { reminderViewModel.deleteReminder(it.id) }
                                },
                                {
                                    reminderToEdit = it
                                    isAddReminderOpened = true
                                })
                        }

                        is HealthObject -> {
                            val icon = when (it.category) {
                                HealthCategory.CHECKUP -> R.drawable.checkup_icon
                                HealthCategory.PREVENTION -> R.drawable.bug_icon
                                HealthCategory.VACCINE -> R.drawable.core_vaccine_icon
                                HealthCategory.MEDICINE -> R.drawable.next_up
                            }
                            val date = if (it.category == HealthCategory.MEDICINE) {
                                LocalDateTime.parse(it.nextTaking).toLocalDate()
                                    .format(DateTimeFormatter.ISO_LOCAL_DATE)
                            } else it.date.orEmpty()
                            val time = if (it.category == HealthCategory.MEDICINE) {
                                LocalDateTime.parse(it.nextTaking)
                                    .toLocalTime()
                                    .format(timeParser)
                            } else it.time.orEmpty()
                            ReminderCard(
                                icon, it.name, it.note, date, time,
                                {
                                    isDeleteOpened = true
                                    actionToDelete = {
                                        healthViewModel.deleteHealthObject(
                                            it.id,
                                            it.category,
                                            pet?.name.orEmpty()
                                        )
                                    }
                                },
                                { onNavigateToHealth(it.category) }
                            )
                        }
                    }
                }
            }
        }
    }
    if (isAddReminderOpened) {
        AddReminderDialog(
            {
                isAddReminderOpened = false
                reminderToEdit = null
            },
            { name, note, date, time, icon, isNotif, notifDate, notifTime, petId ->
                if (reminderToEdit == null)
                    reminderViewModel.addReminder(
                        name,
                        note,
                        date,
                        time,
                        icon,
                        isNotif,
                        notifDate,
                        notifTime,
                        petId,
                        pet?.name.orEmpty(),
                        pet?.animal.orEmpty()
                    )
                else
                    reminderViewModel.updateReminder(
                        reminderToEdit!!.copy(
                            name = name,
                            note = note,
                            date = date,
                            time = time,
                            icon = icon,
                            isNotif = isNotif,
                            notifDate = notifDate,
                            notifTime = notifTime
                        ),
                        pet?.animal.orEmpty(),
                        pet?.name.orEmpty()
                    )
                isAddReminderOpened = false
                reminderToEdit = null
            },
            petId,
            reminderToEdit != null,
            reminderToEdit?.name.orEmpty(),
            reminderToEdit?.note.orEmpty(),
            reminderToEdit?.date?.let { LocalDate.parse(it) } ?: LocalDate.now(),
            reminderToEdit?.time.orEmpty(),
            reminderToEdit?.icon ?: R.drawable.reminder,
            reminderToEdit?.isNotif ?: false,
            reminderToEdit?.notifDate ?: "same day",
            reminderToEdit?.notifTime.orEmpty()
        )
    }
    if (isDeleteOpened) {
        DeleteDialog(
            {
                isDeleteOpened = false
                actionToDelete = null
            }
        ) {
            actionToDelete?.invoke()
            isDeleteOpened = false
            actionToDelete = null
        }
    }
}

@Composable
private fun ReminderCard(
    icon: Int,
    name: String,
    note: String?,
    date: String,
    time: String?,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    var isMenuOpened by remember { mutableStateOf(false) }
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painterResource(icon), "card icon")
                Spacer(Modifier.width(12.dp))
                Column(Modifier.wrapContentHeight()) {
                    TextMaker(name, 12.sp, fontWeight = FontWeight.SemiBold)
                    if (!note.isNullOrEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        TextMaker(note, 10.sp, smallTextColor, FontWeight.SemiBold)
                    }
                }
            }
            Row(
                Modifier.wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.wrapContentWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    val parsedDate = remember(date)
                    { LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).format(dateParser) }
                    TextMaker(parsedDate, 10.sp, fontWeight = FontWeight.Medium)
                    if (!time.isNullOrEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        TextMaker(time, 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box {
                    Icon(
                        Icons.Default.MoreVert,
                        "More options",
                        Modifier
                            .size(18.dp)
                            .clickable(indication = null, interactionSource = null) {
                                isMenuOpened = true
                            }
                    )
                    DropdownMoreOptions(
                        isMenuOpened, { isMenuOpened = false },
                        onEditClick, onDeleteClick
                    )
                }
            }
        }
    }
}