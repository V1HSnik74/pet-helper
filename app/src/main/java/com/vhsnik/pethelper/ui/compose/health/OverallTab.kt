package com.vhsnik.pethelper.ui.compose.health

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun OverallTab(
    healthViewModel: HealthViewModel = hiltViewModel<HealthViewModel>(),
    noteViewModel: NoteViewModel = hiltViewModel(),
    petId: Int,
    animal: String
) {
    val notes by noteViewModel.allOverallNotes
        .collectAsStateWithLifecycle()
    val allHealthObjectsSorted by
    healthViewModel.allUpcomingHealthObjects
        .collectAsStateWithLifecycle()
    val medicines by remember {
        healthViewModel.getCategoryHealthObjects(
            HealthCategory.MEDICINE
        )
    }.collectAsStateWithLifecycle()
    val preventionStatus by
    healthViewModel.preventionPercentage
        .collectAsStateWithLifecycle()
    val vaccinePercentage by remember {
        healthViewModel.getVaccinationPercentage(animal)
    }.collectAsStateWithLifecycle()
    val isCheckUpDone by
    healthViewModel.checkUpStatus
        .collectAsStateWithLifecycle()
    var isAddNotesOpened by remember { mutableStateOf(false) }
    var isDeleteOpened by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var idToDelete by remember { mutableStateOf<Long?>(null) }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 2.dp, end = 2.dp, bottom = 20.dp, top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HealthOverviewTab(
            medicines.size,
            preventionStatus.percentage == 1f,
            vaccinePercentage == 1f,
            isCheckUpDone
        )
        PagerCard(
            "Next Up",
            null,
            allHealthObjectsSorted
        ) { it, hasLine ->
            val icon = when (it.category) {
                HealthCategory.MEDICINE -> R.drawable.next_up
                HealthCategory.CHECKUP -> R.drawable.checkup_icon
                HealthCategory.PREVENTION -> R.drawable.bug_icon
                HealthCategory.VACCINE -> R.drawable.core_vaccine_icon
            }
            val isMedicine = it.category == HealthCategory.MEDICINE
            val date = if (isMedicine)
                LocalDateTime.parse(it.nextTaking, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .format(dateParser)
            else LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
                .format(dateParser)
            val time = if (isMedicine)
                LocalDateTime.parse(it.nextTaking, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .format(timeParser)
            else it.time ?: ""
            PagerCardItem(
                icon,
                it.name,
                it.note.orEmpty(),
                date,
                time,
                (!it.note.isNullOrEmpty()),
                !it.time.isNullOrEmpty(),
                hasLine,
                (!it.note.isNullOrEmpty()), {}, {}, hasMenu = false
            )
        }
        PagerCard(
            "Notes",
            { isAddNotesOpened = true },
            notes
        ) { it, hasNote ->
            PagerCardItem(
                R.drawable.note_overall,
                it.description,
                "",
                it.date!!,
                "",
                hasAddInfoLeft = false,
                hasAddInfoRight = false,
                hasLine = hasNote,
                hasNote = false, {
                    isDeleteOpened = true
                    idToDelete = it.id
                }, {
                    isAddNotesOpened = true
                    noteToEdit = it
                }
            )
        }
    }
    if (isAddNotesOpened) {
        AddHealthNoteDialog(
            {
                isAddNotesOpened = false
                noteToEdit = null
            },
            { note, date, _ ->
                if (noteToEdit == null)
                    noteViewModel.addNote(note, null, NoteSection.OVERALL_HEALTH, date)
                else
                    noteViewModel.updateNote(noteToEdit!!.copy(description = note, date = date))
                isAddNotesOpened = false
                noteToEdit = null
            },
            petId,
            noteToEdit?.description.orEmpty(),
            noteToEdit?.date?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
                ?: LocalDate.now()
        )
    }
    if (isDeleteOpened) {
        DeleteDialog(
            {
                idToDelete = null
                isDeleteOpened = false
            }
        ) {
            noteViewModel.deleteNote(idToDelete!!)
            idToDelete = null
            isDeleteOpened = false
        }
    }
}

@Composable
private fun HealthOverviewTab(
    medicationsSize: Int,
    isParasitesDone: Boolean,
    isVaccinesDone: Boolean,
    isCheckUpDone: Boolean
) {
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
            TextMaker(
                "Health Overview",
                12.sp,
                fontWeight = FontWeight.SemiBold
            )
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
                HealthOverviewItem(
                    R.drawable.parasites_yellow,
                    R.drawable.parasites_green,
                    isParasitesDone,
                    "Parasites",
                    if (isParasitesDone) "Protected" else "Needs action"
                )
                HealthOverviewItem(
                    R.drawable.shield_yellow,
                    R.drawable.shield_good,
                    isVaccinesDone,
                    "Vaccination",
                    if (isVaccinesDone) "Up to date" else "Needs action"
                )
                HealthOverviewItem(
                    R.drawable.checkups_yellow,
                    R.drawable.checkup_green,
                    isCheckUpDone,
                    "Check-up",
                    if (isCheckUpDone) "Done" else "Needs action"
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
        TextMaker(
            label,
            10.sp,
            healthTabsColor,
            FontWeight.SemiBold
        )
        TextMaker(
            text,
            10.sp,
            if (isDone) PastelleColors.GREEN.iconColor else PastelleColors.YELLOW.iconColor,
            FontWeight.SemiBold
        )
    }
}