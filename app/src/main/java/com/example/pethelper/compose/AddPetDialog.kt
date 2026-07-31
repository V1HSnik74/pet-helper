package com.example.pethelper.compose
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pethelper.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

val genderTextColor = Color(0xFF5C5C5C)
val textFieldContainerColor = Color(0xFFFFFCF9)
val textFieldCursorColor = Color(0xFFD9BDAD)
@Composable
fun AddPetDialog(onDismiss: () -> Unit,
                 onSavePet: (name: String, breed: String, gender: String, photo: String?) -> Unit){
    var name by remember { mutableStateOf("") }
    var breed by remember {mutableStateOf("")}
    var gender by remember {mutableStateOf("Male")}
    var photo by remember {mutableStateOf<String?>(null)}
    var uriPreview by remember{mutableStateOf<Uri?>(null)}

    var isPhotoLoading by remember {mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            uri -> uri?.let{
        uriPreview = uri
        isPhotoLoading = true
        coroutineScope.launch {
            val path = withContext(Dispatchers.IO){
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ){
        Card(
            modifier = Modifier.wrapContentHeight().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = onDismiss){
                    Image(painter = painterResource(R.drawable.cancel), contentDescription = "Cancel",
                        modifier = Modifier.size(28.dp))
                }
            }
            Column(Modifier.fillMaxWidth()
                .padding(horizontal = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(R.drawable.paw_upper),
                        contentDescription = "Upper paw", Modifier.size(48.dp))
                }
                Spacer(Modifier.height(16.dp))
                TextMaker("Add New Pet", 20.sp)
                Spacer(Modifier.height(8.dp))
                TextMaker("Tell us about your pet", 12.sp, smallTextColor)
                Spacer(Modifier.height(16.dp))
                TextMaker("Photo", 12.sp, modifier = Modifier.align(Alignment.Start))
                AsyncImage(modifier = Modifier.align(Alignment.CenterHorizontally)
                    .size(100.dp).clip(CircleShape).clickable{launcher.launch("image/*")},
                        model = if (uriPreview == null) R.drawable.choose_photo_icon else uriPreview,
                        contentDescription = "Choose photo",
                        contentScale = if (uriPreview == null) {
                            androidx.compose.ui.layout.ContentScale.Fit
                        }
                else {
                    androidx.compose.ui.layout.ContentScale.Crop
                })
                Spacer(Modifier.height(8.dp))
                TextMaker("JPG, PNG", fontSize = 12.sp, color = smallTextColor)
                Spacer(Modifier.height(16.dp))
                TextMaker("Name", 12.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(Modifier.height(8.dp))
                OutlinedTextFieldsMaker(name, {name = it},
                    "e.g. Buddy", R.drawable.tag_icon)
                Spacer(Modifier.height(16.dp))
                TextMaker("Breed", 12.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(Modifier.height(8.dp))
                OutlinedTextFieldsMaker(breed, {breed = it},
                    "e.g. Poodle", R.drawable.little_paw)
                Spacer(Modifier.height(16.dp))
                TextMaker("Gender", 12.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)){
                        GenderButton("Male", R.drawable.male_icon,
                            (gender == "Male"), {gender = "Male"},
                            modifier = Modifier.weight(1f))
                        GenderButton("Female", R.drawable.female_icon,
                            (gender == "Female"), {gender = "Female"},
                            modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { onSavePet(name, breed, gender, photo) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(25.dp),
                    enabled = !isPhotoLoading)
                {
                    TextMaker("Save Pet", 14.sp, Color.White)
                }
            }
        }
    }
}

@Composable
private fun GenderButton(selectedGender: String, icon: Int, isSelected: Boolean, onClick: () -> Unit,
                 modifier: Modifier = Modifier){
        Card(
            modifier = modifier.height(40.dp)
                .clickable(interactionSource = null,
                    indication = null){onClick()},
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if(isSelected) buttonColor else Color(0xFFD9BDAD)),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) Color(0xFFFDF1ED) else Color(0xFFFFFCF9))
        ) {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "$selectedGender icon"
                )
                Spacer(Modifier.width(8.dp))
                TextMaker(selectedGender, 12.sp, genderTextColor)
            }
        }
}

@Composable
private fun OutlinedTextFieldsMaker(value: String, onValueChange: (String) -> Unit, text: String, icon: Int){
    OutlinedTextField(value = value, onValueChange = onValueChange,
        placeholder = {
            TextMaker(text, 12.sp, smallTextColor)
        },
        leadingIcon = {
            Image(
                painter = painterResource(icon),
                contentDescription = "icon",
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = textFieldContainerColor,
            unfocusedContainerColor = textFieldContainerColor,
            focusedBorderColor = textFieldCursorColor,
            unfocusedBorderColor = textFieldCursorColor,
            cursorColor = textFieldCursorColor
        )
    )
}
