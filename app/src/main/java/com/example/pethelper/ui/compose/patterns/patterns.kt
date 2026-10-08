package com.example.pethelper.ui.compose.patterns

import android.Manifest
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.example.pethelper.R
import com.example.pethelper.ui.theme.*
import com.google.accompanist.permissions.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

val timeParser: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
val daySelector =
    listOf("1 day before", "same day", "2 days before", "3 days before", "1 week before")
val times = (0..23).map { LocalTime.of(it, 0).format(timeParser) }

val medicineTimeSelector =
    listOf("5 minutes before", "1 minute before", "10 minutes before")

val preventions =
    listOf("Deworming", "Flea Prevention", "Tick Prevention")

val dateParser: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
val fullDateParser: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)

enum class Dialogs {
    DATE, TIME, DATE_UPC, TIME_UPC
}

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

fun getAvailableMinutes(isTimeDue: Boolean, selectedDate: LocalDate, selectedHour: Int): List<Int> {
    val currTime = LocalTime.now()
    val isToday = selectedDate == LocalDate.now()
    val minMinute = if (isToday && isTimeDue && selectedHour == currTime.hour) {
        minOf(59, currTime.minute + 1)
    } else 0
    val maxMinute = if (isToday && !isTimeDue && selectedHour == currTime.hour) {
        maxOf(0, currTime.minute - 1)
    } else 59
    return if (minMinute <= maxMinute) (minMinute..maxMinute).toList() else emptyList()
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

@Composable
fun TextMaker(
    text: String, fontSize: TextUnit,
    color: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Bold, modifier: Modifier = Modifier,
    textDecoration: TextDecoration = TextDecoration.None,
    lineHeight: TextUnit = fontSize * 1.2f
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = InterFamily,
        color = color,
        fontWeight = fontWeight,
        modifier = modifier,
        textDecoration = textDecoration,
        lineHeight = lineHeight
    )
}

@Composable
fun ButtonMaker(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(25.dp)
    )
    {
        TextMaker(text, 14.sp, Color.White)
    }
}

@Composable
fun BasicTextFieldMaker(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier,
    singleLine: Boolean = true,
    paddingHor: Dp = 16.dp,
    paddingVert: Dp = 8.dp,
    contentAlignment: Alignment = Alignment.CenterStart
) {
    BasicTextField(
        value = value, onValueChange = { onValueChange(it) },
        singleLine = singleLine,
        modifier = modifier,
        textStyle = TextStyle(fontSize = 12.sp, fontFamily = InterFamily),
        cursorBrush = SolidColor(lightBrown),
        decorationBox = {
            Box(
                modifier = Modifier
                    .background(
                        textFieldContainerColor,
                        RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, lightBrown, RoundedCornerShape(10.dp))
                    .padding(paddingHor, paddingVert),
                contentAlignment = contentAlignment
            ) {
                if (value.isEmpty()) {
                    TextMaker(
                        placeholder, 12.sp, smallTextColor,
                        FontWeight.Normal
                    )
                }
                it.invoke()
            }
        }
    )
}

@Composable
fun DialogChip(isSelected: Boolean, text: String, onClick: () -> Unit) {
    Card(
        Modifier
            .wrapContentSize()
            .clickable(interactionSource = null, indication = null) { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(if (isSelected) brownColor else cardColor),
        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else lightBrown)
    ) {
        TextMaker(
            text,
            10.sp,
            if (isSelected) Color.White else Color.Black,
            FontWeight.SemiBold,
            Modifier.padding(8.dp)
        )
    }
}

@Composable
fun DialogPattern(
    label: String,
    icon: Int,
    onDismiss: () -> Unit,
    onSaveInfo: () -> Unit,
    isEnabled: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(backgroundColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        contentDescription = "$label icon",
                        painter = painterResource(icon),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    TextMaker(label, 16.sp)
                    Spacer(Modifier.height(16.dp))
                    content()
                    Spacer(Modifier.height(16.dp))
                    ButtonMaker(
                        label,
                        onSaveInfo,
                        enabled = isEnabled
                    )
                }
                Icon(
                    painterResource(R.drawable.cancel),
                    "Dismiss",
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, top = 16.dp)
                        .clickable(indication = null, interactionSource = null) { onDismiss() }
                )
            }
        }
    }
}

@Composable
fun LabelAndTextField(
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String, singleLine: Boolean = true,
    contentAlignment: Alignment = Alignment.CenterStart, height: Dp = 40.dp,
    paddingHor: Dp = 16.dp,
    paddingVert: Dp = 8.dp
) {
    Column(horizontalAlignment = Alignment.Start) {
        TextMaker(label, 12.sp)
        Spacer(Modifier.height(8.dp))
        BasicTextFieldMaker(
            value, { onValueChange(it) }, placeholder, Modifier
                .height(height)
                .fillMaxWidth(),
            singleLine,
            contentAlignment = contentAlignment,
            paddingHor = paddingHor,
            paddingVert = paddingVert
        )
    }
}

