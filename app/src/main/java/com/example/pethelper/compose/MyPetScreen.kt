package com.example.pethelper.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.db.PetsViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MyPetScreen(petsViewModel: PetsViewModel, id: Int, onCardClick: (Int) -> Unit) {
    val pet by petsViewModel.getPetById(id).collectAsState(initial = null)
    Box(
        Modifier
            .fillMaxSize()
            .background(backgroundScreenColor)
            .padding(start = 25.dp, top = 76.dp, end = 25.dp, bottom = 56.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextMaker("My Pet", 24.sp)
                Image(
                    painter = painterResource(R.drawable.paw),
                    contentDescription = "paw icon"
                )
            }
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item { Spacer(Modifier.height(32.dp)) }
                item {
                    pet?.let { pet ->
                        val birthday = pet.birthday ?: ""
                        val formatter =
                            remember { DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH) }

                        val age = if (birthday.isNotEmpty()) {
                            val period = Period.between(
                                LocalDate.parse(birthday, formatter),
                                LocalDate.now()
                            )
                            period.years.toString()
                        } else "N/A"
                        PetInfoCard(
                            pet.photo, pet.name, pet.sex, pet.breed,
                            age, pet.weight, pet.height,
                            onCardClick = { onCardClick(pet.id) }
                        )
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SectionButton(
                            painterResource(R.drawable.heart_icon),
                            "Health",
                            14.sp,
                            Modifier.weight(1f)
                        )
                        SectionButton(
                            painterResource(R.drawable.bone_icon),
                            "Nutrition",
                            14.sp,
                            Modifier.weight(1f)
                        )
                        SectionButton(
                            painterResource(R.drawable.bell_icon),
                            "Reminders",
                            12.sp,
                            Modifier.weight(1f)
                        )
                        SectionButton(
                            painterResource(R.drawable.notes_icon),
                            "Notes",
                            14.sp,
                            Modifier.weight(1f)
                        )
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
                item { Upcoming() }
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    pet?.let { pet ->
                        AboutSection(
                            pet.name, pet.about,
                            painterResource(R.drawable.paw_notes_icon)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetInfoCard(
    photo: String?, name: String, gender: String,
    breed: String, age: String, weight: Float?, height: Float?, onCardClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentDescription = "Pet photo",
                model = photo,
                contentScale = if (photo == null)
                    androidx.compose.ui.layout.ContentScale.Fit
                else androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(Modifier.width(5.dp))
            Column(
                Modifier.fillMaxHeight()
            ) {
                Row(
                    Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextMaker(name, 24.sp, modifier = Modifier.weight(1f, fill = false))
                    Spacer(Modifier.width(4.dp))
                    Image(
                        painter = if (gender == "Male") painterResource(R.drawable.male)
                        else painterResource(R.drawable.female),
                        contentDescription = "Gender"
                    )
                }
                Spacer(Modifier.height(8.dp))
                TextMaker(
                    breed,
                    16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
                AgeCard(age)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.weight_icon),
                            contentDescription = "Weight icon"
                        )
                        Spacer(Modifier.width(4.dp))
                        TextMaker(
                            if (weight == null) "N/A" else "$weight kg",
                            14.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.height_icon),
                            contentDescription = "Height icon"
                        )
                        TextMaker(
                            if (height == null) "N/A" else "$height cm",
                            14.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AgeCard(age: String) {
    Card(
        Modifier
            .wrapContentSize()
            .padding(start = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(Color(0xFFFFE2D0))
    )
    {
        TextMaker(
            if (age == "N/A") "N/A" else "$age year(s) old",
            14.sp, Color(0xFF381B0A), FontWeight.SemiBold,
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 22.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SectionButton(painter: Painter, text: String, fontSize: TextUnit, modifier: Modifier) {
    Card(
        modifier.height(92.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painter, contentDescription = "Button icon")
            Spacer(Modifier.height(8.dp))
            TextMaker(text, fontSize, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun AboutSection(
    name: String,
    about: String?,
    painter: Painter? = null,
    hasAbout: Boolean = false,
    onOpenDialog: () -> Unit = {}
) {
    Card(
        Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f, fill = false)
            ) {
                TextMaker("About $name", 16.sp)
                Spacer(Modifier.height(8.dp))
                TextMaker(
                    about ?: "No description added yet",
                    14.sp, fontWeight = FontWeight.Normal
                )
            }
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = "paw about icon"
                )
            }
            if (hasAbout)
                EditAbout(modifier = Modifier, onOpenDialog = onOpenDialog)
        }
    }
}


@Composable
fun Upcoming() {
    Card(
        Modifier
            .fillMaxWidth()
            .height(209.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextMaker("Upcoming", 14.sp)
                TextMaker("View all", 14.sp, Color(0xFF7B3A15))
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) { }
        }
    }
}