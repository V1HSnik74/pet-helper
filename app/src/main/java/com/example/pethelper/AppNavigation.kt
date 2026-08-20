package com.example.pethelper

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pethelper.compose.MyPetScreen
import com.example.pethelper.compose.NotesScreen
import com.example.pethelper.compose.NutritionScreen
import com.example.pethelper.compose.PetProfileScreen
import com.example.pethelper.compose.PetSelectionScreen
import com.example.pethelper.db.AllergyViewModel
import com.example.pethelper.db.FoodItemViewModel
import com.example.pethelper.db.NoteNutritionViewModel
import com.example.pethelper.db.NoteViewModel
import com.example.pethelper.db.PetsViewModel
import com.example.pethelper.db.SupplementViewModel
import com.example.pethelper.db.TreatViewModel

sealed class Screen(val route: String) {
    object Selection : Screen("PetSelectionScreen")
    object Details : Screen("MyPetScreen/{petId}") {
        fun createRoute(petId: Int) = "MyPetScreen/$petId"
    }

    object Profile : Screen("PetProfileScreen/{petId}") {
        fun createRoute(petId: Int) = "PetProfileScreen/$petId"
    }

    object Notes : Screen("NotesScreen/{petId}") {
        fun createRoute(petId: Int) = "NotesScreen/$petId"
    }

    object Nutrition : Screen("NutritionScreen/{petId}") {
        fun createRoute(petId: Int) = "NutritionScreen/$petId"
    }
}

@Composable
fun AppNavigation(
    viewModel: PetsViewModel,
    noteViewModel: NoteViewModel,
    foodItemViewModel: FoodItemViewModel,
    allergyViewModel: AllergyViewModel,
    supplementViewModel: SupplementViewModel,
    noteNutritionViewModel: NoteNutritionViewModel,
    treatViewModel: TreatViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Selection.route
    ) {
        composable(Screen.Selection.route) {
            PetSelectionScreen(viewModel = viewModel, onPetClick = {
                navController.navigate(Screen.Details.createRoute(it))
            })
        }

        composable(
            Screen.Details.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: -1
            MyPetScreen(
                viewModel, petId, onCardClick = {
                    navController.navigate(Screen.Profile.createRoute(it))
                }, onButtonClick = { navController.navigate(Screen.Notes.createRoute(it)) },
                onNutritionClick = { navController.navigate(Screen.Nutrition.createRoute(it)) })
        }

        composable(
            Screen.Profile.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: -1
            PetProfileScreen(viewModel, petId)
        }

        composable(
            Screen.Notes.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: -1
            NotesScreen(petId, noteViewModel)
        }

        composable(
            Screen.Nutrition.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) {
            val petId = it.arguments?.getInt("petId") ?: -1
            NutritionScreen(
                petId, foodItemViewModel, supplementViewModel,
                treatViewModel, noteNutritionViewModel, allergyViewModel,
                onBackClick = {navController.popBackStack()}
            )
        }

    }
}