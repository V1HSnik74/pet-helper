package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Box
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
fun AddCheckUpHistory(
    onDismiss: () -> Unit,
    onAddCheckUp: (
        name: String,
        note: String,
        date: String,
        time: String,
        isNotif: Boolean,
        dateNotif: String,
        timeNotif: String,
        isDone: Boolean,
        petId: Int
    ) -> Unit,
    petId: Int
) {
    val curDay = remember { LocalDate.now() }
    val curTime = remember { LocalTime.now().format(timeParser) }
    val curMonth = remember { YearMonth.now() }
    var type by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var noteUpc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(curDay) }
    var dateUpc by remember { mutableStateOf(curDay.plusMonths(12)) }
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
        "Add Check-up History",
        R.drawable.checkup_dialog,
        onDismiss,
        {
            onAddCheckUp(
                type,
                note,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time,
                false,
                "",
                "",
                true,
                petId
            )
            onAddCheckUp(
                type,
                noteUpc,
                dateUpc.format(DateTimeFormatter.ISO_LOCAL_DATE),
                timeUpc,
                isNotif,
                if (isNotif) notifDate else "",
                if (isNotif) notifTime else "",
                false,
                petId
            )
        },
        type.isNotEmpty()
    ) {
        LabelAndTextField("Check-up Type", type, { type = it }, "e.g. Annual Wellness Exam")
        Spacer(Modifier.height(16.dp))
        LabelAndTextField("Note (optional)", note, { note = it }, "e.g. Veterinary \"Cute Paws\"")
        Spacer(Modifier.height(16.dp))
        LabelAndTextField(
            "Note For Upcoming Check-up (optional)",
            noteUpc,
            { noteUpc = it },
            "e.g. Veterinary \"Cute Paws\""
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                LabelAndDateTime("Date", date.format(dateParser)) { isDateOpened = true }
            }
            Spacer(Modifier.width(16.dp))
            Box(Modifier.weight(1f)) {
                LabelAndDateTime("Date Due", dateUpc.format(dateParser)) { isDateUpcOpened = true }
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
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                LabelAndDateTime("Time", time) { isTimeOpened = true }
                DropdownMenuPattern(isTimeOpened, { isTimeOpened = false }, times) {
                    time = it
                    isTimeOpened = false
                }
            }
            Spacer(Modifier.width(16.dp))
            Box(Modifier.weight(1f)) {
                LabelAndDateTime("Time Due", timeUpc) { isTimeUpcOpened = true }
                DropdownMenuPattern(isTimeUpcOpened, { isTimeUpcOpened = false }, times) {
                    timeUpc = it
                    isTimeUpcOpened = false
                }
            }
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
