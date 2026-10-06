package com.example.pethelper.compose.petScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pethelper.R
import com.example.pethelper.compose.health.PetCardChip
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.PagerCardItem
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.db.entity.CheckUp
import com.example.pethelper.db.entity.Medicine
import com.example.pethelper.db.entity.Prevention
import com.example.pethelper.db.entity.Reminder
import com.example.pethelper.db.entity.Vaccine
import com.example.pethelper.db.viewModel.CheckUpViewModel
import com.example.pethelper.db.viewModel.MedicineViewModel
import com.example.pethelper.db.viewModel.PetsViewModel
import com.example.pethelper.db.viewModel.PreventionViewModel
import com.example.pethelper.db.viewModel.ReminderViewModel
import com.example.pethelper.db.viewModel.VaccineViewModel
import com.example.pethelper.ui.theme.backgroundColor
import com.example.pethelper.ui.theme.cardColor
import com.example.pethelper.ui.theme.femaleColor
import com.example.pethelper.ui.theme.maleColor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MyPetScreen(
    petsViewModel: PetsViewModel, id: Int, onCardClick: (Int) -> Unit, onButtonClick: (Int) -> Unit,
    onNutritionClick: (Int) -> Unit, onHealthClick: (Int) -> Unit, onRemindersClick: (Int) -> Unit,
    medicineViewModel: MedicineViewModel, vaccineViewModel: VaccineViewModel,
    checkUpViewModel: CheckUpViewModel, preventionViewModel: PreventionViewModel,
    reminderViewModel: ReminderViewModel, onActivityClick: (Int) -> Unit
) {
    petsViewModel.setCurPet(id)
    val curPet = petsViewModel.currPet
    val upcomingMedicines by medicineViewModel.getAllMedicinesByPet(id)
        .collectAsState(emptyList())
    val upcomingVaccines by vaccineViewModel.allUpcomingVaccines(id)
        .collectAsState(emptyList())
    val upcomingCheckUps by checkUpViewModel.getAllUpcomingCheckUpsByPet(id)
        .collectAsState(emptyList())
    val upcomingPreventions by preventionViewModel.getAllUpcomingPreventionsByPet(id)
        .collectAsState(emptyList())
    val allReminders by reminderViewModel.getAllRemindersByPet(id)
        .collectAsState(emptyList())
    val upcomingReminders = remember(allReminders) {
        allReminders.filter {
            val date = LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
            val time =
                if (!it.time.isNullOrEmpty()) LocalTime.parse(
                    it.time,
                    timeParser
                ) else LocalTime.parse(
                    "23:59",
                    timeParser
                )
            val dateTime = LocalDateTime.of(date, time)
            dateTime >= LocalDateTime.now()
        }
    }
    val allUpcomingSorted = remember(
        upcomingMedicines,
        upcomingVaccines,
        upcomingPreventions,
        upcomingCheckUps,
        upcomingReminders
    ) {
        buildList {
            addAll(upcomingMedicines)
            addAll(upcomingVaccines)
            addAll(upcomingCheckUps)
            addAll(upcomingPreventions)
            addAll(upcomingReminders)
        }.sortedBy {
            when (it) {
                is Vaccine -> {
                    LocalDateTime.of(
                        LocalDate.parse(it.date),
                        LocalTime.parse(it.time, timeParser)
                    )
                }

                is Prevention -> {
                    LocalDateTime.of(
                        LocalDate.parse(it.date),
                        LocalTime.parse("23:59")
                    )
                }

                is CheckUp -> {
                    LocalDateTime.of(
                        LocalDate.parse(it.date),
                        LocalTime.parse(it.time, timeParser)
                    )
                }

                is Medicine -> {
                    LocalDateTime.parse(it.nextTaking)
                }

                is Reminder -> {
                    LocalDateTime.of(
                        LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE),
                        if (!it.time.isNullOrEmpty()) LocalTime.parse(
                            it.time,
                            timeParser
                        ) else LocalTime.parse(
                            "23:59",
                            timeParser
                        )
                    )
                }

                else -> null
            }
        }
    }
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
                    is Vaccine -> R.drawable.core_vaccine_icon
                    is Prevention -> R.drawable.bug_icon
                    is Medicine -> R.drawable.next_up
                    is Reminder -> it.icon
                    else -> R.drawable.checkup_icon
                }
                val title = when (it) {
                    is Vaccine -> it.name
                    is Prevention -> it.action
                    is Medicine -> it.name
                    is CheckUp -> it.name
                    is Reminder -> it.name
                    else -> ""
                }
                val note = when (it) {
                    is Vaccine -> ""
                    is Prevention -> it.note.orEmpty()
                    is Medicine -> it.note.orEmpty()
                    is CheckUp -> it.note.orEmpty()
                    is Reminder -> it.note.orEmpty()
                    else -> ""
                }
                val date = when (it) {
                    is Vaccine -> it.date
                    is Prevention -> it.date
                    is Medicine -> LocalDateTime.parse(it.nextTaking).toLocalDate()
                        .format(dateParser)

                    is CheckUp -> it.date
                    is Reminder -> it.date
                    else -> ""
                }
                val time = when (it) {
                    is Vaccine -> it.time
                    is Prevention -> "23:59"
                    is Medicine -> LocalDateTime.parse(it.nextTaking).toLocalTime()
                        .format(timeParser)

                    is CheckUp -> it.time
                    is Reminder -> it.time ?: "23:59"
                    else -> ""
                }
                PagerCardItem(
                    icon,
                    title,
                    note,
                    date,
                    time,
                    note.isNotEmpty(),
                    true,
                    hasLine,
                    note.isNotEmpty()
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
fun PetInfoCard(
    pet: PetsViewModel.PetFields,
    onCardClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val photoModifier = remember {
                Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            }
            if (pet.photo == null) {
                Image(
                    painterResource(R.drawable.no_photo_profile),
                    modifier = photoModifier,
                    contentDescription = "Pet photo",
                    contentScale = ContentScale.Fit
                )
            } else {
                AsyncImage(
                    model = pet.photo,
                    modifier = photoModifier,
                    contentDescription = "Pet photo",
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(30.dp))
            Column(
                Modifier.height(120.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(if (pet.animal == "Dog") R.drawable.dog else R.drawable.cat),
                        null,
                        tint = if (pet.gender == "Male") maleColor else femaleColor
                    )
                    Spacer(Modifier.width(4.dp))
                    TextMaker(pet.name, 16.sp, modifier = Modifier.weight(1f, fill = false))
                }
                TextMaker(
                    pet.breed,
                    16.sp,
                    fontWeight = FontWeight.Normal
                )
                PetCardChip(pet.age)
                Row(Modifier.fillMaxWidth()) {
                    PetCardChip(pet.weight)
                    Spacer(Modifier.width(8.dp))
                    PetCardChip(pet.height)
                }
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
    pet: PetsViewModel.PetFields,
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