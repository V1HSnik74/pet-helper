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

val catVaccines = listOf("Rabies Vaccine", "FVRCP", "FeLV")
val dogVaccines = listOf("Rabies Vaccine", "DHPPi", "Bordetella")

@Composable
fun AddUpcomingVaccine(
    onDismiss: () -> Unit,
    onSaveInfo: (HealthObject) -> Unit,
    petId: Int,
    animal: String,
    isUpdate: Boolean,
    label: String,
    isDone: Boolean,
    vaccine: String,
    date: LocalDate,
    time: String,
    isNotif: Boolean,
    notifDate: String?,
    notifTime: String?
) {
    var vaccine by remember { mutableStateOf(vaccine) }
    var date by remember { mutableStateOf(date) }
    var time by remember(date) { mutableStateOf(time) }
    var isNotif by remember { mutableStateOf(isNotif) }
    var notifDate by remember { mutableStateOf(notifDate) }
    var notifTime by remember { mutableStateOf(notifTime) }
    var activeDialog by remember { mutableStateOf<Dialogs?>(null) }
    DialogPattern(
        if (isUpdate) "Update $label Vaccine" else "Add $label Vaccine",
        R.drawable.vaccine_dialog,
        onDismiss,
        {
            onSaveInfo(
                HealthObject(
                    vaccine,
                    HealthCategory.VACCINE,
                    petId,
                    null,
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    time,
                    isDone,
                    isNotif,
                    if (isNotif) notifDate else null,
                    if (isNotif) notifTime else null
                )
            )
        },
        vaccine.isNotBlank() && time.isNotEmpty()
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
                    ) {
                        vaccine = it
                    }
                }
            } else {
                dogVaccines.forEach {
                    DialogChip(
                        vaccine == it,
                        it
                    ) {
                        vaccine = it
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth()) {
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
                        if (label == "History")
                            it <= LocalDate.now()
                        else
                            it >= LocalDate.now()
                    },
                    if (label == "History") YearMonth.now().minusMonths(36) else YearMonth.now(),
                    if (label == "History") YearMonth.now() else YearMonth.now().plusMonths(36)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        if (label != "History"){
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
        }
        if (label != "History") {
            ReminderBlock(
                Modifier.fillMaxWidth(),
                isNotif,
                { isNotif = it },
                true,
                notifDate!!,
                {
                    notifDate = it
                },
                getAvailableOptionsNotifications(
                    date,
                    time
                ),
                notifTime!!,
                {
                    notifTime = it
                },
                date,
                time
            )
        }
    }
}