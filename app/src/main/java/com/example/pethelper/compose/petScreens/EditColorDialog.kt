package com.example.pethelper.compose.petScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.ui.theme.buttonColor

@Composable
fun UpdateColorDialog(color: String, onDismiss: () -> Unit, onUpdateColor: (String) -> Unit) {
    var selectedColor by remember { mutableStateOf(color) }
    DialogPattern(
        "Edit Color",
        R.drawable.color_icon,
        onDismiss,
        { onUpdateColor(selectedColor) },
        selectedColor.isNotBlank()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabelAndTextField("Color", selectedColor, { selectedColor = it }, "e.g. Black")
            Column(Modifier.fillMaxWidth()) {
                TextMaker("Popular Colors", 12.sp)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Colors.entries.forEach {
                        ColorCircle(
                            it.values,
                            selectedColor == it.values,
                            { selectedColor = it.values },
                            it.color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorCircle(value: String, isSelected: Boolean, onClick: () -> Unit, color: Color) {
    Box(
        Modifier
            .size(35.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                color = if (isSelected) buttonColor else {
                    if (value == "White") Color.Black else Color.Transparent
                },
                width = if (isSelected) 2.dp else 0.5.dp,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

private enum class Colors(val color: Color, val values: String) {
    BLACK(Color.Black, "Black"),
    CREME(Color(0xFFFFEAC0), "Creme"),
    GOLDEN(Color(0xFFEFB644), "Golden"),
    BROWN(Color(0xFFA14E00), "Brown"),
    WHITE(Color.White, "White"),
    GRAY(Color.Gray, "Gray")
}