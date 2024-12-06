/*package org.myapp.mymeal

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun AppNavigation(repository: FirestoreRepository) {
    // Create the NavHostController using rememberNavController
    val navController = rememberNavController()

    // Use NavHost with NavHostController
    NavHost(navController = navController, startDestination = "mealList") {
        // Meal List Screen
        composable("mealList") {
            MealListScreen(repository = repository, navController = navController)
        }

        // Meal Details Screen
        composable(
            route = "mealDetails/{name}/{photo}/{price}",
            arguments = listOf(
                navArgument("name") { type = androidx.navigation.NavType.StringType },
                navArgument("photo") { type = androidx.navigation.NavType.StringType },
                navArgument("price") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
            val photo = backStackEntry.arguments?.getString("photo") ?: ""
            val price = backStackEntry.arguments?.getString("price") ?: "0.0"
            MealDetailsScreen(name = name, photo = photo, price = price)
        }
    }
}
*/