package com.example.pethelper.compose.patterns

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarMedicine(
    selectedDays: List<LocalDate>,
    onValueChanged: (List<LocalDate>) -> Unit
) {
    val curMonth = remember { YearMonth.now() }
    val daysOfWeek = remember { daysOfWeek() }
    val isSelectable: (LocalDate) -> Boolean = { it.dayOfMonth <= 28 }
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
                startMonth = curMonth,
                endMonth = curMonth,
                firstVisibleMonth = curMonth,
                firstDayOfWeek = daysOfWeek.first(),
            )
            val visibleMonth = state.firstVisibleMonth.yearMonth
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box {
                    TextMaker(
                        "${
                            visibleMonth.month.getDisplayName(
                                TextStyle.FULL,
                                Locale.ENGLISH
                            )
                        } ${visibleMonth.year}",
                        12.sp, textDecoration = TextDecoration.Underline
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
                        isSelected = selectedDays.contains(day.date) && day.position == DayPosition.MonthDate,
                        isSelectable = isSelectable
                    ) {
                        val updatedSelectedDays = if (selectedDays.contains(day.date))
                            selectedDays - day.date
                        else selectedDays + day.date
                        onValueChanged(updatedSelectedDays)
                    }
                }
            )
        }
    }
}


@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    isSelectable: (LocalDate) -> Boolean,
    onClick: (CalendarDay) -> Unit
) {
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


