package com.example.pethelper.compose.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.backgroundColor

@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSaveNote: (description: String, category: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    DialogPattern(
        "Add Note",
        R.drawable.note_icon,
        onDismiss,
        {
            onSaveNote(description, selectedCategory)
        },
        selectedCategory.isNotEmpty() && description.isNotBlank()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabelAndTextField(
                "Note",
                description,
                { description = it },
                "Write your note here",
                false,
                Alignment.TopStart,
                100.dp,
                16.dp,
                16.dp
            )
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextMaker("Category", 12.sp)
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NoteCardContent.entries.forEach {
                        CategoryButton(
                            it,
                            { selectedCategory = it.category },
                            Modifier.weight(1f),
                            selectedCategory == it.category
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun CategoryButton(
    item: NoteCardContent, onClick: () -> Unit, modifier: Modifier, isSelected: Boolean
) {
    Card(
        modifier
            .height(65.dp)
            .clickable(interactionSource = null, indication = null) { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(if (isSelected) item.color else backgroundColor),
        border = BorderStroke(width = 1.dp, color = item.textColor)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = "Button icon",
                modifier = Modifier.size(21.dp),
                tint = item.textColor
            )
            Spacer(Modifier.height(6.dp))
            TextMaker(item.category, 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
