package com.example.pethelper.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.db.Pet
import com.example.pethelper.db.PetsViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MyPetScreen(petsViewModel: PetsViewModel, id: Int){
    LaunchedEffect(id) {
        petsViewModel.getPetById(id)
    }
    val pet by petsViewModel.selectedPet.collectAsState()
    Box(Modifier.fillMaxSize()
        .background(backgroundScreenColor)
        .padding(start = 25.dp, top = 76.dp, end = 25.dp)){
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextMaker("My Pet", 24.sp)
                Image(painter = painterResource(R.drawable.paw),
                    contentDescription = "paw icon")
            }
            Spacer(Modifier.height(32.dp))
            pet?.let { pet ->
                PetInfoCard(pet.photo, pet.name, pet.sex, pet.breed,
                    pet.age, pet.weight, pet.height)
            }
        }
    }
}

@Composable
fun PetInfoCard(photo: String?, name: String, gender: String,
                breed: String, age: Int?, weight: Float?, height: Float?){
    Card(Modifier.fillMaxWidth()
        .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ){
        Row(Modifier.fillMaxWidth()
            .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center){
            AsyncImage(modifier = Modifier.size(160.dp)
                .clip(CircleShape),
                contentDescription = "Pet photo",
                model = photo,
                contentScale = if (photo == null)
                    androidx.compose.ui.layout.ContentScale.Fit
                else androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(Modifier.width(5.dp))
            Column(Modifier.fillMaxHeight()
                .padding(horizontal = 14.dp)
            ){
                Row(Modifier.fillMaxWidth()){
                    TextMaker(name, 24.sp)
                    Spacer(Modifier.width(4.dp))
                    Image(painter = if (gender == "Male") painterResource(R.drawable.male)
                    else painterResource(R.drawable.female),
                        contentDescription = "Gender")
                }
                Spacer(Modifier.height(8.dp))
                TextMaker(breed, 16.sp, fontWeight = FontWeight.Normal)
                Spacer(Modifier.height(8.dp))
                AgeCard(age)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Image(painter = painterResource(R.drawable.weight_icon),
                            contentDescription = "Weight icon")
                        TextMaker(if (weight == null) "No info" else "$weight kg",
                            14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(R.drawable.height_icon),
                            contentDescription = "Height icon")
                        TextMaker(if (height == null) "No info" else "$height cm",
                            14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun AgeCard(age: Int?){
    Card(Modifier.wrapContentSize().padding(horizontal = 22.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(Color(0xFFFFE2D0)))
    {
        TextMaker(if (age == null) "No info" else "$age year(s) old",
            14.sp, Color(0xFF381B0A), FontWeight.SemiBold,
            Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun SectionButton(painter: Painter, text: String, fontSize: TextUnit){
    Card(Modifier.height(92.dp).width(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Column(verticalArrangement = Arrangement.Center) {
            Image(painter = painter, contentDescription = "Button icon")
            Spacer(Modifier.height(8.dp))
            TextMaker(text, fontSize, fontWeight = FontWeight.SemiBold)
        }
    }
}