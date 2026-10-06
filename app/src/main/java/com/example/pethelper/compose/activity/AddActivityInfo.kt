package com.example.pethelper.compose.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.ui.theme.buttonColor

val activityLevels = mapOf(
    "Low" to R.drawable.low_battery,
    "Medium" to R.drawable.medium_battery,
    "High" to R.drawable.high_battery
)

@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit, onSaveActivity: (
        activityLevel: String, favActivities: String,
        limitations: String, goals: String, petId: Int
    ) -> Unit,
    petId: Int
) {
    var activityLevel by remember { mutableStateOf("Medium") }
    var favActivities by remember { mutableStateOf("") }
    var limitations by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    DialogPattern(
        "Add Activity Information", R.drawable.addactivityicon, onDismiss,
        {
            onSaveActivity(
                activityLevel,
                favActivities,
                limitations,
                goals,
                petId
            )
        }, goals.isNotBlank()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextMaker("Activity level", 12.sp)
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    activityLevels.forEach { (text, icon) ->
                        ActivityLevelButton(activityLevel == text, text, icon)
                        { activityLevel = text }
                    }
                }
            }
            LabelAndTextField(
                "Favorite activities (optional)",
                favActivities,
                { favActivities = it },
                "e.g. Ball, swimming"
            )
            LabelAndTextField(
                "Physical limitations (optional)",
                limitations,
                { limitations = it },
                "e.g. can't run"
            )
            LabelAndTextField(
                "Goals",
                goals,
                { goals = it },
                "e.g. Maintain healthy weight, learn tricks"
            )
        }

    }
}

@Composable
private fun ActivityLevelButton(isSelected: Boolean, text: String, icon: Int, onClick: () -> Unit) {
    Card(
        Modifier
            .wrapContentWidth()
            .clickable(indication = null, interactionSource = null) { onClick() },
        colors = CardDefaults.cardColors(if (isSelected) buttonColor else Color.Transparent),
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, buttonColor)
    ) {
        Row(
            Modifier
                .wrapContentWidth()
                .padding(12.dp, 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(icon),
                null,
                tint = if (isSelected) Color.White else buttonColor
            )
            Spacer(Modifier.width(8.dp))
            TextMaker(
                text,
                12.sp,
                if (isSelected) Color.White else buttonColor,
                FontWeight.SemiBold
            )
        }
    }
}