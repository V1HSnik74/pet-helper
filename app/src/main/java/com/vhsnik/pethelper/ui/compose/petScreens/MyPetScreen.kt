package com.vhsnik.pethelper.ui.compose.petScreens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.*
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.*
import com.vhsnik.pethelper.ui.viewModel.*
import java.time.LocalDateTime

@Composable
fun MyPetScreen(
    petsViewModel: PetsViewModel = hiltViewModel(),
    id: Int,
    onCardClick: (Int) -> Unit,
    onButtonClick: (Int) -> Unit,
    onNutritionClick: (Int) -> Unit,
    onHealthClick: (Int) -> Unit, onRemindersClick: (Int) -> Unit,
    reminderViewModel: ReminderViewModel = hiltViewModel(),
    onActivityClick: (Int) -> Unit
) {
    LaunchedEffect(id) {
        petsViewModel.setCurPet(id)
    }
    val curPet = petsViewModel.currPet
    val allUpcomingSorted by remember(id) {
        reminderViewModel.getAllRemindersSorted(id)
    }.collectAsStateWithLifecycle(emptyList())
    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(64.dp))
        TextMaker("My Pet Screen", 18.sp)
        Spacer(Modifier.height(20.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            curPet?.let {
                PetInfoCard(
                    curPet
                ) { onCardClick(curPet.id) }
            }
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionButton(
                    painterResource(R.drawable.heart_icon),
                    "Health",
                    12.sp,
                    Modifier.weight(1f),
                ) { onHealthClick(id) }
                SectionButton(
                    painterResource(R.drawable.activity_icon),
                    "Activity",
                    12.sp,
                    Modifier.weight(1f)
                ) { onActivityClick(id) }
                SectionButton(
                    painterResource(R.drawable.bone_icon),
                    "Nutrition",
                    12.sp,
                    Modifier.weight(1f)
                ) { onNutritionClick(id) }
                SectionButton(
                    painterResource(R.drawable.bell_icon),
                    "Reminders",
                    11.sp,
                    Modifier.weight(1f)
                ) { onRemindersClick(id) }
                SectionButton(
                    painterResource(R.drawable.notes_icon),
                    "Notes",
                    12.sp,
                    Modifier.weight(1f),
                ) { onButtonClick(id) }
            }
            PagerCard(
                "Upcoming",
                null,
                allUpcomingSorted
            ) { it, hasLine ->
                val icon = when (it) {
                    is Reminder -> it.icon
                    is HealthObject -> when (it.category) {
                        HealthCategory.CHECKUP -> R.drawable.checkup_icon
                        HealthCategory.VACCINE -> R.drawable.core_vaccine_icon
                        HealthCategory.MEDICINE -> R.drawable.next_up
                        HealthCategory.PREVENTION -> R.drawable.bug_icon
                    }

                    else -> null
                }
                val title = when (it) {
                    is Reminder -> it.name
                    is HealthObject -> it.name
                    else -> ""
                }
                val note = when (it) {
                    is Reminder -> it.note.orEmpty()
                    is HealthObject -> it.note.orEmpty()
                    else -> ""
                }
                val date = when (it) {
                    is Reminder -> it.date
                    is HealthObject -> if (it.category == HealthCategory.MEDICINE)
                        LocalDateTime.parse(it.nextTaking).toLocalDate().format(dateParser)
                    else it.date!!

                    else -> ""
                }
                val time = when (it) {
                    is Reminder -> it.time ?: "23:59"
                    is HealthObject -> if (it.category == HealthCategory.MEDICINE)
                        LocalDateTime.parse(it.nextTaking).toLocalTime().format(timeParser)
                    else it.time ?: "23:59"

                    else -> ""
                }
                PagerCardItem(
                    icon!!,
                    title,
                    note,
                    date,
                    time,
                    note.isNotEmpty(),
                    time.isNotEmpty(),
                    hasLine,
                    note.isNotEmpty(), {}, {}, hasMenu = false
                )
            }
            curPet?.let {
                AboutSection(
                    curPet,
                    painterResource(R.drawable.paw_notes_icon)
                )
            }
        }

    }
}

@Composable
fun SectionButton(
    painter: Painter,
    text: String,
    fontSize: TextUnit,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier
            .height(72.dp)
            .clickable(interactionSource = null, indication = null) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painter, contentDescription = "$text button", Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            TextMaker(text, fontSize, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun AboutSection(
    pet: PetFields,
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
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f, fill = false)
            ) {
                TextMaker("About ${pet.name}", 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                TextMaker(
                    pet.about,
                    12.sp, fontWeight = FontWeight.Normal
                )
            }
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = "paw about icon",
                    modifier = Modifier.size(40.dp)
                )
            }
            if (hasAbout)
                EditAbout(modifier = Modifier, onOpenDialog = onOpenDialog)
        }
    }
}
