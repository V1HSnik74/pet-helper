package com.example.pethelper.ui.compose.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pethelper.R
import com.example.pethelper.data.entity.*
import com.example.pethelper.ui.compose.patterns.*
import com.example.pethelper.ui.theme.*
import com.example.pethelper.ui.viewModel.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val checkUpTips = listOf(
    "Physical examination", "Weight & body condition check", "Heart & lung check",
    "Vaccination review", "Health advice & recommendations"
)

private enum class DialogsCheckUp {
    UPCOMING, HISTORY, NOTE
}

@Composable
fun CheckUpTab(
    healthViewModel: HealthViewModel = hiltViewModel<HealthViewModel>(),
    petId: Int,
    noteViewModel: NoteViewModel = hiltViewModel(),
    petName: String,
    animal: String
) {
    val allUpcomingCheckUps by remember(petId) {
        healthViewModel.getAllUpcomingCategory(
            petId,
            HealthCategory.CHECKUP
        )
    }.collectAsStateWithLifecycle(emptyList())
    val checkUpsHistory by remember(petId) {
        healthViewModel.getAllHistoryCategory(
            petId,
            HealthCategory.CHECKUP
        )
    }.collectAsStateWithLifecycle(emptyList())
    val allNotes by remember(petId) { noteViewModel.getAllCheckUpNotes(petId) }
        .collectAsStateWithLifecycle(emptyList())
    val mostUpcomingCheckUp = remember(allUpcomingCheckUps) { allUpcomingCheckUps.firstOrNull() }
    var activeDialog by remember { mutableStateOf<DialogsCheckUp?>(null) }
    var isDeleteDialog by remember { mutableStateOf(false) }
    var checkUpHistoryToEdit by remember { mutableStateOf<HealthObject?>(null) }
    var checkUpUpcToEdit by remember { mutableStateOf<HealthObject?>(null) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var actionToDelete by remember { mutableStateOf<(() -> Unit)?>(null) }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 2.dp, end = 2.dp, bottom = 20.dp, top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (mostUpcomingCheckUp != null) {
            NextCheckUpFrame(
                mostUpcomingCheckUp.name,
                mostUpcomingCheckUp.date!!,
                mostUpcomingCheckUp.time!!
            )
        }
        PagerCard(
            "Upcoming",
            {
                activeDialog =
                    DialogsCheckUp.UPCOMING
            },
            allUpcomingCheckUps
        ) { item, hasLine ->
            CardItem(
                item, hasLine,
                {
                    isDeleteDialog = true
                    actionToDelete = {
                        healthViewModel.deleteHealthObject(
                            item.id,
                            HealthCategory.CHECKUP,
                            petName
                        )
                    }
                },
                {
                    activeDialog = DialogsCheckUp.UPCOMING
                    checkUpUpcToEdit = item
                },
                true,
                { healthViewModel.markAsDone(item, petName) }
            )
        }
        PagerCard(
            "Check-up History",
            {
                activeDialog =
                    DialogsCheckUp.HISTORY
            },
            checkUpsHistory
        ) { item, hasLine ->
            CardItem(
                item, hasLine, {
                    isDeleteDialog = true
                    actionToDelete = {
                        healthViewModel.deleteHealthObject(
                            item.id,
                            HealthCategory.CHECKUP,
                            petName
                        )
                    }
                },
                {
                    activeDialog = DialogsCheckUp.UPCOMING
                    checkUpHistoryToEdit = item
                })
        }
        PagerCard(
            "Notes",
            {
                activeDialog =
                    DialogsCheckUp.NOTE
            },
            allNotes
        ) { item, hasLine ->
            PagerCardItem(
                R.drawable.note_checkup,
                item.description,
                "",
                item.date!!,
                "",
                hasAddInfoLeft = false,
                hasAddInfoRight = false,
                hasLine = hasLine,
                hasNote = false,
                {
                    isDeleteDialog = true
                    actionToDelete = { noteViewModel.deleteNote(item.id) }
                }, {
                    noteToEdit = item
                    activeDialog = DialogsCheckUp.NOTE
                }
            )
        }
        CheckUpTipsFrame()
    }
    if (activeDialog == DialogsCheckUp.UPCOMING) {
        val itemToEdit = checkUpHistoryToEdit ?: checkUpUpcToEdit
        val isHistory = checkUpHistoryToEdit != null
        AddUpcomingCheckUp(
            {
                activeDialog = null
                checkUpHistoryToEdit = null
                checkUpUpcToEdit = null
            },
            { ho ->
                if (itemToEdit == null)
                    healthViewModel.addHealthObject(ho, animal, petName)
                else {
                    healthViewModel.updateHealthObject(ho.copy(id = itemToEdit.id), animal, petName)
                }
                activeDialog = null
                checkUpHistoryToEdit = null
                checkUpUpcToEdit = null
            },
            petId,
            isHistory,
            if (isHistory) "History" else "Upcoming",
            itemToEdit != null,
            itemToEdit?.name.orEmpty(),
            itemToEdit?.note.orEmpty(),
            itemToEdit?.date?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
                ?: LocalDate.now(),
            itemToEdit?.time.orEmpty(),
            itemToEdit?.isNotif ?: false,
            itemToEdit?.dateNotif ?: "same day",
            itemToEdit?.timeNotif.orEmpty()
        )
    }
    if (activeDialog == DialogsCheckUp.HISTORY) {
        AddCheckUpHistory(
            { activeDialog = null },
            { ho ->
                healthViewModel.addHealthObject(
                    ho,
                    animal,
                    petName
                )
                activeDialog = null
            },
            petId
        )
    }
    if (activeDialog == DialogsCheckUp.NOTE) {
        AddHealthNoteDialog(
            {
                activeDialog = null
                noteToEdit = null
            },
            { note, date, petId ->
                if (noteToEdit == null)
                    noteViewModel.addNote(note, null, petId, NoteSection.CHECK_UPS, date)
                else
                    noteViewModel.updateNote(noteToEdit!!.copy(description = note, date = date))
                activeDialog = null
                noteToEdit = null
            },
            petId,
            noteToEdit?.description.orEmpty(),
            noteToEdit?.date?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
                ?: LocalDate.now()
        )
    }
    if (isDeleteDialog) {
        DeleteDialog(
            {
                isDeleteDialog = false
                actionToDelete = null
            }
        ) {
            actionToDelete?.invoke()
            isDeleteDialog = false
            actionToDelete = null
        }
    }
}

