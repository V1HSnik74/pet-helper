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
import com.example.pethelper.db.AllergyViewModel
import com.example.pethelper.db.FoodItemViewModel
import com.example.pethelper.db.NoteNutritionViewModel
import com.example.pethelper.db.NoteViewModel
import com.example.pethelper.db.PetsViewModel
import com.example.pethelper.db.SupplementViewModel
import com.example.pethelper.db.TreatViewModel

class MainActivity : ComponentActivity() {
    private val petsViewModel: PetsViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val supplementViewModel: SupplementViewModel by viewModels()
    private val allergyViewModel: AllergyViewModel by viewModels()
    private val foodItemViewModel: FoodItemViewModel by viewModels()
    private val treatViewModel: TreatViewModel by viewModels()
    private val noteNutritionViewModel: NoteNutritionViewModel by viewModels()
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
                        treatViewModel
                    )
                }
            }
        }
    }
}
