package com.example.pethelper.compose.petScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.backgroundColor
import com.example.pethelper.compose.patterns.buttonColor
import com.example.pethelper.compose.patterns.selectedColor
import com.example.pethelper.compose.patterns.smallTextColor
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState

@Composable
fun UpdateWeightHeightDialog(
    onDismiss: () -> Unit, onUpdateValue: (Float) -> Unit, isWeight: Boolean, valueInt: Int,
    valueFloat: Int, count: Int, firstValue: Int, lastValue: Int
) {
    var selectedInteger by remember { mutableIntStateOf(valueInt) }
    var selectedFraction by remember { mutableIntStateOf(valueFloat) }
    val totalValue = selectedInteger + (selectedFraction / 10f)
    DialogPattern(
        if (isWeight) "Edit Weight" else "Edit Height",
        if (isWeight) R.drawable.weight_icon_dialog else R.drawable.height_icon_dialog,
        onDismiss,
        { onUpdateValue(totalValue) },
        totalValue != 0f
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextMaker(
                    "$selectedInteger.$selectedFraction",
                    28.sp, buttonColor, FontWeight.SemiBold
                )
                Spacer(Modifier.width(4.dp))
                TextMaker(
                    if (isWeight) "kg" else "cm",
                    16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WheelPickerColumn(
                    selectedInteger,
                    { selectedInteger = it },
                    "$firstValue-$lastValue",
                    count,
                    firstValue,
                    if (isWeight) "kg" else "cm"
                )
                Spacer(Modifier.width(24.dp))
                Image(
                    painterResource(R.drawable.dot),
                    null
                )
                Spacer(Modifier.width(24.dp))
                WheelPickerColumn(
                    selectedFraction,
                    { selectedFraction = it },
                    "0-9",
                    10,
                    0,
                    if (isWeight) ".kg" else ".cm"
                )
            }
        }
    }
}

@Composable
private fun WheelPickerCard(
    count: Int,
    initIndex: Int,
    onValueChange: (Int) -> Unit,
    firstValue: Int
) {
    val state = rememberFWheelPickerState(initIndex - firstValue)
    LaunchedEffect(state.currentIndex) {
        onValueChange(state.currentIndex + firstValue)
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFFFD8C1)
        ),
        colors = CardDefaults.cardColors(backgroundColor),
        modifier = Modifier.width(72.dp)
    )
    {
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(selectedColor, RoundedCornerShape(8.dp))
            )
            FVerticalWheelPicker(
                count = count,
                unfocusedCount = 1,
                itemHeight = 36.dp,
                state = state
            ) { index ->
                val displayValue = index + firstValue
                val isSelected = state.currentIndexSnapshot == index
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TextMaker(
                        displayValue.toString(), 20.sp,
                        if (isSelected) buttonColor else Color.Black,
                        if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }

    }
}

@Composable
private fun WheelPickerColumn(
    value: Int,
    onValueChange: (Int) -> Unit,
    text: String,
    count: Int,
    firstValue: Int,
    label: String
) {
    Column(
        Modifier.wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextMaker(
            label,
            12.sp, fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        WheelPickerCard(count, value, { onValueChange(it) }, firstValue)
        Spacer(Modifier.height(8.dp))
        TextMaker(
            text, 12.sp, smallTextColor,
            FontWeight.SemiBold
        )
    }
}