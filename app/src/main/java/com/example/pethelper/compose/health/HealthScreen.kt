package com.example.pethelper.compose.health

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.ScreenTitle
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.backgroundColor
import com.example.pethelper.compose.patterns.buttonColor
import com.example.pethelper.compose.petScreens.PetInfoCard
import com.example.pethelper.db.viewModel.CheckUpViewModel
import com.example.pethelper.db.viewModel.MedicineViewModel
import com.example.pethelper.db.viewModel.NoteHealthViewModel
import com.example.pethelper.db.viewModel.PetsViewModel
import com.example.pethelper.db.viewModel.PreventionViewModel
import com.example.pethelper.db.viewModel.VaccineViewModel
import kotlinx.coroutines.launch

enum class TabButtonsHealth(val icon: Int, val title: String) {
    OVERALL(R.drawable.overall_button, "Overall"),
    MEDICATIONS(R.drawable.medicine_button, "Medications"),
    PARASITES(R.drawable.parasites_button, "Parasites"),
    VACCINATIONS(R.drawable.vaccine_button, "Vaccinations"),
    CHECKUPS(R.drawable.checkups_button, "Check-ups")
}

@Composable
fun HealthScreen(
    petId: Int,
    onBackClick: () -> Unit,
    petsViewModel: PetsViewModel,
    vaccineViewModel: VaccineViewModel,
    preventionViewModel: PreventionViewModel,
    checkUpViewModel: CheckUpViewModel,
    noteHealthViewModel: NoteHealthViewModel,
    medicineViewModel: MedicineViewModel
) {
    val pet by petsViewModel.getPetById(petId).collectAsState(null)
    pet?.let {
        val petName = remember(petId) { it.name }
        val tabs = TabButtonsHealth.entries
        val pagerState = rememberPagerState(0) { tabs.size }
        val scope = rememberCoroutineScope()
        Column(
            Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(start = 22.dp, end = 22.dp, bottom = 50.dp),
            verticalArrangement = Arrangement.Top
        ) {
            ScreenTitle("Health") { onBackClick() }
            Spacer(Modifier.height(20.dp))
            pet?.let { pet ->
                PetInfoCard(
                    pet.photo,
                    pet.name,
                    pet.sex,
                    pet.breed,
                    pet.birthday ?: "",
                    pet.weight,
                    pet.height,
                    {},
                    pet.animal
                )
            }
            Spacer(Modifier.height(16.dp))
            TabButtonsRow(
                { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                pagerState.currentPage
            )
            Spacer(Modifier.height(16.dp))
            HorizontalPager(
                pagerState,
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { index ->
                when (tabs[index]) {
                    TabButtonsHealth.OVERALL -> OverallTab(
                        vaccineViewModel,
                        medicineViewModel,
                        preventionViewModel,
                        checkUpViewModel,
                        noteHealthViewModel,
                        petId,
                        it.animal
                    )

                    TabButtonsHealth.VACCINATIONS -> VaccinationsTab(
                        vaccineViewModel,
                        petId,
                        petName,
                        it.animal
                    )

                    TabButtonsHealth.MEDICATIONS -> MedicineTab(
                        medicineViewModel,
                        petId,
                        petName
                    )

                    TabButtonsHealth.PARASITES -> PreventionTab(
                        preventionViewModel,
                        petId,
                        petName
                    )

                    TabButtonsHealth.CHECKUPS -> CheckUpTab(
                        checkUpViewModel,
                        petId,
                        noteHealthViewModel,
                        petName
                    )
                }
            }
        }
    }

}

@Composable
fun PetCardChip(text: String) {
    Card(
        Modifier.wrapContentSize(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        border = BorderStroke(1.dp, Color(0xFFAF8268))
    ) {
        TextMaker(
            text,
            12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(8.dp, 4.dp)
        )
    }
}

@Composable
private fun TabButton(
    isSelected: Boolean, icon: Int, label: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier
            .clickable(indication = null, interactionSource = null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painterResource(icon), "Tab Icon",
            tint = if (isSelected) buttonColor else Color(0xFF4B332E)
        )
        Spacer(Modifier.height(2.dp))
        TextMaker(label, 10.sp, if (isSelected) buttonColor else Color(0xFF4B332E))
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .background(if (isSelected) buttonColor else Color.Transparent)
                .height(1.dp)
        )
    }
}

@Composable
private fun TabButtonsRow(onSwipe: (Int) -> Unit, selectedTabIndex: Int) {
    Box(Modifier.fillMaxWidth()) {
        HorizontalDivider(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            color = Color(0xFFE8E8E8), thickness = 1.dp
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabButtonsHealth.entries.forEachIndexed { index, tab ->
                TabButton(
                    selectedTabIndex == index,
                    tab.icon,
                    tab.title,
                    Modifier.weight(1f)
                ) { onSwipe(index) }
            }
        }
    }
}