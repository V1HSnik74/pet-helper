package com.example.pethelper.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R

private enum class Categories(val category: String) {
    FEEDING("Feeding"),
    ALLERGY("Allergy"),
    SUPPLEMENTS("Supplements"),
    TREATS("Treats"),
    Other("Other")
}

@Composable
fun AddNoteNutritionDialog(
    onDismiss: () -> Unit,
    onUpdateNote: (note: String, category: String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(backgroundColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        contentDescription = "Note icon",
                        painter = painterResource(R.drawable.note_icon)
                    )
                    Spacer(Modifier.height(8.dp))
                    TextMaker("Add Note", 16.sp)
                    Spacer(Modifier.height(16.dp))
                    TextMaker("Note", 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note, onValueChange = { note = it },
                        singleLine = true,
                        placeholder = { TextMaker("Write your note here", 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = textFieldContainerColor,
                            unfocusedContainerColor = textFieldContainerColor,
                            focusedBorderColor = textFieldCursorColor,
                            unfocusedBorderColor = textFieldCursorColor,
                            cursorColor = textFieldCursorColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    TextMaker("Category", 12.sp)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Categories.entries.forEach {
                            CategoryButtonNutrition(
                                selectedCategory == it.category,
                                it.category
                            ) { selectedCategory = it.category }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    ButtonMaker(
                        "Save Note",
                        { onUpdateNote(note, selectedCategory) },
                        enabled = note.isNotEmpty() && selectedCategory.isNotEmpty()
                    )
                }
                IconButton(
                    onDismiss, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 16.dp)
                ) {
                    Image(
                        painterResource(R.drawable.cancel),
                        contentDescription = "cancel"
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryButtonNutrition(isSelected: Boolean, text: String, onClick: () -> Unit) {
    Card(
        Modifier
            .wrapContentSize()
            .clickable(interactionSource = null, indication = null) { onClick() },
        colors = CardDefaults.cardColors(if (isSelected) selectedColor else Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isSelected) buttonColor else Color(0xFFAF8268))
    ) {
        TextMaker(
            text,
            10.sp,
            Color(0xFF2B2B2B),
            FontWeight.SemiBold,
            Modifier.padding(10.dp, 8.dp)
        )
    }
}