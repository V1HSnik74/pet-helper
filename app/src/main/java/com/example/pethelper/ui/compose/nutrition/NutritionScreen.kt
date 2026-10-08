package com.example.pethelper.ui.compose.nutrition

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.pethelper.R
import com.example.pethelper.data.entity.*
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.ui.theme.*
import com.example.pethelper.ui.viewModel.*

@Composable
fun NutritionScreen(
    petId: Int,
    onBackClick: () -> Unit,
    nutritionViewModel: NutritionViewModel = hiltViewModel(),
    petsViewModel: PetsViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) {
        petsViewModel.setCurPet(petId)
    }
    val allFoodItems by remember(petId) {
        nutritionViewModel.getAllFoodItems(petId)
    }.collectAsStateWithLifecycle(emptyList())
    val allAllergies by remember(petId) {
        nutritionViewModel.getAllAllergies(petId)
    }.collectAsStateWithLifecycle(emptyList())
    val allSupplements by remember(petId) {
        nutritionViewModel.getAllSupplements(petId)
    }.collectAsStateWithLifecycle(emptyList())
    val allTreats by remember(petId) {
        nutritionViewModel.getAllTreats(petId)
    }.collectAsStateWithLifecycle(emptyList())
    val allNotes by remember(petId) {
        nutritionViewModel.getAllNotesNutrition(petId)
    }.collectAsStateWithLifecycle(emptyList())
    var activeDialog by remember { mutableStateOf("") }
    val curPet = petsViewModel.currPet
    val isGenerating by nutritionViewModel.isGenerating.collectAsStateWithLifecycle()
    val generatedRation by nutritionViewModel.generatedRation.collectAsStateWithLifecycle()
    var idForDelete by remember { mutableStateOf<Long?>(null) }
    var deleteAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var foodItemToEdit by remember { mutableStateOf<FoodItem?>(null) }
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(22.dp, 64.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.back),
                contentDescription = "Back button",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickable(indication = null, interactionSource = null) { onBackClick() }
            )
            TextMaker(
                "Nutrition", 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(Modifier.height(20.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextMaker(
                "Monitor your pet's diet including allergies, supplements and treats!",
                14.sp,
                fontWeight = FontWeight.Normal
            )
            PagerCard(
                "Food & Portion",
                { activeDialog = "foodItem" },
                allFoodItems
            ) { item, hasNote ->
                GeneratedItem(
                    item.name,
                    item.description,
                    item.icon,
                    item.portionSize + " " + item.unit,
                    {
                        idForDelete = item.id
                        activeDialog = "delete"
                        deleteAction = { nutritionViewModel.deleteFoodItem(item.id) }
                    },
                    {
                        foodItemToEdit = item
                        activeDialog = "foodItem"
                    }
                )
                if (hasNote)
                    HorizontalDivider(
                        Modifier.fillMaxWidth(),
                        1.dp,
                        dividerColor
                    )
            }
            curPet?.let {
                GenerateButton(
                    {
                        nutritionViewModel.generateRation(
                            curPet, allAllergies, allSupplements
                        )
                    },
                    "Nutrition",
                    isGenerating
                )
            }
            OtherNutritionCard(
                "Allergies & Sensitives",
                {
                    idForDelete = it
                    deleteAction = { nutritionViewModel.deleteOtherNutrition(it) }
                    activeDialog = "delete"
                },
                { activeDialog = "allergyDialog" },
                allAllergies
            )
            OtherNutritionCard(
                "Supplements",
                {
                    idForDelete = it
                    deleteAction = { nutritionViewModel.deleteOtherNutrition(it) }
                    activeDialog = "delete"
                },
                { activeDialog = "supplementDialog" },
                allSupplements
            )
            OtherNutritionCard(
                "Treats",
                {
                    idForDelete = it
                    deleteAction = { nutritionViewModel.deleteOtherNutrition(it) }
                    activeDialog = "delete"
                },
                { activeDialog = "treatDialog" },
                allTreats
            )
            PagerCard("Notes", { activeDialog = "notesDialog" }, allNotes, true) { item, hasLine ->
                NoteItemCard(
                    item, hasLine,
                    {
                        noteToEdit = item
                        activeDialog = "notesDialog"
                    },
                    {
                        idForDelete = item.id
                        deleteAction = { nutritionViewModel.deleteNoteNutrition(item.id) }
                        activeDialog = "delete"
                    }
                )
            }
            Spacer(Modifier.height(2.dp))
        }
    }
    when (activeDialog) {
        "foodItem" -> AddFoodDialog(
            {
                activeDialog = ""
                foodItemToEdit = null
            },
            { name, description, portionSize, icon, unit, petId ->
                if (foodItemToEdit != null) {
                    nutritionViewModel.updateFoodItem(
                        foodItemToEdit!!.copy(
                            name = name,
                            description = description,
                            portionSize = portionSize,
                            icon = icon,
                            unit = unit
                        )
                    )
                    foodItemToEdit = null
                } else {
                    nutritionViewModel.addFoodItem(
                        name,
                        description,
                        portionSize,
                        unit,
                        icon,
                        petId
                    )
                }
                activeDialog = ""
            },
            petId, if (foodItemToEdit != null) "Update Food Item" else "Add Food Item",
            foodItemToEdit?.name.orEmpty(),
            foodItemToEdit?.description.orEmpty(),
            foodItemToEdit?.portionSize.orEmpty(),
            foodItemToEdit?.unit ?: "g",
            foodItemToEdit?.icon ?: R.drawable.dry_food
        )

        "allergyDialog" -> AddOtherNutritionDialog(
            { activeDialog = "" },
            { name, petId ->
                nutritionViewModel.addOtherNutrition(name, "Allergy", petId)
                activeDialog = ""
            },
            petId,
            "Allergy",
            R.drawable.allergy_icon,
            "e.g. Fish",
            true
        )

        "supplementDialog" -> AddOtherNutritionDialog(
            { activeDialog = "" },
            { name, petId ->
                nutritionViewModel.addOtherNutrition(name, "Supplement", petId)
                activeDialog = ""
            },
            petId,
            "Supplement",
            R.drawable.supplement_icon,
            "e.g. salmon oil"
        )

        "treatDialog" -> AddOtherNutritionDialog(
            { activeDialog = "" },
            { name, petId ->
                nutritionViewModel.addOtherNutrition(name, "Treat", petId)
                activeDialog = ""
            },
            petId,
            "Treat",
            R.drawable.treat_icon,
            "e.g. rabbit sausage"
        )

        "notesDialog" -> AddNoteNutritionDialog(
            {
                activeDialog = ""
                noteToEdit = null
            },
            { note, category, petId ->
                if (noteToEdit != null) {
                    nutritionViewModel.updateNoteNutrition(
                        noteToEdit!!.copy(
                            description = note,
                            category = category
                        )
                    )
                } else {
                    nutritionViewModel.addNoteNutrition(note, category, petId)
                }
                activeDialog = ""
                noteToEdit = null
            },
            petId,
            noteToEdit?.description.orEmpty(),
            noteToEdit?.category.orEmpty()
        )

        "delete" -> DeleteDialog(
            {
                activeDialog = ""
                idForDelete = null
                deleteAction = null
            }
        ) {
            deleteAction?.invoke()
            activeDialog = ""
            idForDelete = null
            deleteAction = null
        }
    }
    generatedRation?.let { ration ->
        GeneratedActivityNutritionPlan(
            { nutritionViewModel.clearGeneratedRation() },
            ration,
            {
                ration.forEach {
                    nutritionViewModel.addFoodItem(
                        it.name,
                        it.description,
                        it.portionSize,
                        it.unit,
                        it.icon,
                        petId
                    )
                }
                nutritionViewModel.clearGeneratedRation()
            },
            {nutritionViewModel.deleteAllFoodItems(petId)},
            "Nutrition"
        ) { item ->
            GeneratedItem(
                item.name,
                item.description,
                item.icon,
                item.portionSize + " " + item.unit,
                isGeneratedList = true
            )
        }
    }
}

