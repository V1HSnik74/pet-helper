package com.example.pethelper.compose.petScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.ButtonMaker
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.backgroundScreenColor
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.compose.patterns.smallTextColor
import com.example.pethelper.db.viewModel.PetsViewModel

@Composable
fun PetSelectionScreen(
    viewModel: PetsViewModel = viewModel(),
    onPetClick: (Int) -> Unit
) {
    val petList by viewModel.allPets.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundScreenColor)
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))
        TextMaker("Select Your Pet", 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(20.dp))
        TextMaker(
            "Choose a pet to view their information", 14.sp,
            Color(0xFFB0A8A3)
        )
        Spacer(Modifier.height(16.dp))
        if (petList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 20.dp, top = 1.dp)
            ) {
                items(petList) { pet ->
                    PetCard(
                        { onPetClick(pet.id) }, pet.name, pet.breed, pet.photo, pet.sex, pet.animal
                    )
                }
            }
        } else {
            TextMaker(
                "No pets added yet", 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ButtonMaker("Add New Pet", {
            showDialog = true
        })
        Spacer(Modifier.height(50.dp))
        if (showDialog) {
            AddPetDialog(
                { showDialog = false },
                { name, breed, gender, photo, animal ->
                    viewModel.addPet(name, breed, gender, photo, animal)
                    showDialog = false
                })
        }
    }
}

@Composable
private fun PetCard(
    onClick: () -> Unit,
    name: String,
    breed: String,
    photo: String?,
    gender: String,
    animal: String
) {
    Card(
        Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(
                interactionSource = null,
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                model = photo ?: R.drawable.no_pet_photo,
                contentDescription = "Pet photo",
                contentScale = if (photo == null)
                    ContentScale.Fit
                else ContentScale.Crop
            )
            Spacer(Modifier.width(24.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(if (animal == "dog") R.drawable.dog else R.drawable.cat),
                        null,
                        tint = if (gender == "Male") Color(0xFF0EA2ED) else Color(0xFFFF409C)
                    )
                    Spacer(Modifier.width(4.dp))
                    TextMaker(name, 16.sp)
                }
                Spacer(Modifier.height(8.dp))
                TextMaker(breed, 12.sp, smallTextColor)
            }
        }
    }
}