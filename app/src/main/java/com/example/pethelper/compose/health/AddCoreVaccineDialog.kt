package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

@Composable
fun AddCoreVaccineDialog(
    onDismiss: () -> Unit,
    onAddVaccine: (
        vaccine: String,
        date: String,
        time: String,
        isNotif: Boolean,
        notifDate: String,
        notifTime: String,
        isDone: Boolean,
        petId: Int
    ) -> Unit,
    petId: Int
) {
    val curTime = remember { LocalTime.now().format(timeParser) }
    val curDay = remember { LocalDate.now() }
    val curMonth = remember { YearMonth.now() }
    var vaccine by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(curDay) }
    var dateUpc by remember { mutableStateOf(curDay.plusYears(1)) }
    var time by remember { mutableStateOf(curTime) }
    var timeUpc by remember { mutableStateOf(curTime) }
    var isNotif by remember { mutableStateOf(false) }
    var notifDate by remember { mutableStateOf("1 day before") }
    var notifTime by remember { mutableStateOf(curTime) }
    var isDateOpened by remember { mutableStateOf(false) }
    var isDateUpcOpened by remember { mutableStateOf(false) }
    var isTimeOpened by remember { mutableStateOf(false) }
    var isTimeUpcOpened by remember { mutableStateOf(false) }
    var isNotifDateOpened by remember { mutableStateOf(false) }
    var isNotifTimeOpened by remember { mutableStateOf(false) }
    DialogPattern(
        label = "Add Core Vaccine",
        icon = R.drawable.vaccine_dialog,
        onDismiss = onDismiss,
        onSaveInfo = {
            onAddVaccine(
                vaccine,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time,
                false,
                "",
                "",
                true,
                petId
            )
            onAddVaccine(
                vaccine,
                dateUpc.format(DateTimeFormatter.ISO_LOCAL_DATE),
                timeUpc,
                isNotif,
                if (isNotif) notifDate else "",
                if (isNotif) notifTime else "",
                false,
                petId
            )
        },
        isEnabled = vaccine.isNotEmpty(),
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
                DialogChip(vaccine == it, it) { vaccine = it }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                LabelAndDateTime(
                    "Date",
                    date.format(dateParser)
                ) { isDateOpened = true }
            }
            Spacer(Modifier.width(16.dp))
            Box(Modifier.weight(1f)) {
                LabelAndDateTime(
                    "Date Due",
                    dateUpc.format(dateParser)
                ) { isDateUpcOpened = true }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                LabelAndDateTime(
                    "Time",
                    time
                ) { isTimeOpened = true }
                DropdownMenuPattern(
                    isTimeOpened,
                    { isTimeOpened = false },
                    times
                ) {
                    time = it
                    isTimeOpened = false
                }
            }
            Spacer(Modifier.width(16.dp))
            Box(Modifier.weight(1f)) {
                LabelAndDateTime(
                    "Time Due",
                    timeUpc
                ) { isTimeUpcOpened = true }
                DropdownMenuPattern(
                    isTimeUpcOpened,
                    { isTimeUpcOpened = false },
                    times
                ) {
                    timeUpc = it
                    isTimeUpcOpened = false
                }
            }
        }
        if (isDateOpened) {
            PopupCalendar(
                { isDateOpened = false },
                {
                    date = it
                    dateUpc = it.plusMonths(12)
                    isDateOpened = false
                },
                date,
                { it <= curDay },
                curMonth.minusMonths(24),
                curMonth
            )
        }
        if (isDateUpcOpened) {
            PopupCalendar(
                { isDateUpcOpened = false },
                {
                    dateUpc = it
                    isDateUpcOpened = false
                },
                dateUpc,
                { it >= curDay },
                curMonth,
                curMonth.plusMonths(24)
            )
        }
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {
            ReminderBlock(
                Modifier,
                isNotif,
                { isNotif = it },
                true,
                notifDate,
                { isNotifDateOpened = true },
                notifTime,
                { isNotifTimeOpened = true }
            )
            DropdownMenuPattern(
                isNotifDateOpened,
                { isNotifDateOpened = false },
                daySelector
            ) {
                notifDate = it
                isNotifDateOpened = false
            }
            DropdownMenuPattern(
                isNotifTimeOpened,
                { isNotifTimeOpened = false },
                times
            ) {
                notifTime = it
                isNotifTimeOpened = false
            }
        }
    }
}