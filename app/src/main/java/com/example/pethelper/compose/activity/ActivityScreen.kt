package com.example.pethelper.compose.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.GenerateButton
import com.example.pethelper.compose.patterns.GeneratedActivityNutritionPlan
import com.example.pethelper.compose.patterns.GeneratedItem
import com.example.pethelper.compose.patterns.HealthTip
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.ScreenTitle
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.petScreens.PetInfoCard
import com.example.pethelper.db.viewModel.ActivityInfoViewModel
import com.example.pethelper.db.viewModel.ActivityViewModel
import com.example.pethelper.db.viewModel.PetsViewModel
import com.example.pethelper.ui.theme.PastelleColors
import com.example.pethelper.ui.theme.backgroundColor
import com.example.pethelper.ui.theme.brownColor
import com.example.pethelper.ui.theme.cardColor
import com.example.pethelper.ui.theme.dividerColor
import com.example.pethelper.ui.theme.smallTextColor

const val APtext =
    "Tell us about your pet’s activity level, preferences and any special needs. This helps us create the best plan for your pet"
const val tipText =
    "Regular activity keeps your pet physically healthy, mentally stimulated and helps prevent behavioral issues like boredom or anxiety"

data class ActivityInfoItem(
    val icon: Int,
    val name: String,
    val value: String?
)

@Composable
fun ActivityScreen(
    petId: Int,
    petsViewModel: PetsViewModel,
    activityInfoViewModel: ActivityInfoViewModel,
    onBackClick: () -> Unit,
    activityViewModel: ActivityViewModel
) {
    val pet = petsViewModel.currPet
    var isAddActivityInfoOpened by remember { mutableStateOf(false) }
    var isPlanOpened by remember { mutableStateOf(false) }
    val activityInfo by
    remember(petId) { activityInfoViewModel.getActivityInfo(petId) }.collectAsState(null)
    val activityLevel = remember(activityInfo) {
        ActivityInfoItem(
            R.drawable.activity_level_icon,
            "Activity level",
            activityInfo?.activityLevel
        )
    }
    val favActivities = remember(activityInfo) {
        ActivityInfoItem(
            R.drawable.favorite_activity,
            "favorite Activity",
            activityInfo?.favActivities
        )
    }
    val limitations = remember(activityInfo) {
        ActivityInfoItem(
            R.drawable.limitations_icon,
            "Physical limitations",
            activityInfo?.limitations
        )
    }
    val goals = remember(activityInfo) {
        ActivityInfoItem(
            R.drawable.goals_icon,
            "Goals",
            activityInfo?.goals
        )
    }
    val info = listOf(activityLevel, favActivities, limitations, goals)
    val activityPlan by remember(petId) {
        activityViewModel.getAllActivities(petId)
    }.collectAsState(emptyList())
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 22.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenTitle("Activity") { onBackClick() }
        Spacer(Modifier.height(20.dp))
        pet?.let {
            PetInfoCard(it) {}
        }
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActivityProfile()
            PagerCard("Activity Information", { isAddActivityInfoOpened = true }, info) { item, _ ->
                ActivityInformation(item.name, item.value, item.icon)
                if (item != info.last())
                    HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, dividerColor)
            }
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isPlanOpened = true
                    },
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp, 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    border = BorderStroke
                        (1.dp, brownColor),
                    interactionSource = null
                ) {
                    TextMaker("Activity Plan", 12.sp, fontWeight = FontWeight.SemiBold)
                }
                pet?.let {
                    GenerateButton(
                        {
                            activityViewModel.getActivitiesRecommendation(
                                it.breed,
                                it.weight,
                                it.birthday,
                                it.neutered,
                                activityLevel.value ?: "",
                                favActivities.value ?: "",
                                limitations.value ?: "None",
                                goals.value ?: "",
                                petId,
                                it.animal
                            )
                        },
                        "Activity",
                        activityViewModel.isGenerating
                    )
                }
            }
            HealthTip(
                PastelleColors.PINK.color,
                PastelleColors.PINK.iconColor,
                PastelleColors.PINK.borderColor,
                "Why it matters",
                tipText,
                R.drawable.bulb
            )
            Spacer(Modifier.height(50.dp))
        }
    }
    if (isAddActivityInfoOpened) {
        AddActivityDialog(
            { isAddActivityInfoOpened = false },
            { activityLevel, favActivities, limitations, goals, petId ->
                activityInfoViewModel.addActivityInfo(
                    activityLevel,
                    favActivities,
                    limitations,
                    goals,
                    petId
                )
                isAddActivityInfoOpened = false
            },
            petId
        )
    }
    if (isPlanOpened) {
        Dialog(
            { isPlanOpened = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(Modifier.fillMaxWidth(0.9f)) {
                PagerCard(
                    "Activity Plan",
                    null,
                    activityPlan,
                ) { item, hasLine ->
                    GeneratedItem(item.name, item.note, R.drawable.addactivityicon, item.duration)
                    if (hasLine)
                        HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, dividerColor)
                }
            }
        }
    }

    activityViewModel.generatedActivities?.let { activities ->
        GeneratedActivityNutritionPlan(
            { activityViewModel.clearGeneratedActivities() },
            activities,
            {
                activities.forEach {
                    activityViewModel.addActivity(it.name, it.note, it.duration, petId)
                }
                activityViewModel.clearGeneratedActivities()
            },
            "Activity",
            { item ->
                GeneratedItem(item.name, item.note, R.drawable.addactivityicon, item.duration)
            }
        )

    }
}

@Composable
private fun ActivityInformation(name: String, value: String?, icon: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(icon),
            null
        )
        Spacer(Modifier.width(10.dp))
        Column(
            Modifier.width(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TextMaker(name, 12.sp, fontWeight = FontWeight.SemiBold)
            TextMaker(
                if (value.isNullOrBlank()) "None" else value, 10.sp, smallTextColor,
                FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ActivityProfile() {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painterResource(R.drawable.addactivityicon),
                null,
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextMaker("Activity Profile", 12.sp, fontWeight = FontWeight.SemiBold)
                TextMaker(APtext, 10.sp, Color(0xFF8C8C8C), FontWeight.Medium)
            }
        }
    }
}