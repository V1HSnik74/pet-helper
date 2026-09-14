package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun AddUpcomingCheckUp(
    onDismiss: () -> Unit,
    onAddCheckUp: (
        name: String,
        note: String,
        date: String,
        time: String,
        isNotif: Boolean,
        dateNotif: String,
        timeNotif: String,
        petId: Int
    ) -> Unit,
    petId: Int
) {
    var checkUp by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now().format(timeParser)) }
    var isNotif by remember { mutableStateOf(false) }
    var notifDate by remember { mutableStateOf("1 day before") }
    var notifTime by remember { mutableStateOf(LocalTime.now().format(timeParser)) }
    var isDateOpened by remember { mutableStateOf(false) }
    var isTimeOpened by remember { mutableStateOf(false) }
    var isNotifDateOpened by remember { mutableStateOf(false) }
    var isNotifTimeOpened by remember { mutableStateOf(false) }
    DialogPattern(
        "Add Upcoming Check-up",
        R.drawable.checkup_dialog,
        onDismiss,
        {
            onAddCheckUp(
                checkUp,
                note,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time,
                isNotif,
                notifDate,
                notifTime,
                petId
            )
        },
        checkUp.isNotEmpty()
    ) {
        LabelAndTextField(
            "Check-up type",
            checkUp,
            { checkUp = it },
            "e.g. Annual Wellness Exam"
        )
        Spacer(Modifier.height(16.dp))
        LabelAndTextField(
            "Note (optional)",
            note,
            { note = it },
            "e.g. Veterinary \"Cute Paws\""
        )
        Spacer(Modifier.height(16.dp))
        Box() {
            LabelAndDateTime(
                "Date",
                date.format(dateParser)
            ) { isDateOpened = true }
            if (isDateOpened) {
                PopupCalendar(
                    { isDateOpened = false },
                    {
                        date = it
                        isDateOpened = false
                    },
                    date,
                    { it >= LocalDate.now() },
                    YearMonth.now(),
                    YearMonth.now().plusMonths(24)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Box() {
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
        Spacer(Modifier.height(16.dp))
        ReminderBlock(
            Modifier,
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