@Composable
private fun OtherNutritionCard(
    label: String,
    onDeleteItem: (Long) -> Unit,
    onAddClick: () -> Unit,
    items: List<OtherNutrition>
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
                Icon(
                    painterResource(R.drawable.prime_plus),
                    contentDescription = "Add $label item",
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null
                    ) { onAddClick() }
                )
            }
            if (items.isNotEmpty()) {
                HorizontalDivider(
                    Modifier.fillMaxWidth(),
                    1.dp,
                    dividerColor
                )
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach {
                        OtherNutritionCardItem(it.name) { onDeleteItem(it.id) }
                    }
                }
            }
        }

    }
}

@Composable
private fun OtherNutritionCardItem(name: String, onDeleteItem: () -> Unit) {
    Card(
        Modifier.wrapContentSize(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        border = BorderStroke(1.dp, brownColor)
    ) {
        Row(
            Modifier
                .wrapContentSize()
                .padding(10.dp, 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextMaker(name, 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(4.dp))
            Icon(
                painterResource(R.drawable.cancel_small), contentDescription = "delete item",
                modifier = Modifier
                    .size(12.dp)
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) { onDeleteItem() })
        }
    }
}

@Composable
private fun NoteItemCard(
    note: Note,
    isLine: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isMenuOpened by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextMaker(
                note.description.replaceFirstChar { char -> char.uppercase() },
                12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            Row(Modifier.wrapContentWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextMaker(note.category!!, 12.sp, fontWeight = FontWeight.Normal)
                Spacer(Modifier.width(4.dp))
                Box {
                    Icon(
                        Icons.Default.MoreVert, "More options", Modifier
                            .size(18.dp)
                            .clickable {
                                isMenuOpened = true
                            })
                    DropdownMoreOptions(
                        isMenuOpened,
                        { isMenuOpened = false },
                        onEditClick,
                        onDeleteClick
                    )
                }
            }
        }
        if (isLine) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerColor)
            )
        }
    }
}