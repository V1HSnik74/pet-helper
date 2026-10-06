package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.HealthTip
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.PagerCardItem
import com.example.pethelper.compose.patterns.StatusProgBar
import com.example.pethelper.db.entity.Prevention
import com.example.pethelper.db.viewModel.PreventionViewModel
import com.example.pethelper.ui.theme.PastelleColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PreventionStatus(
    val percentage: Float,
    val isProtectedFleas: Boolean,
    val isProtectedWorms: Boolean,
    val isProtectedTicks: Boolean
)

@Composable
fun PreventionTab(preventionViewModel: PreventionViewModel, petId: Int, petName: String) {
    val allUpcomingPreventions by preventionViewModel.getAllUpcomingPreventionsByPet(petId)
        .collectAsState(emptyList())
    val preventionHistory by preventionViewModel.getPreventionsHistoryByPet(petId)
        .collectAsState(emptyList())
    val preventionCompletionInfo = remember(preventionHistory, allUpcomingPreventions) {
        getPreventionPercentage(
            preventionHistory,
            allUpcomingPreventions
        )
    }
    val percentage = preventionCompletionInfo.percentage
    val isProtectedFleas = preventionCompletionInfo.isProtectedFleas
    val isProtectedTicks = preventionCompletionInfo.isProtectedTicks
    val isProtectedWorms = preventionCompletionInfo.isProtectedWorms
    var isUpcomingOpened by remember { mutableStateOf(false) }
    var isTreatmentHistoryOpened by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 2.dp, end = 2.dp, bottom = 20.dp, top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusProgBar(
            false,
            percentage,
            if (percentage == 1f) "$petName is protected against all parasites" else "$petName is not totally protected from parasites",
            isProtectedFleas = isProtectedFleas,
            isProtectedTicks = isProtectedTicks,
            isProtectedWorms = isProtectedWorms,
            modifier = Modifier.wrapContentWidth()
        )
        PagerCard(
            "Upcoming",
            { isUpcomingOpened = true },
            allUpcomingPreventions
        ) { item, hasLine ->
            PagerCardItem(
                if (item.action.contains(
                        "worm",
                        ignoreCase = true
                    )
                ) R.drawable.worm_icon else R.drawable.bug_icon,
                item.action,
                item.note.orEmpty(),
                item.date,
                "",
                hasAddInfoLeft = true,
                hasAddInfoRight = false,
                hasLine,
                !item.note.isNullOrEmpty()
            )
        }
        PagerCard(
            "Treatment History",
            { isTreatmentHistoryOpened = true },
            preventionHistory
        ) { item, hasLine ->
            PagerCardItem(
                if (item.action.contains(
                        "worm",
                        ignoreCase = true
                    )
                ) R.drawable.worm_icon else R.drawable.bug_icon,
                item.action,
                item.note.orEmpty(),
                item.date,
                "",
                hasAddInfoLeft = true,
                hasAddInfoRight = false,
                hasLine,
                !item.note.isNullOrEmpty()
            )
        }
        HealthTip(
            PastelleColors.BLUE.color,
            PastelleColors.BLUE.iconColor,
            PastelleColors.BLUE.borderColor,
            "Parasites Prevention Tips",
            "Year-round prevention is the best way to keep $petName healthy and comfortable",
            R.drawable.bulb
        )
    }
    if (isUpcomingOpened) {
        AddUpcomingPreventionDialog(
            { isUpcomingOpened = false },
            { action, note, date, isNotif, dateNotif, timeNotif, petId ->
                preventionViewModel.addPrevention(
                    action,
                    note,
                    date,
                    isNotif,
                    dateNotif,
                    timeNotif,
                    petId,
                    petName,
                    false
                )
                isUpcomingOpened = false
            },
            petId
        )
    }
    if (isTreatmentHistoryOpened) {
        AddTreatmentHistoryDialog(
            { isTreatmentHistoryOpened = false },
            { action, note, date, isNotif, dateNotif, timeNotif, petId, isDone ->
                preventionViewModel.addPrevention(
                    action,
                    note,
                    date,
                    isNotif,
                    dateNotif,
                    timeNotif,
                    petId,
                    petName,
                    isDone
                )
                isTreatmentHistoryOpened = false
            },
            petId
        )
    }
}

fun getPreventionPercentage(
    preventionHistory: List<Prevention>,
    upcomingPreventions: List<Prevention>
): PreventionStatus {
    if (preventionHistory.isEmpty()) return PreventionStatus(
        0f,
        isProtectedFleas = false,
        isProtectedWorms = false,
        isProtectedTicks = false
    )
    var isProtectedFleas = false
    var isProtectedWorms = false
    var isProtectedTicks = false
    val preventions = listOf("flea", "tick", "worm")
    val currDate = LocalDate.now()
    val donePreventions = preventionHistory.filter {
        val date = LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
        val monthsToAdd = if (it.action.contains("worm", true)) 3L else 1L
        val expirationDateDef = date.plusMonths(monthsToAdd)
        val dateDue = upcomingPreventions.find { prev ->
            prev.action.contains(it.action, true)
                    || it.action.contains(prev.action, true)
        }?.date.orEmpty()
        val expirationDate = if (dateDue.isEmpty()) expirationDateDef
        else {
            val dateDueLD = LocalDate.parse(dateDue, DateTimeFormatter.ISO_LOCAL_DATE)
            if (dateDueLD.isBefore(expirationDateDef)) dateDueLD else expirationDateDef
        }
        !expirationDate.isBefore(currDate)
    }.map { it.action }
        .toList()
    var userCount = 0
    for (prev in preventions) {
        val isDone = donePreventions.any {
            it.contains(prev, true)
        }
        if (isDone)
            userCount += 1
    }
    if (donePreventions.any {
            it.contains("flea", true)
        }) isProtectedFleas = true
    if (donePreventions.any {
            it.contains("tick", true)
        }) isProtectedTicks = true
    if (donePreventions.any {
            it.contains("worm", true)
        }) isProtectedWorms = true
    return PreventionStatus(
        userCount.toFloat() / 3,
        isProtectedFleas,
        isProtectedWorms,
        isProtectedTicks
    )
}