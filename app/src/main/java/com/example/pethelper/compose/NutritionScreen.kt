package com.example.pethelper.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.pethelper.R
import com.example.pethelper.db.AllergyViewModel
import com.example.pethelper.db.FoodItem
import com.example.pethelper.db.FoodItemViewModel
import com.example.pethelper.db.NoteNutrition
import com.example.pethelper.db.NoteNutritionViewModel
import com.example.pethelper.db.SupplementViewModel
import com.example.pethelper.db.TreatViewModel

@Composable
fun NutritionScreen(
    petId: Int,
    foodItemViewModel: FoodItemViewModel,
    supplementViewModel: SupplementViewModel,
    treatViewModel: TreatViewModel,
    noteNutritionViewModel: NoteNutritionViewModel,
    allergyViewModel: AllergyViewModel,
    onBackClick: () -> Unit
) {
    val allFoodItems by remember(petId) {
        foodItemViewModel.allFoodItemsByPet(petId)
    }.collectAsState(emptyList())
    val allAllergies by remember(petId) {
        allergyViewModel.getAllAllergiesByPet(petId)
    }.collectAsState(emptyList())
    val allSupplements by remember(petId) {
        supplementViewModel.getAllSupplementsByPet(petId)
    }.collectAsState(emptyList())
    val allTreats by remember(petId) {
        treatViewModel.getAllTreatsByPet(petId)
    }.collectAsState(emptyList())
    val allNotes by remember(petId) {
        noteNutritionViewModel.getAllNoteNutritionByPet(petId)
    }.collectAsState(emptyList())
    var activeDialog by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp),
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

        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextMaker(
                "Monitor your pet's diet including allergies, supplements and treats!",
                14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(20.dp))
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
                        TextMaker("Food & Portion", 12.sp, fontWeight = FontWeight.SemiBold)
                        Icon(
                            painterResource(R.drawable.prime_plus),
                            contentDescription = "Add Food Item",
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = null
                            ) {
                                activeDialog = "foodItem"
                            }
                        )
                    }
                    HorizontalDivider(
                        Modifier.fillMaxWidth(),
                        1.dp,
                        Color(0xFFF2F2F2)
                    )
                    if (allFoodItems.isNotEmpty()) {
                        val pages = remember(allFoodItems) { allFoodItems.chunked(4) }
                        val pagerState = rememberPagerState(pageCount = { pages.size })
                        HorizontalPager(
                            pagerState,
                            Modifier
                                .fillMaxWidth()
                                .height(272.dp),
                            verticalAlignment = Alignment.Top
                        ) { currentPage ->
                            val pageItems = pages[currentPage]
                            Column(Modifier.fillMaxWidth()) {
                                pageItems.forEachIndexed { index, foodItem ->
                                    FoodItemCard(foodItem, !(pageItems.size == 4 && index == 3))
                                }

                            }
                        }
                    }
                }

            }
            Spacer(Modifier.height(20.dp))
            OtherNutritionCard(
                "Allergies & Sensitives", onAddClick = { activeDialog = "allergyDialog" },
                cardFunc = {
                    allAllergies.forEach {
                        OtherNutritionCardItem(it.name) { allergyViewModel.deleteAllergy(it.id) }
                    }
                })
            Spacer(Modifier.height(20.dp))
            OtherNutritionCard("Supplements", cardFunc = {
                allSupplements.forEach {
                    OtherNutritionCardItem(it.name) { supplementViewModel.deleteSupplement(it.id) }
                }
            }, onAddClick = { activeDialog = "supplementDialog" })
            Spacer(Modifier.height(20.dp))
            OtherNutritionCard("Treats", cardFunc = {
                allTreats.forEach {
                    OtherNutritionCardItem(it.name) { treatViewModel.deleteTreat(it.id) }
                }
            }, onAddClick = { activeDialog = "treatDialog" })
            Spacer(Modifier.height(20.dp))
            NotesCard(allNotes) { activeDialog = "notesDialog" }
            Spacer(Modifier.height(30.dp))
        }
    }
    when (activeDialog) {
        "foodItem" -> AddFoodDialog(
            { activeDialog = "" }, { name, description, portionSize, icon, unit, petId ->
                foodItemViewModel.addFoodItem(name, description, portionSize, unit, icon, petId)
                activeDialog = ""
            }, petId
        )

        "allergyDialog" -> AddAllergyDialog(
            { activeDialog = "" },
            { name, petId ->
                allergyViewModel.addAllergy(name, petId)
                activeDialog = ""
            },
            petId
        )

        "supplementDialog" -> AddSupplementTreatDialog(
            { activeDialog = "" },
            { name, petId ->
                supplementViewModel.addSupplement(name, petId)
                activeDialog = ""
            },
            "Supplements",
            R.drawable.supplement_icon,
            "e.g. salmon oil",
            petId
        )

        "treatDialog" -> AddSupplementTreatDialog(
            { activeDialog = "" },
            { name, petId ->
                treatViewModel.addTreat(name, petId)
                activeDialog = ""
            },
            "Treats",
            R.drawable.treat_icon,
            "e.g. rabbit sausage",
            petId
        )

        "notesDialog" -> AddNoteNutritionDialog(
            { activeDialog = "" },
            { note, category, petId ->
                noteNutritionViewModel.addNoteNutrition(note, category, petId)
                activeDialog = ""
            },
            petId
        )
    }
}

