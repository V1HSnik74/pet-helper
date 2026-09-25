package com.example.pethelper.compose.reminder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.RemindersCalendar
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.backgroundColor
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.compose.patterns.smallTextColor
import com.example.pethelper.db.viewModel.PetsViewModel
import com.example.pethelper.db.viewModel.ReminderViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RemindersScreen(
    reminderViewModel: ReminderViewModel,
    petId: Int,
    onBackClick: () -> Unit,
    petsViewModel: PetsViewModel
) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var isAddReminderOpened by remember { mutableStateOf(false) }
    val allEventsThisDate by remember(date, petId) {
        reminderViewModel.getAllRemindersByPetSelectedDay(
            date.format(
                DateTimeFormatter.ISO_LOCAL_DATE
            ), petId
        )
    }.collectAsState(emptyList())
    val pet by petsViewModel.getPetById(petId).collectAsState(null)
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(22.dp, 50.dp)
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
                    .size(22.dp)
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
                items(allEventsThisDate, { it.id }) {
                    ReminderCard(it.icon, it.name, it.note, it.date, it.time)
                }
            }
        }
    }
    if (isAddReminderOpened) {
        AddReminderDialog(
            { isAddReminderOpened = false },
            { name, note, date, time, icon, isNotif, notifDate, notifTime, petId ->
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
                    if (pet != null) pet!!.name else ""
                )
                isAddReminderOpened = false
            },
            petId
        )
    }
}

@Composable
private fun ReminderCard(icon: Int, name: String, note: String?, date: String, time: String?) {
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
            Column(Modifier.wrapContentWidth(),
                verticalArrangement = Arrangement.Center) {
                val parsedDate = remember(date)
                { LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).format(dateParser) }
                TextMaker(parsedDate, 10.sp, fontWeight = FontWeight.Medium)
                if (!time.isNullOrEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    TextMaker(time, 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}