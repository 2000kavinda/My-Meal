package org.myapp.mymeal.view.buyMeal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.components.BottomNavigationBar
import org.myapp.mymeal.components.MealCard
import org.myapp.mymeal.controller.BuyMealController
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.ui.theme.ColorThemes


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealListScreen(
    repository: HistoryController,
    onMealClick: (Meal) -> Unit,
) {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var filteredMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val selectedItem = remember { mutableStateOf(0) }
    var buyMealController= BuyMealController()

    var meal = Meal(
        name = "Default Meal",
        photo = "https://example.com/default-photo.jpg",
        price = 0.0,
        description = "Default description",
        type = "Default type"
    )

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            meals = buyMealController.getMeals()
            filteredMeals = meals
            errorMessage = if (meals.isEmpty()) "No meals found" else ""
        } catch (e: Exception) {
            errorMessage = "Error fetching data: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
            .background(ColorThemes.PrimaryTextColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    filteredMeals = if (query.isEmpty()) {
                        meals
                    } else {
                        meals.filter { it.name.contains(query, ignoreCase = true) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                placeholder = { Text(text = "Search meals...") },
                singleLine = true
            )

            if (isLoading) {
                CircularProgressIndicator(color = ColorThemes.PrimaryButtonColor)
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage)
            } else {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    filteredMeals.forEach { meal ->
                        MealCard(meal = meal, onMealClick = onMealClick)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)

        ) {
            BottomNavigationBar(
                selectedItem = selectedItem.value,
                onItemSelected = { selectedItem.value = it },
                onProfileClick = {
                    navigationManager.navigateTo(Screen.ProfileScreen(meal = meal))
                }
            )
        }
    }
}


