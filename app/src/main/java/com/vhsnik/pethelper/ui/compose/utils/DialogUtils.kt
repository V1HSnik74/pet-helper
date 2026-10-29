package com.vhsnik.pethelper.ui.compose.utils

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.theme.*
import java.time.*

enum class Dialogs {
    DATE, TIME, DATE_UPC, TIME_UPC
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
                    Column(Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())) {
                        content()
                        Spacer(Modifier.height(1.dp))
                    }
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
fun PopupTimePicker(
    onDismiss: () -> Unit,
    onTimeChanged: (String) -> Unit,
    selectedDate: LocalDate,
    isTimeUpc: Boolean,
    maxHourLimit: Int = 23,
    maxMinuteLimit: Int = 59
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
                WheelPickerTime(onTimeChanged, selectedDate, isTimeUpc, maxHourLimit, maxMinuteLimit)
            }
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

@Composable
fun DeleteDialog(onDismiss: () -> Unit, onDelete: () -> Unit) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier.fillMaxWidth(0.75f),
            colors = CardDefaults.cardColors(cardColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextMaker("Delete Item", 18.sp)
                TextMaker(
                    "Are you sure you want to delete this item?",
                    14.sp,
                    fontWeight = FontWeight.Normal
                )
                Row(Modifier.fillMaxWidth()) {
                    Button(
                        onDismiss,
                        colors = ButtonDefaults.buttonColors(selectedColor),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(16.dp, 10.dp),
                        interactionSource = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TextMaker("Cancel", 14.sp, buttonColor)
                    }
                    Spacer(Modifier.width(24.dp))
                    Button(
                        onDelete,
                        colors = ButtonDefaults.buttonColors(buttonColor),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(16.dp, 10.dp),
                        interactionSource = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TextMaker("Delete", 14.sp, Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PortionSizeDurationValue(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: (String) -> Unit,
    isMenuOpened: Boolean,
    onDismiss: () -> Unit,
    measure: String,
    onClickArrow: () -> Unit,
    units: List<String>
) {
    Card(
        Modifier.wrapContentSize(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, lightBrown),
        colors = CardDefaults.cardColors(cardColor)
    ) {
        Row(
            Modifier
                .wrapContentWidth()
                .padding(8.dp, 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(it.filter { char -> char.isDigit() }) },
                singleLine = true,
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp),
                textStyle = TextStyle(
                    fontSize = 12.sp, fontFamily = InterFamily, textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(lightBrown),
                decorationBox = {
                    Box(
                        modifier = Modifier
                            .background(
                                cardColor
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (value.isEmpty()) {
                            TextMaker(
                                "20", 12.sp, smallTextColor,
                                FontWeight.Normal
                            )
                        }
                        it.invoke()
                    }
                }
            )
            Spacer(Modifier.width(4.dp))
            TextMaker(measure, 10.sp)
            DropdownMenu(
                expanded = isMenuOpened,
                onDismissRequest = onDismiss,
                modifier = Modifier
                    .heightIn(max = 100.dp)
                    .background(Color.White)
            ) {
                units.forEach {
                    DropdownMenuItem(
                        {
                            TextMaker(
                                it,
                                12.sp
                            )
                        },
                        { onClick(it) }
                    )
                }
            }
            Image(
                painterResource(R.drawable.small_arrow),
                "list of units",
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = null
                ) { onClickArrow() }
            )
        }
    }
}
