package com.example.pethelper.compose.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField

@Composable

fun AddSupplementTreatDialog(
    onDismiss: () -> Unit,
    onSaveInfo: (name: String, petId: Int) -> Unit,
    label: String,
    icon: Int,
    placeholder: String,
    petId: Int
) {
    var value by remember { mutableStateOf("") }
    DialogPattern(
        "Add $label", icon, onDismiss,
        { onSaveInfo(value, petId) },
        value.isNotBlank()
    ) {
        LabelAndTextField(label, value, { value = it }, placeholder)
    }
}