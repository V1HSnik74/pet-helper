package com.vhsnik.pethelper.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhsnik.pethelper.data.entity.Note
import com.vhsnik.pethelper.data.entity.NoteSection
import com.vhsnik.pethelper.data.repository.interfaces.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NotesRepository
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun allNotes(petId: Int): Flow<List<Note>> {
        return _selectedCategory.flatMapLatest {
            if (it == "All") {
                noteRepository.getAllNotesBySection(petId, NoteSection.COMMON)
            } else {
                noteRepository.getAllCommonNotesByCategory(petId, it)
            }
        }
    }

    fun addNote(
        description: String,
        category: String? = null,
        petId: Int,
        section: NoteSection,
        date: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.addNote(
                Note(
                    description,
                    petId,
                    section,
                    date,
                    category
                )
            )
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateNote(note)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(id)
        }
    }

    fun getAllOverallNotes(petId: Int): Flow<List<Note>> {
        return noteRepository.getAllNotesBySection(petId, NoteSection.OVERALL_HEALTH)
    }

    fun getAllCheckUpNotes(petId: Int): Flow<List<Note>> {
        return noteRepository.getAllNotesBySection(petId, NoteSection.CHECK_UPS)
    }
}