@Composable
private fun FoodItemCard(foodItem: FoodItem, isLine: Boolean = true) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .padding(12.dp, 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(painterResource(foodItem.icon), contentDescription = "food item icon")
                Spacer(Modifier.width(10.dp))
                Column(
                    Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    TextMaker(foodItem.name, 12.sp, fontWeight = FontWeight.SemiBold)
                    if (foodItem.description.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        TextMaker(foodItem.description, 10.sp, fontWeight = FontWeight.Normal)
                    }
                }
            }
            Row(
                Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                PortionSizeCard(foodItem.portionSize, foodItem.unit)
                Spacer(Modifier.width(10.dp))
                Icon(
                    painterResource(R.drawable.pen_notes),
                    tint = Color(0xFFAF8268),
                    modifier = Modifier.size(18.dp),
                    contentDescription = "edit food item"
                )
            }
        }
        if (isLine) {
            HorizontalDivider(
                Modifier.fillMaxWidth(),
                1.dp,
                Color(0xFFF2F2F2)
            )
        }
    }
}

@Composable
private fun PortionSizeCard(portionSize: String, unit: String) {
    Card(
        Modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(cardColor),
        border = BorderStroke(1.dp, Color(0xFFAF8268)),
        shape = RoundedCornerShape(5.dp)
    ) {
        TextMaker(
            "$portionSize $unit", 10.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp, 2.dp)
        )
    }
}

@Composable
private fun OtherNutritionCard(
    label: String,
    cardFunc: @Composable () -> Unit,
    onAddClick: () -> Unit
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
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF2F2F2))
            )
            FlowRow(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(12.dp, 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cardFunc()
            }
        }

    }
}

@Composable
private fun OtherNutritionCardItem(name: String, onDeleteItem: () -> Unit) {
    Card(
        Modifier.wrapContentSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(selectedColor)
    ) {
        Row(
            Modifier
                .wrapContentSize()
                .padding(10.dp, 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextMaker(name, 12.sp, fontWeight = FontWeight.Normal)
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
private fun NotesCard(allNotes: List<NoteNutrition>, onAddNote: () -> Unit) {
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
                TextMaker("Notes", 12.sp, fontWeight = FontWeight.SemiBold)
                Icon(
                    painterResource(R.drawable.prime_plus),
                    contentDescription = "Add note item",
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null
                    ) { onAddNote() }
                )
            }
            HorizontalDivider(
                Modifier.fillMaxWidth(),
                1.dp,
                Color(0xFFF2F2F2)
            )
            if (allNotes.isNotEmpty()) {
                val pages = remember(allNotes) { allNotes.chunked(5) }
                val pagerState = rememberPagerState(pageCount = { pages.size })
                HorizontalPager(
                    pagerState,
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) { currentPage ->
                    val pageItems = pages[currentPage]
                    Column(Modifier.fillMaxWidth().height(225.dp)) {
                        pageItems.forEachIndexed { index, note ->
                            NoteItemCard(note, !(pageItems.size == 5 && index == 4))
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun NoteItemCard(note: NoteNutrition, isLine: Boolean) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextMaker(
                note.note.replaceFirstChar { char -> char.uppercase() },
                12.sp,
                fontWeight = FontWeight.Normal
            )
            TextMaker(note.category, 12.sp, fontWeight = FontWeight.Normal)
        }
        if (isLine) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF2F2F2))
            )
        }
    }
}