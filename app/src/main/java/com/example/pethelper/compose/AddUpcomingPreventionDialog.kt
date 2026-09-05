package com.example.pethelper.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pethelper.R
import java.time.LocalDate
import java.time.YearMonth

val preventions =
    listOf("Deworming", "Flea Prevention", "Tick Prevention", "Flea & Tick Prevention")

@Composable
fun AddUpcomingPreventionDialog(onDismiss: () -> Unit, onAddPrevention: () -> Unit) {
    var action by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var isNotif by remember { mutableStateOf(false) }
    var dayNotif by remember { mutableStateOf("1 day before") }
    var timeNotif by remember { mutableStateOf("12:00") }
    var isDateOpened by remember { mutableStateOf(false) }
    var isDateNotifOpened by remember { mutableStateOf(false) }
    var isTimeNotifOpened by remember { mutableStateOf(false) }
    DialogPattern(
        "Add Upcoming Prevention", R.drawable.parasites_dialog,
        onDismiss, onAddPrevention, action.isNotEmpty()
    ) {
        LabelAndTextField("Action", action, { action = it }, "e.g. Flea Prevention")
        Spacer(Modifier.height(16.dp))
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            preventions.forEach {
                DialogChip(action == it, it) { action = it }
            }
        }
        Spacer(Modifier.height(16.dp))
        LabelAndTextField("Note (optional)", note, { note = it }, "e.g. Milbemax")
        Spacer(Modifier.height(16.dp))
        LabelAndDateTime("Date", date.format(dateParser)) { isDateOpened = true }
        if (isDateOpened) {
            PopupCalendar(
                { isDateOpened = false },
                { date = it },
                date,
                { it >= LocalDate.now() },
                YearMonth.now(),
                YearMonth.now().plusMonths(12)
            )
        }
        Spacer(Modifier.height(16.dp))
        ReminderBlock(
            Modifier.align(Alignment.Start),
            isNotif,
            { isNotif = it },
            true,
            dayNotif,
            { isDateNotifOpened = true },
            timeNotif,
            { isTimeNotifOpened = false }
        )
        DropdownMenuPattern(
            isDateNotifOpened,
            { isDateNotifOpened = false },
            daySelector
        ) {
            dayNotif = it
            isDateNotifOpened = false
        }
        DropdownMenuPattern(
            isTimeNotifOpened,
            { isTimeNotifOpened = false },
            times
        ) {
            timeNotif = it
            isTimeNotifOpened = false
        }
    }
}