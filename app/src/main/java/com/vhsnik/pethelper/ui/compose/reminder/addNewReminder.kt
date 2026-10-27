package com.vhsnik.pethelper.ui.compose.reminder

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.PastelleColors
import java.time.*
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
    petId: Int,
    isUpdate: Boolean,
    name: String,
    note: String,
    date: LocalDate,
    time: String,
    icon: Int,
    isNotif: Boolean,
    notifDate: String,
    notifTime: String
) {
    var name by remember { mutableStateOf(name) }
    var note by remember { mutableStateOf(note) }
    var date by remember { mutableStateOf(date) }
    var time by remember(date) { mutableStateOf(time) }
    var icon by remember { mutableIntStateOf(icon) }
    var isNotif by remember { mutableStateOf(isNotif) }
    var notifDate by remember { mutableStateOf(notifDate) }
    var notifTime by remember { mutableStateOf(notifTime) }
    var currDialog by remember { mutableStateOf<Dialogs?>(null) }
    DialogPattern(
        if (isUpdate) "Update Event" else "Add Event",
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
            if (!isUpdate)
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
                    ) { currDialog = Dialogs.DATE }
                    if (currDialog == Dialogs.DATE) {
                        PopupCalendar(
                            { currDialog = null },
                            {
                                date = it
                                currDialog = null
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
                    ) { currDialog = Dialogs.TIME }
                    if (currDialog == Dialogs.TIME) {
                        PopupTimePicker(
                            { currDialog = null },
                            { time = it },
                            date,
                            true
                        )
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
                },
                getAvailableOptionsNotifications(date, if (time == "") "23:59" else time),
                notifTime,
                {
                    notifTime = it
                },
                date
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