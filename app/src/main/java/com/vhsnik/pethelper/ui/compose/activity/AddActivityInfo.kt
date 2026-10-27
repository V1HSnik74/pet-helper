package com.vhsnik.pethelper.ui.compose.activity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.buttonColor

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
        "Update Activity Information", R.drawable.addactivityicon, onDismiss,
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
                TextMaker(
                    "Activity level",
                    12.sp
                )
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    activityLevels.forEach { (text, icon) ->
                        ActivityLevelButton(
                            activityLevel == text,
                            text,
                            icon
                        )
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