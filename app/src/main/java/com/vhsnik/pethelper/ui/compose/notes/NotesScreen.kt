package com.vhsnik.pethelper.ui.compose.notes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.*
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.*
import com.vhsnik.pethelper.ui.viewModel.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
fun NotesScreen(
    petId: Int,
    onBackClick: (Int) -> Unit,
    noteViewModel: NoteViewModel = hiltViewModel(),
    petsViewModel: PetsViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) {
        petsViewModel.setCurPet(petId)
        noteViewModel.setCurPetId(petId)
    }
    val selectedCategory by noteViewModel.selectedCategory.collectAsStateWithLifecycle()
    val allNotes by
    noteViewModel.allNotes
        .collectAsStateWithLifecycle()
    var note by remember { mutableStateOf<Note?>(null) }
    var idForDelete by remember { mutableStateOf<Long?>(null) }
    var isDialogActive by remember { mutableStateOf(false) }
    var isDeleteActive by remember { mutableStateOf(false) }
    if (isDialogActive) {
        AddNoteDialog(
            {
                isDialogActive = false
                note = null
            },
            { description, category ->
                if (note != null) {
                    noteViewModel.updateNote(
                        note!!.copy(
                            description = description,
                            category = category
                        )
                    )
                } else {
                    noteViewModel.addNote(
                        description,
                        category,
                        NoteSection.COMMON,
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    )
                }
                note = null
                isDialogActive = false
            },
            note?.description.orEmpty(),
            note?.category.orEmpty()
        )
    }
    if (isDeleteActive) {
        DeleteDialog(
            { isDeleteActive = false },
            {
                noteViewModel.deleteNote(idForDelete!!)
                isDeleteActive = false
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
            TextMaker(
                "Notes",
                18.sp,
                fontWeight = FontWeight.SemiBold
            )
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
                    NoteCategoryButton(
                        it,
                        selectedCategory == it
                    ) { cat -> noteViewModel.selectCategory(cat) }
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
                    items(allNotes, key = { it.id }) {
                        val cardContent = NoteCardContent.valueOf(it.category!!.uppercase())
                        val dateLD = LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
                        NoteCard(
                            cardContent,
                            dateLD.format(fullDateParser),
                            it.description,
                            cardContent.textColor, {
                                note = it
                                isDialogActive = true
                            },
                            {
                                idForDelete = it.id
                                isDeleteActive = true
                            }
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
            if (isSelected) brownColor
            else backgroundColor
        ),
        border = BorderStroke(
            color = if (isSelected) Color.Transparent else brownColor,
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
    iconColor: Color,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
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
                TextMaker(
                    date,
                    12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.wrapContentSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        "More options",
                        Modifier
                            .size(20.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) { isMenuExpanded = true },
                        tint = iconColor
                    )
                    DropdownMoreOptions(
                        isMenuExpanded,
                        { isMenuExpanded = false },
                        onEditClick,
                        onDeleteClick
                    )
                }

            }
            Spacer(Modifier.height(16.dp))
            TextMaker(
                description,
                14.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.height(16.dp))
            NoteCardCategoryCard(
                category.category,
                category.categoryCardColor,
                category.textColor
            )
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