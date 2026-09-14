package com.example.pethelper.compose.health

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.PagerCard
import com.example.pethelper.compose.patterns.PagerCardItem
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.compose.patterns.dateParser
import com.example.pethelper.db.entity.CheckUp
import com.example.pethelper.db.viewModel.CheckUpViewModel
import com.example.pethelper.db.viewModel.NoteHealthViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val checkUpTips = listOf(
    "Physical examination", "Weight & body condition check", "Heart & lung check",
    "Vaccination review", "Health advice & recommendations"
)

@Composable
fun CheckUpTab(
    checkUpViewModel: CheckUpViewModel,
    petId: Int,
    healthNoteViewModel: NoteHealthViewModel,
    petName: String
) {
    val allUpcomingCheckUps by checkUpViewModel.getAllUpcomingCheckUpsByPet(petId)
        .collectAsState(emptyList())
    val checkUpsHistory by checkUpViewModel.getCheckUpsHistoryByPet(petId)
        .collectAsState(emptyList())
    val allNotes by healthNoteViewModel.getAllCheckUpNotesByPet(petId)
        .collectAsState(emptyList())
    val mostUpcomingCheckUp = remember(allUpcomingCheckUps) { allUpcomingCheckUps.firstOrNull() }
    var isUpcomingOpened by remember { mutableStateOf(false) }
    var isAddCheckUpHistoryOpened by remember { mutableStateOf(false) }
    var isAddNoteOpened by remember { mutableStateOf(false) }
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
                mostUpcomingCheckUp.date,
                mostUpcomingCheckUp.time
            )
        }
        PagerCard(
            "Upcoming",
            { isUpcomingOpened = true },
            allUpcomingCheckUps
        ) { item, hasLine ->
            CardItem(item, hasLine)
        }
        PagerCard(
            "Check-up History",
            { isAddCheckUpHistoryOpened = true },
            checkUpsHistory
        ) { item, hasLine ->
            CardItem(item, hasLine)
        }
        PagerCard(
            "Notes",
            { isAddNoteOpened = true },
            allNotes
        ) { item, hasLine ->
            PagerCardItem(
                R.drawable.note_checkup,
                item.note,
                "",
                item.date,
                "",
                hasAddInfoLeft = false,
                hasAddInfoRight = false,
                hasLine = hasLine,
                hasNote = false
            )
        }
        CheckUpTipsFrame()
    }
    if (isUpcomingOpened) {
        AddUpcomingCheckUp(
            { isUpcomingOpened = false },
            { name, note, date, time, isNotif, dateNotif, timeNotif, petId ->
                checkUpViewModel.addCheckUp(
                    name,
                    note,
                    date,
                    time,
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
    if (isAddCheckUpHistoryOpened) {
        AddCheckUpHistory(
            { isAddCheckUpHistoryOpened = false },
            { name, note, date, time, isNotif, dateNotif, timeNotif, isDone, petId ->
                checkUpViewModel.addCheckUp(
                    name,
                    note,
                    date,
                    time,
                    isNotif,
                    dateNotif,
                    timeNotif,
                    petId,
                    petName,
                    isDone
                )
                isAddCheckUpHistoryOpened = false
            },
            petId
        )
    }
    if (isAddNoteOpened) {
        AddHealthNoteDialog(
            { isAddNoteOpened = false },
            { note, date, petId, category ->
                healthNoteViewModel.addNoteHealth(note, date, petId, category)
                isAddNoteOpened = false
            },
            petId,
            "CheckUp"
        )
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
        colors = CardDefaults.cardColors(Color(0xFFF8F2F9)),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, Color(0xFFD3C7F1))
    ) {
        Row(
            Modifier
                .wrapContentSize()
                .padding(16.dp, 12.dp)
        ) {
            Image(painterResource(R.drawable.next_checkup_icon), "next check-up icon")
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                TextMaker("Next Check-up", 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(16.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextMaker(name, 12.sp, fontWeight = FontWeight.SemiBold)
                    TextMaker("$dateFormatted $time", 12.sp, fontWeight = FontWeight.Normal)
                    TextMaker("in $inDate days", 12.sp, Color(0xFFCF4143))
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    {},
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    border = BorderStroke(1.dp, Color(0xFFD3C7F1)),
                    contentPadding = PaddingValues(16.dp, 8.dp),
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    TextMaker(
                        "Add to Calendar",
                        12.sp,
                        Color(0xFF554184),
                        fontWeight = FontWeight.SemiBold
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
            TextMaker("What's Included?", 12.sp, fontWeight = FontWeight.SemiBold)
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
                        TextMaker(it, 12.sp, fontWeight = FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(item: CheckUp, hasLine: Boolean) {
    PagerCardItem(
        R.drawable.checkup_icon,
        item.name,
        item.note.orEmpty(),
        item.date,
        item.time,
        hasAddInfoLeft = true,
        hasAddInfoRight = true,
        hasLine,
        !item.note.isNullOrEmpty()
    )
}