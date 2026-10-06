package com.example.pethelper.compose.patterns

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.ui.theme.buttonColor
import com.example.pethelper.ui.theme.cardColor
import com.example.pethelper.ui.theme.smallTextColor
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun RemindersCalendar(
    currentDate: LocalDate,
    onValueChanged: (LocalDate) -> Unit
) {
    val startDate = remember { currentDate.minusDays(500) }
    val endDate = remember { currentDate.plusDays(500) }
    var selection by remember { mutableStateOf(currentDate) }
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val state = rememberWeekCalendarState(
                startDate = startDate,
                endDate = endDate,
                firstVisibleWeekDate = currentDate,
            )
            val coroutineScope = rememberCoroutineScope()
            val visibleWeek = state.firstVisibleWeek
            val firstDayOfVisibleWeek = visibleWeek.days.first().date
            val calendarText = remember(firstDayOfVisibleWeek) {
                firstDayOfVisibleWeek.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally)
            ) {
                Icon(
                    painterResource(R.drawable.calendar_arrow),
                    "Previous week",
                    Modifier
                        .size(10.dp, 20.dp)
                        .clickable(indication = null, interactionSource = null) {
                            coroutineScope.launch {
                                state.animateScrollToWeek(
                                    firstDayOfVisibleWeek.minusWeeks(1)
                                )
                            }
                        }
                )
                TextMaker(
                    calendarText,
                    14.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painterResource(R.drawable.calendar_arrow),
                    "Next week",
                    Modifier
                        .size(10.dp, 20.dp)
                        .rotate(180f)
                        .clickable(indication = null, interactionSource = null) {
                            coroutineScope.launch {
                                state.animateScrollToWeek(
                                    firstDayOfVisibleWeek.plusWeeks(1)
                                )
                            }
                        }
                )
            }
            Spacer(Modifier.height(24.dp))
            WeekCalendar(
                state = state,
                dayContent = { day ->
                    Day(day.date, isSelected = selection == day.date) { clicked ->
                        if (selection != clicked) {
                            selection = clicked
                            onValueChanged(day.date)
                        }
                    }
                },
            )
        }
    }

}

@Composable
private fun Day(date: LocalDate, isSelected: Boolean, onClick: (LocalDate) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .clickable(indication = null, interactionSource = null)
            { onClick(date) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        TextMaker(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase(),
            fontSize = 10.sp,
            color = smallTextColor,
            fontWeight = FontWeight.SemiBold,
        )
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (isSelected) buttonColor else cardColor),
            contentAlignment = Alignment.Center
        ) {
            TextMaker(
                text = date.format(DateTimeFormatter.ofPattern("dd")),
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
