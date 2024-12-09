package org.myapp.mymeal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import createHttpClient
import io.ktor.client.engine.cio.CIO

@Composable
fun NavigationHost(navigationManager: NavigationManager) {
    val currentScreen by navigationManager.currentScreen.collectAsState()

    when (currentScreen) {
        is Screen.MealList -> MealListScreen(
            repository = FirestoreRepository(),
            onMealClick = { meal ->
                navigationManager.navigateTo(Screen.MealDetails(meal))
            }
        )
        is Screen.MealDetails -> MealDetailsScreen(
            meal = (currentScreen as Screen.MealDetails).meal,
            onBack = { navigationManager.navigateBack() },
        )
    }
}
