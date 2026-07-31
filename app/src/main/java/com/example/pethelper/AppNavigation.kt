package com.example.pethelper

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pethelper.compose.MyPetScreen
import com.example.pethelper.compose.PetProfileScreen
import com.example.pethelper.compose.PetSelectionScreen
import com.example.pethelper.db.PetsViewModel

sealed class Screen(val route: String){
    object Selection: Screen("PetSelectionScreen")
    object Details: Screen("MyPetScreen/{petId}"){
        fun createRoute(petId: Int) = "MyPetScreen/$petId"
    }
    object Profile: Screen("PetProfileScreen/{petId}"){
        fun createRoute(petId: Int) = "PetProfileScreen/$petId"
    }
}

@Composable
fun AppNavigation(viewModel: PetsViewModel){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Selection.route
    ){
        composable(Screen.Selection.route){
            PetSelectionScreen(viewModel = viewModel, onPetClick = {
                    id -> navController.navigate(Screen.Details.createRoute(id))
            })
        }

        composable(Screen.Details.route,
            arguments = listOf(navArgument("petId") {type = NavType.IntType})){
                backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: -1
            MyPetScreen(viewModel, petId, onCardClick = {
                id -> navController.navigate(Screen.Profile.createRoute(id))
            })
        }

        composable(Screen.Profile.route,
            arguments = listOf(navArgument("petId") {type = NavType.IntType})){
                backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: -1
            PetProfileScreen(viewModel, petId)
        }
    }
}