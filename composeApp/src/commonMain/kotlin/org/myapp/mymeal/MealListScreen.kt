package org.myapp.mymeal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
@Composable
fun MealListScreen(
    repository: FirestoreRepository,
    onMealClick: (Meal) -> Unit
) {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            meals = repository.getMeals()
            errorMessage = if (meals.isEmpty()) "No meals found" else ""
        } catch (e: Exception) {
            errorMessage = "Error fetching meals: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(meals.size) { index ->
                    val meal = meals[index]
                    MealCard(meal = meal, onMealClick = onMealClick)
                }
            }
        }
    }
}



@Composable
fun MealCard(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onMealClick(meal) },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = meal.photo,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Meal: ${meal.name}", fontWeight = FontWeight.Bold)
            Text(text = "Price: $${meal.price}")
        }
    }
}


