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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogChip
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.TextMaker

val categories = listOf("Feeding", "Allergy", "Treats", "Supplements", "Other")

@Composable
fun AddNoteNutritionDialog(
    onDismiss: () -> Unit,
    onUpdateNote: (note: String, category: String, petId: Int) -> Unit,
    petId: Int
) {
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
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
                100.dp
            )
            Column(Modifier.fillMaxWidth()) {
                TextMaker("Category", 12.sp)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach {
                        DialogChip(selectedCategory == it, it) { selectedCategory = it }
                    }
                }
            }
        }
    }
}