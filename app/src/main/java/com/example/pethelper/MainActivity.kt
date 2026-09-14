package com.example.pethelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.pethelper.db.viewModel.AllergyViewModel
import com.example.pethelper.db.viewModel.CheckUpViewModel
import com.example.pethelper.db.viewModel.FoodItemViewModel
import com.example.pethelper.db.viewModel.NoteHealthViewModel
import com.example.pethelper.db.viewModel.NoteNutritionViewModel
import com.example.pethelper.db.viewModel.NoteViewModel
import com.example.pethelper.db.viewModel.PetsViewModel
import com.example.pethelper.db.viewModel.PreventionViewModel
import com.example.pethelper.db.viewModel.SupplementViewModel
import com.example.pethelper.db.viewModel.TreatViewModel
import com.example.pethelper.db.viewModel.VaccineViewModel

class MainActivity : ComponentActivity() {
    private val petsViewModel: PetsViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val supplementViewModel: SupplementViewModel by viewModels()
    private val allergyViewModel: AllergyViewModel by viewModels()
    private val foodItemViewModel: FoodItemViewModel by viewModels()
    private val treatViewModel: TreatViewModel by viewModels()
    private val noteNutritionViewModel: NoteNutritionViewModel by viewModels()
    private val vaccineViewModel: VaccineViewModel by viewModels()
    private val preventionViewModel: PreventionViewModel by viewModels()
    private val checkUpViewModel: CheckUpViewModel by viewModels()
    private val healthNoteViewModel: NoteHealthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        petsViewModel,
                        noteViewModel,
                        foodItemViewModel,
                        allergyViewModel,
                        supplementViewModel,
                        noteNutritionViewModel,
                        treatViewModel,
                        vaccineViewModel,
                        preventionViewModel,
                        checkUpViewModel,
                        healthNoteViewModel
                    )
                }
            }
        }
    }
}