@Composable
fun LabelAndDateTime(label: String, value: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        TextMaker(label, 12.sp)
        Spacer(Modifier.height(8.dp))
        CardWithDropdown(Modifier.fillMaxWidth(), value, onClick)
    }
}

@Composable
fun DropdownMenuPattern(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    content: List<String>,
    onClick: (String) -> Unit
) {
    DropdownMenu(
        isExpanded, onDismiss,
        Modifier
            .background(cardColor)
            .height(100.dp)
    ) {
        content.forEach {
            DropdownMenuItem(
                { TextMaker(it, 12.sp) },
                onClick = { onClick(it) }
            )
        }
    }
}

@Composable
fun PopupCalendar(
    onDismiss: () -> Unit,
    onValueChange: (LocalDate) -> Unit,
    date: LocalDate,
    isSelectable: (LocalDate) -> Boolean,
    startMonth: YearMonth,
    endMonth: YearMonth
) {
    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.TopStart,
        offset = IntOffset(0, 100)
    ) {
        Box(Modifier.fillMaxWidth(0.7f)) {
            Calendar(
                date,
                { onValueChange(it) },
                startMonth,
                endMonth,
                isSelectable
            )
        }
    }
}

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

@Composable
fun DropdownField(
    modifier: Modifier,
    value: String,
    content: List<String>,
    onClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier.wrapContentSize(Alignment.TopStart)) {
        CardWithDropdown(Modifier.fillMaxWidth(), value) { isExpanded = true }
        DropdownMenuPattern(
            isExpanded, { isExpanded = false },
            content
        ) {
            onClick(it)
            isExpanded = false
        }
    }
}

@Composable
fun CardWithDropdown(modifier: Modifier = Modifier, value: String, onClick: () -> Unit) {
    Card(
        modifier
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(cardColor),
        border = BorderStroke(1.dp, lightBrown)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextMaker(
                value,
                12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(
                painterResource(R.drawable.back), "choose value",
                Modifier
                    .clickable(interactionSource = null, indication = null) { onClick() }
                    .size(16.dp)
                    .rotate(-90f)
            )

        }
    }
}

@Composable
fun PopupTimePicker(
    onDismiss: () -> Unit,
    onTimeChanged: (String) -> Unit,
    selectedDate: LocalDate,
    isTimeUpc: Boolean,
    maxHourLimit: Int = 23
) {
    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.TopStart,
        offset = IntOffset(0, 150)
    ) {
        Card(
            Modifier.wrapContentSize(),
            colors = CardDefaults.cardColors(cardColor),
            shape = RoundedCornerShape(15.dp)
        ) {
            Box(Modifier.padding(8.dp)) {
                WheelPickerTime(onTimeChanged, selectedDate, isTimeUpc, maxHourLimit)
            }
        }
    }
}

@Composable
fun ScreenTitle(text: String, onBackClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.back),
            contentDescription = "Back button",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable(indication = null, interactionSource = null) { onBackClick() }
        )
        TextMaker(
            text, 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun <T> PagerCard(
    label: String,
    onAddItem: (() -> Unit)? = null,
    content: List<T>,
    isNote: Boolean = false,
    itemContent: @Composable (item: T, hasLine: Boolean) -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
                if (onAddItem != null) {
                    Icon(
                        painterResource(R.drawable.prime_plus),
                        contentDescription = "Add item",
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = null
                        ) { onAddItem() }
                    )
                }
            }
            HorizontalDivider(
                Modifier.fillMaxWidth(),
                1.dp,
                dividerColor
            )
            if (content.isNotEmpty()) {
                val pages = remember(content) { content.chunked(4) }
                val pagerState = rememberPagerState(pageCount = { pages.size })
                HorizontalPager(
                    pagerState,
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) { currentPage ->
                    val pageItems = pages[currentPage]
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = if (isNote) 142.dp else 215.dp)
                    ) {
                        pageItems.forEachIndexed { index, item ->
                            val hasLine = !(pageItems.size == 4 && index == 3)
                            itemContent(item, hasLine)

                        }
                    }
                }
            }
        }

    }
}

