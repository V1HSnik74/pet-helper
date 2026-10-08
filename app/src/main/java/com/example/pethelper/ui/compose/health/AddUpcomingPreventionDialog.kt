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
fun AddUpcomingPreventionDialog(
    onDismiss: () -> Unit,
    onAddPrevention: (HealthObject) -> Unit,
    petId: Int,
    isDone: Boolean,
    label: String,
    isUpdate: Boolean,
    action: String,
    note: String,
    date: LocalDate,
    isNotif: Boolean,
    dayNotif: String,
    timeNotif: String
) {
    var action by remember { mutableStateOf(action) }
    var note by remember { mutableStateOf(note) }
    var date by remember { mutableStateOf(date) }
    var isNotif by remember { mutableStateOf(isNotif) }
    var dayNotif by remember { mutableStateOf(dayNotif) }
    var timeNotif by remember { mutableStateOf(timeNotif) }
    var isDateOpened by remember { mutableStateOf(false) }
    DialogPattern(
        if (isUpdate) "Update $label Prevention" else "Add $label Prevention",
        R.drawable.parasites_dialog,
        onDismiss,
        {
            onAddPrevention(
                HealthObject(
                    action,
                    HealthCategory.PREVENTION,
                    petId,
                    note.ifBlank { null },
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    null,
                    isDone,
                    isNotif,
                    if (isNotif) dayNotif else null,
                    if (isNotif) timeNotif else null
                )
            )
        },
        action.isNotEmpty()
    ) {
        LabelAndTextField(
            "Action",
            action,
            { action = it },
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
                ) { action = it }
            }
        }
        Spacer(Modifier.height(16.dp))
        LabelAndTextField(
            "Note (optional)",
            note,
            { note = it },
            "e.g. Milbemax"
        )
        Spacer(Modifier.height(16.dp))
        Box {
            LabelAndDateTime(
                "Date",
                date.format(dateParser)
            ) { isDateOpened = true }
            if (isDateOpened) {
                PopupCalendar(
                    { isDateOpened = false },
                    { date = it },
                    date,
                    {
                        if (label == "History") {
                            it <= LocalDate.now()
                        } else {
                            it >= LocalDate.now()
                        }
                    },
                    if (label == "History") YearMonth.now().minusMonths(12) else YearMonth.now(),
                    if (label == "History") YearMonth.now() else YearMonth.now().plusMonths(12)
                )
            }
        }
        if (label != "History") {
            Spacer(Modifier.height(16.dp))
            ReminderBlock(
                Modifier,
                isNotif,
                { isNotif = it },
                true,
                dayNotif,
                {
                    dayNotif = it
                },
                getAvailableOptionsNotifications(
                    date
                ),
                timeNotif,
                {
                    timeNotif = it
                },
                date
            )
        }
    }
}