package com.example.pethelper.ui.compose.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pethelper.R
import com.example.pethelper.data.entity.HealthCategory
import com.example.pethelper.data.entity.HealthObject
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.ui.viewModel.HealthViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private enum class DialogsVaccines {
    UPCOMING, REMINDER, HISTORY, LEARN_MORE
}

@Composable
fun VaccinationsTab(
    healthViewModel: HealthViewModel = hiltViewModel(),
    petId: Int,
    petName: String,
    animal: String
) {
    val allUpcomingVaccines by remember(petId) {
        healthViewModel.getAllUpcomingCategory(petId, HealthCategory.VACCINE)
    }.collectAsStateWithLifecycle(emptyList())
    val vaccineHistory by remember(petId) {
        healthViewModel.getAllHistoryCategory(petId, HealthCategory.VACCINE)
    }.collectAsStateWithLifecycle(emptyList())
    val theMostUpcomingVaccine = allUpcomingVaccines.firstOrNull()
    var activeDialog by remember { mutableStateOf<DialogsVaccines?>(null) }
    val percentage by remember(petId, animal) {
        healthViewModel.getVaccinationPercentage(petId, animal)
    }.collectAsStateWithLifecycle(0f)
    var isDeleteOpened by remember { mutableStateOf(false) }
    var idForDelete by remember { mutableStateOf<Long?>(null) }
    var vaccineUpcToEdit by remember { mutableStateOf<HealthObject?>(null) }
    var vaccineHistoryToEdit by remember { mutableStateOf<HealthObject?>(null) }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 2.dp, end = 2.dp, bottom = 20.dp, top = 2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            StatusProgBar(
                true,
                percentage,
                if (percentage == 1f)
                    "$petName is up to date on most core vaccines"
                else
                    "$petName is not totally up to date on most core vaccines",
                { activeDialog = DialogsVaccines.LEARN_MORE },
                modifier = Modifier
                    .weight(2f)
                    .wrapContentHeight()
            )
            Spacer(Modifier.width(4.dp))
            UpcomingBlock(
                Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                "Vaccine Reminder",
                true,
                theMostUpcomingVaccine?.name.orEmpty(),
                theMostUpcomingVaccine?.date.orEmpty(),
                theMostUpcomingVaccine?.time.orEmpty(),
                theMostUpcomingVaccine != null
            ) {
                activeDialog =
                    DialogsVaccines.REMINDER
            }

        }
        Spacer(Modifier.height(16.dp))
        PagerCard(
            "Core Vaccines",
            {
                activeDialog =
                    DialogsVaccines.HISTORY
            },
            vaccineHistory
        ) { item, hasLine ->
            PagerCardItem(
                R.drawable.core_vaccine_icon,
                item.name,
                "",
                item.date!!,
                item.time!!,
                hasAddInfoLeft = false,
                hasAddInfoRight = false,
                hasLine = hasLine,
                hasNote = false,
                {
                    isDeleteOpened = true
                    idForDelete = item.id
                },
                {
                    activeDialog = DialogsVaccines.UPCOMING
                    vaccineHistoryToEdit = item
                }
            )
        }
    }
    if (activeDialog == DialogsVaccines.REMINDER) {
        Dialog(
            { activeDialog = null },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(Modifier.fillMaxWidth(0.9f)) {
                PagerCard(
                    "Upcoming",
                    {
                        activeDialog =
                            DialogsVaccines.UPCOMING
                    },
                    allUpcomingVaccines,
                ) { item, hasLine ->
                    PagerCardItem(
                        R.drawable.core_vaccine_icon,
                        item.name,
                        "",
                        item.date!!,
                        item.time!!,
                        hasAddInfoLeft = false,
                        hasAddInfoRight = false,
                        hasLine = hasLine,
                        hasNote = false,
                        {
                            isDeleteOpened = true
                            idForDelete = item.id
                        },
                        {
                            activeDialog = DialogsVaccines.UPCOMING
                            vaccineUpcToEdit = item
                        },
                        true,
                        { healthViewModel.markAsDone(item, petName) }
                    )
                }
            }
        }
    }
    if (activeDialog == DialogsVaccines.UPCOMING) {
        val editingItem = vaccineHistoryToEdit ?: vaccineUpcToEdit
        val isHistory = vaccineHistoryToEdit != null
        AddUpcomingVaccine(
            {
                activeDialog = null
                vaccineUpcToEdit = null
                vaccineHistoryToEdit = null
            },
            {
                if (editingItem != null) {
                    healthViewModel.updateHealthObject(
                        it.copy(id = editingItem.id), animal, petName
                    )
                } else
                    healthViewModel.addHealthObject(it, animal, petName)
                activeDialog = null
                vaccineHistoryToEdit = null
                vaccineUpcToEdit = null
            },
            petId,
            animal,
            editingItem != null,
            if (isHistory) "History" else "Upcoming",
            isHistory,
            editingItem?.name.orEmpty(),
            editingItem?.date?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
                ?: LocalDate.now(),
            editingItem?.time ?: "",
            editingItem?.isNotif ?: false,
            editingItem?.dateNotif ?: "same day",
            editingItem?.timeNotif ?: ""
        )
    }
    if (activeDialog == DialogsVaccines.HISTORY) {
        AddCoreVaccineDialog(
            { activeDialog = null },
            { healthObject ->
                healthViewModel.addHealthObject(healthObject, animal, petName)
                activeDialog = null
            },
            petId, animal
        )
    }
    if (isDeleteOpened) {
        DeleteDialog(
            {
                isDeleteOpened = false
                idForDelete = null
            }
        ) {
            isDeleteOpened = false
            healthViewModel.deleteHealthObject(idForDelete!!, HealthCategory.VACCINE, petName)
            idForDelete = null
        }
    }
    if (activeDialog == DialogsVaccines.LEARN_MORE) {
        LearnMore(animal) { activeDialog = null }
    }
}