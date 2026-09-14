package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.pethelper.compose.patterns.LabelAndDateTime
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.PopupCalendar
import com.example.pethelper.compose.patterns.ReminderBlock
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.compose.patterns.daySelector
import com.example.pethelper.compose.patterns.preventions
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.compose.patterns.times
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun AddTreatmentHistoryDialog(
    onDismiss: () -> Unit,
    onAddPrevention: (
        action: String,
        note: String,
        date: String,
        isNotif: Boolean,
        dateNotif: String,
        timeNotif: String,
        petId: Int,
        isDone: Boolean
    ) -> Unit,
    petId: Int
) {
    val curDay = remember { LocalDate.now() }
    val curMonth = remember { YearMonth.now() }
    var action by remember { mutableStateOf("") }
    val monthsToAdd = if (action == "Deworming") 3L else 1L
    var note by remember { mutableStateOf("") }
    var noteUpc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(curDay) }
    var dateUpc by remember { mutableStateOf(curDay.plusMonths(monthsToAdd)) }
    var isNotif by remember { mutableStateOf(false) }
    var dateNotif by remember { mutableStateOf("1 day before") }
    var timeNotif by remember { mutableStateOf(LocalTime.now().format(timeParser)) }
    var isDateOpened by remember { mutableStateOf(false) }
    var isDateUpcOpened by remember { mutableStateOf(false) }
    var isDateNotifOpened by remember { mutableStateOf(false) }
    var isTimeNotifOpened by remember { mutableStateOf(false) }
    DialogPattern(
        "Add Treatment History",
        R.drawable.parasites_dialog,
        onDismiss,
        {
            onAddPrevention(
                action,
                note,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                false,
                "",
                "",
                petId,
                true
            )
            onAddPrevention(
                action,
                noteUpc,
                dateUpc.format(DateTimeFormatter.ISO_LOCAL_DATE),
                isNotif,
                if (isNotif) dateNotif else "",
                if (isNotif) timeNotif else "",
                petId,
                false
            )
        },
        action.isNotEmpty()
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            LabelAndTextField(
                "Action",
                action,
                {
                    action = it
                    dateUpc = date.plusMonths(monthsToAdd)
                },
                "e.g. Flea Prevention"
            )
            Spacer(Modifier.height(16.dp))
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                preventions.forEach {
                    DialogChip(
                        action == it,
                        it
                    ) {
                        action = it
                        dateUpc = date.plusMonths(monthsToAdd)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            LabelAndTextField(
                "Note (optional)",
                note,
                {
                    note = it
                },
                "e.g. Milbemax"
            )
            Spacer(Modifier.height(16.dp))
            LabelAndTextField(
                "Note For Upcoming Prevention (optional)",
                noteUpc,
                { noteUpc = it },
                "e.g. Milbemax"
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.weight(1f)) {
                    LabelAndDateTime(
                        "Date",
                        date.format(dateParser)
                    ) { isDateOpened = true }
                    if (isDateOpened) {
                        PopupCalendar(
                            { isDateOpened = false },
                            {
                                date = it
                                dateUpc = it.plusMonths(monthsToAdd)
                                isDateOpened = false
                            },
                            date,
                            { it <= curDay },
                            curMonth.minusMonths(12),
                            curMonth
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Box(Modifier.weight(1f)) {
                    LabelAndDateTime(
                        "Date Due",
                        dateUpc.format(dateParser)
                    ) { isDateUpcOpened = true }
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
                            curMonth.plusMonths(12)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            ReminderBlock(
                Modifier,
                isNotif,
                { isNotif = it },
                true,
                dateNotif,
                {
                    dateNotif = it
                    isDateNotifOpened = false
                },
                daySelector,
                timeNotif,
                {
                    timeNotif = it
                    isTimeNotifOpened = false
                },
                times
            )
        }

    }
}