package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
    val percentage =
        remember(vaccineHistory) { countVaccinePercentage(vaccineHistory, catVaccines) }
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
                {},
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
                isReminderOpened = true
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
            Box(Modifier.fillMaxWidth(0.9f)) {
                PagerCard(
                    "Upcoming",
                    { isAddUpcomingDialogOpened = true },
                    allUpcomingVaccines,
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
                isAddUpcomingDialogOpened = false
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
                isAddHistoryVaccineOpened = false
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