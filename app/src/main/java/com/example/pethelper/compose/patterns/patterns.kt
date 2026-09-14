package com.example.pethelper.compose.patterns


import android.Manifest
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import com.example.pethelper.R
import com.example.pethelper.compose.petScreens.textFieldContainerColor
import com.example.pethelper.compose.petScreens.textFieldCursorColor
import com.example.pethelper.ui.theme.InterFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.chunked
import kotlin.collections.forEachIndexed

val buttonColor = Color(0xFFE27380)
val backgroundColor = Color(0xFFFFF8F2)
val backgroundScreenColor = Color(0xFFFFF9F2)
val smallTextColor = Color(0xFFB0A8A3)
val selectedColor = Color(0xFFFDF1ED)
val cardColor = Color(0xFFFFFCF9)
val brownColor = Color(0xFFAF8268)

val times = (0..23).map { LocalTime.of(it, 0).format(DateTimeFormatter.ofPattern("HH:mm")) }
val daySelector =
    listOf("1 day before", "same day", "2 days before", "3 days before", "1 week before")

val medicineTimeSelector =
    listOf("1 hour before", "5 minutes before", "10 minutes before", "30 minutes before")

val preventions =
    listOf("Deworming", "Flea Prevention", "Tick Prevention", "Flea & Tick Prevention")

val dateParser: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
val timeParser: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)

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
        cursorBrush = SolidColor(textFieldCursorColor),
        decorationBox = {
            Box(
                modifier = Modifier
                    .background(
                        textFieldContainerColor,
                        RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, textFieldCursorColor, RoundedCornerShape(10.dp))
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
        colors = CardDefaults.cardColors(selectedColor),
        border = BorderStroke(1.dp, if (isSelected) buttonColor else Color.Transparent)
    ) {
        TextMaker(
            text,
            10.sp,
            Color(0xFF2B2B2B),
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
                        painter = painterResource(icon)
                    )
                    Spacer(Modifier.height(8.dp))
                    TextMaker(label, 16.sp)
                    Spacer(Modifier.height(16.dp))
                    content()
                    Spacer(Modifier.height(16.dp))
                    ButtonMaker(
                        "Save $label",
                        onSaveInfo,
                        enabled = isEnabled
                    )
                }
                IconButton(
                    onDismiss, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 16.dp)
                ) {
                    Image(
                        painterResource(R.drawable.cancel),
                        contentDescription = "cancel"
                    )
                }
            }
        }
    }
}

@Composable
fun LabelAndTextField(
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String, singleLine: Boolean = true,
    contentAlignment: Alignment = Alignment.CenterStart
) {
    Column(horizontalAlignment = Alignment.Start) {
        TextMaker(label, 12.sp)
        Spacer(Modifier.height(8.dp))
        BasicTextFieldMaker(
            value, { onValueChange(it) }, placeholder, Modifier
                .height(40.dp)
                .fillMaxWidth(),
            singleLine,
            contentAlignment = contentAlignment
        )
    }
}

@Composable
fun LabelAndDateTime(label: String, value: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.Start) {
        TextMaker(label, 12.sp)
        Spacer(Modifier.height(8.dp))
        Card(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(cardColor),
            border = BorderStroke(1.dp, textFieldCursorColor)
        ) {
            CardWithDropdown(value, onClick)
        }
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
            .heightIn(max = 100.dp)
            .background(cardColor)
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
    onClickSecond: (String) -> Unit = {},
    secondContent: List<String> = emptyList()
) {
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
                LaunchedEffect(permissionState.status.isGranted) {
                    if (permissionState.status.isGranted && !isNotif) {
                        onCheckedChange(true)
                    }
                }
                Switch(
                    checked = isNotif, onCheckedChange = {
                        if (it && !permissionState.status.isGranted) permissionState.launchPermissionRequest()
                        else onCheckedChange(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = buttonColor,
                        checkedThumbColor = cardColor,
                        uncheckedThumbColor = backgroundColor,
                        uncheckedTrackColor = Color(0xFFAF8268)
                    ),
                    modifier = Modifier
                        .scale(0.75f)
                        .requiredSize(36.dp, 22.dp)
                )
            } else {
                Switch(
                    checked = isNotif, onCheckedChange = { onCheckedChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = buttonColor,
                        checkedThumbColor = cardColor,
                        uncheckedThumbColor = backgroundColor,
                        uncheckedTrackColor = Color(0xFFAF8268)
                    ),
                    modifier = Modifier
                        .scale(0.75f)
                        .requiredSize(36.dp, 22.dp)
                )
            }

        }
        if (isNotif) {
            Spacer(Modifier.height(16.dp))
            RemindMeRow(
                value, content, onClick, isSecondBlock, secondValue, secondContent, onClickSecond
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
    secondContent: List<String> = emptyList(),
    onClickSecond: (String) -> Unit = {}
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextMaker("Remind me", 12.sp, fontWeight = FontWeight.Normal)
        DropdownField(Modifier.weight(1f), value, content) { onClick(it) }

        if (isSecondBlock) {
            TextMaker("at", 12.sp, fontWeight = FontWeight.Normal)
            DropdownField(Modifier.weight(1f), secondValue, secondContent) { onClickSecond(it) }
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
    Box(modifier) {
        CardWithDropdown(value) { isExpanded = true }
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
private fun CardWithDropdown(value: String, onClick: () -> Unit) {
    Card(
        Modifier
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(cardColor),
        border = BorderStroke(1.dp, textFieldCursorColor)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextMaker(value, 12.sp, fontWeight = FontWeight.Normal, modifier = Modifier.weight(1f))
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
    label: String, onAddItem: () -> Unit, content: List<T>,
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
                Icon(
                    painterResource(R.drawable.prime_plus),
                    contentDescription = "Add item",
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null
                    ) { onAddItem() }
                )
            }
            HorizontalDivider(
                Modifier.fillMaxWidth(),
                1.dp,
                Color(0xFFF2F2F2)
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
                            .heightIn(min = 215.dp)
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
    hasNote: Boolean
) {
    val parsedDate =
        remember(date) {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).format(dateParser)
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
                Icon(
                    painterResource(R.drawable.pen_notes),
                    "edit information",
                    Modifier.size(18.dp),
                    Color(0xFF4B332E)
                )
            }
        }
        if (hasLine) {
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF2F2F2))
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.birthday_calendar),
                    "calendar icon",
                    Modifier.size(20.dp)
                )
                Image(
                    painterResource(R.drawable.notifications),
                    "edit notifications settings"
                )
            }
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
            color = if (percentage == 1f) Color(0xFF008400) else Color(0xFFD27918),
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
        colors = CardDefaults.cardColors(if (isProtected) Color(0xFFEFF6E9) else Color(0xFFFFF6E8)),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, if (isProtected) Color(0xFFDFEEDF) else Color(0xFFFDF1ED)),
        modifier = modifier
    ) {
        Column(
            Modifier.padding(12.dp, 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(icon), "$label Icon")
            Spacer(Modifier.height(2.dp))
            TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            TextMaker(
                if (isProtected) "Protected" else "Not Protected", 10.sp,
                if (isProtected) Color(0xFF008400) else Color(0xFFD27918),
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
