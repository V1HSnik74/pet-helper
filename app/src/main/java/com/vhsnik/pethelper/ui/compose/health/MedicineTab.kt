package com.vhsnik.pethelper.ui.compose.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.data.entity.HealthCategory
import com.vhsnik.pethelper.data.entity.HealthObject
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.PastelleColors
import com.vhsnik.pethelper.ui.viewModel.HealthViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MedicineTab(
    healthViewModel: HealthViewModel = hiltViewModel(),
    petId: Int,
    petName: String,
    animal: String
) {
    val allMedicines by remember {
        healthViewModel.getCategoryHealthObjects(
            HealthCategory.MEDICINE
        )
    }.collectAsStateWithLifecycle()
    val allUpcomingMedicines by remember {
        healthViewModel.getAllUpcomingCategory(HealthCategory.MEDICINE)
    }.collectAsStateWithLifecycle()
    var isAddMedicationOpened by remember { mutableStateOf(false) }
    var medicineToEdit by remember { mutableStateOf<HealthObject?>(null) }
    var idToDelete by remember { mutableStateOf<Long?>(null) }
    var isDeleteOpened by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 2.dp, end = 2.dp, bottom = 20.dp, top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PagerCard(
            "Upcoming",
            null,
            allUpcomingMedicines
        ) { item, hasLine ->
            val dateTime =
                LocalDateTime.parse(item.nextTaking, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val date =
                dateTime.format(dateParser)
            val time =
                dateTime.format(timeParser)
            PagerCardItem(
                R.drawable.next_up,
                item.name,
                item.note.orEmpty(),
                date,
                time,
                true,
                hasAddInfoRight = true,
                hasLine = hasLine,
                hasNote = !item.note.isNullOrEmpty(), {}, {}, hasMenu = false
            )
        }
        PagerCard(
            "All medications",
            { isAddMedicationOpened = true },
            allMedicines
        ) { item, hasLine ->
            PagerCardItem(
                R.drawable.next_up,
                item.name,
                item.note.orEmpty(),
                "${item.intervalTime} time(s) a ${item.intervalPeriod}",
                "",
                hasAddInfoLeft = true,
                hasAddInfoRight = false,
                hasLine = hasLine,
                hasNote = !item.note.isNullOrEmpty(), {
                    isDeleteOpened = true
                    idToDelete = item.id
                }, {
                    medicineToEdit = item
                    isAddMedicationOpened = true
                }
            )
        }
        HealthTip(
            PastelleColors.YELLOW.color,
            PastelleColors.YELLOW.iconColor,
            PastelleColors.YELLOW.borderColor,
            "Medication Tip",
            "Give medications with food when recommended to avoid upset stomach",
            R.drawable.bulb
        )
    }
    if (isAddMedicationOpened) {
        AddMedicineDialog(
            {
                isAddMedicationOpened = false
                medicineToEdit = null
            },
            {
                if (medicineToEdit == null)
                    healthViewModel.addHealthObject(it, animal, petName)
                else
                    healthViewModel.updateHealthObject(
                        it.copy(id = medicineToEdit!!.id),
                        animal,
                        petName
                    )
                isAddMedicationOpened = false
                medicineToEdit = null
            },
            petId,
            medicineToEdit?.name.orEmpty(),
            medicineToEdit?.note.orEmpty(),
            medicineToEdit?.time ?: "12:00",
            medicineToEdit?.intervalTime ?: "1",
            medicineToEdit?.intervalPeriod ?: "day",
            medicineToEdit?.intervalValue ?: "",
            medicineToEdit?.isNotif ?: false,
            medicineToEdit?.timeNotif ?: "5 minutes before"
        )
    }
    if (isDeleteOpened) {
        DeleteDialog({
            idToDelete = null
            isDeleteOpened = false
        }) {
            healthViewModel.deleteHealthObject(idToDelete!!, HealthCategory.MEDICINE, petName)
            idToDelete = null
            isDeleteOpened = false
        }
    }
}