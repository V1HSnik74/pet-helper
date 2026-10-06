package com.example.pethelper.compose.petScreens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pethelper.compose.patterns.BasicTextFieldMaker
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField

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