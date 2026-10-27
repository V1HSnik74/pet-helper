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
fun AddCoreVaccineDialog(
    onDismiss: () -> Unit,
    onAddVaccine: (HealthObject) -> Unit,
    petId: Int,
    animal: String
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
    var activeDialog by remember { mutableStateOf<Dialogs?>(null) }
    DialogPattern(
        label = "Add Core Vaccine",
        icon = R.drawable.vaccine_dialog,
        onDismiss = onDismiss,
        onSaveInfo = {
            onAddVaccine(
                HealthObject(
                    vaccine,
                    HealthCategory.VACCINE,
                    petId,
                    null,
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    time,
                    true
                )

            )
            onAddVaccine(
                HealthObject(
                    vaccine,
                    HealthCategory.VACCINE,
                    petId,
                    null,
                    dateUpc.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    timeUpc,
                    false,
                    isNotif,
                    if (isNotif) notifDate else null,
                    if (isNotif) notifTime else null
                )
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
            if (animal == "Cat") {
                catVaccines.forEach {
                    DialogChip(
                        vaccine == it,
                        it
                    ) { vaccine = it }
                }
            } else {
                dogVaccines.forEach {
                    DialogChip(
                        vaccine == it,
                        it
                    ) { vaccine = it }
                }
            }
        }
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