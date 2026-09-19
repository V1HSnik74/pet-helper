package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.HealthTip
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.PagerCardItem
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.compose.patterns.timeParser
import com.example.pethelper.db.viewModel.MedicineViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MedicineTab(medicineViewModel: MedicineViewModel, petId: Int, petName: String) {
    val allMedicines by medicineViewModel.getAllMedicinesByPet(petId).collectAsState(emptyList())
    val allUpcomingMedicines = remember(allMedicines) { allMedicines.sortedBy { it.nextTaking } }
    var isAddMedicationOpened by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 2.dp, end = 2.dp, bottom = 20.dp, top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PagerCard("Upcoming", null, allUpcomingMedicines) { item, hasLine ->
            val dateTime = LocalDateTime.parse(item.nextTaking, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val date = dateTime.format(dateParser)
            val time = dateTime.format(timeParser)
            PagerCardItem(
                R.drawable.next_up,
                item.name,
                item.note.orEmpty(),
                date,
                time,
                true,
                hasAddInfoRight = true,
                hasLine = hasLine,
                hasNote = !item.note.isNullOrEmpty()
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
                "${item.intervalTimes} time(s) a ${item.intervalPeriod}",
                "",
                hasAddInfoLeft = true,
                hasAddInfoRight = false,
                hasLine = hasLine,
                hasNote = !item.note.isNullOrEmpty()
            )
        }
        HealthTip(
            Color(0xFFFFF6E8),
            Color(0xFFD27918),
            Color(0xFFFDF1ED),
            "Medication Tip",
            "Give medications with food when recommended to avoid upset stomach",
            R.drawable.bulb
        )
    }
    if (isAddMedicationOpened) {
        AddMedicineDialog(
            { isAddMedicationOpened = false },
            { medicine, note, time, intervalTimes, intervalPeriod, isNotif, timeNotif, intervalValue, petId ->
                medicineViewModel.addMedicine(
                    medicine,
                    note,
                    time,
                    intervalTimes,
                    intervalPeriod,
                    isNotif,
                    timeNotif,
                    petId,
                    petName,
                    intervalValue
                )
                isAddMedicationOpened = false
            },
            petId
        )
    }
}