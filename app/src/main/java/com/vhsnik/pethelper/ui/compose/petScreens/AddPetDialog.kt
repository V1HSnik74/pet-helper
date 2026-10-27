package com.vhsnik.pethelper.ui.compose.petScreens

import android.net.Uri
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.*
import com.vhsnik.pethelper.ui.viewModel.PetsViewModel

@Composable
fun AddPetDialog(
    onDismiss: () -> Unit,
    onSavePet: (name: String, breed: String, gender: String, photoUri: Uri?, animal: String) -> Unit
) {
    val petsViewModel = hiltViewModel<PetsViewModel>()
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var animal by remember { mutableStateOf("") }
    val isPhotoLoading by petsViewModel.isPhotoLoading.collectAsStateWithLifecycle()
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { photoUri = it }
    }
    DialogPattern(
        "Add New Pet",
        R.drawable.paw_upper,
        onDismiss,
        {
            onSavePet(
                name,
                breed,
                gender,
                photoUri,
                animal
            )
        },
        name.isNotBlank() && breed.isNotBlank() && gender.isNotBlank() && animal.isNotBlank() && !isPhotoLoading
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    model = if (photoUri == null) R.drawable.choose_photo_icon else photoUri,
                    contentDescription = "Choose photo",
                    contentScale = if (photoUri == null)
                        ContentScale.Fit
                    else ContentScale.Crop
                )
            }
            LabelAndTextField("Name", name, { name = it }, "e.g. Buddy")
            LabelAndTextField("Breed", breed, { breed = it }, "e.g. Poodle")
            Column(Modifier.fillMaxWidth()) {
                TextMaker("Gender", 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    GenderAnimalButton(
                        gender == "Male",
                        "Male",
                        { gender = "Male" },
                        R.drawable.male_icon,
                        Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    GenderAnimalButton(
                        gender == "Female",
                        "Female",
                        { gender = "Female" },
                        R.drawable.female_icon,
                        Modifier.weight(1f)
                    )
                }
            }
            Column(Modifier.fillMaxWidth()) {
                TextMaker("Animal", 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    GenderAnimalButton(
                        animal == "Cat",
                        "Cat",
                        { animal = "Cat" },
                        R.drawable.cat,
                        Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    GenderAnimalButton(
                        animal == "Dog",
                        "Dog",
                        { animal = "Dog" },
                        R.drawable.dog,
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GenderAnimalButton(
    isSelected: Boolean, text: String, onClick: () -> Unit, icon: Int, modifier: Modifier
) {
    Card(
        modifier
            .wrapContentHeight()
            .clickable(indication = null, interactionSource = null) {
                onClick()
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(if (isSelected) selectedColor else cardColor),
        border = BorderStroke(1.dp, if (isSelected) buttonColor else lightBrown)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(icon),
                null,
                Modifier.size(22.dp),
                if (isSelected) buttonColor else brownColor
            )
            Spacer(Modifier.width(6.dp))
            TextMaker(text, 12.sp, Color(0xFF5C5C5C))
        }
    }
}