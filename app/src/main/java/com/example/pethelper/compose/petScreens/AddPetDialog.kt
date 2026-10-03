package com.example.pethelper.compose.petScreens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.brownColor
import com.example.pethelper.compose.patterns.buttonColor
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.compose.patterns.selectedColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

val textFieldContainerColor = Color(0xFFFFFCF9)
val textFieldCursorColor = Color(0xFFD9BDAD)

@Composable
fun AddPetDialog(
    onDismiss: () -> Unit,
    onSavePet: (name: String, breed: String, gender: String, photo: String?, animal: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf<String?>(null) }
    var uriPreview by remember { mutableStateOf<Uri?>(null) }
    var animal by remember { mutableStateOf("") }

    var isPhotoLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uriPreview = uri
            isPhotoLoading = true
            coroutineScope.launch {
                val path = withContext(Dispatchers.IO) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val file = File(directory, "pet_${System.currentTimeMillis()}.jpg")
                    file.outputStream().use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    file.absolutePath
                }
                photo = path
                isPhotoLoading = false
            }

        }
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
                photo,
                animal
            )
        },
        name.isNotBlank() && breed.isNotBlank() && gender.isNotBlank() && animal.isNotBlank()
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
                    model = if (uriPreview == null) R.drawable.choose_photo_icon else uriPreview,
                    contentDescription = "Choose photo",
                    contentScale = if (uriPreview == null)
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
                        animal == "cat",
                        "Cat",
                        { animal = "cat" },
                        R.drawable.cat,
                        Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    GenderAnimalButton(
                        animal == "dog",
                        "Dog",
                        { animal = "dog" },
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
        border = BorderStroke(1.dp, if (isSelected) buttonColor else textFieldCursorColor)
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