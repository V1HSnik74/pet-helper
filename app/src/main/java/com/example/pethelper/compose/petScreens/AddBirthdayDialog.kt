package com.example.pethelper.compose.petScreens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.Calendar
import com.example.pethelper.compose.patterns.DialogPattern
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun UpdateBirthdayDialog(
    onDismiss: () -> Unit,
    currentDate: String,
    onUpdateBirthday: (String) -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH) }
    var selectedDate by remember(currentDate)
    {
        mutableStateOf(
            if (currentDate.isNotEmpty()) LocalDate.parse(
                currentDate,
                dateFormatter
            ) else LocalDate.now()
        )
    }
    val year = selectedDate.year
    val month = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val day = selectedDate.dayOfMonth
    val stringDate = "$month $day, $year"
    val today = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    DialogPattern(
        "Edit Birthday",
        R.drawable.birthday_icon,
        onDismiss,
        { onUpdateBirthday(stringDate) },
        stringDate.isNotEmpty()
    ) {
        Calendar(
            selectedDate,
            { selectedDate = it },
            currentMonth.minusMonths(500),
            currentMonth,
            { it <= today })
    }
}
