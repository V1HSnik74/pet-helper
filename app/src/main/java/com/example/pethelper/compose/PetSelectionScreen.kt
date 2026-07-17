package com.example.pethelper.compose


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.db.PetsViewModel

@Composable
fun PetSelectionScreen(
    viewModel: PetsViewModel = viewModel()
){
    val petList by viewModel.allPets.collectAsState(initial = emptyList())
    var selectedPetId by remember { mutableStateOf<Int?>(null) }
    var showDialog by remember {mutableStateOf(false)}

    Box(modifier = Modifier.fillMaxSize().background(backgroundScreenColor)
        .padding(top = 76.dp, start = 26.dp, end = 26.dp, bottom = 160.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                TextMaker("Select Your Pet",24.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(16.dp))
                Image(painter = painterResource(R.drawable.paw),
                    contentDescription = "paw icon",
                    modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.height(24.dp))
            TextMaker("Choose a pet to view their information",14.sp,
                Color(0xFFB0A8A3), modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(24.dp))
            if (petList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    items(petList) {
                        pet -> PetCard(selectedPetId == pet.id,
                        {selectedPetId = pet.id}, pet.name, pet.breed, pet.photo)
                    }
                }
            }
            else {
                TextMaker("No pets added yet", 24.sp,
                    modifier = Modifier.weight(1f).align(Alignment.CenterHorizontally))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {showDialog = true},
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor))
            {
                TextMaker("+ Add New Pet", 16.sp, Color.White)
            }
            if (showDialog) {
                AddPetDialog({showDialog = false},
                    {name, breed, gender, photo -> viewModel.addPet(name, breed, gender, photo)
                    showDialog = false})
            }
        }

    }
}

@Composable
private fun PetCard(isSelected: Boolean,onClick: () -> Unit, name: String, breed: String, photo: String?){
    val interactionSource = remember { MutableInteractionSource() }
    Card(modifier = Modifier.fillMaxWidth()
        .height(108.dp)
        .clickable(interactionSource = interactionSource,
            indication = null) {onClick()},
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = 1.dp,
            color = if (isSelected) buttonColor else Color.Transparent
        ),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) selectedColor else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            AsyncImage(
                modifier = Modifier.size(76.dp).clip(CircleShape),
                model = photo ?: R.drawable.no_pet_photo,
                contentDescription = "Pet photo",
                contentScale = if (photo == null) {
                    androidx.compose.ui.layout.ContentScale.Fit
                }
                else {
                    androidx.compose.ui.layout.ContentScale.Crop
                }
            )
            Spacer(Modifier.width(24.dp))
            Column(Modifier.weight(1f)){
                TextMaker(name, 18.sp)
                Spacer(Modifier.height(8.dp))
                TextMaker(breed, 14.sp, smallTextColor)
            }
            if (isSelected){
                Image(contentDescription = "Selected pet",
                    painter = painterResource(R.drawable.selected),
                    alignment = Alignment.TopEnd)
            }
        }
    }
}