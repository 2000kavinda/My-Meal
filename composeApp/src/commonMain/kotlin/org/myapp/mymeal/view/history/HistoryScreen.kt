package org.myapp.mymeal.view.history

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
import org.myapp.mymeal.components.HistoryCard
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.ui.theme.ColorThemes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    sharedViewModel: SharedViewModel,
    repository: HistoryController,
    onMealClick: (Meal) -> Unit,
) {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var filteredMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    val meal = Meal(
        name = "Default Meal",
        photo = "",
        price = 0.0,
        description = "Default description",
        type = "Default type"
    )
    val selectedItem = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            meals = repository.getOrders(currentUserEmail?:"")
            filteredMeals = meals
            errorMessage = if (meals.isEmpty()) "No meals found" else ""

            } catch (e: Exception) {
            errorMessage = "Error fetching data: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorThemes.PrimaryTextColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(end = 16.dp,
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 56.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = ColorThemes.PrimaryButtonColor)
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage)
            } else {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    filteredMeals.forEach { meal ->
                        HistoryCard(meal = meal, onMealClick = onMealClick)
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







