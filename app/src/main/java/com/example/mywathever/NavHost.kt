package com.example.mywathever

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyApp(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("shopping_list/{goalId}") { navBackStackEntry ->
            // Retrieve goalId from the route arguments
            val goalId = navBackStackEntry.arguments?.getString("goalId")?.toIntOrNull() ?: 0
            // Get a reference to MainViewModel
            val mainViewModel: MainViewModel = viewModel()
            // Pass the viewModel and goalId to the ShoppingListScreen
            ShoppingListScreen(viewModel = mainViewModel, goalId = goalId)
        }
        composable("createGoal") {
            CreateGoalScreen(navController = navController)
        }
        composable("goalSettings/{goalId}") { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId")?.toIntOrNull()
            if (goalId != null) {
                GoalSettingsScreen(goalId = goalId, navController = navController)
            }
        }
    }
}
