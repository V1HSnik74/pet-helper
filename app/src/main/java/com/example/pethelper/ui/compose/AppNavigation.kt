package com.example.pethelper.ui.compose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.pethelper.data.entity.HealthCategory
import com.example.pethelper.ui.compose.activity.ActivityScreen
import com.example.pethelper.ui.compose.health.HealthScreen
import com.example.pethelper.ui.compose.notes.NotesScreen
import com.example.pethelper.ui.compose.nutrition.NutritionScreen
import com.example.pethelper.ui.compose.reminder.RemindersScreen

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

    object Health : Screen("HealthScreen/{petId}?tab={tab}") {
        fun createRoute(petId: Int, tab: Int = 0) = "HealthScreen/$petId?tab=$tab"
    }

    object Reminders : Screen("RemindersScreen/{petId}") {
        fun createRoute(petId: Int) = "RemindersScreen/$petId"
    }

    object Activity : Screen("ActivityScreen/{petId}") {
        fun createRoute(petId: Int) = "ActivityScreen/$petId"
    }
}

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()
    val animDur = 250
    NavHost(
        navController = navController,
        startDestination = Screen.Selection.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(animDur)
            ) + fadeIn(tween(animDur))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(animDur)
            ) + fadeOut(tween(animDur))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(animDur)
            ) + fadeIn(tween(animDur))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(animDur)
            ) + fadeOut(tween(animDur))
        }
    ) {
        composable(Screen.Selection.route) {
            com.example.pethelper.ui.compose.petScreens.PetSelectionScreen(
                onPetClick = {
                    navController.navigate(
                        Screen.Details.createRoute(
                            it
                        )
                    )
                })
        }

        composable(
            Screen.Details.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: -1
            com.example.pethelper.ui.compose.petScreens.MyPetScreen(
                id = petId,
                onCardClick = {
                    navController.navigate(
                        Screen.Profile.createRoute(
                            it
                        )
                    )
                },
                onButtonClick = {
                    navController.navigate(
                        Screen.Notes.createRoute(
                            it
                        )
                    )
                },
                onNutritionClick = {
                    navController.navigate(
                        Screen.Nutrition.createRoute(
                            it
                        )
                    )
                },
                onHealthClick = {
                    navController.navigate(
                        Screen.Health.createRoute(
                            it
                        )
                    )
                },
                onRemindersClick = {
                    navController.navigate(
                        Screen.Reminders.createRoute(
                            it
                        )
                    )
                },
                onActivityClick = {
                    navController.navigate(
                        Screen.Activity.createRoute(
                            it
                        )
                    )
                }
            )
        }

        composable(
            Screen.Profile.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: -1
            com.example.pethelper.ui.compose.petScreens.PetProfileScreen(id = petId) { navController.popBackStack() }
        }

        composable(
            Screen.Notes.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: -1
            NotesScreen(
                petId,
                { navController.popBackStack() })
        }

        composable(
            Screen.Nutrition.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) {
            val petId = it.arguments?.getInt("petId") ?: -1
            NutritionScreen(
                petId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            Screen.Health.route,
            arguments = listOf(
                navArgument("petId") { type = NavType.IntType },
                navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
        ) {
            val petId = it.arguments?.getInt("petId") ?: -1
            val tab = it.arguments?.getInt("tab") ?: 0
            HealthScreen(
                petId,
                tab,
                { navController.popBackStack() }
            )
        }

        composable(
            Screen.Reminders.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) {
            val petId = it.arguments?.getInt("petId") ?: -1
            RemindersScreen(
                petId = petId,
                onBackClick = { navController.popBackStack() },
                onNavigateToHealth = { cat ->
                    val tabToOpen = when (cat) {
                        HealthCategory.VACCINE -> 3
                        HealthCategory.MEDICINE -> 1
                        HealthCategory.PREVENTION -> 2
                        HealthCategory.CHECKUP -> 4
                    }
                    navController.navigate(Screen.Health.createRoute(petId, tabToOpen))
                }
            )
        }

        composable(
            Screen.Activity.route,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) {
            val petId = it.arguments?.getInt("petId") ?: -1
            ActivityScreen(
                petId,
                { navController.popBackStack() },
            )
        }
    }
}