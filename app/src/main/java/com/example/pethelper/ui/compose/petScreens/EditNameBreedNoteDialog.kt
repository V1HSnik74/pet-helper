package com.example.pethelper.ui.compose.petScreens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.example.pethelper.ui.compose.patterns.*

@Composable
fun EditNameBreedNoteDialog(
    onDismiss: () -> Unit, onUpdateInfo: (String) -> Unit,
    label: String, placeholder: String,
    value: String,
    icon: Int,
    isNote: Boolean = false
) {
    var newValue by remember { mutableStateOf(value) }
    DialogPattern(
        "Edit $label",
        icon,
        onDismiss,
        { onUpdateInfo(newValue) },
        newValue.isNotBlank()
    ) {
        if (isNote) {
            BasicTextFieldMaker(
                newValue,
                { newValue = it },
                placeholder,
                Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                true,
                16.dp,
                16.dp,
                Alignment.TopStart
            )
        } else {
            LabelAndTextField(
                label,
                newValue,
                { newValue = it },
                placeholder
            )
        }

    }
}