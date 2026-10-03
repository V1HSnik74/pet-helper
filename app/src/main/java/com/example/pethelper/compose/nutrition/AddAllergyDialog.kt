package com.example.pethelper.compose.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogChip
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.TextMaker

val popularAllergies = listOf("Chicken", "Beef", "Dairy", "Eggs", "Fish", "Wheat", "Corn", "Soy")

@Composable
fun AddAllergyDialog(
    onDismiss: () -> Unit, onSaveAllergy: (name: String, petId: Int) -> Unit,
    petId: Int
) {
    var allergy by remember { mutableStateOf("") }
    DialogPattern(
        "Add Allergy",
        R.drawable.allergy_icon,
        onDismiss,
        { onSaveAllergy(allergy, petId) },
        allergy.isNotBlank()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabelAndTextField("Allergy", allergy, { allergy = it }, "e.g. Fish")
            Column(Modifier.fillMaxWidth()) {
                TextMaker("Popular Allergies", 12.sp)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularAllergies.forEach {
                        DialogChip(
                            allergy.equals(it, true),
                            it
                        ) {
                            allergy = it
                        }
                    }
                }
            }
        }
    }
}