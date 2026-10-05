package com.example.pethelper.compose.petScreens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
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
    onValueChange: (String) -> Unit,
    value: String,
    icon: Int,
    isNote: Boolean = false
) {
    DialogPattern(
        "Edit $label",
        icon,
        onDismiss,
        { onUpdateInfo(value) },
        value.isNotBlank()
    ) {
        if (isNote) {
            BasicTextFieldMaker(
                value,
                onValueChange,
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
                value,
                onValueChange,
                placeholder
            )
        }

    }
}