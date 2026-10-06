package com.example.pethelper.compose.reminder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.DropdownMenuPattern
import com.example.pethelper.compose.patterns.LabelAndDateTime
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.PopupCalendar
import com.example.pethelper.compose.patterns.ReminderBlock
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.compose.patterns.daySelector
import com.example.pethelper.compose.patterns.getAvailableTimes
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.compose.patterns.times
import com.example.pethelper.ui.theme.PastelleColors
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAddReminder: (
        name: String,
        note: String?,
        date: String,
        time: String?,
        icon: Int,
        isNotif: Boolean,
        notifDate: String,
        notifTime: String,
        petId: Int
    ) -> Unit,
    petId: Int
) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf("") }
    var icon by remember { mutableIntStateOf(R.drawable.reminder) }
    var isNotif by remember { mutableStateOf(false) }
    var notifDate by remember { mutableStateOf("1 day before") }
    var notifTime by remember { mutableStateOf(LocalTime.now().format(timeParser)) }
    var isDateOpened by remember { mutableStateOf(false) }
    var isTimeOpened by remember { mutableStateOf(false) }
    var isNotifDateOpened by remember { mutableStateOf(false) }
    var isNotifTimeOpened by remember { mutableStateOf(false) }
    DialogPattern(
        "Add Upcoming Event",
        R.drawable.reminder_dialog_icon,
        onDismiss,
        {
            onAddReminder(
                name,
                note,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time,
                icon,
                isNotif,
                if (isNotif) notifDate else "",
                if (isNotif) notifTime else "",
                petId
            )
        },
        name.isNotBlank()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TextMaker(
                "Note: Use Health tab to add health reminders",
                12.sp,
                fontWeight = FontWeight.Medium
            )
            LabelAndTextField(
                "Reminder name",
                name,
                { name = it },
                "e.g. Buy food for Ben"
            )
            LabelAndTextField(
                "Note (optional)",
                note,
                { note = it },
                "e.g. Dry food and tuna pate"
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(Modifier.weight(1f)) {
                    LabelAndDateTime(
                        "Date",
                        date.format(dateParser)
                    ) { isDateOpened = true }
                    if (isDateOpened) {
                        PopupCalendar(
                            { isDateOpened = false },
                            {
                                date = it
                                isDateOpened = false
                            },
                            date,
                            { it >= LocalDate.now() },
                            YearMonth.now(),
                            YearMonth.now().plusYears(1)
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Box(Modifier.weight(1f)) {
                    LabelAndDateTime(
                        "Time (optional)",
                        time
                    ) { isTimeOpened = true }
                    DropdownMenuPattern(
                        isTimeOpened,
                        { isTimeOpened = false },
                        getAvailableTimes(true, date)
                    ) {
                        time = it
                        isTimeOpened = false
                    }
                }
            }
            Column(Modifier.fillMaxWidth()) {
                TextMaker("Choose an icon", 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icons.entries.forEach {
                        Image(
                            painterResource(it.icon),
                            "Reminder icon",
                            Modifier
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (icon == it.icon) it.color else Color.Transparent
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    indication = null, interactionSource = null
                                )
                                { icon = it.icon }
                        )
                    }
                }
            }
            ReminderBlock(
                Modifier,
                isNotif,
                { isNotif = it },
                true,
                notifDate,
                {
                    notifDate = it
                    isNotifDateOpened = false
                },
                daySelector,
                notifTime,
                {
                    notifTime = it
                    isNotifTimeOpened = false
                },
                times
            )
        }
    }
}

enum class Icons(val icon: Int, val color: Color) {
    OTHER(R.drawable.reminder, PastelleColors.PURPLE.iconColor),
    NUTRITION(R.drawable.nutrition_reminder, PastelleColors.GREEN.iconColor),
    SHOPPING(R.drawable.shopping_reminder, PastelleColors.YELLOW.iconColor),
    GROOMING(R.drawable.grooming_reminder, PastelleColors.BLUE.iconColor)
}