package com.vhsnik.pethelper.ui.compose.health

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.*
import com.vhsnik.pethelper.ui.compose.utils.*
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun AddUpcomingCheckUp(
    onDismiss: () -> Unit,
    onAddCheckUp: (HealthObject) -> Unit,
    petId: Int,
    isDone: Boolean,
    label: String,
    isUpdate: Boolean,
    checkUp: String,
    note: String,
    date: LocalDate,
    time: String,
    isNotif: Boolean,
    dateNotif: String,
    timeNotif: String
) {
    var checkUp by remember { mutableStateOf(checkUp) }
    var note by remember { mutableStateOf(note) }
    var date by remember { mutableStateOf(date) }
    var time by remember(date) { mutableStateOf(time) }
    var isNotif by remember { mutableStateOf(isNotif) }
    var notifDate by remember { mutableStateOf(dateNotif) }
    var notifTime by remember { mutableStateOf(timeNotif) }
    var activeDialog by remember { mutableStateOf<Dialogs?>(null) }
    DialogPattern(
        if (isUpdate) "Update $label Check-up" else "Add $label Check-up",
        R.drawable.checkup_dialog,
        onDismiss,
        {
            onAddCheckUp(
                HealthObject(
                    checkUp,
                    HealthCategory.CHECKUP,
                    petId,
                    note.ifBlank { null },
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    time,
                    isDone,
                    isNotif,
                    if (isNotif) notifDate else null,
                    if (isNotif) notifTime else null
                )
            )
        },
        checkUp.isNotBlank() && time.isNotEmpty()
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
        Box {
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
                        activeDialog = null
                    },
                    date,
                    {
                        if (label == "History") {
                            it <= LocalDate.now()
                        } else {
                            it >= LocalDate.now()
                        }
                    },
                    if (label == "History") YearMonth.now().minusMonths(24) else YearMonth.now(),
                    if (label == "History") YearMonth.now() else YearMonth.now().plusMonths(24)
                )
            }
        }
        if (label != "History") {
            Spacer(Modifier.height(16.dp))
            Box {
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
                        true
                    )
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
                    date,
                    time
                ),
                notifTime,
                {
                    notifTime = it
                },
                date,
                time
            )
        }
    }
}