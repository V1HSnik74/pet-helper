package com.example.pethelper.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun BirthdayCalendar(
    adjacentMonths: Long = 500, currentDay: LocalDate,
    onValueChanged: (LocalDate) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(adjacentMonths) }
    val endMonth = remember { currentMonth }
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
                        val minYear = startMonth.year
                        val maxYear = visibleMonth.year
                        val years = (maxYear downTo minYear).toList()
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
                        isSelected = selectedDay == day.date && day.position == DayPosition.MonthDate
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
private fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
    val isFuture = day.date > LocalDate.now()
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .testTag("MonthDay")
            .padding(6.dp)
            .clip(CircleShape)
            .background(color = if (isSelected) buttonColor else Color.Transparent)
            .clickable(
                enabled = day.position == DayPosition.MonthDate && !isFuture,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when {
            isFuture -> smallTextColor
            day.position == DayPosition.MonthDate -> if (isSelected) Color.White else Color.Black
            else -> smallTextColor
        }
        TextMaker(day.date.dayOfMonth.toString(), 12.sp, textColor)
    }
}


