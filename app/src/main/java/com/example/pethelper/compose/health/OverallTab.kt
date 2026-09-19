package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.db.viewModel.CheckUpViewModel
import com.example.pethelper.db.viewModel.MedicineViewModel
import com.example.pethelper.db.viewModel.PreventionViewModel
import com.example.pethelper.db.viewModel.VaccineViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.PagerCardItem
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.db.entity.CheckUp
import com.example.pethelper.db.entity.Medicine
import com.example.pethelper.db.entity.Prevention
import com.example.pethelper.db.entity.Vaccine
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.db.viewModel.NoteHealthViewModel

@Composable
fun OverallTab(
    vaccineViewModel: VaccineViewModel,
    medicineViewModel: MedicineViewModel,
    preventionViewModel: PreventionViewModel,
    checkUpViewModel: CheckUpViewModel,
    noteHealthViewModel: NoteHealthViewModel,
    petId: Int
) {
    val notes by noteHealthViewModel.getAllOverallNotesByPet(petId).collectAsState(emptyList())
    var isAddNotesOpened by remember { mutableStateOf(false) }
    val medications by medicineViewModel.getAllMedicinesByPet(petId).collectAsState(emptyList())
    val historyVaccine by vaccineViewModel.vaccineHistory(petId).collectAsState(emptyList())
    val vaccinesUpc by vaccineViewModel.allUpcomingVaccines(petId).collectAsState(emptyList())
    val preventions by preventionViewModel.getPreventionsHistoryByPet(petId)
        .collectAsState(emptyList())
    val preventionsUpc by preventionViewModel.getAllUpcomingPreventionsByPet(petId)
        .collectAsState(emptyList())
    val checkUpHistory by checkUpViewModel.getCheckUpsHistoryByPet(petId)
        .collectAsState(emptyList())
    val checkUpsUpc by checkUpViewModel.getAllUpcomingCheckUpsByPet(petId)
        .collectAsState(emptyList())
    val allUpcoming = mutableListOf<Any>()
    allUpcoming.addAll(preventionsUpc)
    allUpcoming.addAll(vaccinesUpc)
    allUpcoming.addAll(checkUpsUpc)
    allUpcoming.addAll(medications)
    val allUpcomingSorted = allUpcoming.sortedBy {
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
                    LocalTime.parse("12:00")
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

            else -> null
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 2.dp, end = 2.dp, bottom = 20.dp, top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HealthOverviewTab(
            medications,
            preventions,
            preventionsUpc,
            historyVaccine,
            vaccinesUpc,
            checkUpHistory
        )
        PagerCard(
            "Next Up",
            null,
            allUpcomingSorted
        ) { it, hasLine ->
            val icon = when (it) {
                is Vaccine -> R.drawable.core_vaccine_icon
                is Prevention -> R.drawable.bug_icon
                is Medicine -> R.drawable.next_up
                else -> R.drawable.checkup_icon
            }
            val title = when (it) {
                is Vaccine -> it.name
                is Prevention -> it.action
                is Medicine -> it.name
                is CheckUp -> it.name
                else -> ""
            }
            val note = when (it) {
                is Vaccine -> ""
                is Prevention -> it.note.orEmpty()
                is Medicine -> it.note.orEmpty()
                is CheckUp -> it.note.orEmpty()
                else -> ""
            }
            val date = when (it) {
                is Vaccine -> it.date
                is Prevention -> it.date
                is Medicine -> LocalDateTime.parse(it.nextTaking).toLocalDate().format(dateParser)
                is CheckUp -> it.date
                else -> ""
            }
            val time = when (it) {
                is Vaccine -> it.time
                is Prevention -> "12:00"
                is Medicine -> LocalDateTime.parse(it.nextTaking).toLocalTime().format(timeParser)
                is CheckUp -> it.time
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
        PagerCard(
            "Notes",
            { isAddNotesOpened = true },
            notes
        ) { it, hasNote ->
            PagerCardItem(
                R.drawable.note_overall,
                it.note,
                "",
                it.date,
                "",
                hasAddInfoLeft = false,
                hasAddInfoRight = false,
                hasLine = hasNote,
                hasNote = false
            )
        }
    }
    if (isAddNotesOpened) {
        AddHealthNoteDialog(
            { isAddNotesOpened = false },
            { note, date, petId, category ->
                noteHealthViewModel.addNoteHealth(note, date, petId, category)
                isAddNotesOpened = false
            },
            petId,
            "Overall"
        )
    }
}

@Composable
private fun HealthOverviewTab(
    medications: List<Medicine>,
    preventions: List<Prevention>,
    upcomingPreventions: List<Prevention>,
    vaccines: List<Vaccine>,
    vaccinesUpc: List<Vaccine>,
    checkUps: List<CheckUp>
) {
    val medicationsSize = remember(medications) { medications.size }
    val isParasitesDone = getPreventionPercentage(preventions, upcomingPreventions).percentage == 1f
    val isVaccinesDone = countVaccinePercentage(vaccines, catVaccines, vaccinesUpc) == 1f
    val isCheckUpsDone = isCheckUpDone(checkUps)
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TextMaker("Health Overview", 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HealthOverviewItem(
                    R.drawable.medicine_yellow,
                    R.drawable.medicine_green,
                    medicationsSize == 0,
                    "Medications",
                    if (medicationsSize == 0) "No medications" else "$medicationsSize upcoming"
                )
                Box(
                    Modifier
                        .width(1.dp)
                        .background(Color(0xFFE8E8E8))
                )
                HealthOverviewItem(
                    R.drawable.parasites_yellow,
                    R.drawable.parasites_green,
                    isParasitesDone,
                    "Parasites",
                    if (isParasitesDone) "Protected" else "Needs action"
                )
                Box(
                    Modifier
                        .width(1.dp)
                        .background(Color(0xFFE8E8E8))
                )
                HealthOverviewItem(
                    R.drawable.shield_yellow,
                    R.drawable.shield_good,
                    isVaccinesDone,
                    "Vaccination",
                    if (isVaccinesDone) "Up to date" else "Needs action"
                )
                Box(
                    Modifier
                        .width(1.dp)
                        .background(Color(0xFFE8E8E8))
                )
                HealthOverviewItem(
                    R.drawable.checkups_yellow,
                    R.drawable.checkup_green,
                    isCheckUpsDone,
                    "Check-up",
                    if (isCheckUpsDone) "Done" else "Needs action"
                )
            }
        }
    }
}

@Composable
private fun HealthOverviewItem(
    icon: Int,
    iconIsDone: Int,
    isDone: Boolean,
    label: String,
    text: String
) {
    Column(
        Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            if (isDone) painterResource(iconIsDone) else painterResource(icon),
            "$label icon health overview"
        )
        TextMaker(label, 10.sp, Color(0xFF4B332E), FontWeight.SemiBold)
        TextMaker(
            text,
            10.sp,
            if (isDone) Color(0xFF008400) else Color(0xFFD27918),
            FontWeight.SemiBold
        )
    }
}

private fun isCheckUpDone(checkUpHistory: List<CheckUp>): Boolean {
    if (checkUpHistory.isEmpty()) return false
    val curDate = LocalDate.now()
    val relevantCheckUps = checkUpHistory.filter {
        val checkUpDate = LocalDate.parse(it.date)
        checkUpDate.plusYears(1) >= curDate
    }
    return relevantCheckUps.any {
        it.name.contains("annual", true) || it.name.contains(
            "year",
            true
        )
    }
}