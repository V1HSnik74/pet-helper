package com.example.pethelper.ui.compose.petScreens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.ui.compose.patterns.TextMaker
import com.example.pethelper.ui.theme.*
import com.example.pethelper.ui.viewModel.*


sealed interface PetProfileDialog {
    data object Birthday : PetProfileDialog
    data object Gender : PetProfileDialog
    data object Weight : PetProfileDialog
    data object Height : PetProfileDialog
    data object Color : PetProfileDialog
    data object IsNeutered : PetProfileDialog
    data object Microchip : PetProfileDialog
    data object Name : PetProfileDialog
    data object Breed : PetProfileDialog
    data object Animal : PetProfileDialog
    data object About : PetProfileDialog
}

@Composable
fun PetProfileScreen(
    petsViewModel: PetsViewModel = hiltViewModel(),
    id: Int,
    onBackClick: (Int) -> Unit
) {
    LaunchedEffect(id) {
        petsViewModel.setCurPet(id)
    }
    val curPet = petsViewModel.currPet
    var activeDialog by remember { mutableStateOf<PetProfileDialog?>(null) }
    val context = LocalContext.current
    val isPhotoLoading by petsViewModel.isPhotoLoading.collectAsStateWithLifecycle()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                curPet?.let { pet ->
                    petsViewModel.savePetPhoto(context, it, pet.id)
                }
            }
        }
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(22.dp, 64.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.back),
                contentDescription = "Back button",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(12.dp, 24.dp)
                    .clickable(indication = null, interactionSource = null) {
                        onBackClick(id)
                    }
            )
            TextMaker(
                "Pet Profile", 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(Modifier.height(20.dp))
        curPet?.let { curPet ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(Modifier.wrapContentSize()) {
                    val photoModifier = remember {
                        Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                    }
                    if (curPet.photo == null || isPhotoLoading) {
                        Image(
                            painterResource(R.drawable.no_photo_profile),
                            modifier = photoModifier,
                            contentDescription = "Pet photo",
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        AsyncImage(
                            model = curPet.photo,
                            modifier = photoModifier,
                            contentDescription = "Pet photo",
                            contentScale = ContentScale.Crop
                        )
                    }
                    Image(
                        painterResource(R.drawable.photo_picker),
                        contentDescription = "Change photo",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                launcher.launch("image/*")
                            }
                    )
                }
                Column(
                    Modifier.wrapContentWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextMaker(
                        curPet.name, 20.sp, textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { activeDialog = PetProfileDialog.Name })
                    Spacer(Modifier.height(4.dp))
                    TextMaker(
                        curPet.breed, 14.sp,
                        fontWeight = FontWeight.Normal,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(onClick = {
                            activeDialog = PetProfileDialog.Breed
                        })
                    )
                }
                InfoCard(curPet) { activeDialog = it }
                AboutSection(
                    curPet, hasAbout = true,
                    onOpenDialog = { activeDialog = PetProfileDialog.About })
                Spacer(Modifier.height(2.dp))
            }
            activeDialog?.let { dialog ->
                when (dialog) {
                    is PetProfileDialog.Birthday -> UpdateBirthdayDialog(
                        onDismiss = { activeDialog = null },
                        onUpdateBirthday = {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(birthday = it) }
                            activeDialog = null
                        },
                        currentDate = if (curPet.birthday.contains("N/A")) "" else curPet.birthday
                    )

                    is PetProfileDialog.Gender -> UpdateGenderAnimalDialog(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(sex = it) }
                            activeDialog = null
                        }, curPet.gender,
                        "Gender",
                        R.drawable.gender_icon,
                        mapOf(
                            "Male" to R.drawable.male_icon,
                            "Female" to R.drawable.female_dialog
                        )
                    )

                    is PetProfileDialog.Weight -> UpdateWeightHeightDialog(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(weight = it) }
                            activeDialog = null
                        }, true,
                        curPet.weightFloat.toInt(),
                        ((curPet.weightFloat - curPet.weightFloat.toInt()) * 10).toInt(),
                        121, 0, 120
                    )

                    is PetProfileDialog.Height -> UpdateWeightHeightDialog(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(height = it) }
                            activeDialog = null
                        }, false,
                        curPet.heightFloat.toInt(),
                        ((curPet.heightFloat - curPet.heightFloat.toInt()) * 10).toInt(),
                        120, 1, 120
                    )

                    is PetProfileDialog.Color -> UpdateColorDialog(
                        if (curPet.color.contains("N/A")) "" else curPet.color,
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(color = it) }
                            activeDialog = null
                        })

                    is PetProfileDialog.IsNeutered -> UpdateNeutered(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(neutered = it) }
                            activeDialog = null
                        },
                        curPet.gender,
                        if (curPet.neutered.contains("N/A")) "" else curPet.neutered
                    )

                    is PetProfileDialog.Name -> EditNameBreedNoteDialog(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(name = it) }
                            activeDialog = null
                        },
                        "Name",
                        "e.g. Buddy",
                        curPet.name,
                        R.drawable.paw_upper
                    )

                    is PetProfileDialog.Breed -> EditNameBreedNoteDialog(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(breed = it) }
                            activeDialog = null
                        },
                        "Breed",
                        "e.g. Poodle",
                        curPet.breed,
                        R.drawable.breed_icon
                    )

                    is PetProfileDialog.Microchip -> EditMicrochipIdDialog(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(microchipId = it) }
                            activeDialog = null
                        },
                        if (curPet.chipId.contains("N/A")) "" else curPet.chipId
                    )

                    is PetProfileDialog.Animal -> UpdateGenderAnimalDialog(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id)
                            { pet -> pet.copy(animal = it) }
                            activeDialog = null
                        },
                        curPet.animal,
                        "Animal",
                        R.drawable.name_icon,
                        mapOf("Dog" to R.drawable.dog, "Cat" to R.drawable.cat)
                    )

                    is PetProfileDialog.About -> EditNameBreedNoteDialog(
                        { activeDialog = null },
                        {
                            petsViewModel.updatePetField(curPet.id) { pet ->
                                pet.copy(about = it)
                            }
                            activeDialog = null
                        },
                        "About ${curPet.name}",
                        "Buddy likes swimming...",
                        if (curPet.about == "No description added yet") "" else curPet.about,
                        R.drawable.note_icon,
                        true
                    )
                }
            }
        }

    }
}


