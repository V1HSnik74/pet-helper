package com.example.pethelper.compose

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.db.Pet
import com.example.pethelper.db.PetsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


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
}

@Composable
fun PetProfileScreen(petsViewModel: PetsViewModel, id: Int) {
    val pet by petsViewModel.getPetById(id).collectAsState(initial = null)
    var activeDialog by remember { mutableStateOf<PetProfileDialog?>(null) }
    pet?.let { pet ->
        var name by remember(pet.id) { mutableStateOf(pet.name) }
        var breed by remember(pet.id) { mutableStateOf(pet.breed) }
        var microchip by remember(pet.id) { mutableStateOf(pet.microchipId ?: "") }
        var color by remember(pet.id) { mutableStateOf(pet.color ?: "") }
        var isNeutered by remember(pet.id) { mutableStateOf(pet.neutered ?: "") }
        var gender by remember(pet.id) { mutableStateOf(pet.sex) }
        var weight by remember(pet.id) { mutableFloatStateOf(pet.weight ?: 0.0f) }
        var height by remember(pet.id) { mutableFloatStateOf(pet.height ?: 0.0f) }
        var photo by remember(pet.id) { mutableStateOf(pet.photo ?: "") }
        var birthday by remember(pet.id) { mutableStateOf(pet.birthday ?: "") }

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        var isPhotoLoading by remember { mutableStateOf(false) }
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    isPhotoLoading = true
                    coroutineScope.launch {
                        val path = withContext(Dispatchers.IO) {
                            val source = ImageDecoder.createSource(context.contentResolver, uri)
                            val bitmap = ImageDecoder.decodeBitmap(source)
                            val directory =
                                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            val file = File(directory, "pet_${System.currentTimeMillis()}.jpg")
                            file.outputStream().use { outputStream ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            }
                            file.absolutePath
                        }
                        photo = path
                        isPhotoLoading = false
                        petsViewModel.updatePetPhoto(pet.id, photo)
                    }
                }
            }
        Column(
            Modifier
                .fillMaxSize()
                .background(backgroundScreenColor)
                .padding(bottom = 52.dp)
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
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                TextMaker(
                    "Pet Profile", 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
            )
            {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.wrapContentSize()) {
                        AsyncImage(
                            model = if (pet.photo == null || isPhotoLoading)
                                R.drawable.no_photo_profile else photo,
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape),
                            contentDescription = "Pet photo",
                            contentScale = if (pet.photo == null)
                                androidx.compose.ui.layout.ContentScale.Fit
                            else androidx.compose.ui.layout.ContentScale.Crop
                        )
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
                    Spacer(Modifier.height(20.dp))
                    Row {
                        TextMaker(name, 24.sp)
                        Spacer(Modifier.width(4.dp))
                        Image(
                            painterResource(R.drawable.pencil),
                            contentDescription = "change name",
                            modifier = Modifier.clickable(
                                onClick = { activeDialog = PetProfileDialog.Name })
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    TextMaker(
                        breed, 16.sp,
                        fontWeight = FontWeight.Normal,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(onClick = {
                            activeDialog = PetProfileDialog.Breed
                        })
                    )
                    Spacer(Modifier.height(20.dp))
                    InfoCard(
                        pet,
                        { activeDialog = PetProfileDialog.Birthday },
                        { activeDialog = PetProfileDialog.Gender },
                        { activeDialog = PetProfileDialog.Weight },
                        { activeDialog = PetProfileDialog.Height },
                        { activeDialog = PetProfileDialog.Color },
                        { activeDialog = PetProfileDialog.IsNeutered },
                        { activeDialog = PetProfileDialog.Microchip })
                    Spacer(Modifier.height(20.dp))
                    AboutSection(name, pet.about, hasAbout = true)
                    Spacer(Modifier.height(52.dp))

                    activeDialog?.let { dialog ->
                        when (dialog) {
                            is PetProfileDialog.Birthday -> UpdateBirthdayDialog(
                                onDismiss = { activeDialog = null },
                                onUpdateBirthday = {
                                    petsViewModel.updateBirthdayDate(it, pet.id)
                                    activeDialog = null
                                }, currentDate = birthday
                            )

                            is PetProfileDialog.Gender -> UpdateGenderDialog(
                                onDismiss = { activeDialog = null },
                                onUpdateGender = {
                                    petsViewModel.updateGender(pet.id, it)
                                    activeDialog = null
                                }, gender
                            )

                            is PetProfileDialog.Weight -> UpdateWeightHeightDialog(
                                { activeDialog = null },
                                {
                                    petsViewModel.updateWeight(it, pet.id)
                                    activeDialog = null
                                }, true,
                                weight.toInt(), ((weight - weight.toInt()) * 10).toInt(),
                                121, 0, 120
                            )

                            is PetProfileDialog.Height -> UpdateWeightHeightDialog(
                                { activeDialog = null },
                                {
                                    petsViewModel.updateHeight(it, pet.id)
                                    activeDialog = null
                                }, false,
                                height.toInt(), ((height - height.toInt()) * 10).toInt(),
                                120, 1, 120
                            )

                            is PetProfileDialog.Color -> UpdateColorDialog(
                                color, { activeDialog = null },
                                {
                                    petsViewModel.updateColor(it, pet.id)
                                    activeDialog = null
                                })

                            is PetProfileDialog.IsNeutered -> UpdateNeutered(
                                { activeDialog = null },
                                {
                                    petsViewModel.updateIsNeutered(it, pet.id)
                                    activeDialog = null
                                },
                                gender, isNeutered
                            )

                            is PetProfileDialog.Name -> UpdateSmallDialog(
                                onDismiss = { activeDialog = null },
                                onUpdateInfo = {
                                    petsViewModel.updateName(it, pet.id)
                                    activeDialog = null
                                },
                                painterResource(R.drawable.name_icon),
                                "Name", "e.g. Buddy", { name = it },
                                name
                            )

                            is PetProfileDialog.Breed -> UpdateSmallDialog(
                                onDismiss = { activeDialog = null },
                                onUpdateInfo = {
                                    petsViewModel.updateBreed(it, pet.id)
                                    activeDialog = null
                                },
                                painterResource(R.drawable.breed_icon),
                                "Breed", "e.g. Poodle", { breed = it }, breed
                            )

                            is PetProfileDialog.Microchip -> UpdateSmallDialog(
                                onDismiss = { activeDialog = null },
                                onUpdateInfo = {
                                    petsViewModel.updateChip(it, pet.id)
                                    activeDialog = null
                                },
                                painterResource(R.drawable.chip_icon),
                                "Microchip ID",
                                "000 000 000 000 000",
                                { microchip = it },
                                microchip,
                                "Enter 15 digits",
                                true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardComponent(
    componentName: String, info: String, painter: Painter, isLine: Boolean = true,
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
                    info, 12.sp, Color(0xFF7B3A15),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = updateFunction)
                )
                Spacer(Modifier.width(16.dp))
                Image(painter, contentDescription = "$componentName icon")
            }
        }
        if (isLine) {
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFF2F2F2)
            )
        }
    }
}

