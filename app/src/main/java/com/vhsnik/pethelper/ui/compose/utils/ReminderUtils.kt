package com.vhsnik.pethelper.ui.compose.utils

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.ui.theme.*
import com.google.accompanist.permissions.*
import java.time.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReminderBlock(
    modifier: Modifier,
    isNotif: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isSecondBlock: Boolean,
    value: String,
    onClick: (String) -> Unit,
    content: List<String>,
    secondValue: String = "",
    onTimeChanged: (String) -> Unit = {},
    selectedDate: LocalDate,
    selectedTime: String = "23:59"
) {
    val switchColors = SwitchDefaults.colors(
        checkedTrackColor = buttonColor,
        checkedThumbColor = cardColor,
        uncheckedThumbColor = cardColor,
        uncheckedTrackColor = brownColor,
        disabledUncheckedTrackColor = brownColor,
        uncheckedBorderColor = brownColor,
        disabledUncheckedThumbColor = cardColor
    )
    Column(Modifier.fillMaxWidth()) {
        TextMaker("Reminder & Notifications", 12.sp, modifier = modifier)
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextMaker("Enable reminder", 12.sp, fontWeight = FontWeight.Normal)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionState = rememberPermissionState(
                    permission = Manifest.permission.POST_NOTIFICATIONS
                )
                Switch(
                    checked = isNotif, onCheckedChange = {
                        if (it && !permissionState.status.isGranted) permissionState.launchPermissionRequest()
                        else onCheckedChange(it)
                    },
                    colors = switchColors,
                    modifier = Modifier
                        .scale(0.75f)
                        .requiredSize(36.dp, 22.dp),
                    enabled = selectedTime.isNotEmpty()
                )
            } else {
                Switch(
                    checked = isNotif, onCheckedChange = { onCheckedChange(it) },
                    colors = switchColors,
                    modifier = Modifier
                        .scale(0.75f)
                        .requiredSize(36.dp, 22.dp),
                    enabled = selectedTime.isNotEmpty()
                )
            }

        }
        if (isNotif) {
            Spacer(Modifier.height(16.dp))
            RemindMeRow(
                value,
                content,
                onClick,
                isSecondBlock,
                secondValue,
                onTimeChanged,
                selectedDate,
                selectedTime
            )
        }
    }
}

@Composable
private fun RemindMeRow(
    value: String,
    content: List<String>,
    onClick: (String) -> Unit,
    isSecondBlock: Boolean,
    secondValue: String = "",
    onTimeChanged: (String) -> Unit = {},
    selectedDate: LocalDate,
    selectedTime: String = "23:59"
) {
    val selectedTimeLT = runCatching { LocalTime.parse(selectedTime, timeParser) }.getOrNull()
    if (selectedTimeLT == null) return
    var isTimePickerOpened by remember { mutableStateOf(false) }
    val allOptions = when (value) {
        "1 day before" -> 1L
        "2 days before" -> 2L
        "3 days before" -> 3L
        "1 week before" -> 7L
        else -> 0L
    }
    val actualNotifDate = selectedDate.minusDays(allOptions)
    val maxHourForNotif = if (value == "same day") selectedTimeLT.hour else 23
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextMaker("Remind me", 12.sp, fontWeight = FontWeight.Normal)
        DropdownField(Modifier.weight(1f), value, content) { onClick(it) }

        if (isSecondBlock) {
            TextMaker("at", 12.sp, fontWeight = FontWeight.Normal)
            Box(Modifier.weight(1f)) {
                CardWithDropdown(Modifier.fillMaxWidth(), secondValue) {
                    isTimePickerOpened = true
                }
            }
            if (isTimePickerOpened) {
                PopupTimePicker(
                    { isTimePickerOpened = false },
                    onTimeChanged,
                    actualNotifDate,
                    true,
                    maxHourForNotif
                )
            }
        }
    }
}