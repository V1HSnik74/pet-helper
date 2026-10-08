package com.example.pethelper.ui.compose.petScreens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.pethelper.R
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.ui.theme.*

val options = listOf("Yes", "No", "Not sure")

@Composable
fun UpdateNeutered(
    onDismiss: () -> Unit,
    onUpdateNeutered: (String) -> Unit,
    gender: String,
    isNeutered: String
) {
    var selectedIsNeutered by remember { mutableStateOf(isNeutered) }
    DialogPattern(
        if (gender == "Male") "Edit Neutered" else "Edit Spayed",
        R.drawable.neutured_icon,
        onDismiss,
        { onUpdateNeutered(selectedIsNeutered) },
        selectedIsNeutered.isNotEmpty()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach {
                NeuteredCard(selectedIsNeutered == it, { selectedIsNeutered = it }, it)
            }
        }
    }
}

@Composable
private fun NeuteredCard(isSelected: Boolean, onClick: () -> Unit, text: String) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = null,
                indication = null
            ) { onClick() },
        colors = CardDefaults.cardColors(if (isSelected) selectedColor else cardColor),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            color = if (isSelected) buttonColor else lightBrown,
            width = 1.dp
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Image(
                    painterResource(R.drawable.neutered_selected),
                    "Selected"
                )
            } else {
                Icon(
                    painterResource(R.drawable.neutered_not_selected),
                    "Not selected",
                    tint = lightBrown
                )
            }
            Spacer(Modifier.width(10.dp))
            TextMaker(text, 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}