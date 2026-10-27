package com.vhsnik.pethelper.ui.compose.utils

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.theme.*
import com.kizitonwose.calendar.compose.*
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Calendar(
    currentDay: LocalDate,
    onValueChanged: (LocalDate) -> Unit,
    startMonth: YearMonth,
    endMonth: YearMonth,
    isSelectable: (LocalDate) -> Boolean
) {
    val startMonth = remember { startMonth }
    val endMonth = remember { endMonth }
    val daysOfWeek = remember { daysOfWeek() }
    var selectedDay by remember(currentDay) { mutableStateOf(currentDay) }
    var isMenuOpened by remember { mutableStateOf(false) }
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            val state = rememberCalendarState(
                startMonth = startMonth,
                endMonth = endMonth,
                firstVisibleMonth = remember(currentDay) { YearMonth.from(currentDay) },
                firstDayOfWeek = daysOfWeek.first(),
            )
            val coroutineScope = rememberCoroutineScope()
            val visibleMonth = state.firstVisibleMonth.yearMonth
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton({
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                    }
                }) {
                    Image(
                        painter = painterResource(R.drawable.calendar_arrow),
                        contentDescription = "Change to previous month"
                    )
                }
                Box {
                    TextMaker(
                        "${
                            visibleMonth.month.getDisplayName(
                                TextStyle.FULL,
                                Locale.ENGLISH
                            )
                        } ${visibleMonth.year}",
                        12.sp, textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(
                            interactionSource = null,
                            indication = null
                        ) { isMenuOpened = true })
                    DropdownMenu(
                        expanded = isMenuOpened,
                        onDismissRequest = { isMenuOpened = false },
                        modifier = Modifier
                            .heightIn(max = 250.dp)
                            .background(Color.White)
                    ) {
                        val startYear = startMonth.year
                        val endYear = endMonth.year
                        val years = (endYear downTo startYear).toList()
                        years.forEach { year ->
                            DropdownMenuItem(
                                { TextMaker(year.toString(), 12.sp) },
                                {
                                    isMenuOpened = false
                                    coroutineScope.launch {
                                        state.scrollToMonth(
                                            YearMonth.of(
                                                year,
                                                visibleMonth.monthValue
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                IconButton({
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                    }
                }) {
                    Image(
                        painter = painterResource(R.drawable.calendar_arrow),
                        contentDescription = "Change to previous month",
                        modifier = Modifier.rotate(180f)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (dayOfWeek in daysOfWeek) {
                    TextMaker(
                        dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase(),
                        10.sp, fontWeight = FontWeight.Normal
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            HorizontalCalendar(
                modifier = Modifier.wrapContentHeight(),
                state = state,
                dayContent = { day ->
                    Day(
                        day,
                        isSelected = selectedDay == day.date && day.position == DayPosition.MonthDate,
                        isSelectable = isSelectable
                    ) {
                        selectedDay = day.date
                        onValueChanged(day.date)
                    }
                }
            )
        }
    }
}


@Composable
private fun Day(day: CalendarDay, isSelected: Boolean, isSelectable: (LocalDate) -> Boolean, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .testTag("MonthDay")
            .padding(6.dp)
            .clip(CircleShape)
            .background(color = if (isSelected) buttonColor else Color.Transparent)
            .clickable(
                enabled = day.position == DayPosition.MonthDate && isSelectable(day.date),
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when {
            !isSelectable(day.date) -> smallTextColor
            day.position == DayPosition.MonthDate -> if (isSelected) Color.White else Color.Black
            else -> smallTextColor
        }
        TextMaker(day.date.dayOfMonth.toString(), 12.sp, textColor)
    }
}


