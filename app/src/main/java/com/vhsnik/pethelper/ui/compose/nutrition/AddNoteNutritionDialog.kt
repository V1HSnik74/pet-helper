package com.vhsnik.pethelper.ui.compose.nutrition

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.compose.utils.*

val categories = listOf("Feeding", "Allergy", "Treats", "Supplements", "Other")

@Composable
fun AddNoteNutritionDialog(
    onDismiss: () -> Unit,
    onUpdateNote: (note: String, category: String, petId: Int) -> Unit,
    petId: Int,
    note: String = "",
    category: String = ""
) {
    var note by remember { mutableStateOf(note) }
    var selectedCategory by remember { mutableStateOf(category) }
    DialogPattern(
        "Add Note",
        R.drawable.note_icon,
        onDismiss,
        {
            onUpdateNote(note, selectedCategory, petId)
        },
        note.isNotBlank() && selectedCategory.isNotEmpty()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabelAndTextField(
                "Note",
                note,
                { note = it },
                "Write down your note here",
                false,
                Alignment.TopStart,
                100.dp,
                16.dp,
                16.dp
            )
            Column(Modifier.fillMaxWidth()) {
                TextMaker(
                    "Category",
                    12.sp
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach {
                        DialogChip(
                            selectedCategory == it,
                            it
                        ) { selectedCategory = it }
                    }
                }
            }
        }
    }
}