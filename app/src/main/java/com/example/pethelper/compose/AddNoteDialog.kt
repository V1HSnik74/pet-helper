package com.example.pethelper.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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

@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSaveNote: (description: String, category: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(backgroundColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextMaker("Add Note", 20.sp)
                    Spacer(Modifier.height(20.dp))
                    TextMaker("Note", 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = textFieldContainerColor,
                            unfocusedContainerColor = textFieldContainerColor,
                            focusedBorderColor = textFieldCursorColor,
                            unfocusedBorderColor = textFieldCursorColor,
                            cursorColor = textFieldCursorColor
                        ),
                        placeholder = {
                            TextMaker(
                                "Write your note here",
                                14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                    TextMaker("Category", 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CategoryButton(
                            "Behavior", R.drawable.star_notes, Color(0xFFE27380),
                            Color(0xFFFFDEE6), Modifier.weight(1f), selectedCategory == "Behavior"
                        ) { selectedCategory = "Behavior" }
                        CategoryButton(
                            "Habits", R.drawable.pen_notes, Color(0xFFC042FF),
                            Color(0xFFF9EDFF), Modifier.weight(1f), selectedCategory == "Habits"
                        ) { selectedCategory = "Habits" }
                        CategoryButton(
                            "Health", R.drawable.heart_notes, Color(0xFF2AAE1E),
                            Color(0xFFD2FFCD), Modifier.weight(1f), selectedCategory == "Health"
                        ) { selectedCategory = "Health" }
                        CategoryButton(
                            "Nutrition", R.drawable.bone_notes, Color(0xFFFF9A0C),
                            Color(0xFFFFF6D3), Modifier.weight(1f), selectedCategory == "Nutrition"
                        ) { selectedCategory = "Nutrition" }
                        CategoryButton(
                            "Other", R.drawable.note_notes, Color(0xFF3D86E4),
                            Color(0xFFE2FFFF), Modifier.weight(1f), selectedCategory == "Other"
                        ) { selectedCategory = "Other" }
                    }
                    Spacer(Modifier.height(24.dp))
                    ButtonMaker(
                        "Save Note",
                        { onSaveNote(description, selectedCategory) },
                        enabled = selectedCategory.isNotEmpty() && description.isNotEmpty()
                    )
                }
                IconButton(
                    onDismiss, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, top = 16.dp)
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
private fun CategoryButton(
    text: String, logo: Int, borderColor: Color, color: Color, modifier: Modifier,
    isSelected: Boolean, onClick: () -> Unit
) {
    Card(
        modifier
            .height(71.dp)
            .clickable(interactionSource = null, indication = null) { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(if (isSelected) color else backgroundColor),
        border = BorderStroke(width = 1.dp, color = borderColor)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(logo),
                contentDescription = "Button icon",
                modifier = Modifier.size(21.dp)
            )
            Spacer(Modifier.height(6.dp))
            TextMaker(text, 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
