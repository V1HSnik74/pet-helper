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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.HealthTip
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.PagerCardItem
import com.example.pethelper.compose.patterns.StatusProgBar
import com.example.pethelper.db.entity.Prevention
import com.example.pethelper.db.viewModel.PreventionViewModel

@Composable
fun PreventionTab(preventionViewModel: PreventionViewModel, petId: Int, petName: String) {
    val allUpcomingPrevention by preventionViewModel.getAllUpcomingPreventionsByPet(petId)
        .collectAsState(emptyList())
    val preventionHistory by preventionViewModel.getPreventionsHistoryByPet(petId)
        .collectAsState(emptyList())
    val percentage = getPreventionPercentage(preventionHistory)
    val isProtectedFleas =
        remember(preventionHistory) {
            preventionHistory.any {
                it.action.contains(
                    "flea",
                    ignoreCase = true
                )
            }
        }
    val isProtectedTicks =
        remember(preventionHistory) {
            preventionHistory.any {
                it.action.contains(
                    "tick",
                    ignoreCase = true
                )
            }
        }
    val isProtectedWorms =
        remember(preventionHistory) {
            preventionHistory.any {
                it.action.contains(
                    "worm",
                    ignoreCase = true
                )
            }
        }
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
            allUpcomingPrevention
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
            Color(0xFFF3F6FA),
            Color(0xFF265896),
            Color(0xFFD2E2F6),
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

private fun getPreventionPercentage(preventionHistory: List<Prevention>): Float {
    if (preventionHistory.isEmpty()) return 0f
    val preventions = listOf("flea", "tick", "worm")
    val donePreventions = preventionHistory.map { it.action }
    val userCount = preventions.count { requiredPrevention ->
        donePreventions.any { it.contains(requiredPrevention, ignoreCase = true) }
    }
    return userCount.toFloat() / preventions.size
}