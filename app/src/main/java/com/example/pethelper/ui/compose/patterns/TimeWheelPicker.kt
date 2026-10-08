package com.example.pethelper.ui.compose.patterns

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.pethelper.R
import com.example.pethelper.ui.theme.*
import com.sd.lib.compose.wheel_picker.*
import java.time.LocalDate
import java.util.Locale

@Composable
private fun WheelPickerCardTime(
    items: List<Int>,
    onValueChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 1.dp,
            color = lightBrown
        ),
        colors = CardDefaults.cardColors(backgroundColor),
        modifier = Modifier.width(56.dp)
    )
    {
        Box(
            Modifier
                .fillMaxWidth()
                .height(96.dp),
            contentAlignment = Alignment.Center
        ) {

            val state = rememberFWheelPickerState(0)
            LaunchedEffect(state.currentIndex) {
                if (state.currentIndex in items.indices)
                    onValueChange(items[state.currentIndex])
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(selectedColor, RoundedCornerShape(8.dp))
            )
            FVerticalWheelPicker(
                count = items.size,
                unfocusedCount = 1,
                itemHeight = 32.dp,
                state = state
            ) { index ->
                val displayValue = items[index]
                val isSelected = state.currentIndexSnapshot == index
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TextMaker(
                        String.format(Locale.ENGLISH, "%02d", displayValue),
                        16.sp,
                        if (isSelected) buttonColor else Color.Black,
                        if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }

    }
}

@Composable
fun WheelPickerTime(
    onValueChange: (String) -> Unit,
    selectedDate: LocalDate,
    isTimeUpc: Boolean,
    maxHourLimit: Int = 23
) {
    val availableHours = remember(selectedDate, isTimeUpc) {
        getAvailableHours(isTimeUpc, selectedDate, maxHourLimit)
    }
    if (availableHours.isNotEmpty()) {
        var selectedHour by remember(availableHours) {
            mutableIntStateOf(availableHours.first())
        }
        val availableMinutes = remember(selectedDate, isTimeUpc, selectedHour) {
            getAvailableMinutes(isTimeUpc, selectedDate, selectedHour)
        }
        var selectedMinute by remember {
            mutableIntStateOf(availableMinutes.first())
        }
        LaunchedEffect(selectedHour, selectedMinute, availableHours, availableMinutes) {
            val timeStr = String.format(Locale.ENGLISH, "%02d:%02d", selectedHour, selectedMinute)
            onValueChange(timeStr)
        }
        Row(
            Modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            WheelPickerCardTime(
                availableHours
            ) { selectedHour = it }
            Spacer(Modifier.width(8.dp))
            Icon(
                painterResource(R.drawable.dot), null,
                modifier = Modifier.size(4.dp),
                tint = buttonColor
            )
            Spacer(Modifier.width(8.dp))
            WheelPickerCardTime(
                availableMinutes
            ) { selectedMinute = it }
        }
    } else {
        TextMaker(
            "No available time this date! Choose another date",
            12.sp,
            fontWeight = FontWeight.Medium
        )
    }

}