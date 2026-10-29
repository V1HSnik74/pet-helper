package com.vhsnik.pethelper.ui.compose.activity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.Activity
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.*
import com.vhsnik.pethelper.ui.viewModel.*

const val APtext =
    "Tell us about your pet’s activity level, preferences and any special needs. This helps us create the best plan for your pet"
const val tipText =
    "Regular activity keeps your pet physically healthy, mentally stimulated and helps prevent behavioral issues like boredom or anxiety"

private data class ActivityInfoItem(
    val icon: Int,
    val name: String,
    val value: String?
)

private enum class OpenedDialogs {
    ADD_INFO, DELETE, UPSERT_ACTIVITY
}

@Composable
fun ActivityScreen(
    petId: Int,
    onBackClick: () -> Unit,
    petsViewModel: PetsViewModel = hiltViewModel(),
    activityViewModel: ActivityViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) {
        activityViewModel.setPetId(petId)
        petsViewModel.setCurPet(petId)
    }
    val pet by petsViewModel.currPet.collectAsStateWithLifecycle()
    val info = remember(pet) {
        listOf(
            ActivityInfoItem(
                R.drawable.activity_level_icon,
                "Activity level",
                pet?.activityLevel
            ),
            ActivityInfoItem(
                R.drawable.favorite_activity,
                "favorite Activity",
                pet?.favActivities
            ),
            ActivityInfoItem(
                R.drawable.limitations_icon,
                "Physical limitations",
                pet?.limitations
            ),
            ActivityInfoItem(
                R.drawable.goals_icon,
                "Goals",
                pet?.goals
            )
        )
    }
    val activityPlan by activityViewModel.allActivities.collectAsStateWithLifecycle()
    var activeDialog by remember { mutableStateOf<OpenedDialogs?>(null) }
    var idForDelete by remember { mutableStateOf<Long?>(null) }
    var activityToEdit by remember { mutableStateOf<Activity?>(null) }
    var isPlanOpened by remember { mutableStateOf(false) }
    val isGenerating by activityViewModel.isGenerating.collectAsStateWithLifecycle()
    val generatedActivities by activityViewModel.generatedActivities.collectAsStateWithLifecycle()
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
            PagerCard(
                "Activity Information",
                { activeDialog = OpenedDialogs.ADD_INFO },
                info
            ) { item, _ ->
                ActivityInformation(
                    item.name,
                    item.value,
                    item.icon
                )
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
                    TextMaker(
                        "Activity Plan",
                        12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                pet?.let {
                    GenerateButton(
                        {
                            activityViewModel.getActivitiesRecommendation(it)
                        },
                        "Activity",
                        isGenerating
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
    if (activeDialog == OpenedDialogs.ADD_INFO) {
        AddActivityDialog(
            { activeDialog = null },
            { activityLevel, favActivities, limitations, goals, petId ->
                petsViewModel.updatePetField(petId) { pet ->
                    pet.copy(
                        activityLevel = activityLevel, favActivities = favActivities,
                        limitations = limitations, goals = goals
                    )
                }
                activeDialog = null
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
                Card(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(backgroundColor)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        PagerCard(
                            "Activity Plan",
                            { activeDialog = OpenedDialogs.UPSERT_ACTIVITY },
                            activityPlan,
                        ) { item, hasLine ->
                            GeneratedItem(
                                item.name,
                                item.note,
                                item.icon,
                                item.duration,
                                {
                                    idForDelete = item.id
                                    activeDialog = OpenedDialogs.DELETE
                                },
                                {
                                    activityToEdit = item
                                    activeDialog = OpenedDialogs.UPSERT_ACTIVITY
                                }
                            )
                            if (hasLine)
                                HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, dividerColor)
                        }
                    }
                }
            }
        }
    }

    if (activeDialog == OpenedDialogs.DELETE) {
        DeleteDialog(
            {
                activeDialog = null
                idForDelete = null
            }
        ) {
            activityViewModel.deleteActivity(idForDelete!!)
            idForDelete = null
            activeDialog = null
        }
    }

    if (activeDialog == OpenedDialogs.UPSERT_ACTIVITY) {
        val durationUnit = activityToEdit?.duration?.split(" ")
        val duration = durationUnit?.get(0)
        val unit = durationUnit?.get(1)
        UpsertActivityDialog(
            {
                activeDialog = null
                activityToEdit = null
            },
            { activity, note, duration, icon, _ ->
                if (activityToEdit != null) {
                    activityViewModel.updateActivity(
                        activityToEdit!!.copy(
                            name = activity,
                            note = note,
                            duration = duration,
                            icon = icon
                        )
                    )
                } else
                    activityViewModel.addActivity(activity, note, duration, icon)
                activeDialog = null
                activityToEdit = null
            },
            activityToEdit != null,
            activityToEdit?.name.orEmpty(),
            activityToEdit?.note.orEmpty(),
            duration ?: "5",
            unit ?: "time(s)",
            activityToEdit?.icon ?: R.drawable.ball,
            petId
        )
    }

    generatedActivities?.let { activities ->
        GeneratedActivityNutritionPlan(
            { activityViewModel.clearGeneratedActivities() },
            activities,
            {
                activityViewModel.saveActivityPlan(activities)
                activityViewModel.clearGeneratedActivities()
            },
            { activityViewModel.deleteActivities() },
            "Activity",
            { item ->
                GeneratedItem(
                    item.name,
                    item.note,
                    item.icon,
                    item.duration,
                    isGeneratedList = true
                )
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
            TextMaker(
                name,
                12.sp,
                fontWeight = FontWeight.SemiBold
            )
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
                TextMaker(
                    "Activity Profile",
                    12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                TextMaker(
                    APtext,
                    10.sp,
                    Color(0xFF8C8C8C),
                    FontWeight.Medium
                )
            }
        }
    }
}