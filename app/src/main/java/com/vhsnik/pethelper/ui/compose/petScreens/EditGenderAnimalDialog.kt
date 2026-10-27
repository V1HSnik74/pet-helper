package com.vhsnik.pethelper.ui.compose.petScreens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.*

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
        border = BorderStroke(1.dp, if (isSelected) buttonColor else lightBrown),
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