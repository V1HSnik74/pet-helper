package com.example.pethelper.ui.compose.health

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pethelper.R
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.ui.theme.*
import com.example.pethelper.ui.viewModel.PetsViewModel
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
    tab: Int = 0,
    onBackClick: () -> Unit,
    petsViewModel: PetsViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) {
        petsViewModel.setCurPet(petId)
    }
    val pet = petsViewModel.currPet
    pet?.let {
        val tabs = TabButtonsHealth.entries
        val pagerState = rememberPagerState(tab) { tabs.size }
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
            com.example.pethelper.ui.compose.petScreens.PetInfoCard(it) {}
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
                        petId = petId, animal = it.animal
                    )

                    TabButtonsHealth.VACCINATIONS -> VaccinationsTab(
                        petId = petId,
                        petName = it.name,
                        animal = it.animal
                    )

                    TabButtonsHealth.MEDICATIONS -> MedicineTab(
                        petId = petId,
                        petName = it.name,
                        animal = it.animal
                    )

                    TabButtonsHealth.PARASITES -> PreventionTab(
                        petName = it.name,
                        petId = petId,
                        animal = it.animal
                    )

                    TabButtonsHealth.CHECKUPS -> CheckUpTab(
                        petId = petId,
                        petName = it.name,
                        animal = it.animal
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
        border = BorderStroke(1.dp, brownColor)
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
            tint = if (isSelected) buttonColor else healthTabsColor
        )
        Spacer(Modifier.height(2.dp))
        TextMaker(
            label,
            10.sp,
            if (isSelected) buttonColor else healthTabsColor
        )
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
            color = dividerColor, thickness = 1.dp
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