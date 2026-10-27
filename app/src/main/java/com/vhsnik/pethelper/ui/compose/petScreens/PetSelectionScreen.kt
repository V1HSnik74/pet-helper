package com.vhsnik.pethelper.ui.compose.petScreens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.Pet
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.*
import com.vhsnik.pethelper.ui.viewModel.PetsViewModel

@Composable
fun PetSelectionScreen(
    viewModel: PetsViewModel = hiltViewModel(),
    onPetClick: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.clearCurPet()
    }
    val petList by viewModel.allPets.collectAsStateWithLifecycle(initialValue = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }
    var id by remember { mutableStateOf<Int?>(null) }
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))
        TextMaker("Select Your Pet", 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(20.dp))
        TextMaker(
            "Choose a pet to view their information", 14.sp,
            smallTextColor
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
                        pet,
                        { onPetClick(pet.id) },
                        {
                            deleteDialog = true
                            id = pet.id
                        }
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
        val context = LocalContext.current
        if (showDialog) {
            AddPetDialog(
                { showDialog = false },
                { name, breed, gender, photoUri, animal ->
                    viewModel.addPet(name, breed, gender, photoUri, animal, context)
                    showDialog = false
                })
        }
        if (deleteDialog) {
            DeleteDialog({ deleteDialog = false }, {
                viewModel.deletePet(id ?: -1)
                deleteDialog = false
            })
        }
    }
}

@Composable
private fun PetCard(
    pet: Pet,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .height(96.dp)
            .combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            ),
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
                model = pet.photo ?: R.drawable.no_pet_photo,
                contentDescription = "Pet photo",
                contentScale = if (pet.photo == null)
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
                        painterResource(if (pet.animal == "Dog") R.drawable.dog else R.drawable.cat),
                        null,
                        tint = if (pet.sex == "Male") maleColor else femaleColor
                    )
                    Spacer(Modifier.width(4.dp))
                    TextMaker(pet.name, 16.sp)
                }
                Spacer(Modifier.height(8.dp))
                TextMaker(pet.breed, 12.sp, smallTextColor)
            }
        }
    }
}