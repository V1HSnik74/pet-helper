package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogChip
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.DropdownMenuPattern
import com.example.pethelper.compose.patterns.LabelAndDateTime
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.PopupCalendar
import com.example.pethelper.compose.patterns.ReminderBlock
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.compose.patterns.daySelector
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.compose.patterns.times
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

val catVaccines = listOf("Rabies Vaccine", "FVRCP", "FeLV")

@Composable
fun AddUpcomingVaccine(
    onDismiss: () -> Unit, onSaveInfo: (
        vaccine: String, date: String, time: String,
        isNotification: Boolean, notifDate: String, notifTime: String, petId: Int
    ) -> Unit,
    petId: Int
) {
    var vaccine by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now().format(timeParser)) }
    var isNotif by remember { mutableStateOf(false) }
    var notifDate by remember { mutableStateOf("1 day before") }
    var notifTime by remember { mutableStateOf(LocalTime.now().format(timeParser)) }
    var isCalendarOpened by remember { mutableStateOf(false) }
    var isTimeOpened by remember { mutableStateOf(false) }
    var isNotifDateOpened by remember { mutableStateOf(false) }
    var isNotifTimeOpened by remember { mutableStateOf(false) }
    DialogPattern(
        "Add Upcoming Vaccine",
        R.drawable.vaccine_dialog,
        onDismiss,
        {
            onSaveInfo(
                vaccine,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time,
                isNotif,
                if (isNotif) notifDate else "",
                if (isNotif) notifTime else "",
                petId
            )
        },
        vaccine.isNotEmpty()
    ) {
        LabelAndTextField(
            "Vaccine",
            vaccine,
            { vaccine = it },
            "e.g. Rabies Vaccine"
        )
        Spacer(Modifier.height(16.dp))
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            catVaccines.forEach {
                DialogChip(vaccine == it, it) {
                    vaccine = it
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth()) {
            LabelAndDateTime(
                "Date",
                date.format(dateParser)
            ) { isCalendarOpened = true }
            if (isCalendarOpened) {
                PopupCalendar(
                    { isCalendarOpened = false },
                    {
                        date = it
                        isCalendarOpened = false
                    },
                    date, { it >= LocalDate.now() },
                    YearMonth.now(),
                    YearMonth.now().plusMonths(36)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Box {
            LabelAndDateTime(
                "Time",
                time
            ) { isTimeOpened = true }
            DropdownMenuPattern(
                isTimeOpened, { isTimeOpened = false },
                times
            ) {
                time = it
                isTimeOpened = false
            }
        }
        Spacer(Modifier.height(16.dp))
        ReminderBlock(
            Modifier.fillMaxWidth(),
            isNotif,
            { isNotif = it },
            true,
            notifDate,
            {
                notifDate = it
                isNotifDateOpened = false
            },
            daySelector,
            notifTime,
            {
                notifTime = it
                isNotifTimeOpened = false
            },
            times
        )
    }
}