@Composable
fun PagerCardItem(
    icon: Int,
    label: String,
    note: String,
    date: String,
    time: String,
    hasAddInfoLeft: Boolean,
    hasAddInfoRight: Boolean,
    hasLine: Boolean = true,
    hasNote: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    hasMarkAsDone: Boolean = false,
    onMarkAsDoneClick: () -> Unit = {},
    hasMenu: Boolean = true
) {
    var isMenuOpened by remember { mutableStateOf(false) }
    val parsedDate =
        remember(date) {
            runCatching {
                LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).format(dateParser)
            }.getOrDefault(date)
        }
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painterResource(icon), "Item Icon")
                Spacer(Modifier.width(10.dp))
                Column(Modifier.wrapContentSize()) {
                    TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
                    if (hasAddInfoLeft && hasNote) {
                        Spacer(Modifier.height(4.dp))
                        TextMaker(note, 10.sp, Color(0xFF727272), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Row(
                Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.wrapContentSize()) {
                    TextMaker(parsedDate, 10.sp, fontWeight = FontWeight.Medium)
                    if (hasAddInfoRight) {
                        Spacer(Modifier.height(4.dp))
                        TextMaker(time, 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.width(10.dp))
                if (hasMenu) {
                    Box {
                        Icon(
                            Icons.Default.MoreVert,
                            "More Options",
                            Modifier
                                .size(18.dp)
                                .clickable {
                                    isMenuOpened = true
                                },
                        )
                        DropdownMoreOptions(
                            isMenuOpened,
                            { isMenuOpened = false },
                            onEdit,
                            onDelete,
                            hasMarkAsDone,
                            onMarkAsDoneClick
                        )
                    }
                }
            }
        }
        if (hasLine) {
            HorizontalDivider(thickness = 1.dp, color = dividerColor)
        }
    }
}

@Composable
fun UpcomingBlock(
    modifier: Modifier,
    label: String,
    isVaccine: Boolean,
    info: String,
    date: String,
    time: String,
    listNotEmpty: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier
            .clickable(indication = null, interactionSource = null, onClick = onClick),
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .wrapContentHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painterResource(R.drawable.birthday_calendar),
                "calendar icon",
                Modifier.size(20.dp)
            )
            TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
            if (listNotEmpty) {
                val dateFormatted = remember(date) {
                    LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
                        .format(dateParser)
                }
                Column(Modifier.fillMaxWidth()) {
                    if (isVaccine) {
                        TextMaker("Next vaccination:", 10.sp, fontWeight = FontWeight.Medium)
                    }
                    TextMaker(info, 10.sp, buttonColor, FontWeight.Medium)
                }
                Column(Modifier.fillMaxWidth()) {
                    TextMaker(dateFormatted, 10.sp, fontWeight = FontWeight.SemiBold)
                    TextMaker(time, 10.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                TextMaker(
                    "No upcoming vaccines yet. Tap to add",
                    10.sp, fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ProgressBar(
    percentage: Float
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(100.dp)
    ) {
        CircularProgressIndicator(
            progress = { percentage.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = if (percentage == 1f) PastelleColors.GREEN.iconColor else PastelleColors.YELLOW.iconColor,
            trackColor = Color(0xFFD9D9D9),
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Round
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextMaker("${(percentage * 100).toInt()}%", 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            TextMaker(
                if (percentage == 1f) "Up to date" else "Needs action",
                10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatusProgBar(
    isVaccine: Boolean, percentage: Float,
    text: String,
    onClick: () -> Unit = {},
    isProtectedFleas: Boolean = false,
    isProtectedTicks: Boolean = false,
    isProtectedWorms: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier,
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .padding(10.dp)
                .wrapContentHeight()
        ) {
            TextMaker(
                if (isVaccine) "Vaccination Status" else "Protection Status",
                12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressBar(percentage)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.wrapContentHeight()) {
                    TextMaker(
                        if (percentage == 1f) "Great Job!" else "Needs Action",
                        12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    TextMaker(text, 10.sp, fontWeight = FontWeight.Normal)
                    if (isVaccine) {
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick,
                            shape = RoundedCornerShape(5.dp),
                            content = {
                                TextMaker(
                                    "Learn More",
                                    10.sp,
                                    Color.White,
                                    FontWeight.SemiBold
                                )
                            },
                            colors = ButtonDefaults.buttonColors(buttonColor),
                            contentPadding = PaddingValues(8.dp, 4.dp),
                            interactionSource = null,
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
            }
            if (!isVaccine) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProtectionStatusProtected(
                        Modifier.weight(1f),
                        isProtectedFleas,
                        R.drawable.fleas_icon,
                        "Fleas"
                    )
                    ProtectionStatusProtected(
                        Modifier.weight(1f),
                        isProtectedTicks,
                        R.drawable.tick,
                        "Ticks"
                    )
                    ProtectionStatusProtected(
                        Modifier.weight(1f),
                        isProtectedWorms,
                        R.drawable.worm,
                        "Worms"
                    )
                }
            }
        }
    }
}

@Composable
private fun ProtectionStatusProtected(
    modifier: Modifier,
    isProtected: Boolean,
    icon: Int,
    label: String
) {
    Card(
        colors = CardDefaults.cardColors(if (isProtected) PastelleColors.GREEN.color else PastelleColors.YELLOW.color),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            1.dp,
            if (isProtected) PastelleColors.GREEN.borderColor else PastelleColors.YELLOW.borderColor
        ),
        modifier = modifier
    ) {
        Column(
            Modifier
                .padding(12.dp, 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(icon), "$label Icon")
            Spacer(Modifier.height(2.dp))
            TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            TextMaker(
                if (isProtected) "Protected" else "Not Protected", 10.sp,
                if (isProtected) PastelleColors.GREEN.iconColor else PastelleColors.YELLOW.iconColor,
                FontWeight.SemiBold
            )

        }
    }
}

@Composable
fun HealthTip(
    color: Color,
    iconColor: Color,
    borderColor: Color,
    label: String,
    text: String,
    icon: Int
) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(color),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(painterResource(icon), "tips icon", tint = iconColor)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                TextMaker(text, 12.sp, fontWeight = FontWeight.Normal)
            }
        }
    }
}
