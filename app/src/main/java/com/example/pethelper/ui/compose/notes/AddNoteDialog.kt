package com.example.pethelper.ui.compose.notes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.pethelper.R
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.ui.theme.backgroundColor

@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSaveNote: (description: String, category: String) -> Unit,
    curDescription: String = "",
    curCategory: String = ""
) {
    var selectedCategory by remember { mutableStateOf(curCategory) }
    var description by remember { mutableStateOf(curDescription) }
    DialogPattern(
        if (curDescription.isEmpty()) "Add Note" else "Update Note",
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
                TextMaker(
                    "Category",
                    12.sp
                )
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
            TextMaker(
                item.category,
                10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
