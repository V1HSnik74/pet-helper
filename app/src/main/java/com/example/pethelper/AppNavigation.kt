package com.example.pethelper

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pethelper.compose.health.HealthScreen
import com.example.pethelper.compose.petScreens.MyPetScreen
import com.example.pethelper.compose.notes.NotesScreen
import com.example.pethelper.compose.nutrition.NutritionScreen
import com.example.pethelper.compose.petScreens.PetProfileScreen
import com.example.pethelper.compose.petScreens.PetSelectionScreen
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

    object Health : Screen("HealthScreen/{petId}") {
        fun createRoute(petId: Int) = "HealthScreen/$petId"
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
    treatViewModel: TreatViewModel,
    vaccineViewModel: VaccineViewModel,
    preventionViewModel: PreventionViewModel,
    checkUpViewModel: CheckUpViewModel,
    noteHealthViewModel: NoteHealthViewModel
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
                onNutritionClick = { navController.navigate(Screen.Nutrition.createRoute(it)) },
                onHealthClick = {navController.navigate(Screen.Health.createRoute(it))})
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
                onBackClick = { navController.popBackStack() },
                viewModel
            )
        }

        composable(
            Screen.Health.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) {
            val petId = it.arguments?.getInt("petId") ?: -1
            HealthScreen(
                petId,
                { navController.popBackStack() },
                viewModel,
                vaccineViewModel,
                preventionViewModel,
                checkUpViewModel,
                noteHealthViewModel
            )
        }

    }
}