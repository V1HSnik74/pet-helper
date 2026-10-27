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
import com.vhsnik.pethelper.ui.viewModel.*
import java.time.LocalDate

private enum class DialogsPrevention {
    UPCOMING, HISTORY
}

@Composable
fun PreventionTab(
    healthViewModel: HealthViewModel = hiltViewModel(),
    petId: Int,
    petName: String,
    animal: String
) {
    val allUpcomingPreventions by remember(petId) {
        healthViewModel.getAllUpcomingCategory(petId, HealthCategory.PREVENTION)
    }.collectAsStateWithLifecycle(emptyList())
    val preventionHistory by remember(petId) {
        healthViewModel.getAllHistoryCategory(petId, HealthCategory.PREVENTION)
    }.collectAsStateWithLifecycle(emptyList())
    val preventionCompletionInfo by remember(petId) {
        healthViewModel.getPreventionPercentage(petId)
    }.collectAsStateWithLifecycle(
        PreventionStatus(
            0f,
            isProtectedFleas = false,
            isProtectedWorms = false,
            isProtectedTicks = false
        )
    )
    var activeDialog by remember { mutableStateOf<DialogsPrevention?>(null) }
    var isDeleteOpened by remember { mutableStateOf(false) }
    var idToDelete by remember { mutableStateOf<Long?>(null) }
    var preventionHistoryToEdit by remember { mutableStateOf<HealthObject?>(null) }
    var preventionUpcToEdit by remember { mutableStateOf<HealthObject?>(null) }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 2.dp, end = 2.dp, bottom = 20.dp, top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusProgBar(
            false,
            preventionCompletionInfo.percentage,
            if (preventionCompletionInfo.percentage == 1f) "$petName is protected against all parasites" else "$petName is not totally protected from parasites",
            isProtectedFleas = preventionCompletionInfo.isProtectedFleas,
            isProtectedTicks = preventionCompletionInfo.isProtectedTicks,
            isProtectedWorms = preventionCompletionInfo.isProtectedWorms,
            modifier = Modifier.wrapContentWidth()
        )
        PagerCard(
            "Upcoming",
            {
                activeDialog =
                    DialogsPrevention.UPCOMING
            },
            allUpcomingPreventions
        ) { item, hasLine ->
            PagerCardItem(
                if (item.name.contains(
                        "worm",
                        ignoreCase = true
                    )
                ) R.drawable.worm_icon else R.drawable.bug_icon,
                item.name,
                item.note.orEmpty(),
                item.date!!,
                "",
                hasAddInfoLeft = true,
                hasAddInfoRight = false,
                hasLine,
                !item.note.isNullOrEmpty(),
                {
                    isDeleteOpened = true
                    idToDelete = item.id
                },
                {
                    activeDialog = DialogsPrevention.UPCOMING
                    preventionUpcToEdit = item
                },
                true,
                { healthViewModel.markAsDone(item, petName) }
            )
        }
        PagerCard(
            "Treatment History",
            {
                activeDialog =
                    DialogsPrevention.HISTORY
            },
            preventionHistory
        ) { item, hasLine ->
            PagerCardItem(
                if (item.name.contains(
                        "worm",
                        ignoreCase = true
                    )
                ) R.drawable.worm_icon else R.drawable.bug_icon,
                item.name,
                item.note.orEmpty(),
                item.date!!,
                "",
                hasAddInfoLeft = true,
                hasAddInfoRight = false,
                hasLine,
                !item.note.isNullOrEmpty(),
                {
                    isDeleteOpened = true
                    idToDelete = item.id
                },
                {
                    activeDialog = DialogsPrevention.UPCOMING
                    preventionHistoryToEdit = item
                }
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
    if (activeDialog == DialogsPrevention.UPCOMING) {
        val itemToEdit = preventionHistoryToEdit ?: preventionUpcToEdit
        val isHistory = preventionHistoryToEdit != null
        AddUpcomingPreventionDialog(
            {
                activeDialog = null
                preventionUpcToEdit = null
                preventionHistoryToEdit = null
            },
            {
                if (itemToEdit != null)
                    healthViewModel.updateHealthObject(it.copy(id = itemToEdit.id), animal, petName)
                else
                    healthViewModel.addHealthObject(it, animal, petName)
                activeDialog = null
                preventionUpcToEdit = null
                preventionHistoryToEdit = null
            },
            petId,
            isHistory,
            if (isHistory) "History" else "Upcoming",
            itemToEdit != null,
            itemToEdit?.name.orEmpty(),
            itemToEdit?.note.orEmpty(),
            itemToEdit?.date?.let { LocalDate.parse(it) } ?: LocalDate.now(),
            itemToEdit?.isNotif ?: false,
            itemToEdit?.dateNotif ?: "same day",
            itemToEdit?.timeNotif.orEmpty()
        )
    }
    if (activeDialog == DialogsPrevention.HISTORY) {
        AddTreatmentHistoryDialog(
            { activeDialog = null },
            { healthObject ->
                healthViewModel.addHealthObject(healthObject, animal, petName)
                activeDialog = null
            },
            petId
        )
    }
    if (isDeleteOpened) {
        DeleteDialog(
            {
                isDeleteOpened = false
                idToDelete = null
            }
        ) {
            healthViewModel.deleteHealthObject(idToDelete!!, HealthCategory.PREVENTION, petName)
            isDeleteOpened = false
            idToDelete = null
        }
    }
}