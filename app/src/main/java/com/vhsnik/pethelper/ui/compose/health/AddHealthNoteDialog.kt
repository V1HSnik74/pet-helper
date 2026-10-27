package com.vhsnik.pethelper.ui.compose.health

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.compose.utils.*
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun AddHealthNoteDialog(
    onDismiss: () -> Unit,
    onAddNote: (note: String, date: String, petId: Int) -> Unit,
    petId: Int,
    noteInit: String,
    date: LocalDate
) {
    val curMonth = remember { YearMonth.now() }
    var note by remember { mutableStateOf(noteInit) }
    var date by remember { mutableStateOf(date) }
    var isDateOpened by remember { mutableStateOf(false) }
    DialogPattern(
        if (noteInit.isNotEmpty()) "Update Note" else "Add Note",
        R.drawable.note_icon,
        onDismiss,
        {
            onAddNote(
                note,
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                petId
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
            Alignment.TopStart,
            100.dp,
            16.dp,
            16.dp
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
                    { it <= LocalDate.now() },
                    curMonth.minusYears(2),
                    curMonth
                )
            }
        }
    }
}