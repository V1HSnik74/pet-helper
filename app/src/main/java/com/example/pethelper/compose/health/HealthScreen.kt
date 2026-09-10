package com.example.pethelper.compose.health

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.ScreenTitle
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.backgroundColor
import com.example.pethelper.compose.patterns.buttonColor
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.db.entity.Pet
import com.example.pethelper.db.viewModel.PetsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class TabButtonsHealth(val icon: Int, val title: String) {
    OVERALL(R.drawable.overall_button, "Overall"),
    VACCINATIONS(R.drawable.vaccine_button, "Vaccinations"),
    MEDICATIONS(R.drawable.medicine_button, "Medications"),
    PARASITES(R.drawable.parasites_button, "Parasites"),
    CHECKUPS(R.drawable.checkups_button, "Check-ups")
}

@Composable
fun HealthScreen(petId: Int, onBackClick: () -> Unit, petsViewModel: PetsViewModel) {
    val currPet by petsViewModel.getPetById(petId).collectAsState(null)
    val tabs = TabButtonsHealth.entries
    val pagerState = rememberPagerState(0) { tabs.size }
    val scope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ScreenTitle("Health") { onBackClick() }
        Spacer(Modifier.height(20.dp))
        currPet?.let { PetCardHealth(it) }
        Spacer(Modifier.height(16.dp))
        TabButtonsRow(
            { scope.launch { pagerState.animateScrollToPage(it) } },
            pagerState.currentPage
        )
        Spacer(Modifier.height(16.dp))
        HorizontalPager(
            pagerState,
            Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (tabs[it]) {
                TabButtonsHealth.OVERALL -> {}
                TabButtonsHealth.VACCINATIONS -> {}
                TabButtonsHealth.MEDICATIONS -> {}
                TabButtonsHealth.PARASITES -> {}
                TabButtonsHealth.CHECKUPS -> {}
            }
        }
    }
}

@Composable
private fun PetCardHealth(pet: Pet) {
    val birthday = remember { pet.birthday ?: "" }
    val age = remember {
        if (birthday.isNotEmpty()) {
            Period.between(
                LocalDate.parse(
                    birthday,
                    DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
                ),
                LocalDate.now()
            ).years
        } else "N/A"
    }
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = pet.photo,
                contentDescription = "Pet Photo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = if (pet.photo == null) ContentScale.Fit
                else ContentScale.Crop
            )
            Spacer(Modifier.width(20.dp))
            Column(Modifier.wrapContentSize()) {
                Row(Modifier.fillMaxWidth()) {
                    TextMaker(pet.name, 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(2.dp))
                    Image(
                        if (pet.sex == "Male") painterResource(R.drawable.male) else painterResource(
                            R.drawable.female
                        ),
                        "Gender Icon",
                        Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                TextMaker(pet.breed, 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PetCardChip(
                        if (age == 1) "$age year old" else "$age years old"
                    )
                    PetCardChip("${pet.weight} kg")
                    PetCardChip("${pet.height} cm")
                }
            }
        }
    }
}

@Composable
private fun PetCardChip(text: String) {
    Card(
        Modifier.wrapContentSize(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(Color(0xFFFFE2D0))
    ) {
        TextMaker(
            text,
            12.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(8.dp, 4.dp)
        )
    }
}

@Composable
private fun TabButton(isSelected: Boolean, icon: Int, label: String, onClick: () -> Unit) {
    Column(
        Modifier
            .wrapContentSize()
            .clickable(indication = null, interactionSource = null, onClick = onClick)
    ) {
        Icon(
            painterResource(icon), "Tab Icon",
            tint = if (isSelected) Color(0xFF4B332E) else buttonColor
        )
        Spacer(Modifier.height(2.dp))
        TextMaker(label, 10.sp, if (isSelected) Color(0xFF4B332E) else buttonColor)
        Spacer(Modifier.height(8.dp))
        if (isSelected) HorizontalDivider(thickness = 1.dp, color = buttonColor)
    }
}

@Composable
private fun TabButtonsRow(onSwipe: (Int) -> Unit, selectedTabIndex: Int) {
    Box(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TabButtonsHealth.entries.forEachIndexed { index, tab ->
                TabButton(selectedTabIndex == index, tab.icon, tab.title) { onSwipe(index) }
            }
        }
        HorizontalDivider(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            color = Color(0xFFE8E8E8), thickness = 1.dp
        )
    }
}