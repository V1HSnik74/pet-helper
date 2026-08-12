package com.example.pethelper.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.db.NoteViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val categoryButtons: List<String> =
    listOf("All", "Behavior", "Habits", "Health", "Nutrition", "Other")

private enum class NoteCardContent(
    val color: Color, val category: String, val icon: Int, val categoryCardColor: Color,
    val textColor: Color
) {
    BEHAVIOR(
        Color(0xFFFFDEE6),
        "Behavior",
        R.drawable.star_notes,
        Color(0xFFFFC7D5),
        Color(0xFFE27380)
    ),
    HABITS(
        Color(0xFFF9EDFF),
        "Habits",
        R.drawable.pen_notes,
        Color(0xFFEAD6FF),
        Color(0xFFC042FF)
    ),
    HEALTH(
        Color(0xFFD2FFCD),
        "Health",
        R.drawable.heart_notes,
        Color(0xFFABFEA2),
        Color(0xFF2AAE1E)
    ),
    NUTRITION(
        Color(0xFFFFF6D3),
        "Nutrition",
        R.drawable.bone_notes,
        Color(0xFFFFEA98),
        Color(0xFFFF9A0C)
    ),
    OTHER(
        Color(0xFFE2FFFF),
        "Other",
        R.drawable.note_notes,
        Color(0xFFC0FAFA),
        Color(0xFF3D86E4)
    )
}

@Composable
fun NotesScreen(petId: Int, noteViewModel: NoteViewModel) {
    var selectedCategory by remember { mutableStateOf("All") }
    val allNotes by remember(selectedCategory, petId) {
        if (selectedCategory == "All") noteViewModel.allNotes(petId)
        else noteViewModel.getNoteByCategory(selectedCategory, petId)
    }.collectAsState(emptyList())
    var isDialogActive by remember { mutableStateOf(false) }
    if (isDialogActive) {
        AddNoteDialog(
            { isDialogActive = false },
            { description, category ->
                val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
                val stringDate = LocalDateTime.now().format(formatter)
                noteViewModel.addNote(description, category, stringDate, petId)
                isDialogActive = false
            })
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 68.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "back"
            )
            TextMaker("Notes", 18.sp, fontWeight = FontWeight.SemiBold)
            IconButton({ isDialogActive = true }) {
                Icon(
                    painter = painterResource(R.drawable.plus),
                    contentDescription = "add note"
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            FlowRow(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryButtons.forEach {
                    NoteCategoryButton(it, selectedCategory == it) { selectedCategory = it }
                }
            }
            Spacer(Modifier.height(20.dp))
            if (allNotes.isNotEmpty()) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(allNotes) {
                        NoteCard(
                            NoteCardContent.valueOf(it.category.uppercase()),
                            it.date, it.description
                        )
                    }
                }
            } else TextMaker("You haven't added any notes yet!", 18.sp)
            Spacer(Modifier.height(40.dp))
        }
    }
}


@Composable
private fun NoteCategoryButton(text: String, isSelected: Boolean, onClick: (String) -> Unit) {
    Card(
        Modifier
            .wrapContentSize()
            .clickable(interactionSource = null, indication = null)
            { onClick(text) },
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            if (isSelected) Color(0xFFAF8268)
            else backgroundColor
        ),
        border = BorderStroke(
            color = if (isSelected) Color.Transparent else Color(0xFFAF8268),
            width = 1.dp
        )
    ) {
        TextMaker(
            text, 14.sp, if (isSelected) Color.White
            else Color.Black, modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

@Composable
private fun NoteCard(category: NoteCardContent, date: String, description: String) {
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(category.color),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(category.icon),
                    contentDescription = "${category.category} icon"
                )
                Spacer(Modifier.width(16.dp))
                TextMaker(date, 14.sp, fontWeight = FontWeight.Normal)
            }
            Spacer(Modifier.height(20.dp))
            TextMaker(description, 18.sp, fontWeight = FontWeight.Normal)
            Spacer(Modifier.height(20.dp))
            NoteCardCategoryCard(category.category, category.categoryCardColor, category.textColor)
        }
    }
}

@Composable
private fun NoteCardCategoryCard(category: String, color: Color, textColor: Color) {
    Card(
        Modifier.wrapContentSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(color)
    )
    {
        TextMaker(
            category,
            14.sp,
            textColor,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}