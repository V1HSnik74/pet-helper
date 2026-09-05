package com.example.pethelper.compose


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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.example.pethelper.ui.theme.InterFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

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



val dateParser: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)

@Composable
fun TextMaker(
    text: String, fontSize: TextUnit,
    color: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Bold, modifier: Modifier = Modifier,
    textDecoration: TextDecoration = TextDecoration.None
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = InterFamily,
        color = color,
        fontWeight = fontWeight,
        modifier = modifier,
        textDecoration = textDecoration
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
            Modifier.padding(10.dp, 2.dp)
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
                    TextMaker(label, 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(8.dp))
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
fun LabelAndTextField(label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(horizontalAlignment = Alignment.Start){
        TextMaker(label, 12.sp)
        Spacer(Modifier.height(8.dp))
        BasicTextFieldMaker(
            value, { onValueChange(it) }, placeholder, Modifier
                .height(40.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun LabelAndDateTime(label: String, value: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.Start){
        TextMaker(label, 12.sp)
        Spacer(Modifier.height(8.dp))
        Card(
            Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(cardColor),
            border = BorderStroke(1.dp, textFieldCursorColor)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextMaker(value, 12.sp, fontWeight = FontWeight.Normal)
                IconButton(
                    onClick,
                    interactionSource = null,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(-90f)
                ) {
                    Icon(painterResource(R.drawable.back), "choose value")
                }
            }
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
            .heightIn(max = 250.dp)
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
        offset = IntOffset(0, 40)
    ) {
        Calendar(
            date,
            { onValueChange(it) },
            startMonth,
            endMonth,
            isSelectable
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReminderBlock(
    modifier: Modifier,
    isNotif: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    hasTwoBlocks: Boolean,
    value: String,
    onClick: () -> Unit,
    secondValue: String = "",
    onClickSecond: () -> Unit = {}
) {
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
                    checkedThumbColor = cardColor
                )
            )
        } else {
            Switch(
                checked = isNotif, onCheckedChange = { onCheckedChange(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = buttonColor,
                    checkedThumbColor = cardColor
                )
            )
        }

    }
    if (isNotif) {
        Spacer(Modifier.height(16.dp))
        RemindMeRow(hasTwoBlocks, value, onClick, secondValue, onClickSecond)
    }
}

@Composable
fun RemindMeRow(
    hasTwoBlocks: Boolean,
    value: String,
    onClick: () -> Unit,
    secondValue: String = "",
    onClickSecond: () -> Unit = {}
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextMaker("Remind me", 12.sp, fontWeight = FontWeight.Normal)
        CardWithDropdown(value, onClick, Modifier.weight(1f))
        if (hasTwoBlocks) {
            TextMaker("at", 12.sp, fontWeight = FontWeight.Normal)
            CardWithDropdown(secondValue, onClickSecond, Modifier.weight(1f))
        }
    }
}

@Composable
fun CardWithDropdown(value: String, onClick: () -> Unit, modifier: Modifier) {
    Card(
        modifier
            .height(40.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(cardColor),
        border = BorderStroke(1.dp, textFieldCursorColor)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextMaker(value, 12.sp, fontWeight = FontWeight.Normal)
            IconButton(
                onClick,
                interactionSource = null,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(-90f)
            ) {
                Icon(painterResource(R.drawable.back), "choose value")
            }
        }
    }
}