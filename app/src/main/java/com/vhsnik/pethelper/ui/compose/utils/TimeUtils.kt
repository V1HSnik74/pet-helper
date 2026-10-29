package com.vhsnik.pethelper.ui.compose.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val dateParser: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
val fullDateParser: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)

val timeParser: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
val daySelector =
    listOf("1 day before", "same day", "2 days before", "3 days before", "1 week before")
val times = (0..23).map { LocalTime.of(it, 0).format(timeParser) }

val medicineTimeSelector =
    listOf("5 minutes before", "1 minute before", "10 minutes before")

fun getAvailableHours(
    isTimeDue: Boolean,
    selectedDate: LocalDate,
    maxHourLimit: Int = 23
): List<Int> {
    val currDate = LocalDate.now()
    if (selectedDate < currDate) return emptyList()
    val currTime = LocalTime.now()
    val isToday = selectedDate == LocalDate.now()
    val minHour = if (isTimeDue && isToday) {
        if (currTime.minute == 59) currTime.hour + 1 else currTime.hour
    } else 0
    val maxHour = minOf(
        maxHourLimit, if (isToday && !isTimeDue) {
            if (currTime.minute == 0) currTime.hour - 1 else currTime.hour
        } else 23
    )
    return if (minHour <= maxHour) (minHour..maxHour).toList() else emptyList()
}

fun getAvailableMinutes(
    isTimeDue: Boolean,
    selectedDate: LocalDate,
    selectedHour: Int,
    maxHourLimit: Int = 23,
    maxMinuteLimit: Int = 59
): List<Int> {
    val currTime = LocalTime.now()
    val isToday = selectedDate == LocalDate.now()
    val minMinute = if (isToday && isTimeDue && selectedHour == currTime.hour) {
        minOf(59, currTime.minute + 1)
    } else 0
    val maxMinute = if (isToday && !isTimeDue && selectedHour == currTime.hour) {
        maxOf(0, currTime.minute - 1)
    } else 59
    val maxMinuteTarget = if (selectedHour == maxHourLimit)
        maxMinuteLimit else 59
    val finalMaxMinute = minOf(maxMinute, maxMinuteTarget)
    return if (minMinute <= finalMaxMinute) (minMinute..finalMaxMinute).toList() else emptyList()
}

fun getAvailableOptionsNotifications(
    selectedDate: LocalDate,
    selectedTime: String = "23:59"
): List<String> {
    val selectedTimeLT = runCatching { LocalTime.parse(selectedTime, timeParser) }.getOrNull()
    if (selectedTimeLT == null) return emptyList()
    return daySelector.filter {
        val allOptions = when (it) {
            "1 day before" -> 1L
            "2 days before" -> 2L
            "3 days before" -> 3L
            "1 week before" -> 7L
            else -> 0L
        }
        val notifDate = selectedDate.minusDays(allOptions)
        val maxHour = if (it == "same day") selectedTimeLT.hour else 23
        getAvailableHours(true, notifDate, maxHour).isNotEmpty()
    }
}