package com.vhsnik.pethelper.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhsnik.pethelper.data.entity.Note
import com.vhsnik.pethelper.data.entity.NoteSection
import com.vhsnik.pethelper.data.repository.interfaces.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NotesRepository
) : ViewModel() {
    private val curPetId = MutableStateFlow<Int?>(null)
    fun setCurPetId(petId: Int) {
        curPetId.value = petId
    }

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allNotes: StateFlow<List<Note>> =
        curPetId.filterNotNull().flatMapLatest { id ->
            _selectedCategory.flatMapLatest {
                if (it == "All") {
                    noteRepository.getAllNotesBySection(id, NoteSection.COMMON)
                } else {
                    noteRepository.getAllCommonNotesByCategory(id, it)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNote(
        description: String,
        category: String? = null,
        section: NoteSection,
        date: String? = null
    ) {
        val petId = curPetId.value ?: return
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val allOverallNotes: StateFlow<List<Note>> =
        curPetId.filterNotNull().flatMapLatest {
            noteRepository.getAllNotesBySection(it, NoteSection.OVERALL_HEALTH)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    val allCheckUpNotes: StateFlow<List<Note>> =
        curPetId.filterNotNull().flatMapLatest {
            noteRepository.getAllNotesBySection(it, NoteSection.CHECK_UPS)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}