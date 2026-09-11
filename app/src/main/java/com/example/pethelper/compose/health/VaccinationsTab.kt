package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.PagerCardItem
import com.example.pethelper.compose.patterns.StatusProgBar
import com.example.pethelper.compose.patterns.UpcomingBlock
import com.example.pethelper.db.entity.Vaccine
import com.example.pethelper.db.viewModel.VaccineViewModel

@Composable
fun VaccinationsTab(vaccineViewModel: VaccineViewModel, petId: Int, petName: String) {
    val allUpcomingVaccines by
    vaccineViewModel.allUpcomingVaccines(petId)
        .collectAsState(emptyList())
    val vaccineHistory by
    vaccineViewModel.vaccineHistory(petId)
        .collectAsState(emptyList())
    val theMostUpcomingVaccine = allUpcomingVaccines.firstOrNull()
    var isReminderOpened by remember { mutableStateOf(false) }
    var isAddUpcomingDialogOpened by remember { mutableStateOf(false) }
    var isAddHistoryVaccineOpened by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusProgBar(
                true,
                countVaccinePercentage(vaccineHistory, catVaccines),
                "",
                {},
                modifier = if (theMostUpcomingVaccine != null) Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                else Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight()
            )
            theMostUpcomingVaccine?.let {
                UpcomingBlock(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    "Vaccine Reminder",
                    true,
                    theMostUpcomingVaccine.name,
                    theMostUpcomingVaccine.date,
                    theMostUpcomingVaccine.time
                ) {
                    isReminderOpened = true
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        PagerCard(
            "Core Vaccines",
            { isAddHistoryVaccineOpened = true },
            vaccineHistory
        ) { item, hasLine ->
            PagerCardItem(
                R.drawable.core_vaccine_icon,
                item.name,
                "",
                item.date,
                item.time,
                hasAddInfoLeft = false,
                hasAddInfoRight = false,
                hasLine = hasLine,
                hasNote = false
            )
        }
    }
    if (isReminderOpened) {
        Dialog(
            { isReminderOpened = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PagerCard(
                "Upcoming",
                { isAddUpcomingDialogOpened = true },
                allUpcomingVaccines
            ) { item, hasLine ->
                PagerCardItem(
                    R.drawable.core_vaccine_icon,
                    item.name,
                    "",
                    item.date,
                    item.time,
                    hasAddInfoLeft = false,
                    hasAddInfoRight = false,
                    hasLine = hasLine,
                    hasNote = false
                )
            }
        }
    }
    if (isAddUpcomingDialogOpened) {
        AddUpcomingVaccine(
            { isAddUpcomingDialogOpened = false },
            { name, date, time, isNotif, notifDate, notifTime, petId ->
                vaccineViewModel.addVaccine(
                    name,
                    date,
                    time,
                    isNotif,
                    notifDate,
                    notifTime,
                    petId,
                    petName,
                    false
                )
            }, petId
        )
    }
    if (isAddHistoryVaccineOpened) {
        AddCoreVaccineDialog(
            { isAddHistoryVaccineOpened = false },
            { name, date, time, isNotif, notifDate, notifTime, isDone, petId ->
                vaccineViewModel.addVaccine(
                    name,
                    date,
                    time,
                    isNotif,
                    notifDate,
                    notifTime,
                    petId,
                    petName,
                    isDone
                )
            },
            petId
        )
    }
}

private fun countVaccinePercentage(vaccines: List<Vaccine>, vaccinesList: List<String>): Float {
    if (vaccinesList.isEmpty()) return 0f
    val doneVaccines = vaccines.map { it.name }
    val userCount = vaccinesList.count { requiredVac ->
        doneVaccines.any { it.contains(requiredVac, ignoreCase = true) }
    }
    return userCount.toFloat() / vaccinesList.size
}