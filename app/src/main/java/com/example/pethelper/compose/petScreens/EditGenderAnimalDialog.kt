package com.example.pethelper.compose.petScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.brownColor
import com.example.pethelper.compose.patterns.buttonColor
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.compose.patterns.selectedColor

@Composable
fun UpdateGenderAnimalDialog(
    onDismiss: () -> Unit,
    onUpdateValue: (value: String) -> Unit,
    currValue: String,
    label: String,
    icon: Int,
    mapOfValues: Map<String, Int>
) {
    var value by remember(currValue) { mutableStateOf(currValue) }
    DialogPattern(
        "Edit $label",
        icon,
        onDismiss,
        { onUpdateValue(value) },
        true
    ) {
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            mapOfValues.entries.forEach {
                GenderAnimalCard(
                    Modifier.weight(1f),
                    { value = it.key },
                    value == it.key,
                    it.value,
                    it.key
                )
            }
        }
    }
}

@Composable
private fun GenderAnimalCard(
    modifier: Modifier,
    onClick: () -> Unit,
    isSelected: Boolean,
    icon: Int,
    text: String
) {
    Card(
        modifier
            .height(80.dp)
            .clickable(indication = null, interactionSource = null) {
                onClick()
            },
        colors = CardDefaults.cardColors(if (isSelected) selectedColor else cardColor),
        border = BorderStroke(1.dp, if (isSelected) buttonColor else textFieldCursorColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(icon),
                null,
                Modifier.size(30.dp),
                if (isSelected) buttonColor else brownColor
            )
            Spacer(Modifier.height(6.dp))
            TextMaker(text, 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}