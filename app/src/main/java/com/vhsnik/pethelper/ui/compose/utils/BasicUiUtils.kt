package com.vhsnik.pethelper.ui.compose.utils

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.theme.*

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
fun DropdownMoreOptions(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    hasMarkAsDone: Boolean = false,
    onMarkAsDoneClick: () -> Unit = {}) {
    DropdownMenu(isExpanded, onDismiss, containerColor = cardColor) {
        DropdownMenuItem(
            text = { TextMaker("Edit", 12.sp) },
            onClick = {
                onDismiss()
                onEditClick()
            }
        )
        DropdownMenuItem(
            text = {TextMaker("Delete", 12.sp)},
            onClick = {
                onDismiss()
                onDeleteClick()
            }
        )
        if (hasMarkAsDone) {
            DropdownMenuItem(
                text = {TextMaker("Mark as done", 12.sp)},
                onClick = {
                    onDismiss()
                    onMarkAsDoneClick()
                }
            )
        }
    }
}