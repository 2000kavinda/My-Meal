package org.myapp.mymeal

import Meal
import MealService
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MealApp(mealService: MealService, coroutineScope: CoroutineScope) {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Search for Meals", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Meal Name") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    meals = mealService.getMeals(query)
                }
            }
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (meals.isEmpty()) {
            Text("No Meals Found. Try Searching!")
        } else {
            MealList(meals = meals, onMealClicked = {
                println("Clicked: ${it.strMeal}")
            })
        }
    }
}

@Composable
fun MealList(meals: List<Meal>, onMealClicked: (Meal) -> Unit) {
    Column {
        meals.forEach { meal ->
            MealItem(meal, onMealClicked)
        }
    }
}

@Composable
fun MealItem(meal: Meal, onMealClicked: (Meal) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(text = meal.strMeal, style = MaterialTheme.typography.h6)
            Text(text = "Category: ${meal.strCategory}")
            Text(text = "Instructions: ${meal.strInstructions.take(100)}...")

            Button(onClick = { onMealClicked(meal) }) {
                Text("Details")
            }
        }
    }
}
