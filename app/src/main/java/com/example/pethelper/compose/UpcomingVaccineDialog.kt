package com.example.pethelper.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import java.time.format.DateTimeFormatter
import java.util.Locale

val catVaccines = listOf("Rabies Vaccine", "FVRCP", "FeLV")

@Composable
fun AddUpcomingVaccine(
    onDismiss: () -> Unit, onSaveInfo: (
        vaccine: String, date: String, time: String,
        isNotification: Boolean, notifDate: String, notifTime: String, petId: Int
    ) -> Unit,
    petId: Int
) {
    var vaccine by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var dateString by remember { mutableStateOf(LocalDate.now().format(dateParser)) }
    var time by remember { mutableStateOf("") }
    var isNotif by remember { mutableStateOf(false) }
    var notifDate by remember { mutableStateOf("1 day before") }
    var notifTime by remember { mutableStateOf("12:00") }
    val isEnabled = vaccine.isNotEmpty() && dateString.isNotEmpty() && time.isNotEmpty()
            && (!isNotif || notifDate.isNotEmpty() && notifTime.isNotEmpty())
    var isCalendarOpened by remember { mutableStateOf(false) }
    var isTimeOpened by remember { mutableStateOf(false) }
    var isNotifDateOpened by remember { mutableStateOf(false) }
    var isNotifTimeOpened by remember { mutableStateOf(false) }
    DialogPattern(
        label = "Add Upcoming Vaccine", icon = R.drawable.vaccine_dialog,
        onDismiss = onDismiss, onSaveInfo = {
            onSaveInfo(
                vaccine, date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time, isNotif, notifDate, notifTime,
                petId
            )
        }, isEnabled = isEnabled
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
            catVaccines.forEach {
                DialogChip(vaccine == it, it) {
                    vaccine = it
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth()) {
            LabelAndDateTime(
                "Date",
                date.format(dateParser)
            ) { isCalendarOpened = true }
            if (isCalendarOpened) {
                PopupCalendar(
                    { isCalendarOpened = false },
                    {
                        date = it
                        dateString = it.format(dateParser)
                        isCalendarOpened = false
                    },
                    date, { it >= LocalDate.now() },
                    YearMonth.now(),
                    YearMonth.now().plusMonths(36)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        LabelAndDateTime(
            "Time",
            time
        ) { isTimeOpened = true }
        DropdownMenuPattern(
            isTimeOpened, { isTimeOpened = false },
            times
        ) {
            time = it
            isTimeOpened = false
        }
        Spacer(Modifier.height(16.dp))
        ReminderBlock(
            Modifier.align(Alignment.Start),
            isNotif, { isNotif = it },
            true, notifDate, { isNotifDateOpened = true },
            notifTime, { isNotifTimeOpened = true }
        )
        DropdownMenuPattern(
            isNotifDateOpened, { isNotifDateOpened = false },
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