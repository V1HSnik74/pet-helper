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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.CalendarMedicine
import com.example.pethelper.compose.patterns.CardWithDropdown
import com.example.pethelper.compose.patterns.DialogChip
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.DropdownField
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.ReminderBlock
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.medicineTimeSelector
import com.example.pethelper.compose.patterns.times
import java.time.LocalDate

val medicineTimes =
    (1..5).map { it.toString() }

val medicinePeriods =
    listOf("day", "week", "month")

val dayIntervals =
    (1..12).map { it.toString() }

val daysOfWeek =
    listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@Composable
fun AddMedicineDialog(
    onDismiss: () -> Unit, onSaveMedicine: (
        medicine: String,
        note: String,
        time: String,
        intervalTimes: String,
        intervalPeriod: String,
        isNotif: Boolean,
        timeNotif: String,
        intervalValue: String,
        petId: Int
    ) -> Unit,
    petId: Int
) {
    var medicine by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("12:00") }
    var intervalTimes by remember { mutableStateOf("1") }
    var intervalPeriod by remember { mutableStateOf("day") }
    var intervalDays by remember { mutableStateOf("2") }
    val intervalWeeks = remember { mutableStateListOf<String>() }
    val intervalMonths = remember { mutableStateListOf<LocalDate>() }
    var isNotif by remember { mutableStateOf(false) }
    var timeNotif by remember { mutableStateOf("5 minutes before") }
    var isCalendarOpened by remember { mutableStateOf(false) }
    DialogPattern(
        "Add Medicine", R.drawable.supplement_icon,
        onDismiss,
        {
            onSaveMedicine(
                medicine,
                note,
                time,
                intervalTimes,
                intervalPeriod,
                isNotif,
                if (isNotif) timeNotif else "",
                when (intervalPeriod) {
                    "day" -> intervalDays
                    "week" -> intervalWeeks.joinToString(",")
                    "month" -> intervalMonths.map { it.dayOfMonth }.joinToString(",")
                    else -> ""
                },
                petId
            )
        },
        when (intervalPeriod) {
            "week" -> medicine.isNotEmpty() &&
                    intervalWeeks.size == intervalTimes.toInt()

            "month" -> intervalMonths.size == intervalTimes.toInt() &&
                    medicine.isNotEmpty()

            else -> medicine.isNotEmpty()
        }
    ) {
        LabelAndTextField(
            "Medicine", medicine, { medicine = it }, "e.g. Probiotic"
        )
        Spacer(Modifier.height(16.dp))
        LabelAndTextField(
            "Note (optional)", note, { note = it }, "e.g. FortiFlora"
        )
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth()) {
            TextMaker("Start Time (time you give the first portion by day)", 12.sp)
            Spacer(Modifier.height(8.dp))
            DropdownField(Modifier, time, times) {
                time = it
            }

        }
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            TextMaker("Frequency", 12.sp)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropdownField(Modifier.weight(1f), intervalTimes, medicineTimes) {
                    intervalTimes = it
                }
                Spacer(Modifier.width(8.dp))
                TextMaker("time(s)", 12.sp, fontWeight = FontWeight.Normal)
                Spacer(Modifier.width(8.dp))
                TextMaker("a", 12.sp, fontWeight = FontWeight.Normal)
                Spacer(Modifier.width(8.dp))
                DropdownField(Modifier.weight(1f), intervalPeriod, medicinePeriods) {
                    intervalPeriod = it
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        TextMaker("Interval", 12.sp)
        Spacer(Modifier.height(8.dp))
        when (intervalPeriod) {
            "day" -> {
                Row(
                    Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextMaker("every", 12.sp, fontWeight = FontWeight.Normal)
                    DropdownField(Modifier, intervalDays, dayIntervals) {
                        intervalDays = it
                    }
                    TextMaker("hour", 12.sp, fontWeight = FontWeight.Normal)
                }
            }

            "week" -> {
                Row(
                    Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FlowRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        daysOfWeek.forEach { day ->
                            val isSelected = intervalWeeks.any {
                                day == it
                            }
                            DialogChip(
                                isSelected,
                                day
                            ) {
                                if (isSelected)
                                    intervalWeeks.remove(day)
                                else
                                    intervalWeeks.add(day)
                            }
                        }
                    }
                }
            }

            "month" -> {
                Row(
                    Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextMaker("the", 12.sp, fontWeight = FontWeight.Normal)
                    Box {
                        CardWithDropdown(
                            intervalMonths.map { it.dayOfMonth }.sorted().joinToString(", ")
                                .ifEmpty { "Select days" }) {
                            isCalendarOpened = true
                        }
                        if (isCalendarOpened) {
                            Popup(
                                onDismissRequest = { isCalendarOpened = false },
                                alignment = Alignment.TopStart,
                                offset = IntOffset(0, 100)
                            ) {
                                Box(Modifier.fillMaxWidth(0.7f)) {
                                    CalendarMedicine(intervalMonths) {
                                        intervalMonths.clear()
                                        intervalMonths.addAll(it)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        ReminderBlock(
            Modifier.wrapContentWidth(),
            isNotif,
            { isNotif = it },
            false,
            timeNotif,
            {
                timeNotif = it
            },
            medicineTimeSelector
        )
    }
}