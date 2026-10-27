package com.vhsnik.pethelper.ui.compose.nutrition

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.ui.compose.utils.*

val popularAllergies = listOf("Chicken", "Beef", "Dairy", "Eggs", "Fish", "Wheat", "Corn", "Soy")

@Composable
fun AddOtherNutritionDialog(
    onDismiss: () -> Unit, onSaveValue: (name: String, petId: Int) -> Unit,
    petId: Int,
    label: String,
    icon: Int,
    placeholder: String,
    isAllergy: Boolean = false
) {
    var value by remember { mutableStateOf("") }
    DialogPattern(
        "Add $label",
        icon,
        onDismiss,
        { onSaveValue(value, petId) },
        value.isNotBlank()
    )
    {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabelAndTextField(
                label,
                value,
                { value = it },
                placeholder
            )
            if (isAllergy) {
                Column(Modifier.fillMaxWidth()) {
                    TextMaker(
                        "Popular Allergies",
                        12.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        popularAllergies.forEach {
                            DialogChip(
                                value.equals(it, true),
                                it
                            ) {
                                value = it
                            }
                        }
                    }
                }
            }

        }
    }
}