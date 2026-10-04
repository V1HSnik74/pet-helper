package com.example.pethelper.compose.notes

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
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
import com.example.pethelper.db.viewModel.NoteViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import com.example.pethelper.compose.patterns.PastelleColors
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.backgroundColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val categoryButtons: List<String> =
    listOf("All", "Behavior", "Habits", "Health", "Nutrition", "Other")

val pink = PastelleColors.PINK
val purple = PastelleColors.PURPLE
val green = PastelleColors.GREEN
val yellow = PastelleColors.YELLOW
val blue = PastelleColors.BLUE

enum class NoteCardContent(
    val color: Color, val category: String, val icon: Int, val categoryCardColor: Color,
    val textColor: Color, val borderColor: Color
) {
    BEHAVIOR(
        pink.color,
        "Behavior",
        R.drawable.star_notes,
        pink.buttonColor,
        pink.iconColor,
        pink.borderColor
    ),
    HABITS(
        purple.color,
        "Habits",
        R.drawable.pen_notes,
        purple.buttonColor,
        purple.iconColor,
        purple.borderColor
    ),
    HEALTH(
        green.color,
        "Health",
        R.drawable.heart_notes,
        green.buttonColor,
        green.iconColor,
        green.borderColor
    ),
    NUTRITION(
        yellow.color,
        "Nutrition",
        R.drawable.bone_notes,
        yellow.buttonColor,
        yellow.iconColor,
        yellow.borderColor
    ),
    OTHER(
        blue.color,
        "Other",
        R.drawable.note_notes,
        blue.buttonColor,
        blue.iconColor,
        blue.borderColor
    )
}

@Composable
fun NotesScreen(petId: Int, noteViewModel: NoteViewModel, onBackClick: (Int) -> Unit) {
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
            .padding(horizontal = 22.dp)
    ) {
        Spacer(Modifier.height(64.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "back",
                Modifier
                    .size(12.dp, 24.dp)
                    .clickable(indication = null, interactionSource = null) { onBackClick(petId) }
            )
            TextMaker("Notes", 18.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                painter = painterResource(R.drawable.prime_plus),
                contentDescription = "add note",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) { isDialogActive = true }
            )

        }
        Spacer(Modifier.height(20.dp))
        Column(
            Modifier
                .fillMaxWidth()
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
                            it.date,
                            it.description,
                            NoteCardContent.valueOf(it.category.uppercase()).textColor
                        )
                    }
                }
            }
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
private fun NoteCard(
    category: NoteCardContent,
    date: String,
    description: String,
    iconColor: Color
) {
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(category.color),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, category.borderColor)
    ) {
        Column(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(category.icon),
                    null,
                    Modifier.size(22.dp),
                    tint = iconColor
                )
                Spacer(Modifier.width(10.dp))
                TextMaker(date, 12.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(16.dp))
            TextMaker(description, 14.sp, fontWeight = FontWeight.Normal)
            Spacer(Modifier.height(16.dp))
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
            12.sp,
            textColor,
            FontWeight.SemiBold,
            Modifier.padding(12.dp, 6.dp)
        )
    }
}