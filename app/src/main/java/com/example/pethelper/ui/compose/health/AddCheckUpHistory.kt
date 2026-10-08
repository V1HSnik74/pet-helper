package com.example.pethelper.ui.compose.health

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pethelper.R
import com.example.pethelper.data.entity.*
import com.example.pethelper.ui.compose.patterns.*
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun AddCheckUpHistory(
    onDismiss: () -> Unit,
    onAddCheckUp: (HealthObject) -> Unit,
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
    var activeDialog by remember { mutableStateOf<Dialogs?>(null) }
    DialogPattern(
        "Add Check-up History",
        R.drawable.checkup_dialog,
        onDismiss,
        {
            onAddCheckUp(
                HealthObject(
                    type,
                    HealthCategory.CHECKUP,
                    petId,
                    note.ifBlank { null },
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    time,
                    isDone = true,
                    isNotif = false
                )
            )
            onAddCheckUp(
                HealthObject(
                    type,
                    HealthCategory.CHECKUP,
                    petId,
                    noteUpc.ifBlank { null },
                    dateUpc.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    timeUpc,
                    false,
                    isNotif,
                    if (isNotif) notifDate else null,
                    if (isNotif) notifTime else null
                )
            )
        },
        type.isNotEmpty()
    ) {
        LabelAndTextField(
            "Check-up Type",
            type,
            { type = it },
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
        LabelAndTextField(
            "Note For Upcoming Check-up (optional)",
            noteUpc,
            { noteUpc = it },
            "e.g. Veterinary \"Cute Paws\""
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                LabelAndDateTime(
                    "Date",
                    date.format(dateParser)
                ) {
                    activeDialog =
                        Dialogs.DATE
                }
                if (activeDialog == Dialogs.DATE) {
                    PopupCalendar(
                        { activeDialog = null },
                        {
                            date = it
                            dateUpc = it.plusMonths(12)
                            activeDialog = null
                        },
                        date,
                        { it <= curDay },
                        curMonth.minusMonths(24),
                        curMonth
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Box(Modifier.weight(1f)) {
                LabelAndDateTime(
                    "Date Due",
                    dateUpc.format(dateParser)
                ) {
                    activeDialog =
                        Dialogs.DATE_UPC
                }
                if (activeDialog == Dialogs.DATE_UPC) {
                    PopupCalendar(
                        { activeDialog = null },
                        {
                            dateUpc = it
                            activeDialog = null
                        },
                        dateUpc,
                        { it >= curDay },
                        curMonth,
                        curMonth.plusMonths(24)
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                LabelAndDateTime(
                    "Time",
                    time
                ) {
                    activeDialog =
                        Dialogs.TIME
                }
                if (activeDialog == Dialogs.TIME) {
                    PopupTimePicker(
                        { activeDialog = null },
                        { time = it },
                        date,
                        false
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Box(Modifier.weight(1f)) {
               LabelAndDateTime(
                    "Time Due",
                    timeUpc
                ) {
                    activeDialog =
                        Dialogs.TIME_UPC
                }
                if (activeDialog == Dialogs.TIME_UPC) {
                    PopupTimePicker(
                        { activeDialog = null },
                        { timeUpc = it },
                        dateUpc,
                        true
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
            notifDate,
            {
                notifDate = it
            },
            getAvailableOptionsNotifications(
                dateUpc,
                timeUpc
            ),
            notifTime,
            {
                notifTime = it
            },
            dateUpc,
            timeUpc
        )
    }
}
