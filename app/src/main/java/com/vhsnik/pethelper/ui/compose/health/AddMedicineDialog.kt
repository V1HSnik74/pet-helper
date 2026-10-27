package com.vhsnik.pethelper.ui.compose.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.*
import com.vhsnik.pethelper.ui.compose.utils.*
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
    onDismiss: () -> Unit,
    onSaveMedicine: (HealthObject) -> Unit,
    petId: Int,
    medicineInit: String,
    note: String,
    time: String,
    intervalTimes: String,
    intervalPeriod: String,
    intervalValue: String,
    isNotif: Boolean,
    timeNotif: String
) {
    var medicine by remember { mutableStateOf(medicineInit) }
    var note by remember { mutableStateOf(note) }
    var time by remember { mutableStateOf(time) }
    var intervalTimes by remember { mutableStateOf(intervalTimes) }
    var intervalPeriod by remember { mutableStateOf(intervalPeriod) }
    var intervalDays by remember { mutableStateOf(if (intervalValue == "24") "1" else intervalValue.ifEmpty { "2" }) }
    val intervalWeeks = remember(intervalValue) {
        mutableStateListOf<String>().apply {
            if (intervalPeriod == "week" && intervalValue.isNotEmpty())
                addAll(intervalValue.split(","))
        }
    }
    val intervalMonths = remember(intervalValue) {
        mutableStateListOf<LocalDate>().apply {
            if (intervalPeriod == "month" && intervalValue.isNotEmpty()) {
                intervalValue.split(",").map {
                    val day = it.toInt()
                    LocalDate.now().withDayOfMonth(day)
                }.also { addAll(it) }
            }
        }
    }
    var isNotif by remember { mutableStateOf(isNotif) }
    var timeNotif by remember { mutableStateOf(timeNotif) }
    var isCalendarOpened by remember { mutableStateOf(false) }
    DialogPattern(
        if (medicineInit.isNotEmpty()) "Update Medicine" else "Add Medicine",
        R.drawable.supplement_icon,
        onDismiss,
        {
            onSaveMedicine(
                HealthObject(
                    medicine,
                    HealthCategory.MEDICINE,
                    petId,
                    note.ifBlank { null },
                    null,
                    time,
                    false,
                    isNotif,
                    null,
                    if (isNotif) timeNotif else null,
                    intervalTimes,
                    intervalPeriod,
                    when (intervalPeriod) {
                        "day" -> if (intervalTimes == "1") "24" else intervalDays
                        "week" -> intervalWeeks.joinToString(",")
                        "month" -> intervalMonths.map { it.dayOfMonth }.joinToString(",")
                        else -> ""
                    }
                )
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
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
                TextMaker(
                    "Start Time (time you give the first portion by day)",
                    12.sp
                )
                Spacer(Modifier.height(8.dp))
                DropdownField(
                    Modifier.fillMaxWidth(),
                    time,
                    times
                ) {
                    time = it
                }

            }
            Spacer(Modifier.height(16.dp))
            Column(
                Modifier.fillMaxWidth()
            ) {
                TextMaker(
                    "Frequency",
                    12.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DropdownField(
                        Modifier.width(65.dp),
                        intervalTimes,
                        medicineTimes
                    ) {
                        intervalTimes = it
                    }
                    Spacer(Modifier.width(8.dp))
                    TextMaker(
                        "time(s)",
                        12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.width(8.dp))
                    TextMaker(
                        "a",
                        12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.width(8.dp))
                    DropdownField(
                        Modifier.width(100.dp),
                        intervalPeriod,
                        medicinePeriods
                    ) {
                        intervalPeriod = it
                    }
                }
            }
            if (!(intervalPeriod == "day" && intervalTimes == "1")) {
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth()) {
                    TextMaker(
                        "Interval",
                        12.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (intervalPeriod) {
                            "day" -> {
                                TextMaker(
                                    "every",
                                    12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                DropdownField(
                                    Modifier.width(65.dp),
                                    intervalDays,
                                    dayIntervals
                                ) {
                                    intervalDays = it
                                }
                                TextMaker(
                                    "hour(s)",
                                    12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }

                            "week" -> {
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

                            "month" -> {
                                TextMaker(
                                    "the",
                                    12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Box {
                                    CardWithDropdown(
                                        Modifier.width(100.dp),
                                        intervalMonths.map { it.dayOfMonth }.sorted()
                                            .joinToString(", ")
                                    ) {
                                        isCalendarOpened = true
                                    }
                                    if (isCalendarOpened) {
                                        Popup(
                                            onDismissRequest = { isCalendarOpened = false },
                                            alignment = Alignment.TopStart,
                                            offset = IntOffset(0, 100)
                                        ) {
                                            Box(Modifier.fillMaxWidth(0.7f)) {
                                                CalendarMedicine(
                                                    intervalMonths
                                                ) {
                                                    intervalMonths.clear()
                                                    intervalMonths.addAll(it)
                                                }
                                            }
                                        }
                                    }
                                }
                                TextMaker(
                                    "of every month",
                                    12.sp,
                                    fontWeight = FontWeight.Normal
                                )
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
                medicineTimeSelector,
                selectedDate = LocalDate.now()
            )
        }
    }
}
