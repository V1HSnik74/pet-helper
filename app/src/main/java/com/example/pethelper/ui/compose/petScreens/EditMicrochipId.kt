package com.example.pethelper.ui.compose.petScreens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.pethelper.R
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.ui.theme.red

private fun isError(value: String): Boolean {
    return value.isNotEmpty() && value.length != 15
}

@Composable
fun EditMicrochipIdDialog(
    onDismiss: () -> Unit,
    onUpdateInfo: (chip: String) -> Unit,
    currChip: String,
) {
    var chip by remember(currChip) { mutableStateOf(currChip) }
    DialogPattern(
        "Edit Microchip ID",
        R.drawable.chip_icon,
        onDismiss,
        { onUpdateInfo(chip) },
        chip.length == 15
    ) {
        Column(Modifier.fillMaxWidth()) {
            TextMaker("Microchip ID", 12.sp)
            Spacer(Modifier.height(8.dp))
            BasicTextFieldMaker(
                chip, { chip = it.filter { char -> char.isDigit() }.take(15) },
                "000 000 000 000 000", Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
            if (isError(chip)) {
                Spacer(Modifier.height(4.dp))
                TextMaker("Enter 15 digits", 10.sp, red, fontWeight = FontWeight.SemiBold)
            }
        }
    }

}