@Composable
fun InfoCard(
    pet: Pet,
    onUpdateBirthday: () -> Unit,
    onUpdateGender: () -> Unit,
    onUpdateWeight: () -> Unit,
    onUpdateHeight: () -> Unit,
    onUpdateColor: () -> Unit,
    onUpdateNeutered: () -> Unit,
    onUpdateChip: () -> Unit
) {
    val weight = if (pet.weight != null) pet.weight.toString() else "N/A"
    val height = if (pet.height != null) pet.height.toString() else "N/A"
    val gender = pet.sex
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            CardComponent(
                "Birthday",
                pet.birthday ?: "N/A",
                painterResource(R.drawable.calendar_profile),
                updateFunction = onUpdateBirthday
            )
            CardComponent(
                "Gender",
                gender,
                painterResource(R.drawable.gender_profile),
                updateFunction = onUpdateGender
            )
            CardComponent(
                "Weight",
                "$weight kg",
                painterResource(R.drawable.weight_profile),
                updateFunction = onUpdateWeight
            )
            CardComponent(
                "Height",
                "$height cm",
                painterResource(R.drawable.height_profile),
                updateFunction = onUpdateHeight
            )
            CardComponent(
                "Color",
                pet.color ?: "N/A",
                painterResource(R.drawable.color_profile),
                updateFunction = onUpdateColor
            )
            CardComponent(
                if (gender == "Male") "Neutered" else "Spayed",
                pet.neutered ?: "N/A",
                painterResource(R.drawable.neutered_profile),
                updateFunction = onUpdateNeutered
            )
            CardComponent(
                "Microchip ID",
                pet.microchipId ?: "N/A",
                painterResource(R.drawable.chip_profile),
                false,
                updateFunction = onUpdateChip
            )
        }
    }
}

@Composable
fun EditAbout(modifier: Modifier) {
    Card(
        modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(Color(0xFFFFE2D0)),
        shape = RoundedCornerShape(10.dp)
    ) {
        TextMaker(
            "Edit", 14.sp, Color(0xFF7B3A15),
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

