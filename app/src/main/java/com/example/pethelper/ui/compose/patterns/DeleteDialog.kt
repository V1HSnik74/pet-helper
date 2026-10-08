package com.example.pethelper.ui.compose.patterns

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.example.pethelper.ui.theme.*

@Composable
fun DeleteDialog(onDismiss: () -> Unit, onDelete: () -> Unit) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier.fillMaxWidth(0.75f),
            colors = CardDefaults.cardColors(cardColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextMaker("Delete Item", 18.sp)
                TextMaker(
                    "Are you sure you want to delete this item?",
                    14.sp,
                    fontWeight = FontWeight.Normal
                )
                Row(Modifier.fillMaxWidth()) {
                    Button(
                        onDismiss,
                        colors = ButtonDefaults.buttonColors(selectedColor),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(16.dp, 10.dp),
                        interactionSource = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TextMaker("Cancel", 14.sp, buttonColor)
                    }
                    Spacer(Modifier.width(24.dp))
                    Button(
                        onDelete,
                        colors = ButtonDefaults.buttonColors(buttonColor),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(16.dp, 10.dp),
                        interactionSource = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TextMaker("Delete", 14.sp, Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMoreOptions(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    hasMarkAsDone: Boolean = false,
    onMarkAsDoneClick: () -> Unit = {}) {
    DropdownMenu(isExpanded, onDismiss, containerColor = cardColor) {
        DropdownMenuItem(
            text = { TextMaker("Edit", 12.sp) },
            onClick = {
                onDismiss()
                onEditClick()
            }
        )
        DropdownMenuItem(
            text = {TextMaker("Delete", 12.sp)},
            onClick = {
                onDismiss()
                onDeleteClick()
            }
        )
        if (hasMarkAsDone) {
            DropdownMenuItem(
                text = {TextMaker("Mark as done", 12.sp)},
                onClick = {
                    onDismiss()
                    onMarkAsDoneClick()
                }
            )
        }
    }
}