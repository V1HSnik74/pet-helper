package com.vhsnik.pethelper.ui.compose.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.*
import com.vhsnik.pethelper.ui.compose.utils.*
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun AddTreatmentHistoryDialog(
    onDismiss: () -> Unit,
    onAddPrevention: (HealthObject) -> Unit,
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
    var activeDialog by remember { mutableStateOf<Dialogs?>(null) }
    DialogPattern(
        "Add Treatment History",
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
                    isDone = true,
                    isNotif = false
                )
            )
            onAddPrevention(
                HealthObject(
                    action,
                    HealthCategory.PREVENTION,
                    petId,
                    noteUpc.ifBlank { null },
                    dateUpc.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    null,
                    false,
                    isNotif,
                    if (isNotif) dateNotif else null,
                    if (isNotif) timeNotif else null
                )
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
                    ) {
                        activeDialog =
                            Dialogs.DATE
                    }
                    if (activeDialog == Dialogs.DATE) {
                        PopupCalendar(
                            { activeDialog = null },
                            {
                                date = it
                                dateUpc = it.plusMonths(monthsToAdd)
                                activeDialog = null
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
                },
                getAvailableOptionsNotifications(
                    dateUpc
                ),
                timeNotif,
                {
                    timeNotif = it
                },
                dateUpc
            )
        }

    }
}