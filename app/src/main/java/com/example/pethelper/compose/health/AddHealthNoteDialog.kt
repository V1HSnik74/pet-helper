package com.example.pethelper.compose.health

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndDateTime
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.PopupCalendar
import com.example.pethelper.compose.patterns.dateParser
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun AddHealthNoteDialog(
    onDismiss: () -> Unit,
    onAddNote: (note: String, date: String, petId: Int, category: String) -> Unit,
    petId: Int,
    category: String
) {
    val curDay = remember { LocalDate.now() }
    val curMonth = remember { YearMonth.now() }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(curDay) }
    var isDateOpened by remember { mutableStateOf(false) }
    DialogPattern(
        "Add Note",
        R.drawable.note_icon,
        onDismiss,
        {
            onAddNote(
                note,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                petId,
                category
            )
        },
        note.isNotEmpty()
    ) {
        LabelAndTextField(
            "Note",
            note,
            { note = it },
            "Write your note here",
            false,
            Alignment.TopStart
        )
        Spacer(Modifier.height(16.dp))
        Box {
            LabelAndDateTime(
                "Date",
                date.format(dateParser)
            ) { isDateOpened = true }
            if (isDateOpened) {
                PopupCalendar(
                    { isDateOpened = false },
                    {
                        date = it
                        isDateOpened = false
                    },
                    date,
                    { it <= curDay },
                    curMonth.minusYears(2),
                    curMonth
                )
            }
        }
    }
}