@Composable
fun NextCheckUpFrame(name: String, date: String, time: String) {
    val localDate = remember(date) { LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE) }
    val dateFormatted = remember(date) { localDate.format(dateParser) }
    val currDay = remember { LocalDate.now() }
    val inDate = remember(localDate) { ChronoUnit.DAYS.between(currDay, localDate) }
    Card(
        Modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(PastelleColors.PURPLE.color),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, PastelleColors.PURPLE.borderColor)
    ) {
        Row(
            Modifier
                .wrapContentSize()
                .padding(16.dp, 12.dp)
        ) {
            Image(painterResource(R.drawable.next_checkup_icon), "next check-up icon")
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                TextMaker(
                    "Next Check-up: $name",
                    12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextMaker(
                        "$dateFormatted $time",
                        12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    TextMaker(
                        "in $inDate days",
                        12.sp,
                        red
                    )
                }
            }
        }
    }
}

@Composable
fun CheckUpTipsFrame() {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            TextMaker(
                "What's Included?",
                12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            Column(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                checkUpTips.forEach {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painterResource(R.drawable.green_tick), "tick icon")
                        Spacer(Modifier.width(8.dp))
                        TextMaker(
                            it,
                            12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(
    item: HealthObject,
    hasLine: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    hasMarkAsDone: Boolean = false,
    onMarkAsDoneClick: () -> Unit = {}
) {
    PagerCardItem(
        R.drawable.checkup_icon,
        item.name,
        item.note.orEmpty(),
        item.date!!,
        item.time!!,
        hasAddInfoLeft = true,
        hasAddInfoRight = true,
        hasLine,
        !item.note.isNullOrEmpty(),
        {
            onDelete()
        },
        {
            onEdit()
        },
        hasMarkAsDone,
        onMarkAsDoneClick
    )
}