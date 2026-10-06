package com.example.pethelper.compose.nutrition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import com.example.pethelper.db.viewModel.AllergyViewModel
import com.example.pethelper.db.viewModel.FoodItemViewModel
import com.example.pethelper.db.entity.NoteNutrition
import com.example.pethelper.db.viewModel.NoteNutritionViewModel
import com.example.pethelper.db.viewModel.PetsViewModel
import com.example.pethelper.db.viewModel.SupplementViewModel
import com.example.pethelper.db.viewModel.TreatViewModel
import com.example.pethelper.compose.patterns.GenerateButton
import com.example.pethelper.compose.patterns.GeneratedActivityNutritionPlan
import com.example.pethelper.compose.patterns.GeneratedItem
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.ui.theme.backgroundColor
import com.example.pethelper.ui.theme.brownColor
import com.example.pethelper.ui.theme.cardColor
import com.example.pethelper.ui.theme.dividerColor

@Composable
fun NutritionScreen(
    petId: Int,
    foodItemViewModel: FoodItemViewModel,
    supplementViewModel: SupplementViewModel,
    treatViewModel: TreatViewModel,
    noteNutritionViewModel: NoteNutritionViewModel,
    allergyViewModel: AllergyViewModel,
    onBackClick: () -> Unit,
    petViewModel: PetsViewModel
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
    val curPet = petViewModel.currPet

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
                    item.portionSize + " " + item.unit
                )
                if (hasNote)
                    HorizontalDivider(
                        Modifier.fillMaxWidth(),
                        1.dp,
                        dividerColor
                    )
            }
            curPet?.let{GenerateButton(
                {
                    foodItemViewModel.generateRation(
                        it.breed,
                        it.weight,
                        allAllergies,
                        it.birthday,
                        allSupplements,
                        it.neutered,
                        petId,
                        it.animal
                    )
                },
                "Nutrition",
                foodItemViewModel.isGenerating
            )}
            OtherNutritionCard(
                "Allergies & Sensitives",
                {
                    allAllergies.forEach {
                        OtherNutritionCardItem(it.name) { allergyViewModel.deleteAllergy(it.id) }
                    }
                },
                { activeDialog = "allergyDialog" },
                allAllergies.isNotEmpty()
            )
            OtherNutritionCard(
                "Supplements", {
                    allSupplements.forEach {
                        OtherNutritionCardItem(it.name) { supplementViewModel.deleteSupplement(it.id) }
                    }
                }, { activeDialog = "supplementDialog" },
                allSupplements.isNotEmpty()
            )
            OtherNutritionCard(
                "Treats", {
                    allTreats.forEach {
                        OtherNutritionCardItem(it.name) { treatViewModel.deleteTreat(it.id) }
                    }
                }, { activeDialog = "treatDialog" },
                allTreats.isNotEmpty()
            )
            PagerCard("Notes", { activeDialog = "notesDialog" }, allNotes, true) {
                item, hasLine ->
                NoteItemCard(item, hasLine)
            }
            Spacer(Modifier.height(2.dp))
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
    foodItemViewModel.generatedRation?.let { ration ->
        GeneratedActivityNutritionPlan(
            { foodItemViewModel.clearGeneratedRation() },
            ration,
            {
                ration.forEach {
                    foodItemViewModel.addFoodItem(
                        it.name,
                        it.description,
                        it.portionSize,
                        it.unit,
                        it.icon,
                        petId
                    )
                }
                foodItemViewModel.clearGeneratedRation()
            },
            "Nutrition"
        ) { item ->
            GeneratedItem(
                item.name,
                item.description,
                item.icon,
                item.portionSize + " " + item.unit
            )
        }
    }
}

@Composable
private fun OtherNutritionCard(
    label: String,
    cardFunc: @Composable () -> Unit,
    onAddClick: () -> Unit,
    notEmpty: Boolean
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
            if (notEmpty) {
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
                    cardFunc()
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
                    .background(dividerColor)
            )
        }
    }
}