@Composable
fun CardComponent(
    componentName: String, info: String, icon: Int, isLine: Boolean = true,
    updateFunction: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextMaker(componentName, 12.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextMaker(
                    info, 12.sp, petProfileIconColor,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = updateFunction)
                )
                Spacer(Modifier.width(16.dp))
                Icon(painterResource(icon), null, Modifier.size(18.dp), tint = petProfileIconColor)
            }
        }
        if (isLine) {
            HorizontalDivider(
                thickness = 1.dp,
                color = dividerColor
            )
        }
    }
}

data class InfoCardComponents(
    val dialog: PetProfileDialog,
    val label: String,
    val value: String,
    val icon: Int
)

@Composable
fun InfoCard(
    pet: PetFields,
    onOpenDialog: (PetProfileDialog) -> Unit
) {
    val items = listOf(
        InfoCardComponents(
            PetProfileDialog.Birthday,
            "Birthday",
            pet.birthday,
            R.drawable.calendar_profile
        ),
        InfoCardComponents(
            PetProfileDialog.Gender,
            "Gender",
            pet.gender,
            R.drawable.gender_profile
        ),
        InfoCardComponents(
            PetProfileDialog.Weight,
            "Weight",
            pet.weight,
            R.drawable.weight_profile
        ),
        InfoCardComponents(
            PetProfileDialog.Height,
            "Height",
            pet.height,
            R.drawable.height_profile
        ),
        InfoCardComponents(
            PetProfileDialog.Color,
            "Color",
            pet.color,
            R.drawable.color_profile
        ),
        InfoCardComponents(
            PetProfileDialog.IsNeutered,
            if (pet.gender == "Male") "Neutered" else "Spayed",
            pet.neutered,
            R.drawable.neutered_profile
        ),
        InfoCardComponents(
            PetProfileDialog.Microchip,
            "Microchip ID",
            pet.chipId,
            R.drawable.chip_profile
        ),
        InfoCardComponents(
            PetProfileDialog.Animal,
            "Animal",
            pet.animal,
            if (pet.animal == "Dog") R.drawable.dog else R.drawable.cat
        )
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            items.forEachIndexed { index, it ->
                CardComponent(
                    it.label, it.value, it.icon,
                    index != items.lastIndex,
                ) { onOpenDialog(it.dialog) }
            }
        }
    }
}

@Composable
fun EditAbout(modifier: Modifier, onOpenDialog: () -> Unit) {
    Card(
        modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(Color(0xFFFFE2D0)),
        shape = RoundedCornerShape(10.dp)
    ) {
        TextMaker(
            "Edit", 12.sp, petProfileIconColor, FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .align(Alignment.CenterHorizontally)
                .clickable(
                    interactionSource = null, indication = null,
                    onClick = onOpenDialog
                )
        )
    }
}

